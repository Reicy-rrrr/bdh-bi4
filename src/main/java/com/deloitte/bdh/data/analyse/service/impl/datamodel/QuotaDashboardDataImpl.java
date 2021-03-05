package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataUnitEnum;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表盘
 */

@Service("quotaDashboardDataImpl")
public class QuotaDashboardDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        BaseComponentDataResponse response = execute(dataModel, buildSql(request.getDataConfig().getDataModel()));
        List<Map<String, Object>> rows = response.getRows();
        DataModelField field = dataModel.getX().get(0);
        if (CollectionUtils.isNotEmpty(rows)) {
            Map<String, Object> map = rows.get(0);
            //设置精度和数据单位
            if (null != field.getPrecision()) {
                map.put("precision", field.getPrecision());
            }
            if (StringUtils.isNotBlank(field.getDataUnit())) {
                map.put("dataUnit", DataUnitEnum.getDesc(field.getDataUnit()));
            }
        }
        return response;
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException(ResourceMessageEnum.X_NOT_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.X_NOT_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }
        //对度量和维度数量有校验
        List<DataModelField> dlFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.DL.getCode()))
                .collect(Collectors.toList());
        List<DataModelField> wdFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.WD.getCode()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(wdFields)) {
            throw new BizException(ResourceMessageEnum.DL_ONLY.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_ONLY.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (CollectionUtils.isEmpty(dlFields)) {
            throw new BizException(ResourceMessageEnum.DL_NOT_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_NOT_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (dlFields.size() > 1) {
            throw new BizException(ResourceMessageEnum.DL_SIZE_ONE.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_SIZE_ONE.getMessage(), ThreadLocalHolder.getLang()));
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
