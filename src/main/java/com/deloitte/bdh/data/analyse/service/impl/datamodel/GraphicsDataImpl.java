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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("graphicsDataImpl")
public class GraphicsDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        String sql = buildSql(dataModel);
        return execute(request.getDataConfig().getDataModel(), sql, list -> {
            if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
                String viewDetail = MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.VIEW_DETAIL);
                if (org.apache.commons.lang3.StringUtils.equals(viewDetail, "true")) {
                    return list;
                }
            }
            List<DataModelField> fields = request.getDataConfig().getDataModel().getX();
            List<String> wds = Lists.newArrayList();
            List<String> dls = Lists.newArrayList();
            Map<String, String> precisionMap = Maps.newHashMap();
            Map<String, String> dataUnitMap = Maps.newHashMap();
            for (DataModelField field : fields) {
                String name = StringUtils.isBlank(field.getAlias()) ? field.getId() : field.getAlias();
                if (DataModelTypeEnum.WD.getCode().equals(field.getQuota())) {
                    wds.add(name);
                } else {
                    dls.add(name);
                }
                if (null != field.getPrecision()) {
                    precisionMap.put(name, field.getPrecision().toString());
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(field.getDataUnit())) {
                    dataUnitMap.put(name, field.getDataUnit());
                }
            }

            List<Map<String, Object>> temp = Lists.newArrayList();
            BigDecimal count = BigDecimal.ZERO;
            for (Map<String, Object> map : list) {
                Map<String, Object> one = Maps.newHashMap();
                List<Object> itemValue = Lists.newArrayList();
                for (String str : wds) {
                    itemValue.add(map.get(str));
                }
                one.put("item", itemValue);

                //度量只会一个
                for (String str : dls) {
                    one.put("count", map.get(str));
                    count = count.add(new BigDecimal(String.valueOf(map.get(str))));
                }

                //设置精度和数据单位
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (null != MapUtils.getObject(precisionMap, entry.getKey())) {
                        one.put("precision", MapUtils.getObject(precisionMap, entry.getKey()));
                    }
                }
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (null != MapUtils.getObject(dataUnitMap, entry.getKey())) {
                        one.put("dataUnit", DataUnitEnum.getDesc(MapUtils.getObject(dataUnitMap, entry.getKey())));
                    }
                }
                temp.add(one);
            }
            //求百分比
            List<Map<String, Object>> result = Lists.newArrayList();
            for (Map<String, Object> map : temp) {
                BigDecimal per = new BigDecimal(String.valueOf(map.get("count")));
                if (per.compareTo(BigDecimal.ZERO) < 1) {
                    continue;
                }
                result.add(map);
                map.put("percent", per.multiply(new BigDecimal("100")).divide(count, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            return result;
        });
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException(ResourceMessageEnum.X_NOT_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.X_NOT_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }
        //饼图对度量和维度数量有校验
        List<DataModelField> dlFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.DL.getCode()))
                .collect(Collectors.toList());
        List<DataModelField> wdFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.WD.getCode()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(wdFields)) {
            throw new BizException(ResourceMessageEnum.WD_NOT_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.X_NOT_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (CollectionUtils.isEmpty(dlFields)) {
            throw new BizException(ResourceMessageEnum.DL_NOT_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.X_NOT_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (wdFields.size() > 2) {
            throw new BizException(ResourceMessageEnum.WD_SIZE_TWO.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.WD_SIZE_TWO.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (dlFields.size() > 1) {
            throw new BizException(ResourceMessageEnum.DL_SIZE_ONE.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_SIZE_ONE.getMessage(), ThreadLocalHolder.getLang()));
        }
        dataModel.setPage(null);
    }
}
