package com.deloitte.bdh.data.analyse.sql;

import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;

import java.util.List;
import java.util.Map;

public interface DataSourceSelection {

    AnalyseSql getBean(DataModel model);

    String buildSql(DataModel model);

    Long getCount(DataModel model);

    List<Map<String, Object>> directExecute(DataModel model, String querySql);
}
