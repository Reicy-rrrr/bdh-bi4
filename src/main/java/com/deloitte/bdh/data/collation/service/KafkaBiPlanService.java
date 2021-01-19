package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.mq.KafkaMessage;

/**
 * 
 * @author jianpeng
 *
 */
public interface KafkaBiPlanService {
	
	/**
	 * 同步更新一条记录启动更新
	 */
	@SuppressWarnings("rawtypes")
	public void BiEtlSyncPlan(KafkaMessage message);
	
	
	/**
	 * 同步更新多条或单条记录启动更新
	 */
	@SuppressWarnings("rawtypes")
	public void BiEtlSyncManyPlan(KafkaMessage message);
	
	
	/**
	 * 同步更新多条条记录启动更新
	 */
	@SuppressWarnings("rawtypes")
	public void BiEtlSyncManyEndPlan(KafkaMessage message);

}
