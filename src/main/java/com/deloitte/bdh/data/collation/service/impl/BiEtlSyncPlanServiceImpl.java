package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.enums.PlanStatusEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlSyncPlanMapper;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.data.collation.service.BiProcessorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    @Autowired
    private BiEtlMappingConfigService configService;
    @Autowired
    private BiProcessorsService processorsService;

    @Override
    public void process(String type) throws Exception {
        //0 同步，1 整理的同步
        if ("0".equals(type)) {
            syncToExecute();
            syncExecuting();
        } else {
            etl();
        }
    }

    private void syncToExecute() {
        //寻找类型为同步，状态为待执行的计划 todo limit
        List<BiEtlSyncPlan> list = syncPlanMapper.selectList(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "0")
                .eq(BiEtlSyncPlan::getPlanStatus, PlanStatusEnum.TO_EXECUTE.getKey())
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
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
        //寻找类型为同步，状态为待执行的计划 todo limit
        List<BiEtlSyncPlan> list = syncPlanMapper.selectList(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "0")
                .eq(BiEtlSyncPlan::getPlanStatus, PlanStatusEnum.EXECUTING.getKey())
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
        );

        list.forEach(s -> {
            syncExecutingTask(s);
        });

    }

    private void syncToExecuteNonTask(BiEtlSyncPlan plan) {
        int count = Integer.parseInt(plan.getProcessCount());
        try {
            //判断已处理次数,超过3次则动作完成。
            if (3 < count) {
                plan.setPlanStatus(PlanStatusEnum.EXECUTED.getKey());
            } else {
                //组装数据 启动nifi 改变执行状态
                BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                        .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
                );

                //非调度发起的同步第一次
                if (0 == count) {
                    // todo 基于条件组装清空语句
                }
                //启动NIFI
                processorsService.runState(config.getRefProcessorsCode(), RunStatusEnum.RUNNING, true);
                //修改plan 执行状态
                plan.setPlanStatus(PlanStatusEnum.EXECUTING.getKey());
                //重置为0
                plan.setProcessCount("0");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            count++;
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
                plan.setPlanStatus(PlanStatusEnum.EXECUTED.getKey());
            } else {
                //组装数据 启动nifi 改变执行状态
                BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                        .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
                );

                //全量是第一次
                SyncTypeEnum typeEnum = SyncTypeEnum.getEnumByKey(config.getType());
                if (0 == count && SyncTypeEnum.FULL == typeEnum) {
                    // todo 基于条件组装清空语句
                }
                //启动NIFI
                processorsService.runState(config.getRefProcessorsCode(), RunStatusEnum.RUNNING, true);
                //修改plan 执行状态
                plan.setPlanStatus(PlanStatusEnum.EXECUTING.getKey());
                //重置为0
                plan.setProcessCount("0");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            count++;
            plan.setProcessCount(String.valueOf(count));
        } finally {
            syncPlanMapper.updateById(plan);
        }
    }

    private void syncExecutingTask(BiEtlSyncPlan plan) {
        int count = Integer.parseInt(plan.getProcessCount());
        try {
            //判断已处理次数,超过10次则动作完成。
            if (10 < count) {
                plan.setPlanStatus(PlanStatusEnum.EXECUTED.getKey());
            } else {
                count++;
                //判断目标数据库与源数据库的表count
                String sqlCount = plan.getSqlCount();
                //todo 实时查询 localCount
                String localCount = plan.getSqlLocalCount();
                if (Integer.parseInt(localCount) < Integer.parseInt(sqlCount)) {
                    // 等待下次再查询
                    plan.setSqlLocalCount(localCount);
                } else {
                    //已同步完成
                    plan.setSqlLocalCount(localCount);
                    plan.setPlanResult(YesOrNoEnum.YES.getKey());
                    //调用nifi 停止与清空
                    BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                            .eq(BiEtlMappingConfig::getCode, plan.getRefMappingCode())
                    );
                    processorsService.runState(config.getRefProcessorsCode(), RunStatusEnum.STOP, true);
                }
                //修改plan 执行状态
                plan.setPlanStatus(PlanStatusEnum.EXECUTED.getKey());
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            plan.setProcessCount(String.valueOf(count));
            syncPlanMapper.updateById(plan);
        }
    }

    private void etl() {

    }
}
