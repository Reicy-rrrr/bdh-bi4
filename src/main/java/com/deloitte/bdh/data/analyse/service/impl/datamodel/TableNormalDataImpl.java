package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:普通表格
 */
@Service("tableNormalDataImpl")
public class TableNormalDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        Map<String, String> customParams = request.getDataConfig().getDataModel().getCustomParams();
        if (MapUtils.isNotEmpty(customParams)) {
            String tableAggregate = MapUtils.getString(customParams, CustomParamsConstants.TABLE_AGGREGATE);
            if (StringUtils.equals(tableAggregate, "true")) {
                if (CollectionUtils.isNotEmpty(request.getDataConfig().getDataModel().getX())) {
                    for (DataModelField field : request.getDataConfig().getDataModel().getX()) {
                        field.setQuota(DataModelTypeEnum.WD.getCode());
                    }
                }
            }
        }
        String sql = buildSql(request.getDataConfig().getDataModel());
        return execute(sql);
    }

    @Override
    protected void validate(DataModel dataModel) {
        //todo
    }
}
