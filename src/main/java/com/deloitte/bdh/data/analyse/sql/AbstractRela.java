package com.deloitte.bdh.data.analyse.sql;

import com.deloitte.bdh.data.analyse.enums.AggregateTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;

public abstract class AbstractRela extends AbstractAnalyseSql {


    protected abstract String select(DataModel model);

    protected abstract String from(DataModel model);

    protected abstract String where(DataModel model);

    protected abstract String groupBy(DataModel model);

    protected abstract String having(DataModel model);

    protected abstract String orderBy(DataModel model);

    protected abstract String page(SqlContext context);

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
