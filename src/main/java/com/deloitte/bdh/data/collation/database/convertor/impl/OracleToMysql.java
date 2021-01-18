package com.deloitte.bdh.data.collation.database.convertor.impl;

import com.deloitte.bdh.data.collation.database.convertor.DbConvertor;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.MysqlDataTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("oracleToMysql")
public class OracleToMysql implements DbConvertor {

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
        if (tf.getColumnType().contains("VARCHAR")) {
            tf.setColumnType(tf.getColumnType().replace("VARCHAR2", "VARCHAR"));
            tf.setDataType(MysqlDataTypeEnum.VARCHAR.getType());
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
        } else if (tf.getColumnType().contains("WITH LOCAL TIME ZONE")) {
            tf.setColumnType("VARCHAR(255)");
            tf.setDataType(MysqlDataTypeEnum.VARCHAR.getType());
        } else if (tf.getColumnType().contains("RAW")) {
            tf.setColumnType("LONGTEXT");
            tf.setDataType(MysqlDataTypeEnum.LONGTEXT.getType());
        } else if (tf.getColumnType().contains("INTERVAL DAY(2) TO")) {
            tf.setColumnType("VARCHAR(255)");
            tf.setDataType(MysqlDataTypeEnum.VARCHAR.getType());
        } else if (tf.getColumnType().contains("INTERVAL YEAR(2) TO")) {
            tf.setColumnType("VARCHAR(255)");
            tf.setDataType(MysqlDataTypeEnum.VARCHAR.getType());
        } else if (tf.getColumnType().contains("BINARY_DOUBLE")) {
            tf.setColumnType("DOUBLE");
            tf.setDataType(MysqlDataTypeEnum.DOUBLE.getType());
        } else if (tf.getColumnType().contains("BINARY_FLOAT")) {
            tf.setColumnType("FLOAT");
            tf.setDataType(MysqlDataTypeEnum.FLOAT.getType());
        }
    }
}
