package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.util.SqlFormatUtil;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
import com.deloitte.bdh.data.analyse.model.datamodel.DataCondition;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.utils.BuildSqlUtil;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
@Slf4j
public abstract class AbstractDataService {
    @Resource
    protected BiUiDemoMapper biUiDemoMapper;

    protected abstract void validate(DataModel dataModel);

    protected BaseComponentDataResponse execute(Sql sql) {
        return execute(sql.build());
    }

    protected BaseComponentDataResponse execute(String sql) {
        return execute(sql, list -> list);
    }

    protected BaseComponentDataResponse execute(String sql, Rows rows) {
        return execute(() -> sql, rows);
    }

    protected BaseComponentDataResponse execute(Sql sqlInterface, Rows rowsInterface) {
        BaseComponentDataResponse response = new BaseComponentDataResponse();
        List<Map<String, Object>> list = null;
        String sql = sqlInterface.build();
        if (StringUtils.isNotBlank(sql)) {
            list = biUiDemoMapper.selectDemoList(sql);
        }
        response.setRows(rowsInterface.set(list));
        response.setSql(sql);
        return response;
    }

    final protected String buildSql(DataModel dataModel) {
        validate(dataModel);
        return buildSelect(dataModel)
                + buildFrom(dataModel)
                + buildWhere(dataModel)
                + buildGroupBy(dataModel)
                + buildHaving(dataModel)
                + buildOrder(dataModel)
                + limit(dataModel);
    }

    protected String buildSelect(DataModel dataModel) {
        List<String> list = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(dataModel.getX())) {
            for (DataModelField s : dataModel.getX()) {
                String express = BuildSqlUtil.select(dataModel.getTableName(), s.getId(), s.getQuota(), s.getAggregateType(), s.getFormatType(), s.getAlias());
                if (StringUtils.isNotBlank(express)) {
                    list.add(express);
                }
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return "SELECT " + AnalyseUtil.join(",", list.toArray(new String[0]));
    }

    protected String buildFrom(DataModel dataModel) {
        return " FROM " + BuildSqlUtil.from(dataModel.getTableName(), null);
    }

    @Deprecated
    protected String buildWhere(DataModel dataModel) {
        List<String> list = Lists.newArrayList();
        list.add(" 1=1 ");
        for (DataModelField s : dataModel.getX()) {
            String express = BuildSqlUtil.where(dataModel.getTableName(), s.getId(), s.getQuota(), null, s.getSymbol(), s.getValue());
            if (StringUtils.isNotBlank(express)) {
                list.add(express);
            }
        }
        if (CollectionUtils.isNotEmpty(dataModel.getConditions())) {
            for (DataCondition condition : dataModel.getConditions()) {
                String express = BuildSqlUtil.where(dataModel.getTableName(), condition.getId(), condition.getQuota(), condition.getFormatType(), condition.getSymbol(), condition.getValue());
                if (StringUtils.isNotBlank(express)) {
                    list.add(express);
                }
            }
        }

        return " WHERE " + AnalyseUtil.join(" AND ", list.toArray(new String[0]));
    }

    protected String buildGroupBy(DataModel dataModel) {
        List<String> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dataModel.getX())) {
            for (DataModelField s : dataModel.getX()) {
                String express = BuildSqlUtil.groupBy(dataModel.getTableName(), s.getId(), s.getQuota(), s.getFormatType());
                if (StringUtils.isNotBlank(express)) {
                    list.add(express);
                }
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return " GROUP BY " + AnalyseUtil.join(" , ", list.toArray(new String[0]));
    }

    @Deprecated
    protected String buildHaving(DataModel dataModel) {
        List<String> list = Lists.newArrayList();
        for (DataModelField s : dataModel.getX()) {
            String express = BuildSqlUtil.having(dataModel.getTableName(), s.getId(), s.getQuota()
                    , s.getAggregateType(), s.getSymbol(), s.getValue());
            if (StringUtils.isNotBlank(express)) {
                list.add(express);
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return " HAVING " + AnalyseUtil.join(" AND ", list.toArray(new String[0]));
    }

    protected String buildOrder(DataModel dataModel) {
        List<String> list = Lists.newArrayList();
        for (DataModelField s : dataModel.getX()) {
            String express = BuildSqlUtil.orderBy(dataModel.getTableName(), s.getId(), s.getQuota()
                    , s.getAggregateType(), s.getFormatType(), s.getOrderType());
            if (StringUtils.isNotBlank(express)) {
                list.add(express);
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return " ORDER BY " + AnalyseUtil.join(" , ", list.toArray(new String[0]));
    }

    protected String limit(DataModel dataModel) {
        if (null == dataModel.getPage()) {
            return "";
        }
        return " LIMIT " + (dataModel.getPage() - 1) * dataModel.getPageSize() + "," + dataModel.getPage() * dataModel.getPageSize();
    }

    public interface Sql {
        String build();
    }

    public interface Rows {
        List<Map<String, Object>> set(List<Map<String, Object>> list);
    }
}
