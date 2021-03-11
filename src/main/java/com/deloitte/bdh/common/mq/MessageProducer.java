package com.deloitte.bdh.common.mq;

import com.deloitte.bdh.data.collation.mq.KafkaMessage;

/**
 * Author:LIJUN
 * Date:10/03/2021
 * Description:
 */
public interface MessageProducer {

    void sendEmailMessage(String body);

    void sendSyncMessage(KafkaMessage message);

}
