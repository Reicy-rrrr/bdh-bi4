package com.deloitte.bdh.data.collation.database;

import com.deloitte.bdh.data.collation.database.dto.DbContext;

public interface DbSelector {

    String work(DbContext context) throws Exception;
}
