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
        if(fields==null || fields.size()==0){
            return;
        }
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
            //把oracle的VARCHAR2 替换成VARCHAR
            tf.setColumnType(tf.getColumnType().replace("VARCHAR2","VARCHAR"));
        }else if(tf.getColumnType().contains("NUMBER")){
            //把oracle的NUMBER 替换成 NUMERIC
            tf.setColumnType(tf.getColumnType().replace("NUMBER","NUMERIC"));
        }else if(tf.getColumnType().contains("BLOB")){
            //把oracle的BLOB 替换成 LONGBLOB
            tf.setColumnType(tf.getColumnType().replace("BLOB","LONGBLOB"));
        }else if(tf.getColumnType().contains("CLOB")){
            //把oracle的BLOB 替换成 LONGBLOB
            tf.setColumnType(tf.getColumnType().replace("CLOB","LONGTEXT"));
        }else if(tf.getColumnType().contains("TIMESTAMP")){
            //mysql 需要去掉长度
            tf.setColumnType("TIMESTAMP");
        }else if(tf.getColumnType().contains("DATE")){
            //mysql 需要去掉长度
            tf.setColumnType("DATETIME");
        }
    }


}
