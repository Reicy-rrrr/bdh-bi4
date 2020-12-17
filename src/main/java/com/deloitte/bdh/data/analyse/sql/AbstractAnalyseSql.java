package com.deloitte.bdh.data.analyse.sql;

import com.deloitte.bdh.data.analyse.enums.AggregateTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.service.AnalyseUserDataService;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractAnalyseSql implements AnalyseSql {
    @Resource
    protected AnalyseUserDataService userDataService;

    @Override
    final public Object assembly(SqlContext context) {
        Object result;
        AnalyseSql.Method method = context.getMethod();
        switch (method) {
            case ASSEMBLYQUERYSQL:
                result = assemblyQuerySql(context);
                break;
            case COUNT:
                result = count(context);
                break;
            case EXECUTE:
                result = execute(context);
                break;
            default:
                result = customizeExecute(context);
        }
        return result;
    }

    protected abstract String assemblyQuerySql(SqlContext context);

    protected abstract String select(DataModel model);

    protected abstract String from(DataModel model);

    protected abstract String where(DataModel model);

    protected abstract String groupBy(DataModel model);

    protected abstract String having(DataModel model);

    protected abstract String orderBy(DataModel model);

    protected abstract String page(SqlContext context);

    protected abstract Long count(SqlContext context);

    protected abstract List<Map<String, Object>> execute(SqlContext context);

    protected List<Map<String, Object>> customizeExecute(SqlContext context) {
        return execute(context);
    }

    protected boolean needGroup(DataModel model) {
        String selectSql = select(model);
        AggregateTypeEnum[] enums = AggregateTypeEnum.values();
        for (AggregateTypeEnum typeEnum : enums) {
            if (selectSql.contains(typeEnum.getKey())) {
                return true;
            }
        }
        return false;
    }

}
