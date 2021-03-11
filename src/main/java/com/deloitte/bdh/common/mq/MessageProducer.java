package com.deloitte.bdh.common.mq;

/**
 * Author:LIJUN
 * Date:10/03/2021
 * Description:
 */
public interface MessageProducer {

    void send(String body, String messageKey);

}
