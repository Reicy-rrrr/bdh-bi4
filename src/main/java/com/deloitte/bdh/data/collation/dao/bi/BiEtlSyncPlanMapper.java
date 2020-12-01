package com.deloitte.bdh.data.collation.dao.bi;

import com.deloitte.bdh.common.base.Mapper;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlanResult;
import com.deloitte.bdh.data.collation.model.request.BiEtlSyncPlanListDto;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lw
 * @since 2020-10-28
 */
public interface BiEtlSyncPlanMapper extends Mapper<BiEtlSyncPlan> {

    List<BiEtlSyncPlanResult> selectPlanList(BiEtlSyncPlanListDto syncPlan);
}
