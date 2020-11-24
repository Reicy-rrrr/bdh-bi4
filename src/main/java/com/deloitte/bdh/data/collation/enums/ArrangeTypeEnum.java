package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * 整理组件整理类型枚举
 *
 * @author chenghzhang
 * @date 2020/11/04
 */
public enum ArrangeTypeEnum {

    REMOVE("remove", "移除字段"),
    SPLIT("split", "拆分字段"),
    COMBINE("combine", "组合字段"),
    NON_NULL("non_null", "排除空值"),
    MODIFY("modify", "字段修改"),
    RENAME("rename", "字段重命名"),
    GROUP("group", "字段分组"),
    LAYER("layer", "字段分层"),
    CONVERT_CASE("convert_case", "大小写转换"),
    TRIM("trim", "去除前后空格"),
    BLANK("blank", "去除字段中空格"),
    REPLACE("replace", "内容替换"),
    FILL("fill", "填充字段"),
    CALCULATE("calculate", "计算字段"),
    SYNC_STRUCTURE("sync_structure", "同步表结构"),
    CONVERT_STRUCTURE("convert_structure", "行列转换"),
    ;

    private String type;

    private String desc;

    ArrangeTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 根据类型获取枚举类型
     *
     * @param type
     * @return
     */
    public static ArrangeTypeEnum get(String type) {
        ArrangeTypeEnum[] enums = ArrangeTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(type)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的整理类型！");
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
