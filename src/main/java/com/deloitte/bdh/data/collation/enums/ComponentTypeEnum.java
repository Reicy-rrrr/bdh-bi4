package com.deloitte.bdh.data.collation.enums;


import org.apache.commons.lang3.StringUtils;

public enum ComponentTypeEnum {

    DATASOURCE("DATASOURCE", "sourceComponent", "数据源"),
    JOIN("JOIN", "joinComponent", "关联"),
    GROUP("GROUP", "groupComponent", "聚合"),
    ARRANGE("ARRANGE", "arrangeComponent", "整理"),
    OUT("OUT", "outComponent", "输出"),

    ;

    private String key;

    private String name;

    private String value;

    ComponentTypeEnum(String key, String name, String value) {
        this.key = key;
        this.name = name;
        this.value = value;
    }

    public static ComponentTypeEnum values(String key) {
        ComponentTypeEnum[] enums = ComponentTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(key, enums[i].getKey())) {
                return enums[i];
            }
        }
        throw new RuntimeException("未找到对应的 ComponentTypeEnum");
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
