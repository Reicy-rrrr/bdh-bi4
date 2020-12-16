package com.deloitte.bdh.data.collation.database.convertor.impl;

import com.deloitte.bdh.data.collation.database.convertor.DbConvertor;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.MysqlDataTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("hanaToMysql")
public class HanaToMysql implements DbConvertor {

    @Override
    public void convertFieldType(List<TableField> fields, DbContext context) {
        if (fields == null || fields.size() == 0) {
            return;
        }

        // 循环调整数据类型
        for (TableField tf : fields) {
            getTableField(tf);
        }
        mysqlSchemaAdapter(fields);
    }


    /**
     * 通过oracle的列 转换成 mysql的列
     *
     * @param tf
     * @return
     */
    private void getTableField(TableField tf) {
        if (tf.getColumnType().contains("NCLOB")) {
            tf.setColumnType("LONGTEXT");
            tf.setDataType(MysqlDataTypeEnum.LONGTEXT.getType());
        } else if (tf.getColumnType().contains("NUMBER")) {
            tf.setColumnType(tf.getColumnType().replace("NUMBER", "NUMERIC"));
            tf.setDataType(MysqlDataTypeEnum.NUMERIC.getType());
        } else if (tf.getColumnType().contains("BLOB")) {
            tf.setColumnType("LONGBLOB");
            tf.setDataType(MysqlDataTypeEnum.LONGBLOB.getType());
        } else if (tf.getColumnType().contains("CLOB")) {
            tf.setColumnType("LONGTEXT");
            tf.setDataType(MysqlDataTypeEnum.LONGTEXT.getType());
        } else if (tf.getColumnType().contains("TIMESTAMP")) {
            tf.setColumnType("TIMESTAMP");
            tf.setDataType(MysqlDataTypeEnum.TIMESTAMP.getType());
        } else if (tf.getColumnType().contains("DATE")) {
            tf.setColumnType("DATETIME");
            tf.setDataType(MysqlDataTypeEnum.DATETIME.getType());
        } else if (tf.getColumnType().contains("NVARCHAR")) {
            tf.setColumnType(tf.getColumnType().replace("NVARCHAR", "VARCHAR"));
            tf.setDataType(MysqlDataTypeEnum.VARCHAR.getType());
        } else if (tf.getColumnType().contains("ALPHANUM")) {
            //ALPHANUM - 存储字母数字字符。整数的值介于1到127之间。
            tf.setColumnType("INT");
            tf.setDataType(MysqlDataTypeEnum.INT.getType());
        } else if (tf.getColumnType().contains("BOOLEAN")) {
            //布尔值
            tf.setColumnType("VARCHAR(10)");
            tf.setDataType(MysqlDataTypeEnum.VARCHAR.getType());
        } else if (tf.getColumnType().contains("DOUBLE")) {
            tf.setColumnType("DOUBLE");
            tf.setDataType(MysqlDataTypeEnum.DOUBLE.getType());
        } else if (tf.getColumnType().contains("REAL")) {
            tf.setColumnType("REAL");
            tf.setDataType(MysqlDataTypeEnum.REAL.getType());
        } else if (tf.getColumnType().contains("SHORTTEXT")) {
            tf.setColumnType("LONGTEXT");
            tf.setDataType(MysqlDataTypeEnum.LONGTEXT.getType());
        } else if (tf.getColumnType().contains("SMALLDECIMAL")) {
            tf.setColumnType("DECIMAL");
            tf.setDataType(MysqlDataTypeEnum.DECIMAL.getType());
        } else if (tf.getColumnType().contains("TIME")) {
            tf.setColumnType("TIME");
            tf.setDataType(MysqlDataTypeEnum.TIME.getType());
        } else if (tf.getColumnType().contains("ST_GEOMETRY")) {
            tf.setColumnType("TEXT");
            tf.setDataType(MysqlDataTypeEnum.TEXT.getType());
        } else if (tf.getColumnType().contains("ST_POINT")) {
            tf.setColumnType("TEXT");
            tf.setDataType(MysqlDataTypeEnum.TEXT.getType());
        }
    }
}
