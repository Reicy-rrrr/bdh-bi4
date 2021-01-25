package com.deloitte.bdh.data.collation.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.taskdefs.Sleep;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.KafkaTypeEnum;
import com.deloitte.bdh.data.collation.enums.PlanResultEnum;
import com.deloitte.bdh.data.collation.enums.PlanStageEnum;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.deloitte.bdh.data.collation.model.RunPlan;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.nifi.template.servie.Transfer;
import com.deloitte.bdh.data.collation.service.BiComponentService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.deloitte.bdh.data.collation.service.BiEtlModelHandleService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import com.deloitte.bdh.data.collation.service.BiProcessorsService;
import com.deloitte.bdh.data.collation.service.KafkaBiPlanService;
import com.deloitte.bdh.data.collation.service.Producter;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@SuppressWarnings("rawtypes")
@DS(DSConstant.BI_DB)
public class KafkaBiPlanServiceImpl implements KafkaBiPlanService{

	
	
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

    @Autowired
    private Producter producter;
	
	@Override
	public void BiEtlSyncPlan(KafkaMessage message) {
		log.error("kafka Plan_start 启动调用更新数据库表变成执行中++++++++++++++++++++++++++++++++");
		//改变执行计划变成已经开始执行
		String body = message.getBody();
		log.error("kafka Plan_start body ++++++++++++++++++++++++++++++++" + body);
		List<BiEtlSyncPlan> list = JsonUtil.string2Obj(body, new TypeReference<List<BiEtlSyncPlan>>() {
        });
		log.error("kafka Plan_start  List<RunPlan> list ++++++++++++++++++++++++++++++++" + list.toString());
		if(!CollectionUtils.isEmpty(list)) {
			syncToExecute(list,message);
			//发送kafka topic 消息 准备查询是否已执行完成
			message.setBeanName(KafkaTypeEnum.Plan_check_end.getType());
//			producter.send(KafkaTypeEnum.Plan_check_end.getType(),message);
			producter.send(message);
		}
		
	}

	@Override
	public void BiEtlSyncManyPlan(KafkaMessage message) {
		log.info("kafka 启动调用更新数据库表查询是否结束，如果已结束 更新标志位");
		String body = message.getBody();
		List<BiEtlSyncPlan> list = JsonUtil.string2Obj(body, new TypeReference<List<BiEtlSyncPlan>>() {
        });
		if(!CollectionUtils.isEmpty(list)) {
			//检查执行计划是否已经完成 未完成发送第二次请求使用延迟消费  如果多条请求全部完成发送topic 请求多条验证完成
			syncExecuting(message,list);
		}
		
	}

	@Override
	public void BiEtlSyncManyEndPlan(KafkaMessage message) {
		log.info("kafka 启动调用更新数据库表多条数据全部结束，查询当前tyep 为1 标志位是否同步结束 如已结束更新标志位");
		String body = message.getBody();
		List<BiEtlSyncPlan> planList = JsonUtil.string2Obj(body, new TypeReference<List<BiEtlSyncPlan>>() {
        });
		if(org.apache.commons.collections4.CollectionUtils.isEmpty(planList)) {
			return;
		}
		try {
			etlToExecute(planList,message);
			etlExecuting(planList , message);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	//启动
	private void syncToExecute(List<com.deloitte.bdh.data.collation.model.BiEtlSyncPlan> list2, KafkaMessage message) {
        //寻找类型为同步，状态为待执行的计划
		BiEtlSyncPlan plan = list2.get(0);
        List<BiEtlSyncPlan> list = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "0")
                .eq(BiEtlSyncPlan::getPlanStage, PlanStageEnum.TO_EXECUTE.getKey())
                .eq(BiEtlSyncPlan::getGroupCode, plan.getGroupCode())
                .eq(BiEtlSyncPlan::getCode, plan.getCode())
                .eq(BiEtlSyncPlan::getTenantId, message.getTenantId())
                .isNull(BiEtlSyncPlan::getPlanResult)
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
        );
        log.error("kafka Plan_start syncToExecute  List<BiEtlSyncPlan> list list ++++++++++++++++++++++++++++++++" + list.toString());
        list.forEach(s -> {
            if (YesOrNoEnum.YES.getKey().equals(s.getIsFirst())) {
            	log.error("kafka Plan_start syncToExecute  YesOrNoEnum.YES.getKey() ++++++++++++++++++++++++++++++++" + list.toString());
                syncToExecuteNonTask(s);
            } else {
            	log.error("kafka Plan_start syncToExecute  YesOrNoEnum.no.getKey() ++++++++++++++++++++++++++++++++" + list.toString());
                syncToExecuteTask(s);
            }
        });

    }
	
