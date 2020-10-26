package com.deloitte.bdh.data.collation.enums;


public enum SyncTypeEnum {

    DIRECT(0, "直连"),
    FULL(1, "全量同步"),
    INCREMENT(2, "增量同步"),

    ;

    private Integer key;

    private String value;

    SyncTypeEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }


    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
