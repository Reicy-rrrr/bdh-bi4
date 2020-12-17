package com.deloitte.bdh.data.analyse.sql.impl;


import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("analysemysql")
public class AnalyseMysql extends AnalyseLocal {
    @Resource
    private DbSelector dbSelector;

    @Override
    protected Long count(SqlContext context) {
        DataModel model = context.getModel();
        if (null != model.getPage()) {
            String select = this.select(model);
            String from = this.from(model);
            String where = this.where(model);
            String groupBy = this.groupBy(model);
            String having = this.having(model);
            String countSql = StringUtils.join(select, from, where, groupBy, having);

            if (StringUtils.isNotBlank(countSql)) {
                countSql = "SELECT count(1) AS TOTAL FROM (" + countSql + ") TABLE_COUNT";
                context.setQuerySql(countSql);
                List<Map<String, Object>> result = customizeExecute(context);
                if (CollectionUtils.isNotEmpty(result)) {
                    return ((BigDecimal) result.get(0).get("TOTAL")).longValue();
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
