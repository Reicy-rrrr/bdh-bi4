package com.deloitte.bdh.data.collation.database.convertor.impl;

import com.deloitte.bdh.data.collation.database.convertor.DbConvertor;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("oracleToMysql")
public class OracleToMysql implements DbConvertor {

    @Override
    public void convertFieldType(List<TableField> fields, DbContext context) {
        if(fields==null || fields.size()==0)
            return;

        //循环数据类型
        for(TableField tf:fields){
            getTableField(tf); //循环调整数据类型
        }
    }


    /**
     * 通过oracle的列 转换成 mysql的列
     * @param tf
     * @return
     */
    private void getTableField(TableField tf){
        if(tf.getColumnType().contains("VARCHAR")){
            tf.setColumnType(tf.getColumnType().replace("VARCHAR2","VARCHAR"));
        }else if(tf.getColumnType().contains("NUMBER")){
            tf.setColumnType(tf.getColumnType().replace("NUMBER","NUMERIC"));
        }else if(tf.getColumnType().contains("BLOB")){
            tf.setColumnType("LONGBLOB");
        }else if(tf.getColumnType().contains("CLOB")){
            tf.setColumnType("LONGTEXT");
        }else if(tf.getColumnType().contains("TIMESTAMP")){
            tf.setColumnType("TIMESTAMP");
        }else if(tf.getColumnType().contains("DATE")){
            tf.setColumnType("DATETIME");
        }else if(tf.getColumnType().contains("WITH LOCAL TIME ZONE")){
            tf.setColumnType("VARCHAR(50)");
        }else if(tf.getColumnType().contains("RAW")){
            tf.setColumnType("LONGTEXT");
        }else if(tf.getColumnType().contains("INTERVAL DAY(2) TO")) {
            tf.setColumnType("VARCHAR(50)");
        }else if(tf.getColumnType().contains("INTERVAL YEAR(2) TO")) {
            tf.setColumnType("VARCHAR(50)");
        }else if(tf.getColumnType().contains("BINARY_DOUBLE")){
            tf.setColumnType("DOUBLE");
        }else if(tf.getColumnType().contains("BINARY_FLOAT")){
            tf.setColumnType("FLOAT");
        }

    }


}
