package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.deloitte.bdh.data.collation.enums.OracleDataTypeEnum;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
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

        //get views
        statement = con.prepareStatement("select t.VIEW_NAME from user_views t");
        result = statement.executeQuery();
        while (result.next()) {
            list.add(result.getString("VIEW_NAME"));
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
            // 列名
            String name = result.getString("COLUMN_NAME");
            // 列注释
            String comments = result.getString("COMMENTS");
            if (true || StringUtils.isBlank(comments)) {
                comments = name;
            }
            // 数据类型
            String dataType = result.getString("DATA_TYPE");
            // Oracle中的TIMESTAMP类型查询出的数据类型为TIMESTAMP(6)，需要特殊处理
            if (dataType.startsWith("TIMESTAMP") && dataType.contains("(")) {
                dataType = dataType.substring(0, dataType.indexOf("("));
            }
            // 字段长度
            String dataLength = result.getString("DATA_LENGTH");
            // 字段精度
            String dataPrecision = result.getString("DATA_PRECISION");
            // 字段标度
            String dataScale = result.getString("DATA_SCALE");
            String length = "0";
            String scale = "0";
            // 时间和日期类型特殊处理
            if (!"DATE".equals(dataType) && !dataType.startsWith("TIMESTAMP")) {
                if (StringUtils.isNotBlank(dataScale)) {
                    scale = dataScale;
                }
                if (StringUtils.isNotBlank(dataPrecision)) {
                    length = dataPrecision;
                } else {
                    length = dataLength;
                }
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
            TableField field = new TableField(OracleDataTypeEnum.values(dataType.toUpperCase()).getValue().getType(), name, comments, columnType.toString(), dataType, length, scale);
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
        return "SELECT * FROM all_tables WHERE OWNER = '" + context.getDbUserName().toUpperCase() + "' ORDER BY table_name";
    }

    @Override
    public String fieldSql(DbContext context) {
        return " SELECT ACC.COMMENTS,T.COLUMN_NAME,T.DATA_TYPE,T.DATA_LENGTH,T.DATA_PRECISION,T.DATA_SCALE FROM USER_TAB_COLUMNS T " +
                " LEFT JOIN ALL_COL_COMMENTS ACC ON T.TABLE_NAME=ACC.TABLE_NAME AND T.COLUMN_NAME=ACC.COLUMN_NAME " +
                " WHERE t.TABLE_NAME='" + context.getTableName().toUpperCase() + "'";
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

    @Override
    protected String buildQueryLimit(DbContext context) {
        return "SELECT * FROM (SELECT tmp.*, ROWNUM AS TEMP_NUM FROM (SELECT * FROM (" + context.getQuerySql() + ")) tmp) WHERE TEMP_NUM BETWEEN 1 AND 10";
    }
}
