package com.deloitte.bdh.data.collation.enums;

import org.apache.commons.lang3.StringUtils;

import com.deloitte.bdh.common.exception.BizException;

public enum KafkaTypeEnum {
	
	Plan_start("Plan_start", "Plan_start", "启动计划修改成执行"),
	Plan_check_end("Plan_check_end", "Plan_check_end", "单条更新数据，多条检查是否全部完成"),
	Plan_checkMany_end("Plan_checkMany_end", "Plan_checkMany_end", "多条数据处理结果完成"),
    
    ;

    private String type;

    private String value;

    private String desc;

    KafkaTypeEnum(String type, String value, String desc) {
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
