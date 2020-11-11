package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlSyncPlanMapper;
import com.deloitte.bdh.data.collation.service.*;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlSyncPlanServiceImpl extends AbstractService<BiEtlSyncPlanMapper, BiEtlSyncPlan> implements BiEtlSyncPlanService {

    @Resource
    private BiEtlSyncPlanMapper syncPlanMapper;

    @Override
    public void createFirstPlan(RunPlan plan) {
        BiEtlSyncPlan syncPlan = new BiEtlSyncPlan();
        syncPlan.setCode(GenerateCodeUtil.generate());
        syncPlan.setGroupCode(plan.getGroupCode());
        //0数据同步、1数据整理
        syncPlan.setPlanType(plan.getPlanType());
        syncPlan.setRefMappingCode(plan.getRefCode());
        syncPlan.setPlanStage(PlanStageEnum.TO_EXECUTE.getKey());
        syncPlan.setSqlLocalCount("0");
        syncPlan.setRefModelCode(plan.getModelCode());
        syncPlan.setTenantId(ThreadLocalHolder.getTenantId());
        syncPlan.setIsFirst(plan.getFirst());
        //设置已处理初始值为0
        syncPlan.setProcessCount("0");
        syncPlan.setPlanResult(null);
        syncPlan.setSqlCount(plan.getCount());
        syncPlanMapper.insert(syncPlan);
    }

}
