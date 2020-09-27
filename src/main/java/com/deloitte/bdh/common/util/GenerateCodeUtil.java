package com.deloitte.bdh.common.util;


/**
 * UUID工具类
 *
 * @author dahpeng
 * @date 2019/05/22
 */
public class GenerateCodeUtil {

    private static final String PREFIX_MODEL_ = "Model";

    public static String genModel() {
        return generate(PREFIX_MODEL_);
    }

    public static String generate(String prefix) {
        return prefix + System.currentTimeMillis();
    }

}
