//package com.deloitte.bdh.data.collation.mq.consumer;
//
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Component;
//import org.springframework.util.concurrent.ListenableFuture;
//import org.springframework.util.concurrent.ListenableFutureCallback;
//
//@Component
//@Slf4j
//public class KafkaProducter {
//	
//	
//	@Autowired
//    private KafkaTemplate kafkaTemplate;
//
//    public void send(String topicKey , Object obj) {
//        String obj2String = JSONObject.toJSONString(obj);
//        log.info("准备发送消息为：{}", obj2String);
//        //发送消息
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicKey, obj);
//        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                //发送失败的处理
//                log.info(topicKey + " - 生产者 发送消息失败：" + throwable.getMessage());
//            }
//
//            @Override
//            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
//                //成功的处理
//                log.info(topicKey + " - 生产者 发送消息成功：" + stringObjectSendResult.toString());
//            }
//        });
//
//
//    }
//
//}
