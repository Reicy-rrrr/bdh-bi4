package com.deloitte.bdh.data.collation.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.mq.MessageProducer;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
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
import com.deloitte.bdh.data.collation.enums.MqTypeEnum;
import com.deloitte.bdh.data.collation.enums.PlanResultEnum;
import com.deloitte.bdh.data.collation.enums.PlanStageEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.service.SyncService;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.deloitte.bdh.data.collation.model.RunPlan;
import com.deloitte.bdh.data.collation.model.request.ConditionDto;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.mq.KafkaSyncDto;
import com.deloitte.bdh.data.collation.nifi.template.servie.Transfer;
import com.deloitte.bdh.data.collation.service.BiComponentParamsService;
import com.deloitte.bdh.data.collation.service.BiComponentService;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.deloitte.bdh.data.collation.service.BiEtlModelHandleService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import com.deloitte.bdh.data.collation.service.BiProcessorsService;

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
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private LocaleMessageService localeMessageService;

    @Override
    public void sync() {
        syncToExecute();
        syncExecuting();
    }

    private void syncToExecute() {
        //???????????????????????????????????????????????????
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
        //???????????????????????????????????????????????????
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
                //?????????????????????,??????5?????????????????????
                throw new BizException(ResourceMessageEnum.EXECUTE_OVER_TIME.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EXECUTE_OVER_TIME.getMessage(), ThreadLocalHolder.getLang()));
            }
            //???????????? ??????nifi ??????????????????
            BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                    .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
            );

            //?????????????????????????????????
            if (0 == count) {
                //???????????????
                String result = configService.validateSource(config);
                if (null != result) {
                    throw new RuntimeException(result);
                }
                dbHandler.truncateTable(config.getToTableName());
            }
            //????????????????????????
            String processorsGroupId = componentService.getProcessorsGroupId(config.getRefComponentCode());
            //??????NIFI
            transfer.run(processorsGroupId);
            //??????plan ????????????
            plan.setPlanStage(PlanStageEnum.EXECUTING.getKey());
            //??????
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
                //?????????????????????,??????5?????????????????????
                throw new RuntimeException("??????????????????");
            }
            //???????????? ??????nifi ??????????????????
            BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                    .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
            );
            String processorsGroupId = componentService.getProcessorsGroupId(config.getRefComponentCode());
            SyncTypeEnum typeEnum = SyncTypeEnum.getEnumByKey(config.getType());
            //????????????????????????????????????????????????????????????
            if (0 == count && SyncTypeEnum.FULL == typeEnum) {
                dbHandler.truncateTable(config.getToTableName());
                //#10002???????????????????????????????????????
                transfer.stop(processorsGroupId);
                //??????
                transfer.clear(processorsGroupId);
            }
            //??????NIFI
            transfer.run(processorsGroupId);
            //??????plan ????????????
            plan.setPlanStage(PlanStageEnum.EXECUTING.getKey());
            //??????
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
                //?????????????????????,??????10?????????????????????
                throw new RuntimeException("??????????????????");
                //???????????????????????????????????????????????? nifi????????????todo
            }
            count++;
            //???????????????????????? localCount
//            String condition = assemblyCondition(plan.getIsFirst(), config);
            long nowCount = dbHandler.getCount(config.getToTableName(), null);

            //??????????????????????????????????????????count
            String sqlCount = plan.getSqlCount();
            String localCount = String.valueOf(nowCount);
            if (Long.parseLong(localCount) < Long.parseLong(sqlCount)) {
                retry = true;
                // ?????????????????????
                plan.setSqlLocalCount(localCount);
            } else {
                //???????????????
                plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());

                //??????plan ????????????
                plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());
                plan.setResultDesc(PlanResultEnum.SUCCESS.getValue());

                //????????????nifi???????????????????????????count
                nowCount = dbHandler.getCount(config.getToTableName(), null);
                plan.setSqlLocalCount(String.valueOf(nowCount));
                // ??????MappingConfig ??? LOCAL_COUNT??? OFFSET_VALUE todo
                config.setLocalCount(String.valueOf(nowCount));
