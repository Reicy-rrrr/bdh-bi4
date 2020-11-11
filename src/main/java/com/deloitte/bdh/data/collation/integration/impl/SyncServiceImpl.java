package com.deloitte.bdh.data.collation.integration.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.integration.SyncService;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.nifi.template.servie.Transfer;
import com.deloitte.bdh.data.collation.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@DS(DSConstant.BI_DB)
@Slf4j
public class SyncServiceImpl implements SyncService {
    @Resource
    private BiEtlSyncPlanService syncPlanService;
    @Autowired
    private BiEtlMappingConfigService configService;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private DbHandler dbHandler;
    @Autowired
    private BiComponentService componentService;
    @Autowired
    private BiEtlModelService modelService;
    @Autowired
    private BiEtlModelHandleService modelHandleService;
    @Autowired
    private Transfer transfer;

    @Override
    public void sync() {
        syncToExecute();
        syncExecuting();
    }

    private void syncToExecute() {
        //寻找类型为同步，状态为待执行的计划
        List<BiEtlSyncPlan> list = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "0")
                .eq(BiEtlSyncPlan::getPlanStage, PlanStageEnum.TO_EXECUTE.getKey())
                .isNull(BiEtlSyncPlan::getPlanResult)
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
                .last("limit 50")
        );

        list.forEach(s -> {
            if (YesOrNoEnum.YES.getKey().equals(s.getIsFirst())) {
                syncToExecuteNonTask(s);
            } else {
                syncToExecuteTask(s);
            }
        });

    }

    private void syncExecuting() {
        //寻找类型为同步，状态为待执行的计划
        List<BiEtlSyncPlan> list = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "0")
                .eq(BiEtlSyncPlan::getPlanStage, PlanStageEnum.EXECUTING.getKey())
                .isNull(BiEtlSyncPlan::getPlanResult)
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
                .last("limit 50")
        );
        list.forEach(this::syncExecutingTask);

    }

    private void syncToExecuteNonTask(BiEtlSyncPlan plan) {
        int count = Integer.parseInt(plan.getProcessCount());
        try {
            //判断已处理次数,超过3次则动作完成。
            if (3 < count) {
                plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            } else {
                //组装数据 启动nifi 改变执行状态
                BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                        .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
                );

                //非调度发起的同步第一次
                if (0 == count) {
                    dbHandler.truncateTable(config.getToTableName());
                }
                //获取归属组件信息
                String processorsGroupId = componentService.getProcessorsGroupId(config.getRefComponentCode());
                //启动NIFI
                transfer.run(processorsGroupId);
                //修改plan 执行状态
                plan.setPlanStage(PlanStageEnum.EXECUTING.getKey());
                //重置
                plan.setProcessCount("0");
                plan.setResultDesc(null);
            }
        } catch (Exception e1) {
            log.error("sync.syncToExecuteNonTask:" + e1);
            count++;
            plan.setResultDesc(e1.getMessage());
            plan.setProcessCount(String.valueOf(count));
        } finally {
            syncPlanService.updateById(plan);
        }
    }

    private void syncToExecuteTask(BiEtlSyncPlan plan) {
        int count = Integer.parseInt(plan.getProcessCount());
        try {
            //判断已处理次数,超过3次则动作完成。
            if (3 < count) {
                plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            } else {
                //组装数据 启动nifi 改变执行状态
                BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                        .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
                );
                String processorsGroupId = componentService.getProcessorsGroupId(config.getRefComponentCode());
                SyncTypeEnum typeEnum = SyncTypeEnum.getEnumByKey(config.getType());
                //第一次执行时，当为全量则清空，增量不处理
                if (0 == count && SyncTypeEnum.FULL == typeEnum) {
                    dbHandler.truncateTable(config.getToTableName());
                    transfer.clear(processorsGroupId);
                }
                //启动NIFI
                transfer.run(processorsGroupId);
                //修改plan 执行状态
                plan.setPlanStage(PlanStageEnum.EXECUTING.getKey());
                //重置
                plan.setProcessCount("0");
                plan.setResultDesc(null);
            }
        } catch (Exception e1) {
            log.error("sync.syncToExecuteTask:" + e1);
            count++;
            plan.setResultDesc(e1.getMessage());
            plan.setProcessCount(String.valueOf(count));
        } finally {
            syncPlanService.updateById(plan);
        }
    }

    private void syncExecutingTask(BiEtlSyncPlan plan) {
        int count = Integer.parseInt(plan.getProcessCount());
        BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
        );

        try {
            //判断已处理次数,超过10次则动作完成。
            if (10 < count) {
                plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                plan.setPlanResult(PlanResultEnum.FAIL.getKey());
                //调用nifi 停止与清空
                String processorsGroupId = componentService.getProcessorsGroupId(config.getRefComponentCode());
                transfer.stop(processorsGroupId);

                //判断是全量还是增量，是否清空表与 nifi偏移量？todo
            } else {
                count++;
                //基于条件实时查询 localCount
                String condition = assemblyCondition(plan.getIsFirst(), config);
                long nowCount = dbHandler.getCount(config.getToTableName(), condition);

                //判断目标数据库与源数据库的表count
                String sqlCount = plan.getSqlCount();
                String localCount = String.valueOf(nowCount);
                if (Long.parseLong(localCount) < Long.parseLong(sqlCount)) {
                    // 等待下次再查询
                    plan.setSqlLocalCount(localCount);
                } else {
                    //已同步完成
                    plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());

                    //调用nifi 停止与清空
                    String processorsGroupId = componentService.getProcessorsGroupId(config.getRefComponentCode());
                    transfer.stop(processorsGroupId);

                    //修改plan 执行状态
                    plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                    plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());
                    plan.setResultDesc(PlanResultEnum.SUCCESS.getValue());

                    //获取停止nifi后的本地最新的数据count
                    nowCount = dbHandler.getCount(config.getToTableName(), condition);
                    plan.setSqlLocalCount(String.valueOf(nowCount));
                    // 设置MappingConfig 的 LOCAL_COUNT和 OFFSET_VALUE todo
                    config.setLocalCount(String.valueOf(nowCount));
//                    config.setOffsetValue();
                    configService.updateById(config);

                    //设置Component 状态为可用
                    BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                            .eq(BiComponent::getCode, config.getRefComponentCode())
                    );
                    component.setEffect(EffectEnum.ENABLE.getKey());
                    componentService.updateById(component);
                }
            }
        } catch (Exception e1) {
            log.error("sync.syncExecutingTask:" + e1);
            plan.setResultDesc(e1.getMessage());
        } finally {
            plan.setProcessCount(String.valueOf(count));
            syncPlanService.updateById(plan);
        }
    }

    private String assemblyCondition(String isFirst, BiEtlMappingConfig config) {
        String condition = null;
        //非第一次且是增量
        if (YesOrNoEnum.NO.getKey().equals(isFirst)) {
            SyncTypeEnum typeEnum = SyncTypeEnum.getEnumByKey(config.getType());
            if (SyncTypeEnum.INCREMENT == typeEnum) {
                String offsetField = config.getOffsetField();
                String offsetValue = config.getOffsetValue();
                if (StringUtils.isNotBlank(offsetValue)) {
                    condition = "'" + offsetField + "' > =" + "'" + offsetValue + "'";
                }
            }
        }
        return condition;
    }

    @Override
    public void etl() throws Exception {
        etlToExecute();
        etlExecuting();
    }

    private void etlToExecute() throws Exception {
        //寻找类型为同步，状态为待执行的计划
        List<BiEtlSyncPlan> list = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "1")
                .eq(BiEtlSyncPlan::getPlanStage, PlanStageEnum.TO_EXECUTE.getKey())
                .isNull(BiEtlSyncPlan::getPlanResult)
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
                .last("limit 50")
        );
        for (BiEtlSyncPlan syncPlan : list) {
            etlToExecuteTask(syncPlan);
        }
    }


    private void etlToExecuteTask(BiEtlSyncPlan plan) {
        try {
            //查看所属组是否都已经同步完成
            List<BiEtlSyncPlan> synclist = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                    .eq(BiEtlSyncPlan::getPlanType, "0")
                    .eq(BiEtlSyncPlan::getGroupCode, plan.getGroupCode())
            );

            for (BiEtlSyncPlan syncPlan : synclist) {
                if (PlanResultEnum.FAIL.getKey().equals(syncPlan.getPlanResult())) {
                    throw new RuntimeException("依赖的同步任务失败:" + syncPlan.getCode());
                }
                //todo 取消状态呢？

                //有任务正在运行中，直接返回待下次处理
                if (null == syncPlan.getPlanResult()) {
                    return;
                }
            }
            //同步任务已经执行完成，开始etl
            ComponentModel componentModel = modelHandleService.handleModel(plan.getRefModelCode());

            //etl组件对应的就是processorsCode
            String processorsCode = plan.getRefMappingCode();
            String tableName = componentModel.getTableName();
            String query = componentModel.getQuerySql();
            String count = String.valueOf(dbHandler.getCountLocal(query));
            //清空
            dbHandler.truncateTable(tableName);
            //启动NIFI
            BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                    .eq(BiProcessors::getCode, processorsCode)
            );
            transfer.run(processors.getProcessGroupId());
            //修改plan 执行状态
            plan.setPlanStage(PlanStageEnum.EXECUTING.getKey());
            plan.setSqlCount(count);
            //重置
            plan.setProcessCount("0");
            plan.setResultDesc(null);
        } catch (Exception e) {
            log.error("etl.etlToExecuteTask:" + e);
            plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            plan.setResultDesc(e.getMessage());
        } finally {
            syncPlanService.updateById(plan);
        }

    }

    private void etlExecuting() {
        //寻找类型为同步，状态为待执行的计划
        List<BiEtlSyncPlan> list = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "1")
                .eq(BiEtlSyncPlan::getPlanStage, PlanStageEnum.EXECUTING.getKey())
                .isNull(BiEtlSyncPlan::getPlanResult)
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
                .last("limit 50")
        );
        list.forEach(this::etlExecutingTask);

    }

    private void etlExecutingTask(BiEtlSyncPlan plan) {
        int count = Integer.parseInt(plan.getProcessCount());
        ComponentModel componentModel = modelHandleService.handleModel(plan.getRefModelCode());
        String processorsCode = plan.getRefMappingCode();
        String tableName = componentModel.getTableName();

        BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, plan.getRefModelCode())
        );

        boolean syncStatus = false;
        try {
            //判断已处理次数,超过10次则动作完成。
            if (10 < count) {
                syncStatus = true;
                plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                plan.setPlanResult(PlanResultEnum.FAIL.getKey());
                //调用nifi 停止与清空
                BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                        .eq(BiProcessors::getCode, processorsCode)
                );
                transfer.stop(processors.getProcessGroupId());
            } else {
                count++;
                //基于条件实时查询 localCount
//                String condition = assemblyCondition(plan.getIsFirst(), config);
                long nowCount = dbHandler.getCount(tableName, null);

                //判断目标数据库与源数据库的表count
                String sqlCount = plan.getSqlCount();
                String localCount = String.valueOf(nowCount);
                if (Long.parseLong(localCount) < Long.parseLong(sqlCount)) {
                    // 等待下次再查询
                    plan.setSqlLocalCount(localCount);
                } else {
                    syncStatus = true;
                    //已同步完成
                    plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());

                    //调用nifi 停止与清空
                    BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                            .eq(BiProcessors::getCode, processorsCode)
                    );
                    transfer.run(processors.getProcessGroupId());

                    //修改plan 执行状态
                    plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                    plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());
                    plan.setResultDesc(PlanResultEnum.SUCCESS.getValue());

                    //获取停止nifi后的本地最新的数据count
                    nowCount = dbHandler.getCount(tableName, null);
                    plan.setSqlLocalCount(String.valueOf(nowCount));
                }
            }
        } catch (Exception e1) {
            log.error("etl.etlExecutingTask:" + e1);
            plan.setResultDesc(e1.getMessage());
        } finally {
            plan.setProcessCount(String.valueOf(count));
            syncPlanService.updateById(plan);
            if (syncStatus) {
                //改变model状态
                model.setSyncStatus(YesOrNoEnum.NO.getKey());
                modelService.updateById(model);
            }
        }
    }

    @Override
    public void model(String modelCode) throws Exception {
        //查询model信息，生成执行集计划
        BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );
        if (YesOrNoEnum.NO.getKey().equals(model.getValidate()) || EffectEnum.DISABLE.getKey().equals(model.getEffect())
                || RunStatusEnum.STOP.getKey().equals(model.getStatus()) || YesOrNoEnum.YES.getKey().equals(model.getSyncStatus())) {
            return;
        }
        //首先获取模板下的数据源组件
        //DATASOURCE
        List<BiComponent> components = componentService.list(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefModelCode, modelCode)
                .eq(BiComponent::getType, ComponentTypeEnum.DATASOURCE.getKey())
        );

        BiProcessors out = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getRelModelCode, modelCode)
                .eq(BiProcessors::getType, BiProcessorsTypeEnum.ETL_SOURCE.getType())
        );

        final String groupCode = GenerateCodeUtil.generate();
        List<RunPlan> runPlans = Lists.newArrayList();

        //database
        List<BiComponent> dbComponents = components.stream()
                .filter(s -> s.getType().equals(ComponentTypeEnum.DATASOURCE.getKey())).collect(Collectors.toList());
        for (BiComponent component : dbComponents) {
            BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                    .eq(BiEtlMappingConfig::getCode, component.getRefMappingCode())
                    .eq(BiEtlMappingConfig::getRefComponentCode, component.getCode()));
            //判断是否归属当前模板
            if (null == config) {
                continue;
            }
            //直连返回
            if (config.getType().equals(SyncTypeEnum.DIRECT.getValue())) {
                continue;
            }
            RunPlan runPlan = RunPlan.builder().groupCode(groupCode).planType("0")
                    .first(YesOrNoEnum.NO.getKey()).modelCode(modelCode).mappingConfigCode(config)
                    .synCount();
            runPlans.add(runPlan);
        }


        //out 当前未count
        RunPlan outPlan = RunPlan.builder().groupCode(groupCode).planType("1")
                .first(YesOrNoEnum.NO.getKey()).modelCode(modelCode).refCode(out.getCode());
        runPlans.add(outPlan);

        runPlans.forEach(s -> syncPlanService.createFirstPlan(s));

        //状态变为正在同步中
        model.setSyncStatus(YesOrNoEnum.YES.getKey());
        modelService.updateById(model);
    }
}
