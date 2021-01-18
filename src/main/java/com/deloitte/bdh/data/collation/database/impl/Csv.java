package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.deloitte.bdh.data.collation.enums.MysqlDataTypeEnum;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@Service("csv")
public class Csv extends AbstractProcess implements DbSelector {

    @Override
    public String test(DbContext context) throws Exception {
        return "连接成功";
    }

    @Override
    public List<String> getTables(DbContext context) throws Exception {
        List<String> list = Lists.newArrayList();
        list.add(context.getDbName());
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
            String comments = result.getString("COLUMN_COMMENT");
            // 数据类型
            String dataType = result.getString("DATA_TYPE");
            // 字段类型
            String columnType = result.getString("COLUMN_TYPE");
            // 字符最大长度
            String characterLength = result.getString("CHARACTER_MAXIMUM_LENGTH");
            // 数字精度
            String numericPrecision = result.getString("NUMERIC_PRECISION");
            String length = "0";
            if (StringUtils.isNotBlank(characterLength) || StringUtils.isNotBlank(numericPrecision)) {
                length = StringUtils.isBlank(characterLength) ? numericPrecision : characterLength;
            }
            // 数字标度
            String numericScale = result.getString("NUMERIC_SCALE");
            String scale = "0";
            if (StringUtils.isNotBlank(numericScale)) {
                scale = numericScale;
            }
            TableField field = new TableField(MysqlDataTypeEnum.values(dataType).getValue().getType(), name, comments, columnType, dataType, length, scale);
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
        return "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=(SELECT DATABASE())";
    }

    @Override
    public String fieldSql(DbContext context) {
        return "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE" +
                " TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME='" + context.getTableName() + "' ORDER BY ORDINAL_POSITION";
    }

    @Override
    protected String selectSql(DbContext context) {
        Integer page = context.getPage();
        Integer size = context.getSize();
        int start = (page - 1) * size;
        return "SELECT * FROM " + context.getTableName() + " LIMIT " + start + ", " + size;
    }

    @Override
    protected String buildQueryLimit(DbContext context) {
        return context.getQuerySql() + " LIMIT 1, 10";
    }
}
