package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.constant.LanguageConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import org.apache.commons.lang3.StringUtils;



public enum DataUnitEnum {

    NONE("", "自动", "automatic", "自动") {
        @Override
        public String expression(String str) {
            return str;
        }
    },
    THOUSAND("THOUSAND", "千", "THOUSAND", "千") {
        @Override
        public String expression(String str) {
            return "(" + str + ")/1000";
        }
    },
    TENTHOUSAND("TENTHOUSAND", "万", "TENTHOUSAND", "萬") {
        @Override
        public String expression(String str) {
            return "(" + str + ")/10000";
        }
    },
    MILLION("MILLION", "百万", "MILLION", "百萬") {
        @Override
        public String expression(String str) {
            return "(" + str + ")/1000000";
        }
    },
    TENMILLION("TENMILLION", "千万", "TENMILLION", "千萬") {
        @Override
        public String expression(String str) {
            return "(" + str + ")/10000000";
        }
    },
    BILLION("BILLION", "亿", "BILLION", "億") {
        @Override
        public String expression(String str) {
            return "(" + str + ")/100000000";
        }
    };

    private String code;
    private String cnDesc;
    private String enDesc;
    private String hkDesc;

    DataUnitEnum(String code, String cnDesc, String enDesc, String hkDesc) {
        this.code = code;
        this.cnDesc = cnDesc;
        this.enDesc = enDesc;
        this.hkDesc = hkDesc;
    }

    public static String getDesc(String code) {
        DataUnitEnum[] enums = DataUnitEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(code)) {
                if (StringUtils.equals(LanguageConstant.EN.getLanguage(), ThreadLocalHolder.getLang())) {
                    return enums[i].getEnDesc();
                } else if (StringUtils.equals(LanguageConstant.HK.getLanguage(), ThreadLocalHolder.getLang())) {
                    return enums[i].getHkDesc();
                } else {
                    return enums[i].getCnDesc();
                }
            }
        }
        return "";
    }

    public static DataUnitEnum values(String code) {
        DataUnitEnum[] enums = DataUnitEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(code)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的认证类型！");
    }

    public String getCode() {
        return code;
    }

    public String getCnDesc() {
        return cnDesc;
    }

    public String getEnDesc() {
        return enDesc;
    }

    public String getHkDesc() {
        return hkDesc;
    }

    public String expression(String str) {
        return str;
    }
}
