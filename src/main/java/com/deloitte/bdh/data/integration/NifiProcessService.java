package com.deloitte.bdh.data.integration;


import java.util.Map;

public interface NifiProcessService {

    /**
     * function:获取集群状态
     *
     * @return Object
     */
    Map<String, Object> cluster() throws Exception;

    /**
     * function:获取token
     *
     * @return String
     */
    String getToken() throws Exception;

    /**
     * function:获取 RootGroupInfo
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getRootGroupInfo() throws Exception;

    /**
     * function:创建 processGroup
     *
     * @return Map<String, Object>
     */
    Map<String, Object> createProcessGroup(Map<String, Object> map, String id) throws Exception;

    /**
     * function:查询 processGroup
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getProcessGroup(String id) throws Exception;

    /**
     * function:删除 processGroup
     *
     * @return Map<String, Object>
     */
    Map<String, Object> delProcessGroup(String id) throws Exception;

    /**
     * function:upd processGroup
     *
     * @return Map<String, Object>
     */
    Map<String, Object> updProcessGroup(Map<String, Object> map) throws Exception;

    /**
     * function:  启动与停止
     *
     * @return Map<String, Object>
     */
    Map<String, Object> runState(String id, String state, boolean group) throws Exception;

    /**
     * function:创建 ControllerService
     *
     * @return Map<String, Object>
     */
    Map<String, Object> createControllerService(Map<String, Object> map) throws Exception;

    /**
     * function:创建除DB类型外的 ControllerService
     *
     * @return Map<String, Object>
     */
    Map<String, Object> createOtherControllerService(Map<String, Object> map) throws Exception;

    /**
     * function:  启动与停止
     *
     * @return Map<String, Object>
     */
    Map<String, Object> runControllerService(String id, String state) throws Exception;

    /**
     * function:查询 单个ControllerService
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getControllerService(String id) throws Exception;

    /**
     * function:删除 单个ControllerService
     *
     * @return Map<String, Object>
     */
    Map<String, Object> delControllerService(String id) throws Exception;

    /**
     * function:修改 单个ControllerService
     *
     * @return Map<String, Object>
     */
    Map<String, Object> updControllerService(Map<String, Object> map) throws Exception;

    /**
     * function:创建 createProcessor
     *
     * @return Map<String, Object>
     */
    Map<String, Object> createProcessor(Map<String, Object> map) throws Exception;

    /**
     * function:查询 getProcessor
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getProcessor(String id) throws Exception;

    /**
     * function:创建 updateProcessor
     *
     * @return Map<String, Object>
     */
    Map<String, Object> updateProcessor(Map<String, Object> map) throws Exception;

    /**
     * function:删除 delProcessor
     *
     * @return id
     */
    Map<String, Object> delProcessor(String id) throws Exception;

    /**
     * function:创建 createConnections
     *
     * @return Map<String, Object>
     */
    Map<String, Object> createConnections(Map<String, Object> map, String id) throws Exception;

    /**
     * function:查询 connections
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getConnections(String id) throws Exception;

    /**
     * function:dropConnections
     *
     * @return Map<String, Object>
     */
    Map<String, Object> dropConnections(String id) throws Exception;

    /**
     * function:delConnections
     *
     * @return Map<String, Object>
     */
    Map<String, Object> delConnections(String id) throws Exception;

    /**
     * function:查询 getListing
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getListingRequest(String connectionId) throws Exception;

    /**
     * function:查询 getListing
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getFlowFileList(String connectionId, String requestId) throws Exception;

    /**
     * function:查询 content
     *
     * @return Map<String, Object>
     */
    String getFlowFileContent(String connectionId, String flowFileId, String clusterNodeId) throws Exception;

    /**
     * function:查询 content(整合，默认读取所有)
     *
     * @return Map<String, Object>
     */
    String preview(String connectionId) throws Exception;
}
