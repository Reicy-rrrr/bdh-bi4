package com.deloitte.bdh.data.collation.mq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.kafka.annotation.TopicPartition;
 
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DelayKafkaConsumer {
 
    String id() default "";
 
    String[] topics() default {};
 
    String errorHandler() default "";
 
    String groupId() default "";
 
    TopicPartition[] topicPartitions() default {};
 
    String beanRef() default "__listener";
}
