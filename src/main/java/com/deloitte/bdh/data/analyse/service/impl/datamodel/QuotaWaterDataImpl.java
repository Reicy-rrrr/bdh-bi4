package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataUnitEnum;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("quotaWaterDataImpl")
public class QuotaWaterDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
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
