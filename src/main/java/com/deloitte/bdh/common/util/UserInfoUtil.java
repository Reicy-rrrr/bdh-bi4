package com.deloitte.bdh.common.util;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.client.FeignClientService;
import com.deloitte.bdh.common.client.dto.IntactUserInfoVoCache;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserInfoUtil {
    private static FeignClientService clientService;
    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(() -> new ConcurrentHashMap<String, Object>() {
    });

    public static void get(RetRequest retRequest, RetResult<Object> tRetResult) {
        if (tRetResult.isSuccess()) {
            Object obj = tRetResult.getData();
            //排除基本类型
            if (null == obj || obj instanceof String || obj instanceof Integer
                    || obj instanceof Double || obj instanceof Long || obj instanceof Boolean
                    || obj instanceof Short || obj instanceof Byte || obj instanceof Char
                    || obj instanceof Float) {
                return;
            }
            if (obj instanceof List || obj instanceof PageResult) {
                if (obj instanceof List) {
                    List list = (List) obj;
                    if (!CollectionUtils.isEmpty(list)) {
                        for (Object objz : list) {
                            entity(objz, retRequest.getLang());
                        }
                    }
                } else {
                    PageResult result = (PageResult) obj;
                    if (!CollectionUtils.isEmpty(result.getRows())) {
                        for (Object objz : result.getRows()) {
                            entity(objz, retRequest.getLang());
                        }
                    }
                }
            } else {
                entity(obj, retRequest.getLang());
            }
        }
    }

    private static void entity(Object obj, String lang) {
        Class clazz = obj.getClass();
        process(obj, clazz, "createUser", lang);
        process(obj, clazz, "modifiedUser", lang);
    }

    private static void process(Object obj, Class clazz, String filedName, String lang) {
        Field field = getField(clazz, filedName);
        if (null == field) {
            return;
        }
        String fieldValue = (String) getValue(field, obj);
        if (StringUtil.isEmpty(fieldValue)) {
            return;
        }
        if (null == lang) {
            lang = "cn";
        }
        if (null == threadLocal.get().get(fieldValue)) {
            if (null == clientService) {
                clientService = (FeignClientService) SpringUtil.getBean("feignClientServiceImpl");
            }
            IntactUserInfoVoCache cache;
            try {
                cache = clientService.getIntactUserInfo(fieldValue, lang);
            } catch (Exception e) {
                threadLocal.get().put(fieldValue, fieldValue);
                return;
            }
            if (null == cache) {
                threadLocal.get().put(fieldValue, fieldValue);
                return;
            }
            String value = cache.getEmployeeName();
            if (StringUtil.isEmpty(value)) {
                value = cache.getFullName();
            }
            threadLocal.get().put(fieldValue, value);
        }
        if (fieldValue.equals(threadLocal.get().get(fieldValue))) {
            return;
        }
        setVaule(field, obj, threadLocal.get().get(fieldValue));
    }


    private static void setVaule(Field field, Object o, Object val) {
        field.setAccessible(true);
        ReflectionUtils.setField(field, o, val);
    }


    private static Object getValue(Field field, Object o) {
        field.setAccessible(true);
        return ReflectionUtils.getField(field, o);
    }

    private static Field getField(Class clazz, String filedName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            Class parentClazz = clazz.getSuperclass();
            if (parentClazz.getName().equals("java.lang.Object")) {
                return field;
            }
            return getField(parentClazz, filedName);
        }
        return field;
    }
}
