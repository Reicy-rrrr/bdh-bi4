package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.RunPlan;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
public interface BiEtlSyncPlanService extends Service<BiEtlSyncPlan> {

    void createFirstPlan(RunPlan plan);

}
