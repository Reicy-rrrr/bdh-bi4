package com.deloitte.bdh.data.analyse.sql;

import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractAnalyseSql implements AnalyseSql {

    @Override
    final public Object assembly(SqlContext context) {
        Object result;
        AnalyseSql.Method method = context.getMethod();
        switch (method) {
            case SELECT:
                result = select(context.getModel());
                break;
            case FROM:
                result = from(context.getModel());
                break;
            case WHERE:
                result = where(context.getModel());
                break;
            case GROUPBY:
                result = groupBy(context.getModel());
                break;
            case HAVING:
                result = having(context.getModel());
                break;
            case ORDERBY:
                result = orderBy(context.getModel());
                break;
            case PAGE:
                result = page(context);
                break;
            case COUNT:
                result = count(context);
                break;
            case CUSTOMIZE:
                result = customize(context.getModel());
                break;
            default:
                result = execute(context);
        }
        return result;
    }

    protected abstract String select(DataModel model);

    protected abstract String from(DataModel model);

    protected abstract String where(DataModel model);

    protected abstract String groupBy(DataModel model);

    protected abstract String having(DataModel model);

    protected abstract String orderBy(DataModel model);

    protected abstract String page(SqlContext context);

    protected abstract Long count(SqlContext context);

    protected abstract List<Map<String, Object>> execute(SqlContext context);

    protected String customize(DataModel model) {
        return null;
    }

}
