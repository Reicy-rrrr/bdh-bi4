package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service(value = "biDataSourceConsumer")
@DS(DSConstant.BI_DB)
@Slf4j
public class BiDataSourceConsumer implements Consumer {
    @Override
    public void invoke(KafkaMessage message) {
        log.info("Consumer.invoke :" + message.toString());
    }
}
