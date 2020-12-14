package com.deloitte.bdh.data.analyse.sql.dto;

import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.sql.AnalyseSql;
import lombok.Data;

@Data
public class SqlContext {
    private DataModel model;
    private AnalyseSql.Method method;
    private String querySql;
}
