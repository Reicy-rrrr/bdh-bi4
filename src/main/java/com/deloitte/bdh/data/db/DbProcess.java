package com.deloitte.bdh.data.db;

import com.deloitte.bdh.data.db.dto.DbContext;


public interface DbProcess {

    String test(DbContext context) throws Exception;

    String getTables(DbContext context) throws Exception;

    String getFields(DbContext context) throws Exception;

}
