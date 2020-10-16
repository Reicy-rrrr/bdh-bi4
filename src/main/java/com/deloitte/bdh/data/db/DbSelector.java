package com.deloitte.bdh.data.db;

import com.deloitte.bdh.data.db.dto.DbContext;

public interface DbSelector {

    String work(DbContext context) throws Exception;
}
