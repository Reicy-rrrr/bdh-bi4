package com.deloitte.bdh.data.analyse.sql;


import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;

public interface AnalyseSql {

    Object assembly(SqlContext context);


    enum Method {
        SELECT,
        FROM,
        WHERE,
        GROUPBY,
        HAVING,
        ORDERBY,
        PAGE,
        COUNT,
        EXECUTE,
        CUSTOMIZE_EXECUTE;

        String key;

        Method() {
        }

        public String getKey() {
            return key;
        }
    }

}
