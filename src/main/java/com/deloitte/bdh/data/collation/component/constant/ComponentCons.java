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

    /** 关联组件参数 */
    public static final String JOIN_PARAM_KEY_TABLES = "tables";
    /** 聚合组件参数 */
    public static final String GROUP_PARAM_KEY_GROUPS = "groups";

    /** 整理组件参数：type */
    public static final String ARRANGE_PARAM_KEY_TYPE = "type";
    /** 整理组件参数：context */
    public static final String ARRANGE_PARAM_KEY_CONTEXT = "context";
    /** 整理组件(拆分)参数：length */
    public static final String ARRANGE_PARAM_KEY_SPLIT_LENGTH = "length";
    /** 整理组件(拆分)参数：separator */
    public static final String ARRANGE_PARAM_KEY_SPLIT_SEPARATOR = "separator";
    /** 整理组件(转换大小写)参数：upper */
    public static final String ARRANGE_PARAM_KEY_CASE_UPPER = "upper";
    /** 整理组件(转换大小写)参数：lower */
    public static final String ARRANGE_PARAM_KEY_CASE_LOWER = "lower";
}
