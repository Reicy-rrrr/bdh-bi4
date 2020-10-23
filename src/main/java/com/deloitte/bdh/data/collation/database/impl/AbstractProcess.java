package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.vo.TableData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractProcess {

    protected Connection connection(DbContext context) throws Exception {
        Class.forName(context.getDriverName());
        return DriverManager.getConnection(context.getDbUrl(), context.getDbUserName(), context.getDbPassword());
    }

    protected TableData getTableData(DbContext context) throws Exception {
        Connection con = this.connection(context);
        PreparedStatement statement = con.prepareStatement(selectSql(context));
        ResultSet result = statement.executeQuery();
        ResultSetMetaData metaData = result.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<LinkedHashMap<String, Object>> rows = Lists.newArrayList();

        String tableName = context.getTableName();
        while (result.next()) {
            LinkedHashMap<String, Object> rowData = Maps.newLinkedHashMap();
            for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
                // hive数据库查询的结果集字段带表名
                String columnName = metaData.getColumnName(colIndex).replace(tableName + ".", "");
                if ("TEMP_NUM".equals(columnName)) {
                    continue;
                }
                rowData.put(columnName, result.getObject(colIndex));
            }
            rows.add(rowData);
        }
        this.close(con);
        TableData tableData = new TableData();
        tableData.setRows(rows);

        long count = getTableCount(context);
        tableData.setTotal(count);
        Integer page = context.getPage();
        Integer size = context.getSize();
        if (size * page >= count) {
            tableData.setMore(false);
        } else {
            tableData.setMore(true);
        }
        return tableData;
    }

    protected long getTableCount(DbContext context) throws Exception {
        Connection con = this.connection(context);
        PreparedStatement statement = con.prepareStatement(countSql(context));
        ResultSet result = statement.executeQuery();
        this.close(con);
        long count = 0L;
        while (result.next()) {
            count = result.getLong(1);
        }
        return count;
    }

    protected abstract String tableSql(DbContext context);

    protected abstract String fieldSql(DbContext context);

    protected abstract String selectSql(DbContext context);

    protected String countSql(DbContext context) {
        return "SELECT COUNT(1) FROM " + context.getTableName();
    }

    protected void close(Connection con) throws Exception {
        if (con != null) {
            con.close();
        }
    }
}
