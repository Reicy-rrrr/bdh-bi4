package com.deloitte.bdh.data.collation.nifi.template.config;

import lombok.Data;

import java.util.UUID;

@Data
public class OutSql extends Template {
    private String dttProcessGroupsId = UUID.randomUUID().toString();
    private String dttFirstProcessorId = UUID.randomUUID().toString();
    //数据来源id
    private String dttDatabaseServieId;
    private String dttSqlQuery;

    private String dttSecondProcessorId = UUID.randomUUID().toString();
    //数据源出口readerid
    private String dttPutReader;
    //数据源出口id
    private String dttPutServiceId;
    //生成表名
    private String dttPutTableName;
    private String dttFirstConnectionId = UUID.randomUUID().toString();
    private String dttSecondConnectionId = UUID.randomUUID().toString();
}
