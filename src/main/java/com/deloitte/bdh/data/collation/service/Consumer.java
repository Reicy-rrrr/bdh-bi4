package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.mq.KafkaMessage;

public interface Consumer<T> {

    void invoke(KafkaMessage<T> message);
}
