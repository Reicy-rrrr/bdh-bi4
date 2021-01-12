package com.deloitte.bdh.data.collation.mq;

import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.service.Consumer;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Data
public class KafkaMessage<T> {
    private String tenantCode;
    private String tenantId;
    private String operator;
    private String uuid;
    private String body;
    private String beanName;
    private Long timestamp;

    public KafkaMessage() {
    }

    public KafkaMessage(String uuid, Object body, String beanName) {
        if (body == null) {
            throw new IllegalArgumentException("body cannot be null.");
        }
        if (beanName == null) {
            throw new IllegalArgumentException("beanName cannot be null.");
        }
        this.tenantCode = ThreadLocalHolder.getTenantCode();
        this.tenantId = ThreadLocalHolder.getTenantId();
        this.operator = ThreadLocalHolder.getOperator();
        this.uuid = (null == uuid || "".equals(uuid)) ? UUID.randomUUID().toString() : uuid;
        this.body = JsonUtil.obj2String(body);
        this.beanName = beanName;
        this.timestamp = System.currentTimeMillis();
    }

    public String getKey() {
        return StringUtils.join(tenantCode, uuid, timestamp);
    }

    public T getBody(Class<T> tClass) {
        if (null == body) {
            throw new RuntimeException("消息体不能为空");
        }
        return JsonUtil.string2Obj(body, tClass);
    }

    public void process() {
        SpringUtil.getBean(this.beanName, Consumer.class).invoke(this);
    }

    @Override
    public String toString() {
        return "KafkaMessage{" +
                "tenantCode='" + tenantCode + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", operator='" + operator + '\'' +
                ", uuid='" + uuid + '\'' +
                ", body='" + body + '\'' +
                ", beanName='" + beanName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
