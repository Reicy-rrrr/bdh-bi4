package com.deloitte.bdh.data.collation.rocket;

import org.springframework.stereotype.Service;

public interface RocketMqProducer {
	
	public void sendRocket(RocketMqMessage message);

}
