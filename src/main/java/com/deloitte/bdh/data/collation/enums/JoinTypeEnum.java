package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;

/**
 * 连接查询类型枚举
 *
 * @author chenghzhang
 */
public enum JoinTypeEnum {

    LEFT_JOIN("left", "LEFT JOIN", "左连接"),
    RIGHT_JOIN("right", "RIGHT JOIN", "右连接"),
    INNER_JOIN("inner", "INNER JOIN", "内连接"),
    FULL_JOIN("full", "FULL JOIN", "全连接"),
    ;

    private String type;

    private String value;

    private String desc;

    JoinTypeEnum(String type, String value, String desc) {
        this.type = type;
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据类型获取枚举类型
     *
     * @param type
     * @return
     */
    public static JoinTypeEnum values(String type) {
        JoinTypeEnum[] enums = JoinTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的连接类型！");
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
