package com.deloitte.bdh.common.interceptor;

import com.deloitte.bdh.common.util.ThreadLocalHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author Ashen
 * @description 接口中添加租户信息
 * @date 12/11/2020
 */
public class FeignCommonRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header("x-bdh-tenant-code", ThreadLocalHolder.getTenantCode());
    }
}
