package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.common.base.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
public interface BiEtlSyncPlanService extends Service<BiEtlSyncPlan> {


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

}
