package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("graphicsDataImpl")
public class GraphicsDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) throws Exception {
        String sql = buildSql(request.getDataConfig().getDataModel());
        return execute(request.getDataConfig().getDataModel(), sql, list -> {
            List<DataModelField> fields = request.getDataConfig().getDataModel().getX();
            List<String> wds = Lists.newArrayList();
            List<String> dls = Lists.newArrayList();
            for (DataModelField field : fields) {
                String name = StringUtils.isBlank(field.getAlias()) ? field.getId() : field.getAlias();
                if (DataModelTypeEnum.WD.getCode().equals(field.getQuota())) {
                    wds.add(name);
                } else {
                    dls.add(name);
                }
            }

            List<Map<String, Object>> result = Lists.newArrayList();
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
                result.add(one);
            }
            //求百分比
            for (Map<String, Object> map : result) {
                BigDecimal per = new BigDecimal(String.valueOf(map.get("count")))
                        .multiply(new BigDecimal("100"))
                        .divide(count, 2, BigDecimal.ROUND_HALF_UP);
                map.put("percent", per.doubleValue());
            }
            return result;
        });
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new RuntimeException("字段列表不能为空");
        }
        //饼图对度量和维度数量有校验
        List<DataModelField> dlFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.DL.getCode()))
                .collect(Collectors.toList());
        List<DataModelField> wdFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.WD.getCode()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(dlFields) || CollectionUtils.isEmpty(wdFields)) {
            throw new RuntimeException("维度与度量字段数量不能为空");
        }

        if (wdFields.size() > 2) {
            throw new RuntimeException("维度字段数量不能大于2");
        }

        if (dlFields.size() > 1) {
            throw new RuntimeException("度量字段数量不能大于1");
        }
        dataModel.setPage(null);
    }
}
