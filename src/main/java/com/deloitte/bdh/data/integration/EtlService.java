package com.deloitte.bdh.data.integration;

import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.request.JoinResourceDto;
import com.deloitte.bdh.data.model.resp.ProcessorsResp;

public interface EtlService {

    /**
     * 引入数据源
     *
     * @param
     * @return
     */
    void joinResource(JoinResourceDto dto) throws Exception;

    /**
     * 查看组件详情
     *
     * @param
     * @return
     */
    ProcessorsResp getProcessors(String processorsCode);
}
