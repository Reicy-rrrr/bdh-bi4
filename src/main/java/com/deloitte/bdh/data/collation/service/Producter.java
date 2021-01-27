package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.mq.KafkaMessage;


public interface Producter {


    void send(KafkaMessage message);
    
    
    void sendEmail(KafkaMessage message);

}
