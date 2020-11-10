package com.deloitte.bdh.data.collation.nifi.template.config;

import lombok.Data;

import java.util.UUID;

@Data
public class SyncSql extends Template {
    private String dttProcessGroupsId = UUID.randomUUID().toString();
    private String dttFirstProcessorId = UUID.randomUUID().toString();
    //数据来源id
    private String dttDatabaseServieId;
    private String dttTableName;
    //返回字段集合
    private String dttColumnsToReturn;
    //查询条件
    private String dttWhereClause;
    //偏移字段
    private String dttMaxValueColumns;
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
