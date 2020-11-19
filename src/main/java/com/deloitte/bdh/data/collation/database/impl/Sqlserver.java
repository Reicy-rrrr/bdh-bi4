package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@Service("sqlserver")
public class Sqlserver extends AbstractProcess implements DbSelector {

    @Override
    public String test(DbContext context) throws Exception {
        Connection con = super.connection(context);
        super.close(con);
        return "连接成功";
    }

    @Override
    public List<String> getTables(DbContext context) throws Exception {
        Connection con = super.connection(context);
        PreparedStatement statement = con.prepareStatement(tableSql(context));
        ResultSet result = statement.executeQuery();
        List<String> list = Lists.newArrayList();
        while (result.next()) {
            list.add(result.getString("name"));
        }
        super.close(con);
        return list;
    }

    @Override
    public List<String> getFields(DbContext context) throws Exception {
        Connection con = super.connection(context);
        PreparedStatement statement = con.prepareStatement(fieldSql(context));
        ResultSet result = statement.executeQuery();
        List<String> list = Lists.newArrayList();
        while (result.next()) {
            list.add(result.getString("COLUMN_NAME"));
        }
        super.close(con);
        return list;
    }

    @Override
    public TableSchema getTableSchema(DbContext context) throws Exception {
        Connection con = super.connection(context);
        PreparedStatement statement = con.prepareStatement(fieldSql(context));
        ResultSet result = statement.executeQuery();
        TableSchema schema = new TableSchema();
        List<TableField> columns = Lists.newArrayList();
        while (result.next()) {
            TableField field = new TableField();
            // 列名
            field.setName(result.getString("COLUMN_NAME"));
            field.setName(field.getName());
            // 数据类型
            String dataType = result.getString("DATA_TYPE");
            // 字符串最大长度
            String characterMaximumLength = result.getString("CHARACTER_MAXIMUM_LENGTH");
            String numericScale = result.getString("NUMERIC_SCALE");
            String numericPrecision = result.getString("NUMERIC_PRECISION");
            if (StringUtil.isNotEmpty(characterMaximumLength)) {
                field.setColumnType(dataType + "(" + characterMaximumLength + ")");
            } else if (StringUtil.isNotEmpty(numericScale) && StringUtil.isNotEmpty(numericPrecision)) {
                // 精度和标度都有值时
                field.setColumnType(dataType + "(" + numericPrecision + "," + numericScale + ")");
            } else if (StringUtil.isNotEmpty(numericPrecision)) {
                field.setColumnType(dataType + "(" + numericPrecision + ")");
            } else {
                field.setColumnType(dataType);
            }
            columns.add(field);
        }
        super.close(con);
        schema.setColumns(columns);
        return schema;
    }

    @Override
    public TableData getTableData(DbContext context) throws Exception {
        return super.getTableData(context);
    }

    @Override
    public long getTableCount(DbContext context) throws Exception {
        return super.getTableCount(context);
    }

    @Override
    public List<Map<String, Object>> executeQuery(DbContext context) throws Exception {
        return super.executeQuery(context);
    }

    @Override
    public String tableSql(DbContext context) {
        return "SELECT * FROM sysobjects WHERE XTYPE='U'";
    }

    @Override
    public String fieldSql(DbContext context) {
        return "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='" + context.getTableName() + "'";
    }

    @Override
    protected String selectSql(DbContext context) {
        Integer page = context.getPage();
        Integer size = context.getSize();
        return "SELECT * FROM (SELECT * , (ROW_NUMBER() OVER(ORDER BY @@SERVERNAME)-1)/" + size + " AS TEMP_NUM FROM " + context.getTableName() + ") temp WHERE TEMP_NUM = " + (page - 1);
    }

    @Override
    protected String buildQueryLimit(DbContext context) {
        return "SELECT * FROM (SELECT * , (ROW_NUMBER() OVER(ORDER BY @@SERVERNAME)-1)/10 AS TEMP_NUM FROM (" + context.getQuerySql() + ") temp1) temp2 WHERE TEMP_NUM = 1";
    }
}
