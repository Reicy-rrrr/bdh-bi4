package com.deloitte.bdh.data.collation.enums;

import org.apache.commons.lang3.StringUtils;

public enum SourceTypeEnum {

    Mysql("1", "mysql", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://IP:PORT/DBNAME"),
    Oracle("3", "oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@IP:PORT:DBNAME"),
    SQLServer("4", "sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:microsoft:sqlserver://IP:PORT; DatabaseName=DBNAME"),
    Hive("5", "hive", "org.apache.hadoop.hive.jdbc.HiveDriver", " jdbc:hive://IP:PORT/DBNAME"),
    Hive2("6", "hive2", "org.apache.hive.jdbc.HiveDriver", "jdbc:hive2://IP:PORT/DBNAME"),
    File_Excel("7", "Excel", "Excel", null),
    File_Csv("8", "Csv", "Csv", null),
    Hana("9", "hana", "com.sap.db.jdbc.Driver", "jdbc:sap://IP:PORT/?databaseName=DBNAME"),
    ;

    private String type;

    private String typeName;

    private String driverName;

    private String url;


    SourceTypeEnum(String type, String typeName, String driverName, String url) {
        this.type = type;
        this.typeName = typeName;
        this.driverName = driverName;
        this.url = url;
    }

    /**
     * 根据缓存key获取描述
     *
     * @param type
     * @return String
     */
    public static String getDriverNameByType(String type) {
        SourceTypeEnum[] enums = SourceTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i].getDriverName();
            }
        }
        throw new RuntimeException("未找到对应的数据源类型");
    }

    /**
     * 根据缓存key获取描述
     *
     * @param type
     * @return String
     */
    public static String getUrlByType(String type) {
        SourceTypeEnum[] enums = SourceTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i].getUrl();
            }
        }
        throw new RuntimeException("未找到对应的数据源类型");
    }

    /**
     * 根据缓存key获取描述
     *
     * @param type
     * @return String
     */
    public static String getNameByType(String type) {
        SourceTypeEnum[] enums = SourceTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i].getTypeName();
            }
        }
        throw new RuntimeException("未找到对应的 typeName");
    }

    public static SourceTypeEnum values(String type) {
        SourceTypeEnum[] enums = SourceTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i];
            }
        }
        throw new RuntimeException("未找到对应的 SourceTypeEnum");
    }

    public String getType() {
        return type;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getUrl() {
        return url;
    }

    public String getTypeName() {
        return typeName;
    }
}
