package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表盘
 */

@Service("quotaDashboardDataImpl")
public class QuotaDashboardDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) throws Exception {
        return execute(request.getDataConfig().getDataModel(), buildSql(request.getDataConfig().getDataModel()));
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
            throw new RuntimeException("仪表盘只能设置度量");
        }
        if (CollectionUtils.isEmpty(dlFields)) {
            throw new RuntimeException("仪表盘设置度量不能为空");
        }
        if (dlFields.size() > 1) {
            throw new RuntimeException("度量字段数量不能大于1");
        }
        dataModel.setPage(null);
    }
}
