package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlSyncPlanMapper;
import com.deloitte.bdh.data.collation.model.request.BiEtlSyncPlanListDto;
import com.deloitte.bdh.data.collation.service.*;
import com.deloitte.bdh.common.base.AbstractService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    @Override
    public PageInfo<BiEtlSyncPlanResult> selectPlanList(BiEtlSyncPlanListDto dto) {
        PageHelper.startPage(dto.getPage(), dto.getSize());
        PageInfo<BiEtlSyncPlanResult> result = new PageInfo(syncPlanMapper.selectPlanList(dto));
        if (CollectionUtils.isNotEmpty(result.getList())) {
            List<BiEtlSyncPlanResult> plans = result.getList();
            for (BiEtlSyncPlanResult plan : plans) {
                // 执行时间默认为任务创建时间
                LocalDateTime executeTime = plan.getCreateDate();
                String cronExpression = plan.getModelCronExpression();
                if (StringUtils.isNotBlank(cronExpression)) {
                    // 下次任务的计划执行时间
                    LocalDateTime nextExecuteTime = getNextExecuteTime(cronExpression, executeTime);
                    // 本次任务的计划执行时间
                    LocalDateTime currExecuteTime = getLastExecuteTime(cronExpression, nextExecuteTime);
                    // 上次任务的计划执行时间
                    LocalDateTime lastExecuteTime = getLastExecuteTime(cronExpression, currExecuteTime);
                    plan.setPlanExecuteTime(currExecuteTime);
                    plan.setLastExecuteTime(lastExecuteTime);
                    plan.setNextExecuteTime(nextExecuteTime);
                }
                plan.setActualExecuteTime(executeTime);
                plan.setPlanTypeDesc(PlanTypeEnum.values(Integer.valueOf(plan.getPlanType())).getDesc());
                plan.setPlanStageDesc(PlanStageEnum.getValue(plan.getPlanStage()));

                // 计算计划执行百分比
                double executePercent = 0D;
                if (StringUtils.isBlank(plan.getSqlCount())) {
                    plan.setPercentage("100%");
                    continue;
                }
                if (StringUtils.isBlank(plan.getSqlLocalCount())) {
                    plan.setPercentage("0%");
                    continue;
                }

                Integer finishCount = Integer.valueOf(plan.getSqlLocalCount());
                Integer totalCount = Integer.valueOf(plan.getSqlCount());
                executePercent = BigDecimal.valueOf(finishCount * 100).divide(BigDecimal.valueOf(totalCount)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (executePercent > 100) {
                    executePercent = 100D;
                }
                plan.setPercentage(executePercent + "%");
            }
        }
        return result;
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
