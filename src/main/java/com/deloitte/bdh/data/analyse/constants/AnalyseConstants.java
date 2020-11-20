package com.deloitte.bdh.data.analyse.constants;

import com.google.common.collect.Lists;

import java.util.List;

public class AnalyseConstants {

    /**
     * 默认父级ID
     */
    public static final String PARENT_ID_ZERO = "0";
    /**
     * 已发布
     */
    public static final String PAGE_CONFIG_PUBLISH = "PUBLISH";
    /**
     * 编辑中
     */
    public static final String PAGE_CONFIG_EDIT = "EDIT";

    public static final List<String> MENSURE_TYPE = Lists.newArrayList(
            "TINYINT","SMALLINT","MEDIUMINT","INT","INTEGER","BIGINT","FLOAT","DOUBLE","DECIMAL");

    public static final List<String> MENSURE_DECIMAL_TYPE = Lists.newArrayList(
            "FLOAT","DOUBLE","DECIMAL");

}
