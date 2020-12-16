package com.deloitte.bdh.data.collation.database.convertor;

import com.deloitte.bdh.common.constant.CommonConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.MysqlDataTypeEnum;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 数据源转化处理
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
public interface DbConvertor {
    /**
     * mysql单行总长度不能大于65535个字节，本系统数据库使用UTF8编码，每个字符占3个字节，因此单行总长度不能超过65535/3
     * 实际测试：在utf8下，当总长度大于等于21842时即会异常，因此本系统限制总长度为21500
     **/
    int mysql_row_max_length = 21500;

    /**
     * 字段转换
     *
     * @param fields
     * @param context
     */
    void convertFieldType(List<TableField> fields, DbContext context);

    /**
     * mysql适配（行长度）
     * 总长度大于mysql行最大长度时需要将部分varchar类型转换为text类型
     *
     * @param fields
     * @return
     */
    default void mysqlSchemaAdapter(List<TableField> fields) {
        // 限制最多100个字段
        if (fields.size() > CommonConstant.MAX_COLUMN_SIZE) {
            throw new BizException("所选字段超出100个，请重新选择！");
        }
        int totalLength = 0;
        for (TableField field : fields) {
            String length = field.getLength();
            if (StringUtils.isNotBlank(length) && StringUtils.isNumeric(length)) {
                totalLength += Integer.valueOf(length);
            } else {
                field.setLength("0");
            }
        }

        // 总长度大于mysql行最大长度时需要将部分varchar类型转换为text类型
        if (totalLength <= mysql_row_max_length) {
            return;
        }

        // 根据字段长度进行逆向排序
        List<TableField> sortedList = Lists.newArrayList(fields);
        sortedList.sort((o1, o2) -> Integer.valueOf(o2.getLength()) - Integer.parseInt(o1.getLength()));
        int index = 0;
        while (index < sortedList.size()) {
            TableField field = sortedList.get(index);
            String dataType = field.getDataType();
            if (dataType.equalsIgnoreCase(MysqlDataTypeEnum.VARCHAR.getType())) {
                totalLength -= Integer.valueOf(field.getLength());
                field.setColumnType(MysqlDataTypeEnum.TEXT.getType());
                field.setDataType(MysqlDataTypeEnum.TEXT.getType());
                field.setLength("0");
                field.setScale("0");
            }
            index++;
            if (totalLength <= mysql_row_max_length) {
                break;
            }
        }

        // 适配结束后如果行长度任然大于mysql的最大行长度，提示错误
        if (totalLength > mysql_row_max_length) {
            throw new BizException("Convert to MySQL error: 所有字段长度总和不能超过21500，请处理后再重试！");
        }
    }
}
