package com.deloitte.bdh.data.collation.integration.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.BiProcessorsTypeEnum;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.PlanResultEnum;
import com.deloitte.bdh.data.collation.enums.PlanStageEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.integration.SyncService;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.deloitte.bdh.data.collation.model.RunPlan;
import com.deloitte.bdh.data.collation.model.request.ConditionDto;
import com.deloitte.bdh.data.collation.nifi.template.servie.Transfer;
import com.deloitte.bdh.data.collation.service.BiComponentParamsService;
import com.deloitte.bdh.data.collation.service.BiComponentService;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.deloitte.bdh.data.collation.service.BiEtlModelHandleService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import com.deloitte.bdh.data.collation.service.BiProcessorsService;
import com.deloitte.bdh.data.collation.service.Producter;

import lombok.extern.slf4j.Slf4j;

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
    private BiComponentParamsService paramsService;
    @Autowired
    private BiEtlDatabaseInfService biEtlDatabaseInfService;
    @Autowired
    private BiEtlModelService modelService;
    @Autowired
    private BiEtlModelHandleService modelHandleService;
    @Autowired
    private Transfer transfer;
//    @Autowired
//    private KafkaProducter KafkaProducter;
    @Autowired
    private Producter KafkaProducter;

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
            if (5 < count) {
                //判断已处理次数,超过5次则动作完成。
                throw new RuntimeException("任务处理超时");
            }
            //组装数据 启动nifi 改变执行状态
            BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                    .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
            );

            //非调度发起的同步第一次
            if (0 == count) {
                //校验表结构
                String result = configService.validateSource(config);
                if (null != result) {
                    throw new RuntimeException(result);
                }
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
        } catch (Exception e) {
            log.error("sync.syncToExecuteNonTask:", e);
            count++;
            plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
            plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            plan.setResultDesc(e.getMessage());
            plan.setProcessCount(String.valueOf(count));
        } finally {
            syncPlanService.updateById(plan);
        }
    }

    private void syncToExecuteTask(BiEtlSyncPlan plan) {
        int count = Integer.parseInt(plan.getProcessCount());
        try {
            if (5 < count) {
                //判断已处理次数,超过5次则动作完成。
                throw new RuntimeException("任务处理超时");
            }
            //组装数据 启动nifi 改变执行状态
            BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                    .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
            );
            String processorsGroupId = componentService.getProcessorsGroupId(config.getRefComponentCode());
            SyncTypeEnum typeEnum = SyncTypeEnum.getEnumByKey(config.getType());
            //第一次执行时，当为全量则清空，增量不处理
            if (0 == count && SyncTypeEnum.FULL == typeEnum) {
                dbHandler.truncateTable(config.getToTableName());
                //#10002此处容错，保证当前是停止的
                transfer.stop(processorsGroupId);
                //清空
                transfer.clear(processorsGroupId);
            }
            //启动NIFI
            transfer.run(processorsGroupId);
            //修改plan 执行状态
            plan.setPlanStage(PlanStageEnum.EXECUTING.getKey());
            //重置
            plan.setProcessCount("0");
            plan.setResultDesc(null);
        } catch (Exception e) {
            log.error("sync.syncToExecuteTask:", e);
            count++;
            plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
            plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            plan.setResultDesc(e.getMessage());
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
        String processorsGroupId = componentService.getProcessorsGroupId(config.getRefComponentCode());
        boolean retry = false;
        try {
            if (10 < count) {
                //判断已处理次数,超过10次则动作完成。
                throw new RuntimeException("任务处理超时");
                //判断是全量还是增量，是否清空表与 nifi偏移量？todo
            }
            count++;
            //基于条件实时查询 localCount
//            String condition = assemblyCondition(plan.getIsFirst(), config);
            long nowCount = dbHandler.getCount(config.getToTableName(), null);

            //判断目标数据库与源数据库的表count
            String sqlCount = plan.getSqlCount();
            String localCount = String.valueOf(nowCount);
            if (Long.parseLong(localCount) < Long.parseLong(sqlCount)) {
                retry = true;
                // 等待下次再查询
                plan.setSqlLocalCount(localCount);
            } else {
                //已同步完成
                plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());

                //修改plan 执行状态
                plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());
                plan.setResultDesc(PlanResultEnum.SUCCESS.getValue());

                //获取停止nifi后的本地最新的数据count
                nowCount = dbHandler.getCount(config.getToTableName(), null);
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
        } catch (Exception e) {
            log.error("sync.syncExecutingTask:", e);
            plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
            plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            plan.setResultDesc(e.getMessage());
        } finally {
            plan.setProcessCount(String.valueOf(count));
            syncPlanService.updateById(plan);

            //不再重试
            if (!retry) {
                try {
                    //#10002 此处需要容错
                    transfer.stop(processorsGroupId);
                } catch (Exception e1) {
                    log.error("sync.syncExecutingTask.stop NIFI:", e1);
                }
            }
        }
    }

    @Deprecated
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

            //触发情况下，不会有数据源的同步
            if (!CollectionUtils.isEmpty(synclist)) {
                for (BiEtlSyncPlan syncPlan : synclist) {
                    if (PlanResultEnum.FAIL.getKey().equals(syncPlan.getPlanResult())) {
                        throw new RuntimeException("依赖的同步任务失败，任务名称：" + syncPlan.getName());
                    }
                    //有任务正在运行中，直接返回待下次处理
                    if (null == syncPlan.getPlanResult()) {
                        return;
                    }
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
            log.error("etl.etlToExecuteTask:", e);
            plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
            plan.setResultDesc(e.getMessage());

            //改变model的运行状态
            BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, plan.getRefModelCode()));
            model.setSyncStatus(YesOrNoEnum.NO.getKey());
            modelService.updateById(model);
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

        BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getCode, processorsCode)
        );

        boolean retry = false;
        try {
            if (10 < count) {
                //判断已处理次数,超过10次则动作完成。
                throw new RuntimeException("任务处理超时");
            }
            count++;
            //基于条件实时查询 localCount
//                String condition = assemblyCondition(plan.getIsFirst(), config);
            long nowCount = dbHandler.getCount(tableName, null);

            //判断目标数据库与源数据库的表count
            String sqlCount = plan.getSqlCount();
            String localCount = String.valueOf(nowCount);
            if (Long.parseLong(localCount) < Long.parseLong(sqlCount)) {
                retry = true;
                // 等待下次再查询
                plan.setSqlLocalCount(localCount);
            } else {
                //已同步完成
                plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());

                //修改plan 执行状态
                plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());
                plan.setResultDesc(PlanResultEnum.SUCCESS.getValue());

                //获取停止nifi后的本地最新的数据count
                nowCount = dbHandler.getCount(tableName, null);
                plan.setSqlLocalCount(String.valueOf(nowCount));

                //改变model状态为非运行
                model.setSyncStatus(YesOrNoEnum.NO.getKey());
                model.setLastExecuteDate(LocalDateTime.now());
                modelService.updateById(model);
            }
        } catch (Exception e) {
            log.error("etl.etlExecutingTask:", e);
            plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
            plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            plan.setResultDesc(e.getMessage());

            //改变model状态为非运行
            model.setSyncStatus(YesOrNoEnum.NO.getKey());
            modelService.updateById(model);
        } finally {
            plan.setProcessCount(String.valueOf(count));
            syncPlanService.updateById(plan);

            //不再重试
            if (!retry) {
                try {
                    //#10002 此处需要容错
                    transfer.stop(processors.getProcessGroupId());
                } catch (Exception e1) {
                    log.error("sync.syncExecutingTask.stop NIFI:", e1);
                }
            }
        }
    }

    @Override
    public void model(String modelCode, String isTrigger) throws Exception {
        //查询model信息，生成执行计划集
        BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );

        if (null == model) {
            log.error("Etl调度验证失败,模板不存在, 调度模板编码:{}", modelCode);
            return;
        }

        if (YesOrNoEnum.NO.getKey().equals(model.getValidate()) || EffectEnum.DISABLE.getKey().equals(model.getEffect())
                || RunStatusEnum.STOP.getKey().equals(model.getStatus()) || YesOrNoEnum.YES.getKey().equals(model.getSyncStatus())) {
            log.error("Etl调度验证失败,模板状态不正常, 调度模板编码:{}", modelCode);
            //todo 抛出事件修改model validate
            return;
        }
        //首先获取模板下的数据源组件
        List<BiComponent> components = componentService.list(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefModelCode, modelCode)
                .eq(BiComponent::getType, ComponentTypeEnum.DATASOURCE.getKey())
        );
        List<BiComponentParams> componentParams = paramsService.list(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefModelCode, modelCode)
                .eq(BiComponentParams::getParamKey, ComponentCons.CONDITION)
        );
        BiProcessors out = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getRelModelCode, modelCode)
                .eq(BiProcessors::getType, BiProcessorsTypeEnum.ETL_SOURCE.getType())
        );

        final String groupCode = GenerateCodeUtil.generate();
        List<RunPlan> runPlans = Lists.newArrayList();

        //不是触发的则同步数据源
        if (YesOrNoEnum.NO.getKey().equals(isTrigger)) {
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

                //校验表结构
                String result = configService.validateSource(config);
                if (null != result) {
                    log.error("Etl调度验证失败:{}", result);
                    //todo 抛出事件修改model validate
                    return;
                }

                // 判断数据源是否被禁用，若有一个被禁用，则不生成调度计划
                BiEtlDatabaseInf biEtlDatabaseInf = biEtlDatabaseInfService.getById(config.getRefSourceId());

                if (SourceTypeEnum.File_Csv.getType().equals(biEtlDatabaseInf.getType())
                        || SourceTypeEnum.File_Excel.getType().equals(biEtlDatabaseInf.getType())) {
                    log.warn("文件型数据源,不需要同步, 组件编码:{}", component.getCode());
                    continue;
                }

                //直连 &本地则返回
                if (config.getType().equals(SyncTypeEnum.DIRECT.getValue())
                        || config.getType().equals(SyncTypeEnum.LOCAL.getValue())) {
                    continue;
                }

                //设置filter
                List<ConditionDto> conditionDtos = Lists.newArrayList();
                Optional<BiComponentParams> optional = componentParams.stream()
                        .filter(s -> s.getRefComponentCode().equals(component.getCode())).findAny();
                if (optional.isPresent()) {
                    String paramValue = optional.get().getParamValue();
                    if (StringUtils.isNotBlank(paramValue)) {
                        conditionDtos = JsonUtil.string2Obj(paramValue, new TypeReference<List<ConditionDto>>() {
                        });
                    }
                }
                RunPlan runPlan = RunPlan.builder()
                        .groupCode(groupCode)
                        .planType("0")
                        .planName(component.getName())
                        .first(YesOrNoEnum.NO.getKey())
                        .modelCode(modelCode)
                        .cronExpression(model.getCronExpression())
                        .mappingConfigCode(config)
                        .synCount(conditionDtos);
                runPlans.add(runPlan);
            }
        }

        //out 当前未count
        RunPlan outPlan = RunPlan.builder()
                .groupCode(groupCode)
                .planName(model.getName())
                .planType("1")
                .first(YesOrNoEnum.NO.getKey())
                .modelCode(modelCode)
                .cronExpression(model.getCronExpression())
                .refCode(out.getCode());
        runPlans.add(outPlan);

        //执行
        runPlans.forEach(s -> syncPlanService.createPlan(s));

        
        //状态变为正在同步中
        model.setSyncStatus(YesOrNoEnum.YES.getKey());
        modelService.updateById(model);
//        KafkaProducter.send(KafkaTypeEnum.Plan_start.getType(), JsonUtil.obj2String(runPlans));
    }
}
