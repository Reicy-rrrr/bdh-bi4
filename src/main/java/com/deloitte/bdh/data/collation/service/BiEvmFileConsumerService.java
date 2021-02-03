package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiEvmFile;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;

public interface BiEvmFileConsumerService {

    void consumer(KafkaMessage<BiEvmFile> message);

}
