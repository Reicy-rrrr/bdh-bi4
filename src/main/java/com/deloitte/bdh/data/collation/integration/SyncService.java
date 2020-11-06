package com.deloitte.bdh.data.collation.integration;

public interface SyncService {
    /**
     * process
     *
     * @param
     * @return
     */
    void sync() throws Exception;

    /**
     * process
     *
     * @param
     * @return
     */
    void etl() throws Exception;

    /**
     * process
     *
     * @param
     * @return
     */
    void model(String modelCode) throws Exception;
}
