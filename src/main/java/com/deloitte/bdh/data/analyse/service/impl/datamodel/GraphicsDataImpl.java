package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.enums.AggregateTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
@Service("graphicsDataImpl")
public class GraphicsDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) throws Exception {
        String sql = buildSql(request.getDataConfig().getDataModel());
        return execute(sql);
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
        //校验度量聚合类型
        dlFields.forEach(s -> AggregateTypeEnum.get(s.getAggregateType()));

    }
}
