package com.deloitte.bdh.data.analyse.constants;


public class CustomParamsConstants {
    //是否聚合
    public static final String TABLE_AGGREGATE = "tableAggregate";

    //以下为指标图所有
    //同比(true:false)
    public static final String CORE_YOY = "coreYoy";
    //环比(true:false)
    public static final String CORE_CHAIN = "coreChain";
    //同比、环比时间值(yyyy-MM-dd)
    public static final String CORE_DATE_VALUE = "coreDateValue";
    //同比、环比时间字段
    public static final String CORE_DATE_KEY = "coreDateKey";
    //同比、环比时间类型（YEAR，YEAR_QUARTERLY，YEAR_MONTH，YEAR_MONTH_DAY）
    public static final String CORE_DATE_TYPE = "coreDateType";

    //散点图
    public static final String SCATTER_NAME = "scatterName";
    public static final String SCATTER_SIZE = "scatterSize";

    //符号地图
    public static final String SYMBOL_SIZE = "symbolSize";

    //是否聚合
    public static final String VIEW_DETAIL = "viewDetail";
}
