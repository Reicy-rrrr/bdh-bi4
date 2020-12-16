package com.deloitte.bdh.data.collation.database.convertor.impl;

import com.deloitte.bdh.data.collation.database.convertor.DbConvertor;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
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
        } else if (tf.getColumnType().contains("NUMBER")) {
            tf.setColumnType(tf.getColumnType().replace("NUMBER", "NUMERIC"));
        } else if (tf.getColumnType().contains("BLOB")) {
            tf.setColumnType("LONGBLOB");
        } else if (tf.getColumnType().contains("CLOB")) {
            tf.setColumnType("LONGTEXT");
        } else if (tf.getColumnType().contains("TIMESTAMP")) {
            tf.setColumnType("TIMESTAMP");
        } else if (tf.getColumnType().contains("DATE")) {
            tf.setColumnType("DATETIME");
        } else if (tf.getColumnType().contains("NVARCHAR")) {
            tf.setColumnType(tf.getColumnType().replace("NVARCHAR", "VARCHAR"));
        } else if (tf.getColumnType().contains("ALPHANUM")) { //ALPHANUM - 存储字母数字字符。整数的值介于1到127之间。
            tf.setColumnType("INT");
        } else if (tf.getColumnType().contains("BOOLEAN")) { //布尔值
            tf.setColumnType("VARCHAR(10)");
        } else if (tf.getColumnType().contains("DOUBLE")) { //布尔值
            tf.setColumnType("DOUBLE");
        } else if (tf.getColumnType().contains("REAL")) {
            tf.setColumnType("REAL");
        } else if (tf.getColumnType().contains("SHORTTEXT")) {
            tf.setColumnType("LONGTEXT");
        } else if (tf.getColumnType().contains("SMALLDECIMAL")) {
            tf.setColumnType("DECIMAL");
        } else if (tf.getColumnType().contains("TIME")) {
            tf.setColumnType("TIME");
        } else if (tf.getColumnType().contains("ST_GEOMETRY")) {
            tf.setColumnType("TEXT");
        } else if (tf.getColumnType().contains("ST_POINT")) {
            tf.setColumnType("TEXT");
        }
    }


}
