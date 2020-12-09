package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * Author:LIJUN
 * Date:09/12/2020
 * Description:
 */
public enum ResourcesTypeEnum {

    PAGE("page", "报表"),
    CATEGORY("category", "文件夹"),
    ;

    private String code;

    private String desc;

    ResourcesTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(String code) {
        ResourcesTypeEnum[] enums = ResourcesTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equals(code)) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    public static ResourcesTypeEnum values(String code) {
        ResourcesTypeEnum[] enums = ResourcesTypeEnum.values();
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

    public String getDesc() {
        return desc;
    }
}
