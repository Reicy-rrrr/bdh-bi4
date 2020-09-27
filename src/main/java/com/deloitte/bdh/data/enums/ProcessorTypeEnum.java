package com.deloitte.bdh.data.enums;

import org.apache.commons.lang3.StringUtils;

public enum ProcessorTypeEnum {

    ExecuteSQL("1", "数据源", "org.apache.nifi.processors.standard.ExecuteSQL"),
    FetchFTP("2", "数据源", "org.apache.nifi.processors.standard.FetchFTP"),
    FetchSFTP("3", "数据源", "org.apache.nifi.processors.standard.FetchSFTP"),
    ;


    private String type;

    private String typeDesc;

    private String value;


    ProcessorTypeEnum(String type, String typeDesc, String value) {
        this.type = type;
        this.typeDesc = typeDesc;
        this.value = value;
    }


    public static String getNifiValue(String type) {
        ProcessorTypeEnum[] enums = ProcessorTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i].getvalue();
            }
        }
        throw new RuntimeException("未找到对应的目标");
    }

    public static String getTypeDesc(String type) {
        ProcessorTypeEnum[] enums = ProcessorTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i].getTypeDesc();
            }
        }
        throw new RuntimeException("未找到对应的目标");
    }

    public String getType() {
        return type;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public String getvalue() {
        return value;
    }
    }
