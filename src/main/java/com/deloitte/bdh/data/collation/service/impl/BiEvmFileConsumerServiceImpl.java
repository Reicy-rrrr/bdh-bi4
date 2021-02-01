package com.deloitte.bdh.data.collation.service.impl;

import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.BiEvmFileConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BiEvmFileConsumerServiceImpl implements BiEvmFileConsumerService {
    @Override
    public void consumer(KafkaMessage message) {
        log.info("BiEvmFileConsumerServiceImpl.beagin :  body:" + message.getBody());
        //todo 解析文件
    }
}
