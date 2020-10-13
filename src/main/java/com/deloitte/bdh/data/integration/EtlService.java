package com.deloitte.bdh.data.integration;

import com.deloitte.bdh.data.model.BiConnections;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.model.request.CreateConnectionsDto;
import com.deloitte.bdh.data.model.request.CreateOutProcessorsDto;
import com.deloitte.bdh.data.model.request.JoinResourceDto;
import com.deloitte.bdh.data.model.request.RunModelDto;
import com.deloitte.bdh.data.model.resp.EtlProcessorsResp;
import com.deloitte.bdh.data.model.resp.EtlRunModelResp;

import java.util.List;

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
    List<BiConnections> connectProcessors(CreateConnectionsDto dto) throws Exception;

    /**
     * 取消关联组件
     *
     * @param
     * @return
     */
    void cancelConnectProcessors(String connectionsCode) throws Exception;

    /**
     * 查看组件详情（包含数据源与bi这面的自定义的组件）
     *
     * @param
     * @return
     */
    EtlProcessorsResp getProcessors(String processorsCode);

    /**
     * 查看组件列表
     *
     * @param
     * @return
     */
    List<EtlProcessorsResp> getProcessorsList(String modelCode);


    /**
     * 输出主键
     *
     * @param
     * @return
     */
    BiProcessors outProcessors(CreateOutProcessorsDto dto) throws Exception;

    /**
     * 运行主键
     *
     * @param
     * @return
     */
    EtlRunModelResp runModel(RunModelDto dto) throws Exception;

    /**
     * 预览组件
     *
     * @param
     * @return
     */
    String preview(String processorsCode) throws Exception;

}
