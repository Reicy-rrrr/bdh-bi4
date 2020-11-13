package com.deloitte.bdh.data.analyse.enums;

import com.deloitte.bdh.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
public enum DataImplEnum {

    TABLE_NORMAL("table","normal", "tableNormalDataImpl"),
    ;

    private final String type;

    private final String tableType;

    private final String dataImpl;

    DataImplEnum(String type, String tableType, String dataImpl) {
        this.type = type;
        this.tableType = tableType;
        this.dataImpl = dataImpl;
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
}
