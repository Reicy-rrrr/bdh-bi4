package com.deloitte.bdh.common.util;


/**
 * UUID工具类
 *
 * @author dahpeng
 * @date 2019/05/22
 */
public class GenerateCodeUtil {

    private static final String PREFIX_MODEL_ = "Model";
    private static final String PREFIX_PROCESSORS_ = "PROS";
    private static final String PREFIX_PROCESSOR_ = "PRO";

    public static String genModel() {
        return generate(PREFIX_MODEL_);
    }

    public static String genProcessors() {
        return generate(PREFIX_PROCESSORS_);
    }

    public static String genProcessor() {
        return generate(PREFIX_PROCESSOR_);
    }

    public static String generate(String prefix) {
        return prefix + System.currentTimeMillis();
    }

}
