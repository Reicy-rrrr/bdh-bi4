package com.deloitte.bdh.data.integration;

import com.deloitte.bdh.data.model.BiConnections;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.model.request.CreateConnectionsDto;
import com.deloitte.bdh.data.model.request.JoinResourceDto;
import com.deloitte.bdh.data.model.resp.EtlProcessorsResp;

public interface EtlService {

    /**
     * 引入数据源
     *
     * @param
     * @return
     */
    BiProcessors joinResource(JoinResourceDto dto) throws Exception;

    /**
     * 移除processors
     *
     * @param
     * @return
     */
    void removeProcessors(String processorsCode) throws Exception;

    /**
     * 关联组件
     *
     * @param
     * @return
     */
    BiConnections connectProcessors(CreateConnectionsDto dto) throws Exception;

    /**
     * 查看组件详情（包含数据源与bi这面的自定义的组件）
     *
     * @param
     * @return
     */
    EtlProcessorsResp getProcessors(String processorsCode);
}
