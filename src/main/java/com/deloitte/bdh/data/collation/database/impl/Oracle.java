package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("oracle")
public class Oracle extends AbstractProcess implements DbSelector {
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
            list.add(result.getString("TABLE_NAME"));
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
            field.setName(result.getString("COLUMN_NAME"));
            field.setType("String");
            field.setDesc("");
            columns.add(field);
        }
        super.close(con);
        schema.setColumns(columns);
        return schema;
    }

    @Override
    public TableData getTableData(DbContext context) throws Exception {
        TableData tableData = super.getTableData(context);
        tableData.getRows().forEach(rowData -> {
            for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof oracle.sql.TIMESTAMP) {
                    try {
                        entry.setValue(new Date(((oracle.sql.TIMESTAMP) value).dateValue().getTime()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return tableData;
    }

    @Override
    public String tableSql(DbContext context) {
        return "SELECT * FROM all_tables WHERE OWNER = '" + context.getDbUserName().toUpperCase() + "' ORDER BY table_name";
    }

    @Override
    public String fieldSql(DbContext context) {
        return "SELECT * FROM user_tab_columns WHERE TABLE_NAME=UPPER('" + context.getTableName().toUpperCase() + "')";
    }

    @Override
    protected String selectSql(DbContext context) {
        Integer page = context.getPage();
        Integer size = context.getSize();
        int start = (page - 1) * size + 1;
        int end = page * size;
        return "SELECT * FROM (SELECT tmp.*, ROWNUM AS TEMP_NUM FROM (SELECT * FROM " + context.getTableName()
                + ") tmp) WHERE TEMP_NUM BETWEEN " + start + " AND " + end;
    }
}
