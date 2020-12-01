package com.deloitte.bdh.data.collation.enums;


import com.deloitte.bdh.common.exception.BizException;

public enum PlanTypeEnum {

    TO_EXECUTE(0, "数据同步"),
    EXECUTING(1, "数据整理");

    private Integer type;

    private String desc;

    PlanTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 根据类型获取枚举类型
     *
     * @param type
     * @return
     */
    public static PlanTypeEnum values(Integer type) {
        PlanTypeEnum[] enums = PlanTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(type)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的计划类型！");
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
