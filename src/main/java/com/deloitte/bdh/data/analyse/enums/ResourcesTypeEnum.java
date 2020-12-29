package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;

/**
 * Author:LIJUN
 * Date:09/12/2020
 * Description:
 */
public enum ResourcesTypeEnum {

    PAGE("page", "报表"),
    CATEGORY("category", "文件夹"),
    DATA_SET("date_set", "数据集"),
    DATA_SET_CATEGORY("date_set_category", "数据集文件架"),
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
            if (StringUtils.equals(enums[i].getCode(),code)) {
                return enums[i];
            }
        }
        throw new BizException("不支持的资源类型");
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
