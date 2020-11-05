package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * 整理组件整理类型枚举
 *
 * @author chenghzhang
 * @date 2020/11/04
 */
public enum ArrangeTypeEnum {

    REMOVE_FIELD(1, "移除字段"),
    SPLIT_FIELD(2, "拆分字段"),
    COMBINE_FIELD(3, "组合字段"),
    EXCLUDE_NULL(4, "排除空值"),
    CONVERT_TYPE(5, "字段类型转换"),
    RENAME_FIELD(6, "字段重命名"),
    GROUP_FIELD(7, "字段分组"),
    LAYER_FIELD(8, "字段分层"),
    TO_UPPERCASE(9, "转大写"),
    TO_LOWERCASE(10, "转小写"),
    TRIM(11, "空格清除"),
    REPLACE(12, "内容替换"),
    FILL_FIELD(13, "填充字段"),
    CALCULATE(14, "计算字段"),
    SYNC_STRUCTURE(15, "同步表结构"),
    CONVERT_STRUCTURE(16, "行列转换"),
    ;

    private Integer type;

    private String desc;

    ArrangeTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 根据类型获取枚举类型
     *
     * @param type
     * @return
     */
    public static ArrangeTypeEnum valueOf(Integer type) {
        ArrangeTypeEnum[] enums = ArrangeTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(type)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的整理类型！");
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
