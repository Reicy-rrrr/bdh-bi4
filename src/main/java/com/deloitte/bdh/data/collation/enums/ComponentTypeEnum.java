package com.deloitte.bdh.data.collation.enums;


import org.apache.commons.lang3.StringUtils;

public enum ComponentTypeEnum {

    DATASOURCE("DATASOURCE", "sourceComponent", "数据源", "Table"),
    JOIN("JOIN", "joinComponent", "关联", "Connector"),
    GROUP("GROUP", "groupComponent", "聚合", "Aggregator"),
    ARRANGE("ARRANGE", "arrangeComponent", "整理", "Arranger"),
    OUT("OUT", "outComponent", "输出", "OutPutter"),

    ;

    private String key;

    private String name;

    private String value;

    private String desc;

    ComponentTypeEnum(String key, String name, String value, String desc) {
        this.key = key;
        this.name = name;
        this.value = value;
        this.desc = desc;
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

    public String getDesc() {
        return desc;
    }
}
