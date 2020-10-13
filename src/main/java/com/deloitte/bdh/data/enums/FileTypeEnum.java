package com.deloitte.bdh.data.enums;

import com.deloitte.bdh.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chenghzhang
 */
public enum FileTypeEnum {

    Excel_Xls("application/vnd.ms-excel", "xls", "xls格式文件"),
    Excel_Xlsx("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx", "xlsx格式文件"),
    Excel_Xlsm("application/vnd.ms-excel.sheet.macroEnabled.12", "xlsm", "xlsm格式文件"),
    Csv("text/csv", "csv", "csv格式文件"),
    ;

    private String type;

    private String value;

    private String desc;

    FileTypeEnum(String type, String value, String desc) {
        this.type = type;
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据文件type获取描述
     *
     * @param type 文件类型
     * @return String
     */
    public static String getDesc(String type) {
        FileTypeEnum[] enums = FileTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i].getDesc();
            }
        }
        return "";
    }

    /**
     * 根据类型获取枚举类型
     * @param type
     * @return
     */
    public static FileTypeEnum values(String type) {
        FileTypeEnum[] enums = FileTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的文件类型！");
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
