package com.deloitte.bdh.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.common.util.UUIDUtil;

import java.util.Arrays;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.client.methods.HttpPost;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP记录WEB请求日志
 *
 * @author pengdh
 * @date 2018/04/09
 */
@Aspect
@Component
public class WebLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    @Pointcut("execution(public * com.deloitte.bdh..controller.*.*(..))"
            + "&& !@annotation(com.deloitte.bdh.common.annotation.NoLog)"
            + "&& !@annotation(com.deloitte.bdh.common.annotation.NoInterceptor)")
    public void logPointCut() {
    }

    @Pointcut("@annotation(com.deloitte.bdh.common.annotation.SystemLog)")
    public void sysLogPointCut() {
    }

    /**
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("logPointCut()")
    public Object process(ProceedingJoinPoint point) throws Throwable {
        String traceId = UUIDUtil.generate();
        MDC.put("traceId", traceId);
        long startTime = System.currentTimeMillis();
        //用改变后的参数执行目标方法
        doBefore(point);
        Object returnValue = point.proceed();
        doAfterReturning(returnValue);
        logger.info("共耗时 : " + (System.currentTimeMillis() - startTime) + "毫秒");
        MDC.clear();
        ThreadLocalHolder.clear();
        return returnValue;
    }

    /**
     * 在切入点开始处切入内容
     */
    public void doBefore(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        logger.info("请求地址 : " + request.getRequestURL().toString());
        logger.info("HTTP METHOD : " + request.getMethod());
        logger.info("IP : " + request.getRemoteAddr());
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

        if (joinPoint.getArgs().length == 0) {
            logger.info("参数 : {} ", "");
        } else if (request.getMethod().equals(HttpPost.METHOD_NAME)) {
            logger.info("参数 : " + JSON.toJSONString(joinPoint.getArgs()[0]) + "");
        } else {
            logger.info("参数 : " + Arrays.toString(joinPoint.getArgs()));
        }

        ThreadLocalHolder.set("tenantCode", request.getHeader("x-bdh-tenant-code"));
        //设置参数
        if (joinPoint.getArgs().length > 0) {

            Map<String, Object> params;
            Object args = joinPoint.getArgs()[0];
            if (args instanceof Map) {
                params = (Map<String, Object>) args;
            } else if (args instanceof RetRequest) {
                params = JsonUtil.JsonStrToMap(JsonUtil.readObjToJson(joinPoint.getArgs()[0]));
            } else {
                return;
            }

            if (null != MapUtils.getString(params, "tenantId")) {
                ThreadLocalHolder.set("tenantId", MapUtils.getString(params, "tenantId"));
            }
            if (null != MapUtils.getString(params, "ip")) {
                ThreadLocalHolder.set("ip", MapUtils.getString(params, "ip"));
            }
            if (null != MapUtils.getString(params, "operator")) {
                ThreadLocalHolder.set("operator", MapUtils.getString(params, "operator"));
            }
        }
    }

    /**
     * 在切入点return内容之后切入内容
     */
    public void doAfterReturning(Object ret) {
        // 处理完请求，返回内容
        if (null != ret && ret instanceof RetResult) {
            RetResult baseResult = (RetResult) ret;
            String traceId = MDC.get("traceId");
            baseResult.setTraceId(traceId);
        }
        logger.info("返回值 : " + JSON.toJSONStringWithDateFormat(ret, "yyyy-MM-dd HH:mm:ss"));
    }

}
