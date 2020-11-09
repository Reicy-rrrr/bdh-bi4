package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Author:LIJUN
 * Date:09/11/2020
 * Description:
 */
public enum YnTypeEnum {
    YES("Y", ""),
    NO("N", ""),
    ;

    private String name;

    private String desc;

    YnTypeEnum(String name, String desc) {
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
        YnTypeEnum[] enums = YnTypeEnum.values();
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
    public static YnTypeEnum values(String type) {
        YnTypeEnum[] enums = YnTypeEnum.values();
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
