package com.deloitte.bdh.data.analyse.sql;

import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.collation.model.BiDataSet;

import java.util.List;
import java.util.Map;

public interface DataSourceSelection {

    String getTableName(DataModel model);

    AnalyseSql getBean(DataModel model);

    AnalyseSql getBean(BiDataSet dataSet);

    String buildSql(DataModel model);

    Long getCount(DataModel model);

    List<Map<String, Object>> execute(DataModel model, String querySql);

    List<Map<String, Object>> expandExecute(DataModel model, Type type);


    interface Type {
        String local(DataModel model, String tableName);
        String mysql(DataModel model, String tableName);
        String oracle(DataModel model, String tableName);
        String sqlServer(DataModel model, String tableName);
        String hana(DataModel model, String tableName);
        //todo expanding
    }

}
