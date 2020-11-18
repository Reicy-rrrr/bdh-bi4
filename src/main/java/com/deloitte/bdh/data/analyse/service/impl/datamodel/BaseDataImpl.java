package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.springframework.stereotype.Service;

/**
 * Author:LIJUN
 * Date:18/11/2020
 * Description:
 */
@Service("baseDataImpl")
public class BaseDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        String sql = buildSql(request.getDataConfig().getDataModel());
        return execute(sql);
    }

    @Override
    protected void validate(DataModel dataModel) {
    }
}
