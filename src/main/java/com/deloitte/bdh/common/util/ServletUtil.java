package com.deloitte.bdh.common.util;


import org.apache.tomcat.util.http.MimeHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

public class ServletUtil {

    public static void currentHeader(String value) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        reflectSetparam(request, "x-bdh-tenant-code", value);
    }

    public static void rSetHeader(String value) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        reflectSetparam(request, "x-bdh-tenant-code", value);
    }

    /**
     * 修改header信息，key-value键值对儿加入到header中
     *
     * @param request
     * @param key
     * @param value
     */
    private static void reflectSetparam(HttpServletRequest request, String key, String value) throws Exception {
        Class<? extends HttpServletRequest> requestClass = request.getClass();
        Field request1 = requestClass.getDeclaredField("request");
        request1.setAccessible(true);
        Object o = request1.get(request);
        Field coyoteRequest = o.getClass().getDeclaredField("coyoteRequest");
        coyoteRequest.setAccessible(true);
        Object o1 = coyoteRequest.get(o);
        Field headers = o1.getClass().getDeclaredField("headers");
        headers.setAccessible(true);
        MimeHeaders o2 = (MimeHeaders) headers.get(o1);
        o2.addValue(key).setString(value);
    }
}
