package com.deloitte.bdh.common.util;


/**
 * UUID工具类
 *
 * @author dahpeng
 * @date 2019/05/22
 */
public class GenerateCodeUtil {

    private static SnowFlakeUtil util = new SnowFlakeUtil(0, 0);
    private static final String PREFIX_MODEL_ = "Model_";
    private static final String PREFIX_PROCESSORS_ = "PROS_";
    private static final String PREFIX_PROCESSOR_ = "PRO_";
    private static final String PREFIX_PARAMS_ = "PARAM_";
    private static final String PREFIX_CONNECT_ = "CON_";
    private static final String PREFIX_REF_ = "REF_";
    private static final String PREFIX_CONNECTS_ = "CONS_";

    private GenerateCodeUtil() {
    }

    public static String genModel() {
        return generate(PREFIX_MODEL_);
    }

    public static String genProcessors() {
        return generate(PREFIX_PROCESSORS_);
    }

    public static String genProcessor() {
        return generate(PREFIX_PROCESSOR_);
    }

    public static String genParam() {
        return generate(PREFIX_PARAMS_);
    }

    public static String genConnect() {
        return generate(PREFIX_CONNECT_);
    }

    public static String genDbRef() {
        return generate(PREFIX_REF_);
    }

    public static String genConnects() {
        return generate(PREFIX_CONNECTS_);
    }

    private static String generate(String prefix) {
        return prefix + util.nextId();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(GenerateCodeUtil.genModel());
        }
    }
}
