package com.deloitte.bdh.data.db.dto;

import com.deloitte.bdh.data.enums.SourceTypeEnum;
import lombok.Data;

@Data
public class DbContext {
    //req
    private Integer method;
    private String dbId;
    private String tableName;

    //process
    private SourceTypeEnum sourceTypeEnum;
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;
    private String driverName;

}
