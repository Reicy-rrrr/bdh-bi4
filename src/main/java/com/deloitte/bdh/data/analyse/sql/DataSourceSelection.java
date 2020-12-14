package com.deloitte.bdh.data.analyse.sql;

import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;

public interface DataSourceSelection {

    AnalyseSql getBean(SqlContext context);

}
