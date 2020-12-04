package com.deloitte.bdh.common.interceptor;

import com.deloitte.bdh.common.annotation.EncryptDecryptField;
import com.deloitte.bdh.common.util.AesUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 加解密接口实现
 */
@Component
public class EncryptDecryptImpl implements IEncryptDecrypt {
    @Override
    public <T> T encrypt(Field[] declaredFields, T parameterObject) throws IllegalAccessException {
        for (Field field : declaredFields) {
            EncryptDecryptField annotation = field.getAnnotation(EncryptDecryptField.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
            encrypt(field, parameterObject);
        }
        return parameterObject;
    }

    @Override
    public <T> T decrypt(T result) throws IllegalAccessException {
        Class<?> parameterObjectClass = result.getClass();
        Field[] declaredFields = parameterObjectClass.getDeclaredFields();
        decrypt(declaredFields, result);
        return result;
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
}