//                    config.setOffsetValue();
                configService.updateById(config);

                //??????Component ???????????????
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

            //????????????
            if (!retry) {
                try {
                    //#10002 ??????????????????
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
        //????????????????????????
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
        //???????????????????????????????????????????????????
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
            //??????????????????????????????????????????
            List<BiEtlSyncPlan> synclist = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                    .eq(BiEtlSyncPlan::getPlanType, "0")
                    .eq(BiEtlSyncPlan::getGroupCode, plan.getGroupCode())
            );

            //?????????????????????????????????????????????
            if (!CollectionUtils.isEmpty(synclist)) {
                for (BiEtlSyncPlan syncPlan : synclist) {
                    if (PlanResultEnum.FAIL.getKey().equals(syncPlan.getPlanResult())) {
                        throw new BizException(ResourceMessageEnum.SYNC_1.getCode(),
                                localeMessageService.getMessage(ResourceMessageEnum.SYNC_1.getMessage(), ThreadLocalHolder.getLang()), syncPlan.getName());
                    }
                    //??????????????????????????????????????????????????????
                    if (null == syncPlan.getPlanResult()) {
                        return;
                    }
                }
            }

            //???????????????????????????????????????etl
            ComponentModel componentModel = modelHandleService.handleModel(plan.getRefModelCode());

            //etl?????????????????????processorsCode
            String processorsCode = plan.getRefMappingCode();
            String tableName = componentModel.getTableName();
            String query = componentModel.getQuerySql();
            String count = String.valueOf(dbHandler.getCountLocal(query));
            //??????
            dbHandler.truncateTable(tableName);
            //??????NIFI
            BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                    .eq(BiProcessors::getCode, processorsCode)
            );
            transfer.run(processors.getProcessGroupId());
            //??????plan ????????????
            plan.setPlanStage(PlanStageEnum.EXECUTING.getKey());
            plan.setSqlCount(count);
            //??????
            plan.setProcessCount("0");
            plan.setResultDesc(null);
        } catch (Exception e) {
            log.error("etl.etlToExecuteTask:", e);
            plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
            plan.setResultDesc(e.getMessage());

            //??????model???????????????
            BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, plan.getRefModelCode()));
            model.setSyncStatus(YesOrNoEnum.NO.getKey());
            modelService.updateById(model);
        } finally {
            syncPlanService.updateById(plan);
        }

    }

    private void etlExecuting() {
        //???????????????????????????????????????????????????
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
                //?????????????????????,??????10?????????????????????
                throw new BizException(ResourceMessageEnum.EXECUTE_OVER_TIME.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EXECUTE_OVER_TIME.getMessage(), ThreadLocalHolder.getLang()));
            }
            count++;
            //???????????????????????? localCount
