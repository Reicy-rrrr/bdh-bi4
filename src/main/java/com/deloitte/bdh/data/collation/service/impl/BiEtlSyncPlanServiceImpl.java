package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlSyncPlanMapper;
import com.deloitte.bdh.data.collation.enums.PlanStageEnum;
import com.deloitte.bdh.data.collation.enums.PlanTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlanResult;
import com.deloitte.bdh.data.collation.model.RunPlan;
import com.deloitte.bdh.data.collation.model.request.BiEtlSyncPlanListDto;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

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
    public BiEtlSyncPlan createPlan(RunPlan plan) {
        BiEtlSyncPlan syncPlan = new BiEtlSyncPlan();
        syncPlan.setCode(GenerateCodeUtil.generate());
        syncPlan.setName(plan.getPlanName());
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

        // 初次配置同步的时候，可能没有cron 表达式，这是否需要设置？
        if (YesOrNoEnum.NO.getKey().equals(plan.getFirst()) && StringUtils.isNotBlank(plan.getCronExpression())) {
            // 查询历史最近的一次任务，创建时间作为本次任务的上次执行时间
            BiEtlSyncPlan hisPlan = this.getOne(new LambdaQueryWrapper<BiEtlSyncPlan>()
                    .eq(BiEtlSyncPlan::getRefModelCode, plan.getModelCode())
                    .eq(BiEtlSyncPlan::getRefMappingCode, plan.getRefCode())
                    .eq(BiEtlSyncPlan::getPlanType, plan.getPlanType())
                    .orderByDesc(BiEtlSyncPlan::getCreateDate)
                    .last("limit 1"));

            if (hisPlan != null) {
                syncPlan.setLastExecuteDate(hisPlan.getCreateDate());
            }
            LocalDateTime now = LocalDateTime.now();
            // 当前时间的上次执行时间为本次的计划时间
            LocalDateTime currExecuteTime = getLastExecuteTime(plan.getCronExpression(), now);
            LocalDateTime nextExecuteTime = getNextExecuteTime(plan.getCronExpression(), now);
            syncPlan.setCurrExecuteDate(currExecuteTime);
            syncPlan.setNextExecuteDate(nextExecuteTime);
        }
        syncPlanMapper.insert(syncPlan);
        return syncPlan;
    }

    @Override
    public PageInfo<BiEtlSyncPlanResult> selectPlanList(BiEtlSyncPlanListDto dto) {
        PageHelper.startPage(dto.getPage(), dto.getSize());
        PageInfo<BiEtlSyncPlanResult> result = new PageInfo(syncPlanMapper.selectPlanList(dto));
        if (CollectionUtils.isNotEmpty(result.getList())) {
            List<BiEtlSyncPlanResult> plans = result.getList();
            for (BiEtlSyncPlanResult plan : plans) {
                plan.setPlanTypeDesc(PlanTypeEnum.values(Integer.valueOf(plan.getPlanType())).getDesc());
                if (StringUtils.isNotBlank(plan.getResultDesc())) {
                    plan.setPlanStageDesc(PlanStageEnum.getValue(plan.getPlanStage()) + "：" + plan.getResultDesc());
                } else {
                    plan.setPlanStageDesc(PlanStageEnum.getValue(plan.getPlanStage()));
                }
            }
        }
        return result;
    }

    @Override
    public void clear() {
        syncPlanMapper.delete(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanStage, PlanStageEnum.EXECUTED)
                .lt(BiEtlSyncPlan::getCreateDate, DateUtils.getLastOfDay(-31))
                .isNotNull(BiEtlSyncPlan::getPlanResult)
        );
    }

    /**
     * 获取上次计划执行时间
     *
     * @param cronExpression cron表达式
     * @param targetTime     模板时间
     * @return LocalDateTime
     */
    private LocalDateTime getLastExecuteTime(String cronExpression, LocalDateTime targetTime) {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronParser cronParser = new CronParser(cronDefinition);
        // 获取上次执行的日期
        ExecutionTime executionTime = ExecutionTime.forCron(cronParser.parse(cronExpression));
        ZonedDateTime lastTime = executionTime.lastExecution(targetTime.atZone(ZoneId.systemDefault())).get();
        return lastTime.toLocalDateTime();
    }

    /**
     * 获取下次计划执行时间
     *
     * @param cronExpression cron表达式
     * @param targetTime     模板时间
     * @return LocalDateTime
     */
    private LocalDateTime getNextExecuteTime(String cronExpression, LocalDateTime targetTime) {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronParser cronParser = new CronParser(cronDefinition);
        // 获取上次执行的日期
        ExecutionTime executionTime = ExecutionTime.forCron(cronParser.parse(cronExpression));
        ZonedDateTime nextTime = executionTime.nextExecution(targetTime.atZone(ZoneId.systemDefault())).get();
        return nextTime.toLocalDateTime();
    }
}
