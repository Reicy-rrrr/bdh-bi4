package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlanResult;
import com.deloitte.bdh.data.collation.model.RunPlan;
import com.deloitte.bdh.data.collation.model.request.BiEtlSyncPlanListDto;
import com.github.pagehelper.PageInfo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
public interface BiEtlSyncPlanService extends Service<BiEtlSyncPlan> {

    void createPlan(RunPlan plan);

    /**
     * 分页查询执行计划
     *
     * @param dto
     * @return
     */
    PageInfo<BiEtlSyncPlanResult> selectPlanList(BiEtlSyncPlanListDto dto);

    void clear();

}
