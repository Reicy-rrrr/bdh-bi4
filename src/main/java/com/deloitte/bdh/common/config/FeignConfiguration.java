package com.deloitte.bdh.common.config;

import com.deloitte.bdh.common.interceptor.FeignCommonAspect;
import com.deloitte.bdh.common.interceptor.FeignCommonRequestInterceptor;
import feign.Feign;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Ashen
 * @date 12/11/2020
 */
@Configuration
public class FeignConfiguration {

    /**
     * 日志级别
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }

    /**
     * 创建Feign请求拦截器 添加租户相关信息
     */
    @Bean
    public FeignCommonRequestInterceptor commonRequestInterceptor() {
        return new FeignCommonRequestInterceptor();
    }

    @Bean
    public FeignCommonAspect feignExceptionAspect() {
        return new FeignCommonAspect();
    }


}