//                String condition = assemblyCondition(plan.getIsFirst(), config);
            long nowCount = dbHandler.getCount(tableName, null);

            //??????????????????????????????????????????count
            String sqlCount = plan.getSqlCount();
            String localCount = String.valueOf(nowCount);
            if (Long.parseLong(localCount) < Long.parseLong(sqlCount)) {
                retry = true;
                // ?????????????????????
                plan.setSqlLocalCount(localCount);
            } else {
                //???????????????
                plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());

                //??????plan ????????????
                plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());
                plan.setResultDesc(PlanResultEnum.SUCCESS.getValue());

                //????????????nifi???????????????????????????count
                nowCount = dbHandler.getCount(tableName, null);
                plan.setSqlLocalCount(String.valueOf(nowCount));

                //??????model??????????????????
                model.setSyncStatus(YesOrNoEnum.NO.getKey());
                model.setLastExecuteDate(LocalDateTime.now());
                modelService.updateById(model);
            }
        } catch (Exception e) {
            log.error("etl.etlExecutingTask:", e);
            plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
            plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            plan.setResultDesc(e.getMessage());

            //??????model??????????????????
            model.setSyncStatus(YesOrNoEnum.NO.getKey());
            modelService.updateById(model);
        } finally {
            plan.setProcessCount(String.valueOf(count));
            syncPlanService.updateById(plan);

            //????????????
            if (!retry) {
                try {
                    //#10002 ??????????????????
                    transfer.stop(processors.getProcessGroupId());
                } catch (Exception e1) {
                    log.error("sync.syncExecutingTask.stop NIFI:", e1);
                }
            }
        }
    }

    @Override
    public void model(String modelCode, String isTrigger) throws Exception {
        //??????model??????????????????????????????
        BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );

        if (null == model) {
            log.error("Etl??????????????????,???????????????, ??????????????????:{}", modelCode);
            return;
        }

        if (YesOrNoEnum.NO.getKey().equals(model.getValidate()) || EffectEnum.DISABLE.getKey().equals(model.getEffect())
                || RunStatusEnum.STOP.getKey().equals(model.getStatus()) || YesOrNoEnum.YES.getKey().equals(model.getSyncStatus())) {
            log.error("Etl??????????????????,?????????????????????, ??????????????????:{}", modelCode);
            //todo ??????????????????model validate
            return;
        }
        //???????????????????????????????????????
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

        //?????????????????????????????????
        if (YesOrNoEnum.NO.getKey().equals(isTrigger)) {
            //database
            List<BiComponent> dbComponents = components.stream()
                    .filter(s -> s.getType().equals(ComponentTypeEnum.DATASOURCE.getKey())).collect(Collectors.toList());
            for (BiComponent component : dbComponents) {
                BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                        .eq(BiEtlMappingConfig::getCode, component.getRefMappingCode())
                        .eq(BiEtlMappingConfig::getRefComponentCode, component.getCode()));

                //??????????????????????????????
                if (null == config) {
                    continue;
                }

                //???????????????
                String result = configService.validateSource(config);
                if (null != result) {
                    log.error("Etl??????????????????:{}", result);
                    //todo ??????????????????model validate
                    return;
                }

                // ?????????????????????????????????????????????????????????????????????????????????
                BiEtlDatabaseInf biEtlDatabaseInf = biEtlDatabaseInfService.getById(config.getRefSourceId());

                if (SourceTypeEnum.File_Csv.getType().equals(biEtlDatabaseInf.getType())
                        || SourceTypeEnum.File_Excel.getType().equals(biEtlDatabaseInf.getType())) {
                    log.warn("??????????????????,???????????????, ????????????:{}", component.getCode());
                    continue;
                }

                //?????? &???????????????
                if (config.getType().equals(SyncTypeEnum.DIRECT.getValue())
                        || config.getType().equals(SyncTypeEnum.LOCAL.getValue())) {
                    continue;
                }

                //??????filter
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

        //out ?????????count
        RunPlan outPlan = RunPlan.builder()
                .groupCode(groupCode)
                .planName(model.getName())
                .planType("1")
                .first(YesOrNoEnum.NO.getKey())
                .modelCode(modelCode)
                .cronExpression(model.getCronExpression())
                .refCode(out.getCode());
        runPlans.add(outPlan);

        //??????
        List<KafkaSyncDto> planMessage = Lists.newArrayList();
        runPlans.forEach(s -> {
                    KafkaSyncDto kfs = new KafkaSyncDto();
                    BiEtlSyncPlan bsp = syncPlanService.createPlan(s);
                    kfs.setCode(bsp.getCode());
                    kfs.setGroupCode(bsp.getGroupCode());
                    kfs.setType("model");
                    planMessage.add(kfs);
                }
        );


        //???????????????????????????
        model.setSyncStatus(YesOrNoEnum.YES.getKey());
        modelService.updateById(model);
        if (YesOrNoEnum.NO.getKey().equals(isTrigger)) {
            KafkaMessage message = new KafkaMessage(UUID.randomUUID().toString().replaceAll("-", ""), planMessage, MqTypeEnum.Plan_start.getType());
            messageProducer.sendSyncMessage(message, 1);
        } else {
            KafkaMessage message = new KafkaMessage(UUID.randomUUID().toString().replaceAll("-", ""), planMessage, MqTypeEnum.Plan_checkMany_end.getType());
            messageProducer.sendSyncMessage(message, 1);
        }


    }
}
