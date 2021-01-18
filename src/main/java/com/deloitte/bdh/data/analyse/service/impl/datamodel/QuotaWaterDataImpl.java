package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataUnitEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("quotaWaterDataImpl")
public class QuotaWaterDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) throws Exception {
        DataModel dataModel = request.getDataConfig().getDataModel();
        return execute(request.getDataConfig().getDataModel(), buildSql(request.getDataConfig().getDataModel()), list -> {
            if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
                String viewDetail = MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.VIEW_DETAIL);
                if (StringUtils.equals(viewDetail, "true")) {
                    return list;
                }
            }
            List<Map<String, Object>> result = Lists.newArrayList();
            Map<String, Object> map = Maps.newHashMap();
            //度量只会一个
            DataModelField field = dataModel.getX().get(0);
            for (Map<String, Object> var : list) {
                for (Map.Entry<String, Object> param : var.entrySet()) {
                    map.put("name", param.getKey());
                    map.put("percent", new BigDecimal(String.valueOf(param.getValue())));
                }
            }
            //设置精度和数据单位
            if (null != field.getPrecision()) {
                map.put("precision", field.getPrecision());
            }
            if (StringUtils.isNotBlank(field.getDataUnit())) {
                map.put("dataUnit", DataUnitEnum.getDesc(field.getDataUnit()));
            }
            result.add(map);
            return result;
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
            throw new RuntimeException("水波图只能设置度量");
        }
        if (CollectionUtils.isEmpty(dlFields)) {
            throw new RuntimeException("水波图设置度量不能为空");
        }
        if (dlFields.size() > 1) {
            throw new RuntimeException("度量字段数量不能大于1");
        }
        dataModel.setPage(null);
    }

    @Override
    public void before(DataModel dataModel) {
        super.before(dataModel);
        if (CollectionUtils.isNotEmpty(dataModel.getX())) {
            for (DataModelField s : dataModel.getX()) {
                s.setDefaultValue("0");
            }
        }
    }
}
