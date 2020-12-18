package com.deloitte.bdh.data.analyse.sql.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.WildcardEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserData;
import com.deloitte.bdh.data.analyse.model.datamodel.DataCondition;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.sql.AbstractRela;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import com.deloitte.bdh.data.analyse.sql.utils.OracleBuildUtil;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("analyseoracle")
public class AnalyseOracle extends AbstractRela {
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
        return StringUtils.join(select, from, where, groupBy, having, orderBy);
    }

    @Override
    protected String select(DataModel model) {
        List<String> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(model.getX())) {
            for (DataModelField s : model.getX()) {
                String express = OracleBuildUtil.select(model.getTableName(), s.getId(), s.getQuota(), s.getAggregateType(),
                        s.getFormatType(), s.getDataType(), s.getPrecision(), s.getAlias(), s.getDefaultValue());
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
        return " FROM " + OracleBuildUtil.from(model.getTableName(), null);
    }

    @Override
    protected String where(DataModel model) {
        List<String> list = Lists.newArrayList();
        list.add(" 1=1 ");
        for (DataModelField s : model.getX()) {
            String express = OracleBuildUtil.where(model.getTableName(), s.getId(), s.getQuota(), s.getSymbol(), s.getValue());
            if (StringUtils.isNotBlank(express)) {
                list.add(express);
            }
        }
        if (CollectionUtils.isNotEmpty(model.getConditions())) {
            for (DataCondition condition : model.getConditions()) {
                String express = "";
                String value = convertValue(condition.getSymbol(), condition.getValue());
                String symbol = WildcardEnum.get(condition.getSymbol()).getCode();
                express = OracleBuildUtil.where(model.getTableName(), condition.getId().get(0), condition.getQuota(), condition.getFormatType(), symbol, value);
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
                    String express = OracleBuildUtil.where(userData.getTableName(), userData.getTableField(), DataModelTypeEnum.WD.getCode(), WildcardEnum.EQ.getCode(), value);
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
                String express = OracleBuildUtil.groupBy(model.getTableName(), s.getId(), s.getQuota()
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
            String express = OracleBuildUtil.having(model.getTableName(), s.getId(), s.getQuota()
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
                    String express = OracleBuildUtil.having(model.getTableName(), condition.getId().get(0), condition.getQuota(),
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
            String express = OracleBuildUtil.orderBy(model.getTableName(), s.getId(), s.getQuota()
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
        return null;
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
                countSql = "SELECT count(1) AS TOTAL FROM (" + countSql + ") TABLE_COUNT";
                context.setQuerySql(countSql);
                List<Map<String, Object>> result = expand(context);
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
            //判断是否分页
            if (null != context.getModel().getPage()) {
                dbContext.setPage(context.getModel().getPage());
                dbContext.setSize(context.getModel().getPageSize());
                PageInfo<Map<String, Object>> pageInfo = dbSelector.executePageQuery(dbContext);
                if (null == pageInfo) {
                    return null;
                }
                return pageInfo.getList();
            }
            return dbSelector.executeQuery(dbContext);
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
            throw new RuntimeException("执行SQL异常");
        }
    }

    private String convertValue(String symbol, List<String> valueList) {
        List<String> convertValueList = Lists.newArrayList();
        for (String value : valueList) {
            for (String escape : OracleBuildUtil.ESCAPE_CHARACTER) {
                if (value.contains(escape)) {
                    value = value.replace(escape, "'" + escape);
                }
            }
            convertValueList.add(value);
        }
        return WildcardEnum.get(symbol).expression(convertValueList);
    }
}
