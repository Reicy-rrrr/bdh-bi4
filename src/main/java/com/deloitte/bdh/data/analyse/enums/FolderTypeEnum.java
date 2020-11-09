package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Author:LIJUN
 * Date:09/11/2020
 * Description:
 */
public enum FolderTypeEnum {
    WD("WD", "维度"),
    DL("DL", "度量"),
    ;

    private String type;

    private String desc;

    FolderTypeEnum(String type, String desc) {
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
        FolderTypeEnum[] enums = FolderTypeEnum.values();
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
    public static FolderTypeEnum values(String type) {
        FolderTypeEnum[] enums = FolderTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(type)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的认证类型！");
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
