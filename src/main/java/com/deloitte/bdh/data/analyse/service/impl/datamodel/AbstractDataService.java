package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.sql.AnalyseSql.Method;

import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.sql.DataSourceSelection;
import com.deloitte.bdh.data.analyse.sql.AnalyseSql;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
@Slf4j
public abstract class AbstractDataService {
    @Resource
    private DataSourceSelection sourceSelection;


    protected abstract void validate(DataModel dataModel);

    protected BaseComponentDataResponse execute(DataModel dataModel, Sql sql) {
        return execute(dataModel, sql.build());
    }

    protected BaseComponentDataResponse execute(DataModel dataModel, String sql) {
        return execute(dataModel, sql, list -> list);
    }

    protected BaseComponentDataResponse execute(DataModel dataModel, String sql, Rows rows) {
        return execute(dataModel, () -> sql, rows);
    }

    protected BaseComponentDataResponse execute(DataModel dataModel, Sql sqlInterface, Rows rowsInterface) {
        BaseComponentDataResponse response = new BaseComponentDataResponse();
        List<Map<String, Object>> list = null;
        String sql = sqlInterface.build();
        if (StringUtils.isNotBlank(sql)) {
            list = directExecute(dataModel, sql);
        }
        response.setRows(rowsInterface.set(list));
        response.setTotal(buildCount(dataModel, sql));
        response.setSql(sql);
        return response;
    }

    final protected String buildSql(DataModel dataModel) {
        validate(dataModel);
        //剔除重复的字段
        before(dataModel);
        return buildSelect(dataModel)
                + buildFrom(dataModel)
                + buildWhere(dataModel)
                + buildGroupBy(dataModel)
                + buildHaving(dataModel)
                + buildOrder(dataModel)
                + page(dataModel);
    }

    protected void before(DataModel dataModel) {
        List<String> ids = Lists.newArrayList();
        List<DataModelField> newX = Lists.newArrayList();
        for (DataModelField field : dataModel.getX()) {
            if (!ids.contains(field.getId())) {
                ids.add(field.getId());
                newX.add(field);
            }
        }
        dataModel.setX(newX);
    }

    protected String buildSelect(DataModel dataModel) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.SELECT);
        AnalyseSql sql = sourceSelection.getBean(context);
        return String.valueOf(sql.assembly(context));
    }

    protected String buildFrom(DataModel dataModel) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.FROM);
        AnalyseSql sql = sourceSelection.getBean(context);
        return String.valueOf(sql.assembly(context));
    }

    protected String buildWhere(DataModel dataModel) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(Method.WHERE);
        AnalyseSql sql = sourceSelection.getBean(context);
        return String.valueOf(sql.assembly(context));
    }

    protected String buildGroupBy(DataModel dataModel) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.GROUPBY);
        AnalyseSql sql = sourceSelection.getBean(context);
        return String.valueOf(sql.assembly(context));
    }

    @Deprecated
    protected String buildHaving(DataModel dataModel) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.HAVING);
        AnalyseSql sql = sourceSelection.getBean(context);
        return String.valueOf(sql.assembly(context));
    }

    protected String buildOrder(DataModel dataModel) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.ORDERBY);
        AnalyseSql sql = sourceSelection.getBean(context);
        return String.valueOf(sql.assembly(context));
    }

    protected String page(DataModel dataModel) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.PAGE);
        AnalyseSql sql = sourceSelection.getBean(context);
        return String.valueOf(sql.assembly(context));

    }

    private Long buildCount(DataModel dataModel, String querySql) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setQuerySql(querySql);
        context.setMethod(Method.COUNT);
        AnalyseSql sql = sourceSelection.getBean(context);
        Object result = sql.assembly(context);
        if (null == result) {
            return null;
        }
        return Long.parseLong((String) result);
    }

    public interface Sql {
        String build();
    }

    public interface Rows {
        List<Map<String, Object>> set(List<Map<String, Object>> list);
    }

    public String getColName(DataModelField dataModelField) {

        if (Objects.isNull(dataModelField)) {
            return "";
        }
        String colName = dataModelField.getId();
        if (StringUtils.isNotBlank(dataModelField.getAlias())) {
            colName = dataModelField.getAlias();
        }
        return colName;
    }

    protected List<Map<String, Object>> directExecute(DataModel dataModel, String querySql) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(Method.EXECUT);
        context.setQuerySql(querySql);
        AnalyseSql sql = sourceSelection.getBean(context);
        Object result = sql.assembly(context);
        if (null == result) {
            return null;
        }
        return (List<Map<String, Object>>) result;
    }

}
