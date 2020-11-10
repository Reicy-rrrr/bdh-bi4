package com.deloitte.bdh.data.collation.nifi.template;


public enum BiParamsEnum {
    DCPS("Database Connection Pooling Service"),
    TableName("Table Name"),
    ColumnstoReturn("Columns to Return"),
    dbfetchwhereclause("db-fetch-where-clause"),
    MaximumvalueColumns("Maximum-value Columns"),

    putdbrecorddcbpservice("put-db-record-dcbp-service"),
    putdbrecordrecordreader("put-db-record-record-reader"),
    putdbrecordtablename("put-db-record-table-name"),

    SQLselectquery("SQL select query"),




    ;


    private String key;


    BiParamsEnum(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }


}
