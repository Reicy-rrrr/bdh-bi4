package com.deloitte.bdh.data.db.impl;

import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.db.DbProcess;
import com.deloitte.bdh.data.db.dto.DbContext;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@Service
public class Mysql extends AbstractProcess implements DbProcess {

    @Override
    public String test(DbContext context) throws Exception {
        Connection con = super.connection(context);
        super.close(con);
        return "连接成功";
    }

    @Override
    public String getTables(DbContext context) throws Exception {
        Connection con = super.connection(context);
        PreparedStatement statement = con.prepareStatement(tableSql(context));
        ResultSet result = statement.executeQuery();
        List<String> list = Lists.newArrayList();
        while (result.next()) {
            list.add(result.getString("TABLE_NAME"));
        }
        super.close(con);
        return JsonUtil.obj2String(list);
    }

    @Override
    public String getFields(DbContext context) throws Exception {
        Connection con = super.connection(context);
        PreparedStatement statement = con.prepareStatement(fieldSql(context));
        ResultSet result = statement.executeQuery();
        List<String> list = Lists.newArrayList();
        while (result.next()) {
            list.add(result.getString("COLUMN_NAME"));
        }
        super.close(con);
        return JsonUtil.obj2String(list);
    }


    @Override
    public String tableSql(DbContext context) {
        return "select * from information_schema.TABLES where TABLE_SCHEMA=(select database())";
    }

    @Override
    public String fieldSql(DbContext context) {
        return "select * from information_schema.COLUMNS where" +
                " TABLE_SCHEMA = (select database()) and TABLE_NAME='" + context.getTableName() + "'";
    }
}
