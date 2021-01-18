package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
public interface AnalyseDataService {

    /**
     * 获取组件数据
     * @param request
     * @return
     */
    BaseComponentDataResponse handle(ComponentDataRequest request) throws Exception;

}
