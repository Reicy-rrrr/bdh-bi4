package com.deloitte.bdh.data.analyse.sql.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.WildcardEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserData;
import com.deloitte.bdh.data.analyse.model.datamodel.DataCondition;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.service.AnalyseUserDataService;
import com.deloitte.bdh.data.analyse.sql.AbstractAnalyseSql;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import com.deloitte.bdh.data.analyse.utils.BuildSqlUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("analyseLocal")
public class AnalyseLocal extends AbstractAnalyseSql {
    @Resource
    protected BiUiDemoMapper biUiDemoMapper;
    @Resource
    private AnalyseUserDataService userDataService;

    @Override
    protected String select(DataModel model) {
        List<String> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(model.getX())) {
            for (DataModelField s : model.getX()) {
                String express = BuildSqlUtil.select(model.getTableName(), s.getId(), s.getQuota(), s.getAggregateType(),
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
        return " FROM " + BuildSqlUtil.from(model.getTableName(), null);
    }

    @Override
    protected String where(DataModel model) {
        List<String> list = Lists.newArrayList();
        list.add(" 1=1 ");
        for (DataModelField s : model.getX()) {
            String express = BuildSqlUtil.where(model.getTableName(), s.getId(), s.getQuota(), s.getSymbol(), s.getValue());
            if (StringUtils.isNotBlank(express)) {
                list.add(express);
            }
        }
        if (CollectionUtils.isNotEmpty(model.getConditions())) {
            for (DataCondition condition : model.getConditions()) {
                String express = "";
                String value = convertValue(condition.getSymbol(), condition.getValue());
                String symbol = WildcardEnum.get(condition.getSymbol()).getCode();
                if (condition.getId().size() == 1) {
                    express = BuildSqlUtil.where(model.getTableName(), condition.getId().get(0), condition.getQuota(), condition.getFormatType(), symbol, value);
                } else { //针对多个字段连接成一个value值的情况做特殊处理
                    express = connectWhere(model.getTableName(), condition.getId(), condition.getQuota(), symbol, value);
                }
                list.add(express);
            }
        }
        //权限条件
        if (StringUtils.isNotBlank(model.getPageId())) {
            LambdaQueryWrapper<BiUiAnalyseUserData> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BiUiAnalyseUserData::getUserId, ThreadLocalHolder.getOperator());
            lambdaQueryWrapper.eq(BiUiAnalyseUserData::getPageId, model.getPageId());
            lambdaQueryWrapper.eq(BiUiAnalyseUserData::getTenantId, ThreadLocalHolder.getTenantId());
            List<BiUiAnalyseUserData> userDataList = userDataService.list(lambdaQueryWrapper);
            if (CollectionUtils.isNotEmpty(userDataList)) {
                for (BiUiAnalyseUserData userData : userDataList) {
                    String value = convertValue(WildcardEnum.EQ.getKey(), Lists.newArrayList(userData.getFieldValue()));
                    String express = BuildSqlUtil.where(userData.getTableName(), userData.getTableField(), DataModelTypeEnum.WD.getCode(), WildcardEnum.EQ.getCode(), value);
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
            for (DataModelField s : model.getX()) {
                String express = BuildSqlUtil.groupBy(model.getTableName(), s.getId(), s.getQuota(), s.getFormatType(), s.getDataType());
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
            String express = BuildSqlUtil.having(model.getTableName(), s.getId(), s.getQuota()
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
                    String express = BuildSqlUtil.having(model.getTableName(), condition.getId().get(0), condition.getQuota(),
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
            String express = BuildSqlUtil.orderBy(model.getTableName(), s.getId(), s.getQuota()
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
    protected String page(DataModel model) {
        if (null == model.getPage()) {
            return "";
        }
        return " LIMIT " + (model.getPage() - 1) * model.getPageSize() + "," + model.getPageSize();
    }

    @Override
    protected String count(SqlContext context) {
        if (null == context.getModel().getPage()) {
            return null;
        }
        String countSql = null;
        if (StringUtils.isNotBlank(context.getQuerySql())) {
            if (StringUtils.containsIgnoreCase(context.getQuerySql(), "LIMIT")) {
                countSql = StringUtils.substringBefore(countSql, "LIMIT");
            }
            countSql = "SELECT count(0) AS TOTAL FROM (" + countSql + ") TABLE_COUNT";
            return String.valueOf(biUiDemoMapper.selectCount(countSql));
        }
        return null;
    }

    @Override
    protected List<Map<String, Object>> execute(SqlContext context) {
        return biUiDemoMapper.selectDemoList(context.getQuerySql());
    }

    private String connectWhere(String tableName, List<String> fields, String quota, String symbol, String value) {
        if (DataModelTypeEnum.DL.getCode().equals(quota)) {
            return null;
        }
        List<String> expressList = Lists.newArrayList();
        for (String field : fields) {
            String express = "`" + tableName + "`.`" + field + "`";
            expressList.add(express);
        }
        String connectExpress = StringUtils.join(expressList, ",");
        return "CONCAT_WS('-'," + connectExpress + ")" + " " + symbol + " " + value;
    }

    private String convertValue(String symbol, List<String> valueList) {
        List<String> convertValueList = Lists.newArrayList();
        for (String value : valueList) {
            for (String escape : AnalyseConstants.ESCAPE_CHARACTER) {
                if (value.contains(escape)) {
                    value = value.replace(escape, "\\" + escape);
                }
            }
            convertValueList.add(value);
        }
        return WildcardEnum.get(symbol).expression(convertValueList);
    }
}
