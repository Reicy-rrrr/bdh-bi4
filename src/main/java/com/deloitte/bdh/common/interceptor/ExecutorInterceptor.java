package com.deloitte.bdh.common.interceptor;


import com.deloitte.bdh.common.annotation.EncryptDecryptClass;
import com.deloitte.bdh.common.annotation.EncryptDecryptField;
import com.deloitte.bdh.common.util.AesUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * mybatis Executor执行器拦截
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
@Component
@Slf4j
public class ExecutorInterceptor implements Interceptor {

    private static final String method_update_key = "update";
    private static final String method_insert_key = "insert";
    private static final String method_query_key = "query";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取该sql语句的类型
        String methodName = invocation.getMethod().getName();
        // 修改和插入操作进行加密
        if (StringUtils.equalsIgnoreCase(method_update_key, methodName) || StringUtils.equalsIgnoreCase(method_insert_key, methodName)) {
            // 获取更新操作参数
            Object parameter = invocation.getArgs()[1];
            if (Objects.nonNull(parameter)) {
                // 对参数进行加密
                encrypt(parameter);
            }
            // 执行update（insert/update）
            Object result = invocation.proceed();
            // 为避免入库时加密后的值影响后续流程，执行结束后对参数进行解密处理
            if (Objects.nonNull(parameter)) {
                decrypt(parameter);
            }
            // 返回执行结果
            return result;
        } else if (StringUtils.equalsIgnoreCase(method_query_key, methodName)) {
            // 执行query
            Object result = invocation.proceed();
            // 执行结果不为空时进行解密
            if (Objects.nonNull(result)) {
                decrypt(result);
            }
            return result;
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 参数加密
     *
     * @param parameter
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    private <T> T encrypt(T parameter) throws IllegalAccessException {
        if (Objects.isNull(parameter) || !needHandle(parameter)) {
            return parameter;
        }

        if (parameter instanceof Collection) {
            // 集合类型，遍历集合中对象进行加密
            Collection collection = (Collection) parameter;
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                encrypt(obj.getClass().getDeclaredFields(), obj);
            }
        } else if (parameter instanceof Map) {
            // map类型，遍历map中的value对象进行加密
            Map map = (Map) parameter;
            Collection collection = map.values();
            Iterator iterator = collection.iterator();
            // mybatis-plus的update接口初始化的map中会有et/param1等对象，且et == param1，需要避免重复加密
            Set<Object> uniques = Sets.newHashSet();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (Objects.isNull(obj)) {
                    continue;
                }
                if (uniques.add(obj)) {
                    encrypt(obj.getClass().getDeclaredFields(), obj);
                }
            }
        } else {
            encrypt(parameter.getClass().getDeclaredFields(), parameter);
        }
        return parameter;
    }

    /**
     * 结果集解密
     *
     * @param result
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    private <T> T decrypt(T result) throws IllegalAccessException {
        if (Objects.isNull(result) || !needHandle(result)) {
            return result;
        }

        if (result instanceof Collection) {
            Collection collection = (Collection) result;
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                decrypt(obj.getClass().getDeclaredFields(), obj);
            }
        } else if (result instanceof Map) {
            Map map = (Map) result;
            Collection collection = map.values();
            Iterator iterator = collection.iterator();
            // 需要避免重复解密
            Set<Object> uniques = Sets.newHashSet();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (Objects.isNull(obj)) {
                    continue;
                }
                if (uniques.add(obj)) {
                    decrypt(obj.getClass().getDeclaredFields(), obj);
                }
            }
        } else {
            Class<?> parameterObjectClass = result.getClass();
            Field[] declaredFields = parameterObjectClass.getDeclaredFields();
            decrypt(declaredFields, result);
        }
        return result;
    }

    private <T> T encrypt(Field[] declaredFields, T parameterObject) throws IllegalAccessException {
        for (Field field : declaredFields) {
            EncryptDecryptField annotation = field.getAnnotation(EncryptDecryptField.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
            encrypt(field, parameterObject);
        }
        return parameterObject;
    }

    /**
     * 单个field加密方法
     *
     * @param field
     * @param parameterObject
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    private <T> T encrypt(Field field, T parameterObject) throws IllegalAccessException {
        field.setAccessible(true);
        Object originalVal = field.get(parameterObject);
        // 字段值为空时不进行加密
        if (Objects.isNull(originalVal)) {
            return parameterObject;
        }
        if (originalVal instanceof BigDecimal) {
            //TODO 定制BigDecimal类型的加密算法
        } else if (originalVal instanceof Integer) {
            //TODO 定制Integer类型的加密算法
        } else if (originalVal instanceof Long) {
            //TODO 定制Long类型的加密算法
        } else if (originalVal instanceof String) {
            String value = (String) originalVal;
            field.set(parameterObject, AesUtil.encrypt(value, ThreadLocalHolder.getTenantCode()));
        }
        return parameterObject;
    }

    /**
     * 多个field解密方法
     *
     * @param declaredFields
     * @param result
     * @throws IllegalAccessException
     */
    private void decrypt(Field[] declaredFields, Object result) throws IllegalAccessException {
        for (Field field : declaredFields) {
            EncryptDecryptField annotation = field.getAnnotation(EncryptDecryptField.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
            decrypt(field, result);
        }
    }

    /**
     * 单个field解密方法
     *
     * @param field
     * @param result
     * @throws IllegalAccessException
     */
    private void decrypt(Field field, Object result) throws IllegalAccessException {
        field.setAccessible(true);
        Object encryptedVal = field.get(result);
        // 字段值为空时不解密
        if (Objects.isNull(encryptedVal)) {
            return;
        }
        if (encryptedVal instanceof BigDecimal) {
            //TODO 定制BigDecimal类型的解密算法
        } else if (encryptedVal instanceof Integer) {
            //TODO 定制Integer类型的解密算法
        } else if (encryptedVal instanceof Long) {
            //TODO 定制Long类型的解密算法
        } else if (encryptedVal instanceof String) {
            String value = (String) encryptedVal;
            field.set(result, AesUtil.decrypt(value, ThreadLocalHolder.getTenantCode()));
        }
    }

    /**
     * 判断是否需要对象是否需要加解密处理
     *
     * @param object
     * @return boolean
     */
    private boolean needHandle(Object object) {
        if (Objects.isNull(object)) {
            return false;
        }
        Class<?> clazz = object.getClass();
        // 集合类型
        if (object instanceof Collection) {
            Collection collection = (Collection) object;
            if (collection.size() == 0) {
                return false;
            }

            Iterator iterator = collection.iterator();
            if (!iterator.hasNext()) {
                return false;
            }

            Class<?> paramClazz = iterator.next().getClass();
            EncryptDecryptClass encryptDecryptClass = AnnotationUtils.findAnnotation(paramClazz, EncryptDecryptClass.class);
            if ((Objects.isNull(encryptDecryptClass))) {
                return false;
            }
        } else if (object instanceof Map) {
            Map map = (Map) object;
            if (map.size() == 0) {
                return false;
            }
            for (Object obj : map.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                Class<?> valueClass = entry.getValue().getClass();
                EncryptDecryptClass valueAn = AnnotationUtils.findAnnotation(valueClass, EncryptDecryptClass.class);
                if (Objects.isNull(valueAn)) {
                    return false;
                }
                break;
            }
        } else {
            EncryptDecryptClass encryptDecryptClass = AnnotationUtils.findAnnotation(clazz, EncryptDecryptClass.class);
            if (Objects.isNull(encryptDecryptClass)) {
                return false;
            }
        }
        return true;
    }
}
