package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
public enum CategoryTypeEnum {
    PRE_DEFINED("PRE_DEFINED", "预定义"),
    CUSTOMER("CUSTOMER", "自定义"),
    COMPONENT("COMPONENT", "图形指标"),
    ;

    private final String code;

    private final String desc;

    CategoryTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据type获取描述
     *
     * @param name 类型
     * @return String
     */
    public static String getDesc(String name) {
        CategoryTypeEnum[] enums = CategoryTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(name)) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    /**
     * 根据类型获取枚举类型
     *
     * @param type
     * @return
     */
    public static CategoryTypeEnum values(String type) {
        CategoryTypeEnum[] enums = CategoryTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(type)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的认证类型！");
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
