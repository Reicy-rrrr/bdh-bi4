package com.deloitte.bdh.data.analyse.sql;


import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;

public interface AnalyseSql {

    Object process(SqlContext context);


    enum Method {
        ASSEMBLY_QUERYSQL,
        COUNT,
        EXECUTE,
        EXPAND_EXECUTE;

        String key;

        Method() {
        }

        public String getKey() {
            return key;
        }
    }

}
