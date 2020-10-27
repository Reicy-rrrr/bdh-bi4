package com.deloitte.bdh.data.collation.database.dto;

import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import lombok.Data;

@Data
public class DbContext {
    //req
    private Integer method;
    private String dbId;
    private String tableName;
    //page param
    private Integer page;
    private Integer size;

    //process
    private SourceTypeEnum sourceTypeEnum;
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;
    private String driverName;

    /** 数据库名 */
    private String dbName;

    /** 条件 */
    private String condition;
}
