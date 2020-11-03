package com.deloitte.bdh.data.collation.component.constant;

/*
 * 组件参数
 */
public class ComponentCons {
    //以下为数据源组件参数
    public static final String DULICATE = "isDuplicate";
    //1：为数据源组件,为”引用副本“时没有值，2：为输出组件时有值
    public static final String REF_PROCESSORS_CDOE = "refProcessorsCode";

    //以下为输出组件参数
    public static final String TO_TABLE_NAME = "toTableName";
    public static final String SQL_SELECT_QUERY = "sqlSelectQuery";

    /** 关联组件参数 */
    public static final String JOIN_PARAM_KEY_TABLES = "tables";
    /** 聚合组件参数 */
    public static final String GROUP_PARAM_KEY_GROUPS = "groups";
}