	private void syncToExecuteNonTask(BiEtlSyncPlan plan) {
        int count = Integer.parseInt(plan.getProcessCount());
        try {
//            if (5 < count) {
//                //判断已处理次数,超过5次则动作完成。
//                throw new RuntimeException("任务处理超时");
//            }
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
            log.error("sync.syncToExecuteNonTask:++++++++++++++++++++++++++++++", e);
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
//            if (5 < count) {
//                //判断已处理次数,超过5次则动作完成。
//                throw new RuntimeException("任务处理超时");
//            }
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
            log.error("sync.syncToExecuteTask:++++++++++++++++++++++++++++++++++++=", e);
            count++;
            plan.setPlanStage(PlanStageEnum.EXECUTED.getKey());
            plan.setPlanResult(PlanResultEnum.FAIL.getKey());
            plan.setResultDesc(e.getMessage());
            plan.setProcessCount(String.valueOf(count));
        } finally {
            syncPlanService.updateById(plan);
        }
    }

    private void syncExecuting(KafkaMessage message, List<com.deloitte.bdh.data.collation.model.BiEtlSyncPlan> list2) {
    	
    	BiEtlSyncPlan plan = list2.get(0);
        //寻找类型为同步，状态为待执行的计划
        List<BiEtlSyncPlan> list = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "0")
                .eq(BiEtlSyncPlan::getPlanStage, PlanStageEnum.EXECUTING.getKey())
                .eq(BiEtlSyncPlan::getGroupCode, plan.getGroupCode())
                .eq(BiEtlSyncPlan::getCode, plan.getCode())
                .eq(BiEtlSyncPlan::getTenantId, message.getTenantId())
                .isNull(BiEtlSyncPlan::getPlanResult)
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
              
        );
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
        	if(list.size() == 1) {
        		if(!syncExecutingTask(list.get(0),message)) {
        			message.setBeanName(KafkaTypeEnum.Plan_check_end.getType());
//            		Producter.send(KafkaTypeEnum.Plan_check_end.getType(),message);
        			try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		producter.send(message);
        		}
        	}else {
        		int num = 0;
        		for (BiEtlSyncPlan s : list) {
        			if(syncExecutingTask(s,message)) {
        				num = num+1;
        			}
				}
        		if(list.size() == num) {
        			message.setBeanName(KafkaTypeEnum.Plan_checkMany_end.getType());
//            		Producter.send(KafkaTypeEnum.Plan_checkMany_end.getType(),message);
        			try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			producter.send(message);
        		}else {
        			message.setBeanName(KafkaTypeEnum.Plan_check_end.getType());
//            		Producter.send(KafkaTypeEnum.Plan_check_end.getType(),message);
        			try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			producter.send(message);
        		}
        	}
        }
        

    }

    

    private boolean syncExecutingTask(BiEtlSyncPlan plan, KafkaMessage message) {
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
                
        		return false;
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
                return true;
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
		return retry;
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


    private void etlToExecute(List<com.deloitte.bdh.data.collation.model.BiEtlSyncPlan> planList, KafkaMessage message) throws Exception {
    	BiEtlSyncPlan plan = planList.get(0);
        //寻找类型为同步，状态为待执行的计划s
        List<BiEtlSyncPlan> list = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "1")
                .eq(BiEtlSyncPlan::getPlanStage, PlanStageEnum.TO_EXECUTE.getKey())
                .eq(BiEtlSyncPlan::getGroupCode, plan.getGroupCode())
                .eq(BiEtlSyncPlan::getTenantId, message.getTenantId())
                .isNull(BiEtlSyncPlan::getPlanResult)
                .orderByAsc(BiEtlSyncPlan::getCreateDate)

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

    private void etlExecuting(List<com.deloitte.bdh.data.collation.model.BiEtlSyncPlan> planList, KafkaMessage message) {
    	BiEtlSyncPlan plan = planList.get(0);
        //寻找类型为同步，状态为待执行的计划
        List<BiEtlSyncPlan> list = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "1")
                .eq(BiEtlSyncPlan::getPlanStage, PlanStageEnum.EXECUTING.getKey())
                .eq(BiEtlSyncPlan::getGroupCode, plan.getGroupCode())
                .eq(BiEtlSyncPlan::getTenantId, message.getTenantId())
                .isNull(BiEtlSyncPlan::getPlanResult)
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
        );
        list.forEach( s ->{
        	etlExecutingTask(s,message);
        });

    }

    private void etlExecutingTask(BiEtlSyncPlan plan, KafkaMessage message) {
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
//            if (10 < count) {
//                //判断已处理次数,超过10次则动作完成。
//                throw new RuntimeException("任务处理超时");
//            }
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
                message.setBeanName(KafkaTypeEnum.Plan_checkMany_end.getType());
//        		Producter.send(KafkaTypeEnum.Plan_checkMany_end.getType(),message);
                Thread.sleep(60000);
                
                producter.send(message);
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

}
