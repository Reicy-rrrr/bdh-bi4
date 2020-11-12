package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Author:LIJUN
 * Date:11/11/2020
 * Description:
 */
public enum CategoryTreeChildrenTypeEnum {
    PAGE("PAGE", "页面"),
    CATEGORY("CATEGORY", "文件夹"),
    ;

    private final String code;

    private final String desc;

    CategoryTreeChildrenTypeEnum(String code, String desc) {
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
        CategoryTreeChildrenTypeEnum[] enums = CategoryTreeChildrenTypeEnum.values();
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
    public static CategoryTreeChildrenTypeEnum values(String type) {
        CategoryTreeChildrenTypeEnum[] enums = CategoryTreeChildrenTypeEnum.values();
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
