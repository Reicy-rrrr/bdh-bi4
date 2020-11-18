package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.utils.BuildSqlUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service("quotaCoreDataImpl")
public class QuotaCoreDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) throws Exception {
        DataModel dataModel = request.getDataConfig().getDataModel();
        String sql = buildSql(dataModel);
        return execute(sql, list -> {
            //未开启直接返回
            if (!isOpen(dataModel)) {
                return list;
            }
            //开启需要自己组装sql并返回

            String sourceSql = doSourceSql(sql, dataModel);
            String hbSql = doHbSql(sql, dataModel);
            String tbeSql = doTbSql(sql, dataModel);

            return null;
        });
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new RuntimeException("字段列表不能为空");
        }

        //对度量和维度数量有校验
        List<DataModelField> dlFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.DL.getCode()))
                .collect(Collectors.toList());
        List<DataModelField> wdFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.WD.getCode()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(wdFields)) {
            throw new RuntimeException("核心指标图只能设置度量");
        }
        if (CollectionUtils.isEmpty(dlFields)) {
            throw new RuntimeException("核心指标图设置度量不能为空");
        }

        //校验同比环比
        if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
            if (isOpen(dataModel) && (StringUtils.isBlank(getCoreDateKey(dataModel)))
                    || StringUtils.isBlank(getCoreDateValue(dataModel))) {
                throw new RuntimeException("核心指标图开启同比或环比后，请选择日期");
            }
        }
        dataModel.setPage(null);
    }

    private String doSourceSql(String sql, DataModel dataModel) {
        String str = null;
        if ("year".equals(getCoreDateType(dataModel))) {
            str = " DATE_FORMAT('#','%Y')=DATE_FORMAT(#,'%Y') ";
        }
        if ("month".equals(getCoreDateType(dataModel))) {
            str = " DATE_FORMAT('#','%Y-%m')=DATE_FORMAT(#,'%Y-%m') ";
        }
        if ("day".equals(getCoreDateType(dataModel))) {
            str = " DATE_FORMAT('#','%Y-%m-%d')=DATE_FORMAT(#,'%Y-%m-%d') ";
        }
        if ("quaterly".equals(getCoreDateType(dataModel))) {
            str = " QUARTER('#')=QUARTER(#) AND DATE_FORMAT('#','%Y')=DATE_FORMAT(#,'%Y')";
        }
        String appendField = str.replaceFirst("#", getCoreDateValue(dataModel)).replace("#", getCoreDateKey(dataModel));
        return BuildSqlUtil.append(sql, appendField, 2);
    }

    private String doHbSql(String sql, DataModel dataModel) {
        //环比增长率=（本期数-上期数）/上期数×100%。
        String str = null;
        if ("year".equals(getCoreDateType(dataModel))) {
            str = " YEAR(DATE_ADD(STR_TO_DATE('#', '%Y-%m-%d'),interval-1 year))=DATE_FORMAT(#,'%Y') ";
        }
        if ("month".equals(getCoreDateType(dataModel))) {
            str = " LEFT(DATE_ADD(STR_TO_DATE('#', '%Y-%m-%d'),interval-1 month),7)=DATE_FORMAT(#,'%Y-%m') ";
        }
        if ("day".equals(getCoreDateType(dataModel))) {
            str = " DATE_ADD(STR_TO_DATE('#', '%Y-%m-%d'),interval-1 day)=DATE_FORMAT(#,'%Y-%m-%d') ";
        }
        if ("quaterly".equals(getCoreDateType(dataModel))) {
            str = " QUARTER(DATE_SUB('#',interval 1 QUARTER))=QUARTER(#) AND DATE_FORMAT('#','%Y')=DATE_FORMAT(#,'%Y') ";
        }
        String appendField = str.replace("#", getCoreDateValue(dataModel)).replace("#", getCoreDateKey(dataModel));
        return BuildSqlUtil.append(sql, appendField, 2);
    }

    private String doTbSql(String sql, DataModel dataModel) {
        //同比增长率=（本期数-同期数）/|同期数|×100%。本年度与上年度
        String str = null;
        if ("year".equals(getCoreDateType(dataModel))) {
            str = " YEAR(DATE_ADD(STR_TO_DATE('#', '%Y-%m-%d'),interval-1 year))=DATE_FORMAT(#,'%Y') ";
        }
        if ("month".equals(getCoreDateType(dataModel))) {
            str = " LEFT(DATE_ADD(STR_TO_DATE('#', '%Y-%m-%d'),interval-12 month),7)=DATE_FORMAT(#,'%Y-%m') ";
        }
        if ("day".equals(getCoreDateType(dataModel))) {
            str = " DATE_ADD('#',interval -1 year)=DATE_FORMAT(#,'%Y-%m-%d') ";
        }
        if ("quaterly".equals(getCoreDateType(dataModel))) {
            str = " QUARTER(DATE_SUB('#',interval 1 QUARTER))=QUARTER(#) AND YEAR(DATE_ADD(STR_TO_DATE('#', '%Y-%m-%d'),interval-1 year)) = DATE_FORMAT(#,'%Y') ";
        }
        String appendField = str.replace("#", getCoreDateValue(dataModel)).replace("#", getCoreDateKey(dataModel));
        return BuildSqlUtil.append(sql, appendField, 2);
    }

    private boolean isOpen(DataModel dataModel) {
        boolean isOpen = false;
        if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
            Boolean hb = getHb(dataModel);
            Boolean tb = getTb(dataModel);
            if (hb || tb) {
                isOpen = true;
            }
        }
        return isOpen;
    }

    private boolean getHb(DataModel dataModel) {
        Boolean bo = MapUtils.getBoolean(dataModel.getCustomParams(), CustomParamsConstants.CORE_HB);
        return null != bo && bo;
    }

    private boolean getTb(DataModel dataModel) {
        Boolean bo = MapUtils.getBoolean(dataModel.getCustomParams(), CustomParamsConstants.CORE_TB);
        return null != bo && bo;
    }

    private String getCoreDateKey(DataModel dataModel) {
        return MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.CORE_DATE_KEY);
    }

    private String getCoreDateValue(DataModel dataModel) {
        return MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.CORE_DATE_VALUE);
    }

    private String getCoreDateType(DataModel dataModel) {
        return MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.CORE_DATE_TYPE);
    }
}
