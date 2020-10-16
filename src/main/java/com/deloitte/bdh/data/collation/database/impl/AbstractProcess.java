package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.data.collation.database.dto.DbContext;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class AbstractProcess {

    protected Connection connection(DbContext context) throws Exception {
        Class.forName(context.getDriverName());
        return DriverManager.getConnection(context.getDbUrl(), context.getDbUserName(), context.getDbPassword());
    }

    protected abstract String tableSql(DbContext context);

    protected abstract String fieldSql(DbContext context);

    protected void close(Connection con) throws Exception {
        if (con != null) {
            con.close();
        }
    }

}
