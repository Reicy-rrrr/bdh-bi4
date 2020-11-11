package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Author:LIJUN
 * Date:11/11/2020
 * Description:
 */
public enum CategoryPageTypeEnum {
    PAGE("PAGE", "页面"),
    CATEGORY("CATEGORY", "文件夹"),
    ;

    private String name;

    private String desc;

    CategoryPageTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    /**
     * 根据type获取描述
     *
     * @param name 类型
     * @return String
     */
    public static String getDesc(Integer name) {
        CategoryPageTypeEnum[] enums = CategoryPageTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getName().equals(name)) {
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
    public static CategoryPageTypeEnum values(String type) {
        CategoryPageTypeEnum[] enums = CategoryPageTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getName().equals(type)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的认证类型！");
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
