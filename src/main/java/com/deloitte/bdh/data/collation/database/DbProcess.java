package com.deloitte.bdh.data.collation.database;

import com.deloitte.bdh.data.collation.database.dto.DbContext;


public interface DbProcess {

    String test(DbContext context) throws Exception;

    String getTables(DbContext context) throws Exception;

    String getFields(DbContext context) throws Exception;

}
