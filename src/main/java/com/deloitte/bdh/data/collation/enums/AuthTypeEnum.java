package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * 数据源认证方式枚举
 *
 * @author chenghzhang
 * @date 2020/10/23
 */
public enum AuthTypeEnum {
    USERNAME(1, "用户名"),
    USERNAME_AND_PASSWORD(2, "用户名和密码"),
    ;

    private Integer type;

    private String desc;

    AuthTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 根据type获取描述
     *
     * @param type 类型
     * @return String
     */
    public static String getDesc(Integer type) {
        AuthTypeEnum[] enums = AuthTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(type)) {
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
    public static AuthTypeEnum get(String type) {
        AuthTypeEnum[] enums = AuthTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(type)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的认证类型！");
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
