package com.deloitte.bdh.data.enums;

import org.apache.commons.lang3.StringUtils;

public enum ProcessorTypeEnum {

    ExecuteSQL("ExecuteSQL", "数据源获取信息", "org.apache.nifi.processors.standard.ExecuteSQL"),
    FetchFTP("FetchFTP", "FTP获取文件", "org.apache.nifi.processors.standard.FetchFTP"),
    FetchSFTP("FetchSFTP", "SFTP数据源", "org.apache.nifi.processors.standard.FetchSFTP"),
    SelectHiveQL("SelectHiveQL", "HIVE获取信息", "org.apache.nifi.processors.hive.SelectHiveQL"),
    PutHiveQL("PutHiveQL", "HIVE存入信息", "org.apache.nifi.processors.hive.PutHiveQL"),
    GetFTP("GetFTP", "FTP获取文件", "org.apache.nifi.processors.standard.GetFTP"),
    UpdateAttribute("UpdateAttribute", "UpdateAttribute", "org.apache.nifi.processors.attributes.UpdateAttribute"),
    ConvertRecord("ConvertRecord", "ConvertRecord", "org.apache.nifi.processors.standard.ConvertRecord"),
    PutSQL("PutSQL", "存入数据", "org.apache.nifi.processors.standard.PutSQL"),
    ConvertJSONToSQL("ConvertJSONToSQL", "Json转Sql", "org.apache.nifi.processors.standard.ConvertJSONToSQL"),

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
