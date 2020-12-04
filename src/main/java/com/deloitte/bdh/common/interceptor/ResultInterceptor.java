package com.deloitte.bdh.common.interceptor;

import com.deloitte.bdh.common.annotation.EncryptDecryptClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

/**
 * mybatis返回结果拦截器（select时解密）
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
@Component
@Slf4j
public class ResultInterceptor implements Interceptor {

    @Autowired
    private IEncryptDecrypt encryptDecrypt;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        if (Objects.isNull(result)) {
            return null;
        }

        if (result instanceof ArrayList) {
            ArrayList resultList = (ArrayList) result;
            if (CollectionUtils.isNotEmpty(resultList) && needToDecrypt(resultList.get(0))) {
                for (int i = 0; i < resultList.size(); i++) {
                    encryptDecrypt.decrypt(resultList.get(i));
                }
            }
        } else {
            if (needToDecrypt(result)) {
                encryptDecrypt.decrypt(result);
            }
        }
        return result;
    }

    public boolean needToDecrypt(Object object) {
        Class<?> objectClass = object.getClass();
        EncryptDecryptClass encryptDecryptClass = AnnotationUtils.findAnnotation(objectClass, EncryptDecryptClass.class);
        if (Objects.nonNull(encryptDecryptClass)) {
            return true;
        }
        return false;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
