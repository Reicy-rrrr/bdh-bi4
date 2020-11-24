package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.springframework.stereotype.Service;

/**
 * 基础格式返回实现类
 */
@Service("baseDataImpl")
public class BaseDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {

        return execute(buildSql(request.getDataConfig().getDataModel()));
    }

    @Override
    protected void validate(DataModel dataModel) {
        //todo
    }
}