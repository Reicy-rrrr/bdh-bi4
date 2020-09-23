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
    Map<String, Object> createProcessGroup(Map<String, Object> map) throws Exception;

    /**
     * function:查询 processGroup
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getProcessGroup(String id) throws Exception;

    /**
     * function:创建 ControllerService
     *
     * @return Map<String, Object>
     */
    Map<String, Object> createControllerService(Map<String, Object> map) throws Exception;

    /**
     * function:查询 单个ControllerService
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getControllerService(String id) throws Exception;

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


}
