package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.deloitte.bdh.data.collation.enums.HanaDataTypeEnum;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@Service("hana")
public class Hana extends AbstractProcess implements DbSelector {

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
            // 字段名称
            String name = result.getString("COLUMN_NAME");
            // 字段注释
            String comments = result.getString("COMMENTS");
            if (true || StringUtils.isBlank(comments)) {
                comments = name;
            }
            // 字段数据类型
            String dataType = result.getString("DATA_TYPE_NAME");
            // 字段长度
            String length = result.getString("LENGTH");
            if (StringUtils.isBlank(length)) {
                length = "0";
            }
            // 字段精度
            String scale = result.getString("SCALE");
            if (StringUtils.isBlank(scale)) {
                scale = "0";
            }
            // 字段类型
            StringBuilder columnType = new StringBuilder(dataType);
            if (!StringUtils.equals("0", length)) {
                columnType.append("(").append(length);
                if (!StringUtils.equals("0", scale)) {
                    columnType.append(",").append(scale);
                }
                columnType.append(")");
            }
            TableField field = new TableField(HanaDataTypeEnum.get(dataType).getValue().getType(), name, comments, columnType.toString(), dataType, length, scale);
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
    public PageInfo<Map<String, Object>> executePageQuery(DbContext context) throws Exception {
        return super.executePageQuery(context);
    }

    @Override
    public String tableSql(DbContext context) {
        return "SELECT * FROM TABLES WHERE SCHEMA_NAME = '" + context.getDbUserName().toUpperCase() + "' AND IS_SYSTEM_TABLE = 'FALSE'";
    }

    @Override
    public String fieldSql(DbContext context) {
        return "SELECT * FROM SYS.COLUMNS WHERE SCHEMA_NAME = '" + context.getDbUserName().toUpperCase() + "' AND TABLE_NAME = '" + context.getTableName() + "' ORDER BY POSITION";
    }

    @Override
    protected String selectSql(DbContext context) {
        Integer page = context.getPage();
        Integer size = context.getSize();
        int start = (page - 1) * size;
        return "SELECT * FROM " + context.getTableName() + " LIMIT " + size + " OFFSET " + start;
    }

    @Override
    protected String buildQueryLimit(DbContext context) {
        return context.getQuerySql() + " LIMIT 10 OFFSET 1";
    }
}
