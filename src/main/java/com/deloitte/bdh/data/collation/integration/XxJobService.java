package com.deloitte.bdh.data.collation.integration;

import java.util.Map;

public interface XxJobService {
    /**
     * ADD
     */
    String GET_JOB = "/bdh-job-admin/bdhJob/getJob";

    /**
     * ADD
     */
    String ADD_PATH = "/bdh-job-admin/bdhJob/addBdhJobObj";

    /**
     * UPDATE
     */
    String UPDATE_PATH = "/bdh-job-admin/bdhJob/updateObj";

    /**
     * REMOVE
     */
    String REMOVE_PATH = "/bdh-job-admin/bdhJob/remove";

    /**
     * START
     */
    String START_PATH = "/bdh-job-admin/bdhJob/start";


    /**
     * STOP
     */
    String STOP_PATH = "/bdh-job-admin/bdhJob/stop";

    /**
     * trigger
     */
    String TRIGGER_PATH = "/bdh-job-admin/bdhJob/trigger";

    /**
     * trigger
     */
    String TRIGGER_PARAMS_PATH = "/bdh-job-admin/bdhJob/triggerWithParams";

    /**
     * getGroup
     */
    String LOAD_BY_TENANT = "/bdh-job-admin/bdhJob/loadByTenantCode";

    /**
     * saveGroup
     */
    String saveGroup = "/bdh-job-admin/bdhJob/saveGroup";

    /**
     * getJob
     *
     * @param
     * @return
     */
    String getJob(String jobDesc);

    /**
     * add
     *
     * @param
     * @return
     */
    void add(String modelCode, String callBackAddress, String cron, Map<String, String> params) throws Exception;

    /**
     * update
     *
     * @param
     * @return
     */
    void addOrUpdate(String modelCode, String callBackAddress, String cron, Map<String, String> params) throws Exception;


    /**
     * remove
     *
     * @param
     * @return
     */
    void remove(String modelCode) throws Exception;


    /**
     * start
     *
     * @param
     * @return
     */
    void start(String modelCode) throws Exception;


    /**
     * stop
     *
     * @param
     * @return
     */
    void stop(String modelCode) throws Exception;

    /**
     * trigger
     *
     * @param
     * @return
     */
    void trigger(String modelCode) throws Exception;

    /**
     * triggerParams
     *
     * @param
     * @return
     */
    void triggerParams(String modelCode, Map<String, String> params) throws Exception;

    /**
     * getGroupByTenant
     *
     * @param
     * @return
     */
    String getGroupByTenant();

    /**
     * saveGroup
     *
     * @param
     * @return
     */
    boolean saveGroup();

}
