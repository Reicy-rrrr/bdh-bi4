package com.deloitte.bdh.data.collation.database.convertor.impl;

import com.deloitte.bdh.data.collation.database.convertor.DbConvertor;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sqlserverToMysql")
public class SqlserverToMysql implements DbConvertor {

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
        if(tf.getColumnType().contains("NVARCHAR")){
            tf.setColumnType(tf.getColumnType().replace("NVARCHAR","VARCHAR"));
        }else if(tf.getColumnType().contains("MONEY")){
            tf.setColumnType(tf.getColumnType().replace("MONEY","NUMERIC(19,4)"));
        }else if(tf.getColumnType().contains("XML")){
            tf.setColumnType(tf.getColumnType().replace("XML","LONGTEXT"));
        }else if(tf.getColumnType().contains("NTEXT")){
            tf.setColumnType(tf.getColumnType().replace("NTEXT","LONGTEXT"));
        }else if(tf.getColumnType().contains("SMALLMONEY")){
            tf.setColumnType("NUMERIC(10,4)");
        }else if(tf.getColumnType().contains("DATE")){
            tf.setColumnType("DATETIME");
        }else if(tf.getColumnType().contains("DATETIME2")){
            tf.setColumnType("DATETIME");
        }else if(tf.getColumnType().contains("NUMERIC")){
            tf.setColumnType(tf.getColumnType().replace("NUMERIC","DECIMAL"));
        }else if(tf.getColumnType().contains("UNIQUEIDENTIFIER")){
            tf.setColumnType("VARCHAR(40)");
        }

    }


}
