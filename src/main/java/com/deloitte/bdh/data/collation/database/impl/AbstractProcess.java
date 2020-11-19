package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractProcess {

    @Autowired
    DataSource dataSource;

    protected Connection connection(DbContext context) throws Exception {
        if (SourceTypeEnum.File_Csv.equals(context.getSourceTypeEnum()) || SourceTypeEnum.File_Excel.equals(context.getSourceTypeEnum())) {
            return dataSource.getConnection();
        }
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

        long count = 0L;
        if (SourceTypeEnum.Hive.equals(context.getSourceTypeEnum())
                || SourceTypeEnum.Hive2.equals(context.getSourceTypeEnum())) {
            count = context.getSize();
        } else {
            count = getTableCount(context);
        }
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
        long count = 0L;
        while (result.next()) {
            count = result.getLong(1);
        }
        this.close(con);
        return count;
    }

    protected List<Map<String, Object>> executeQuery(DbContext context) throws Exception {
        Connection con = this.connection(context);
        PreparedStatement statement = con.prepareStatement(buildQueryLimit(context));
        ResultSet result = statement.executeQuery();
        ResultSetMetaData metaData = result.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> rows = Lists.newArrayList();

        String tableName = context.getTableName();
        while (result.next()) {
            Map<String, Object> rowData = Maps.newLinkedHashMap();
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
        return rows;
    }

    protected abstract String tableSql(DbContext context);

    protected abstract String fieldSql(DbContext context);

    protected abstract String selectSql(DbContext context);

    protected abstract String buildQueryLimit(DbContext context);

    protected String countSql(DbContext context) {
        if (StringUtils.isNotBlank(context.getCondition())) {
            return "SELECT COUNT(1) FROM " + context.getTableName() + " WHERE " + context.getCondition();
        }
        return "SELECT COUNT(1) FROM " + context.getTableName();
    }

    protected void close(Connection con) throws Exception {
        if (con != null) {
            con.close();
        }
    }
}
