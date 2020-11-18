package com.deloitte.bdh.data.analyse.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
public enum DataImplEnum {
    TABLE_NORMAL("table", "normal", "tableNormalDataImpl", "普通表格"),
    TABLE_CROSS("table", "cross", "crossPivotDataImpl", "交叉透视图"),
    //圆图
    GRAPHICS_PIE("graphics", "pie", "graphicsDataImpl", "饼状图"),
    GRAPHICS_RING("graphics", "ring", "graphicsDataImpl", "水波图"),
    //指标图
    QUOTA("quota", "water", "quotaWaterDataImpl", "指标图"),
    //过滤
    DATA_RANGE("filter", "range", "dataRangeDataImpl", "数据范围过滤"),
    BASE_DATA("filter", "base", "baseDataImpl", "普通字段过滤"),

    ;

    private final String type;

    private final String tableType;

    private final String dataImpl;

    private final String desc;

    DataImplEnum(String type, String tableType, String dataImpl, String desc) {
        this.type = type;
        this.tableType = tableType;
        this.dataImpl = dataImpl;
        this.desc = desc;
    }

    public static String getImpl(String type, String tableType) {
        DataImplEnum[] enums = DataImplEnum.values();
        for (DataImplEnum anEnum : enums) {
            if (StringUtils.equals(anEnum.getType(), type) && StringUtils.equals(anEnum.getTableType(), tableType)) {
                return anEnum.getDataImpl();
            }
        }
        return "";
    }

    public String getType() {
        return type;
    }

    public String getTableType() {
        return tableType;
    }

    public String getDataImpl() {
        return dataImpl;
    }

    public String getDesc() {
        return desc;
    }
}
