package com.deloitte.bdh.common.interceptor;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author Ashen
 * @date 12/11/2020
 */
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class FeignCommonAspect {

    @Pointcut("@within(org.springframework.cloud.openfeign.FeignClient)")
    public void feignClientPointCut() {
    }

    /**
     * Feign 调用接口异常捕捉
     */
    @Around("feignClientPointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        if (args != null && args.length == 1 && args[0] instanceof RetRequest) {
            RetRequest<?> retRequest = (RetRequest) args[0];
            retRequest.setLang(ThreadLocalHolder.get("lang"));
            retRequest.setOperator(ThreadLocalHolder.getOperator());
        }
        Object object = proceedingJoinPoint.proceed();
        //抛出接口异常
        if (object instanceof RetResult<?>) {
            RetResult<?> retResult = (RetResult<?>) object;
            if (retResult.getCode() != 0) {
                throw new BizException(retResult.getMessage());
            }
        }
        return object;
    }
}
