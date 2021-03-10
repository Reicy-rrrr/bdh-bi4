package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.rocket.RocketMqMessage;

public interface Consumer<T> {

    void invoke(KafkaMessage<T> message);
    
    void invokeRocket(RocketMqMessage<T> message);
}
