package com.deloitte.bdh.common.annotation;

import java.lang.annotation.*;

/**
 * 加解密字段注解
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptDecryptField {
}
