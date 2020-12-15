package com.deloitte.bdh.data.analyse.sql.impl;


import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("analysemysql")
public class AnalyseMysql extends AnalyseLocal {
    @Resource
    private DbSelector dbSelector;

    @Override
    protected Long count(SqlContext context) {
        if (null != context.getModel().getPage()) {
            String countSql = context.getQuerySql();
            if (StringUtils.isNotBlank(countSql)) {
                if (StringUtils.containsIgnoreCase(countSql, "LIMIT")) {
                    countSql = StringUtils.substringBefore(countSql, "LIMIT");
                }

                countSql = "SELECT count(1) AS TOTAL FROM (" + countSql + ") TABLE_COUNT";
                context.setQuerySql(countSql);
                List<Map<String, Object>> result = execute(context);
                if (null != result && CollectionUtils.isNotEmpty(result)) {
                    return (Long) result.get(0).get("TOTAL");
                }
            }
        }
        return null;
    }

    @Override
    protected List<Map<String, Object>> execute(SqlContext context) {
        DbContext dbContext = new DbContext();
        dbContext.setDbId(context.getDbId());
        dbContext.setQuerySql(context.getQuerySql());
        try {
            return dbSelector.executeQuery(dbContext);
        } catch (Exception e) {
            log.error("执行异常:", e);
            throw new RuntimeException("执行SQL异常");
        }
    }
}
