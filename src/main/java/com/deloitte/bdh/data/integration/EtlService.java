package com.deloitte.bdh.data.integration;

import com.deloitte.bdh.data.model.request.JoinResourceDto;

public interface EtlService {

    /**
     * 引入数据源
     *
     * @param
     * @return
     */
    void joinResource(JoinResourceDto dto) throws Exception;
}
