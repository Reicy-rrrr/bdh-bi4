package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * 计算类型
 *
 * @author chenghzhang
 * @date 2020/12/17
 */
public enum CalculateTypeEnum {
    ORDINARY("ordinary", "普通四则运算"),
    FUNCTION("function", "函数式运算"),
    ;

    private String type;

    private String desc;

    CalculateTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 根据类型获取枚举
     *
     * @param type
     * @return
     */
    public static CalculateTypeEnum get(String type) {
        CalculateTypeEnum[] enums = CalculateTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(type)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的计算类型！");
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
