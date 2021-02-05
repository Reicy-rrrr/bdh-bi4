package com.deloitte.bdh.common.client;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

import com.deloitte.bdh.common.util.ThreadLocalHolder;



/**
 * @author Ashen
 * @date 17/12/2020
 */
@Component("feignKeyGenerator")
public class FeignKeyGenerator implements KeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    if (params.length == 0) {
      return ThreadLocalHolder.getTenantCode() + ":" + ThreadLocalHolder.get("lang");
    }
    return new SimpleKey(params);
  }
}
