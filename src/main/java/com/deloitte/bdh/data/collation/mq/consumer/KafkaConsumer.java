package com.deloitte.bdh.data.collation.mq.consumer;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.KafkaBiPlanService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaConsumer {
	
	@Autowired
    private KafkaBiPlanService kafkaBiPlanService;
	
	@KafkaListener(topics = "Plan_start", groupId = "bi_sync")
    public void Plan_start(ConsumerRecord<String, String> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        Optional op = Optional.ofNullable(record.value());
        if (op.isPresent()) {
        	Object msg = op.get();
        	KafkaMessage message = JsonUtil.string2Obj(record.value(), KafkaMessage.class);
        	if (null != message) {
        		ThreadLocalHolder.async(message.getTenantCode(), message.getTenantId(), message.getOperator(), message::process);
        		kafkaBiPlanService.BiEtlSyncPlan(message);
        	}
            log.info("Plan_start 消费了： Topic:" + topic + ",Message:" + msg);
            ack.acknowledge();
        }
    }
	
	
	@KafkaListener(topics = "Plan_check_end", groupId = "bi_sync")
    public void Plan_check_end(ConsumerRecord<String, String> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		Optional op = Optional.ofNullable(record.value());
        if (op.isPresent()) {
        	Object msg = op.get();
        	KafkaMessage message = JsonUtil.string2Obj(record.value(), KafkaMessage.class);
        	if (null != message) {
        		ThreadLocalHolder.async(message.getTenantCode(), message.getTenantId(), message.getOperator(), message::process);
        		kafkaBiPlanService.BiEtlSyncManyPlan(message);
        	}
            log.info("Plan_check_end 消费了： Topic:" + topic + ",Message:" + msg);
            ack.acknowledge();
        }
    }
	
	
	@KafkaListener(topics = "Plan_checkMany_end", groupId = "bi_sync")
    public void Plan_checkMany_end(ConsumerRecord<String, String> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		Optional op = Optional.ofNullable(record.value());
        if (op.isPresent()) {
        	Object msg = op.get();
        	KafkaMessage message = JsonUtil.string2Obj(record.value(), KafkaMessage.class);
        	if (null != message) {
        		ThreadLocalHolder.async(message.getTenantCode(), message.getTenantId(), message.getOperator(), message::process);
        		kafkaBiPlanService.BiEtlSyncManyEndPlan(message);
        	}
            log.info("Plan_checkMany_end 消费了： Topic:" + topic + ",Message:" + msg);
            ack.acknowledge();
        }
    }

}
