package com.deloitte.bdh.data.enums;

import org.apache.commons.lang3.StringUtils;

public enum SourceTypeEnum {

    Mysql_8("1", "mysql8+", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://IP:PORT/DBNAME"),
    Mysql_7("2", "mysql7", "com.mysql.jdbc.Driver", "jdbc:mysql://IP:PORT/DBNAME"),
    Oracle("3", "oracel", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@IP:PORT:DBNAME");

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
