package com.deloitte.bdh.data.analyse.sql.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.enums.WildcardEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserData;
import com.deloitte.bdh.data.analyse.model.datamodel.DataCondition;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.sql.AbstractRela;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import com.deloitte.bdh.data.analyse.sql.utils.SqlserverBuildUtil;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
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
@Service("analysesqlserver")
public class AnalyseSqlServer extends AbstractRela {
    @Resource
    private DbSelector dbSelector;

    @Override
    protected String assemblyQuerySql(SqlContext context) {
        DataModel model = context.getModel();
        String select = this.select(model);
        String from = this.from(model);
        String where = this.where(model);
        String groupBy = this.groupBy(model);
        String having = this.having(model);
        String orderBy = this.orderBy(model);
        String page = this.page(context);
        if (StringUtils.isNotBlank(page)) {
            return page.replace("SQL", compatibleOrderBy(StringUtils.join(select, from, where, groupBy, having, orderBy)));
        }
        return StringUtils.join(select, from, where, groupBy, having, orderBy);
    }

    @Override
    protected String select(DataModel model) {
        List<String> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(model.getX())) {
            for (DataModelField s : model.getX()) {
                String express = SqlserverBuildUtil.select(model.getTableName(), s.getId(), s.getQuota(), s.getAggregateType(),
                        s.getFormatType(), s.getDataType(), s.getDataUnit(), s.getPrecision(), s.getAlias(), s.getDefaultValue());
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

    @Override
    protected String from(DataModel model) {
        return " FROM " + SqlserverBuildUtil.from(model.getTableName(), null);
    }

    @Override
    protected String where(DataModel model) {
        List<String> list = Lists.newArrayList();
        list.add(" 1=1 ");
        for (DataModelField s : model.getX()) {
            String express = SqlserverBuildUtil.where(model.getTableName(), s.getId(), s.getQuota(), s.getSymbol(), s.getValue());
            if (StringUtils.isNotBlank(express)) {
                list.add(express);
            }
        }
        if (CollectionUtils.isNotEmpty(model.getConditions())) {
            for (DataCondition condition : model.getConditions()) {
                String express = "";
                String value = convertValue(condition.getSymbol(), condition.getValue());
                String symbol = WildcardEnum.get(condition.getSymbol()).getCode();
                express = SqlserverBuildUtil.where(model.getTableName(), condition.getId().get(0), condition.getQuota(), condition.getFormatType(), symbol, value);
                list.add(express);
            }
        }
        //权限条件
        if (StringUtils.isNotBlank(model.getPageId())) {
            LambdaQueryWrapper<BiUiAnalyseUserData> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BiUiAnalyseUserData::getUserId, ThreadLocalHolder.getOperator());
            lambdaQueryWrapper.eq(BiUiAnalyseUserData::getPageId, model.getPageId());
            lambdaQueryWrapper.eq(BiUiAnalyseUserData::getComponentId, model.getComponentId());
            lambdaQueryWrapper.eq(BiUiAnalyseUserData::getTenantId, ThreadLocalHolder.getTenantId());
            List<BiUiAnalyseUserData> userDataList = userDataService.list(lambdaQueryWrapper);
            if (CollectionUtils.isNotEmpty(userDataList)) {
                for (BiUiAnalyseUserData userData : userDataList) {
                    String value = convertValue(WildcardEnum.EQ.getKey(), Lists.newArrayList(userData.getFieldValue()));
                    String express = SqlserverBuildUtil.where(userData.getTableName(), userData.getTableField(), DataModelTypeEnum.WD.getCode(), WildcardEnum.EQ.getCode(), value);
                    if (StringUtils.isNotBlank(express)) {
                        list.add(express);
                    }
                }
            }
        }

        return " WHERE " + AnalyseUtil.join(" AND ", list.toArray(new String[0]));
    }

    @Override
    protected String groupBy(DataModel model) {
        List<String> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(model.getX())) {
            boolean needGroup = needGroup(model);
            for (DataModelField s : model.getX()) {
                String express = SqlserverBuildUtil.groupBy(model.getTableName(), s.getId(), s.getQuota()
                        , s.getFormatType(), s.getDataType(), needGroup || s.isNeedGroup());
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

    @Override
    protected String having(DataModel model) {
        List<String> list = Lists.newArrayList();
        for (DataModelField s : model.getX()) {
            String express = SqlserverBuildUtil.having(model.getTableName(), s.getId(), s.getQuota()
                    , s.getAggregateType(), s.getSymbol(), s.getValue());
            if (StringUtils.isNotBlank(express)) {
                list.add(express);
            }
        }
        if (CollectionUtils.isNotEmpty(model.getConditions())) {
            for (DataCondition condition : model.getConditions()) {
                String value = convertValue(condition.getSymbol(), condition.getValue());
                String symbol = WildcardEnum.get(condition.getSymbol()).getCode();
                if (StringUtils.equals(condition.getQuota(), DataModelTypeEnum.DL.getCode()) &&
                        StringUtils.isNotBlank(condition.getAggregateType())) {
                    String express = SqlserverBuildUtil.having(model.getTableName(), condition.getId().get(0), condition.getQuota(),
                            condition.getAggregateType(), symbol, value);
                    list.add(express);
                }

            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return " HAVING " + AnalyseUtil.join(" AND ", list.toArray(new String[0]));
    }

    @Override
    protected String orderBy(DataModel model) {
        List<String> list = Lists.newArrayList();
        for (DataModelField s : model.getX()) {
            String express = SqlserverBuildUtil.orderBy(model.getTableName(), s.getId(), s.getQuota()
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

    @Override
    protected String page(SqlContext context) {
        DataModel model = context.getModel();
        if (null == model.getPage()) {
            return "";
        }
        return "SELECT * FROM (SELECT * , (ROW_NUMBER() OVER(ORDER BY @@SERVERNAME)-1)/" + model.getPageSize() + " AS TEMP_NUM FROM (\n" +
                "        SQL\n" +
                ") temp1) temp2 WHERE TEMP_NUM = " + model.getPage() + "-1";
    }

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
                countSql = "SELECT count(1) AS TOTAL FROM (" + compatibleOrderBy(countSql) + ") TABLE_COUNT";
                context.setQuerySql(countSql);
                List<Map<String, Object>> result = expand(context);
                if (CollectionUtils.isNotEmpty(result)) {
                    return ((Integer) result.get(0).get("TOTAL")).longValue();
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
            List<Map<String, Object>> list = dbSelector.executeQuery(dbContext);
            if (CollectionUtils.isNotEmpty(list)) {
                for (Map<String, Object> map : list) {
                    map.remove("TEMP_NUM");
                }
            }
            return list;
        } catch (Exception e) {
            log.error("执行异常:", e);
            throw new RuntimeException("执行SQL异常");
        }
    }

    @Override
    protected List<Map<String, Object>> expand(SqlContext context) {
        DbContext dbContext = new DbContext();
        dbContext.setDbId(context.getDbId());
        dbContext.setQuerySql(context.getQuerySql());
        try {
            return dbSelector.executeQuery(dbContext);
        } catch (Exception e) {
            log.error("执行异常:", e);
            throw new BizException(ResourceMessageEnum.EXECUTE_SQL_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXECUTE_SQL_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    private String convertValue(String symbol, List<String> valueList) {
        List<String> convertValueList = Lists.newArrayList();
        for (String value : valueList) {
            for (String escape : SqlserverBuildUtil.ESCAPE_CHARACTER) {
                if (value.contains(escape)) {
                    value = value.replace(escape, "'" + escape);
                }
            }
            convertValueList.add(value);
        }
        return WildcardEnum.get(symbol).expression(convertValueList);
    }

    private String compatibleOrderBy(String sql) {
        if (StringUtils.isBlank(sql)) {
            return sql;
        }
        if (!sql.contains("ORDER BY")) {
            return sql;
        }
        return sql.replace("SELECT ", "SELECT TOP 99.999999999999 percent");
    }
}
