package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.ThreadLocalUtil;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlSyncPlanMapper;
import com.deloitte.bdh.data.collation.service.*;
import com.deloitte.bdh.common.base.AbstractService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private BiEtlMappingConfigService configService;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private DbHandler dbHandler;
    @Autowired
    private BiComponentService componentService;
    @Autowired
    private BiComponentParamsService componentParamsService;
    @Autowired
    private BiEtlModelService modelService;

    @Override
    public void sync() throws Exception {
        syncToExecute();
        syncExecuting();
    }

    private void syncToExecute() {
        //寻找类型为同步，状态为待执行的计划
        List<BiEtlSyncPlan> list = syncPlanMapper.selectList(new LambdaQueryWrapper<BiEtlSyncPlan>()
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
        List<BiEtlSyncPlan> list = syncPlanMapper.selectList(new LambdaQueryWrapper<BiEtlSyncPlan>()
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
                String processorsCode = getProcessorsCode(config);
                //启动NIFI
                processorsService.runState(processorsCode, RunStatusEnum.RUNNING, true);
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
            syncPlanMapper.updateById(plan);
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

                SyncTypeEnum typeEnum = SyncTypeEnum.getEnumByKey(config.getType());
                if (0 == count && SyncTypeEnum.FULL == typeEnum) {
                    //全量则清空
                    dbHandler.truncateTable(config.getToTableName());
                }
                //启动NIFI
                processorsService.runState(getProcessorsCode(config), RunStatusEnum.RUNNING, true);
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
            syncPlanMapper.updateById(plan);
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
                String processorsCode = getProcessorsCode(config);
                async(() -> {
                    processorsService.runState(processorsCode, RunStatusEnum.STOP, true);
                });
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
                    String processorsCode = getProcessorsCode(config);
                    async(() -> processorsService.runState(processorsCode, RunStatusEnum.STOP, true));

                    //修改plan 执行状态
                    plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                    plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());
                    plan.setResultDesc(PlanResultEnum.SUCCESS.getValue());

                    //获取停止nifi后的本地最新的数据count
                    nowCount = dbHandler.getCount(config.getToTableName(), condition);
                    plan.setSqlLocalCount(localCount);
                    // 设置MappingConfig 的 LOCAL_COUNT和 OFFSET_VALUE todo
                    config.setLocalCount(String.valueOf(nowCount));
//                    config.setOffsetValue();
                    configService.save(config);

                    //设置Component 状态为可用
                    BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                            .eq(BiComponent::getCode, config.getRefComponentCode())
                    );
                    component.setEffect(EffectEnum.ENABLE.getKey());
                    component.setModifiedDate(LocalDateTime.now());
                    componentService.updateById(component);
                }
            }
        } catch (Exception e1) {
            log.error("sync.syncExecutingTask:" + e1);
            plan.setResultDesc(e1.getMessage());
        } finally {
            plan.setProcessCount(String.valueOf(count));
            syncPlanMapper.updateById(plan);
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

    private String getProcessorsCode(BiEtlMappingConfig config) {
        BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, config.getRefComponentCode())
        );
        if (null == component) {
            throw new RuntimeException("EtlServiceImpl.getProcessorsCode.error : 未找到目标 组件");
        }
        BiComponentParams componentParams = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
                .eq(BiComponentParams::getParamKey, ComponentCons.REF_PROCESSORS_CDOE)
        );
        if (null == componentParams) {
            throw new RuntimeException("EtlServiceImpl.getProcessorsCode.error : 未找到目标组件 参数");
        }
        return componentParams.getParamValue();
    }

    @Override
    public void etl() throws Exception {
        etlToExecute();
        etlExecuting();
    }

    private void etlToExecute() throws Exception {
        //寻找类型为同步，状态为待执行的计划
        List<BiEtlSyncPlan> list = syncPlanMapper.selectList(new LambdaQueryWrapper<BiEtlSyncPlan>()
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
            List<BiEtlSyncPlan> synclist = syncPlanMapper.selectList(new LambdaQueryWrapper<BiEtlSyncPlan>()
                    .eq(BiEtlSyncPlan::getPlanType, "0")
                    .eq(BiEtlSyncPlan::getGroupCode, plan.getGroupCode())
            );

            for (BiEtlSyncPlan syncPlan : synclist) {
                if (PlanResultEnum.FAIL.getKey().equals(syncPlan.getPlanResult())) {
                    throw new RuntimeException("依赖的同步任务失败:" + syncPlan.getCode());
                }
                //todo 取消状态呢？
            }
            //同步任务已经执行完成，开始etl
            List<BiComponentParams> paramsList = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                    .eq(BiComponentParams::getRefComponentCode, plan.getRefMappingCode())
            );
            String processorsCode = paramsList.stream()
                    .filter(p -> p.getParamKey().equals(ComponentCons.REF_PROCESSORS_CDOE)).findAny().get().getParamValue();
            String tableName = paramsList.stream()
                    .filter(p -> p.getParamKey().equals(ComponentCons.TO_TABLE_NAME)).findAny().get().getParamValue();
            String query = paramsList.stream()
                    .filter(p -> p.getParamKey().equals(ComponentCons.SQL_SELECT_QUERY)).findAny().get().getParamValue();

            String count = String.valueOf(dbHandler.getCountLocal(query));
            //清空
            dbHandler.truncateTable(tableName);
            //启动NIFI
            processorsService.runState(processorsCode, RunStatusEnum.RUNNING, true);
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
            plan.setModifiedDate(LocalDateTime.now());
            syncPlanMapper.updateById(plan);
        }

    }

    private void etlExecuting() {
        //寻找类型为同步，状态为待执行的计划
        List<BiEtlSyncPlan> list = syncPlanMapper.selectList(new LambdaQueryWrapper<BiEtlSyncPlan>()
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
        BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
        );

        try {
            //判断已处理次数,超过10次则动作完成。
            if (10 < count) {
                plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                plan.setPlanResult(PlanResultEnum.FAIL.getKey());
                //调用nifi 停止与清空
                String processorsCode = getProcessorsCode(config);
                async(() -> processorsService.runState(processorsCode, RunStatusEnum.STOP, true));
            } else {
                count++;
                //基于条件实时查询 localCount
//                String condition = assemblyCondition(plan.getIsFirst(), config);
                long nowCount = dbHandler.getCount(config.getToTableName(), null);

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
                    String processorsCode = getProcessorsCode(config);
                    async(() -> processorsService.runState(processorsCode, RunStatusEnum.STOP, true));

                    //修改plan 执行状态
                    plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
                    plan.setPlanResult(PlanResultEnum.SUCCESS.getKey());
                    plan.setResultDesc(PlanResultEnum.SUCCESS.getValue());

                    //获取停止nifi后的本地最新的数据count
                    nowCount = dbHandler.getCount(config.getToTableName(), null);
                    plan.setSqlLocalCount(localCount);
                    // 设置MappingConfig 的 LOCAL_COUNT和 OFFSET_VALUE todo
                    config.setLocalCount(String.valueOf(nowCount));
//                    config.setOffsetValue();
                    configService.save(config);

                    //设置Component 状态为可用
                    BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                            .eq(BiComponent::getCode, config.getRefComponentCode())
                    );
                    component.setEffect(EffectEnum.ENABLE.getKey());
                    component.setModifiedDate(LocalDateTime.now());
                    componentService.updateById(component);
                }
            }
        } catch (Exception e1) {
            log.error("sync.etlExecutingTask:" + e1);
            plan.setResultDesc(e1.getMessage());
        } finally {
            plan.setProcessCount(String.valueOf(count));
            syncPlanMapper.updateById(plan);
        }
    }

    @Override
    public void model(String modelCode) throws Exception {
        //查询model信息，生成执行集计划
        BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );
        if (!"1".equals(model.getValidate()) || EffectEnum.DISABLE.getKey().equals(model.getEffect())
                || RunStatusEnum.STOP.getKey().equals(model.getStatus())) {
            return;
        }
        //首先获取模板下的数据源组件
        List<BiComponent> components = componentService.list(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefModelCode, modelCode)
                .and(wrapper -> wrapper
                        .eq(BiComponent::getType, ComponentTypeEnum.DATASOURCE.getKey())
                        .or()
                        .eq(BiComponent::getType, ComponentTypeEnum.OUT.getKey())
                )
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
        BiComponent outComponent = components.stream()
                .filter(s -> s.getType().equals(ComponentTypeEnum.OUT.getKey())).findAny().get();
        RunPlan outPlan = RunPlan.builder().groupCode(groupCode).planType("1")
                .first(YesOrNoEnum.NO.getKey()).modelCode(modelCode).refCode(outComponent.getCode());
        runPlans.add(outPlan);

        runPlans.forEach(this::createFirstPlan);
    }

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
        syncPlan.setCreateDate(LocalDateTime.now());
        syncPlan.setCreateUser(ThreadLocalUtil.getOperator());
        syncPlan.setTenantId(ThreadLocalUtil.getTenantId());
        syncPlan.setIsFirst(plan.getFirst());
        //设置已处理初始值为0
        syncPlan.setProcessCount("0");
        syncPlan.setPlanResult(null);
        syncPlan.setSqlCount(plan.getCount());
        syncPlanMapper.insert(syncPlan);
    }

}
