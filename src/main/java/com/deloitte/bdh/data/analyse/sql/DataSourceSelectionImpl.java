package com.deloitte.bdh.data.analyse.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import com.deloitte.bdh.data.collation.enums.DataSetTypeEnum;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DataSourceSelectionImpl implements DataSourceSelection {
    private static final String ANALYSE_PREFIX = "analyse";

    @Resource
    private BiDataSetService dataSetService;


    @Override
    public AnalyseSql getBean(DataModel model) {
        AnalyseSql bean = null;
        DataSetTypeEnum typeEnum = DataSetTypeEnum.getEnumByKey(checkDataSet(model).getType());
        switch (typeEnum) {
            case DEFAULT:
            case MODEL:
                //本地
                bean = SpringUtil.getBean(ANALYSE_PREFIX + "Local", AnalyseSql.class);
                break;
            default:
        }
        return bean;
    }

    @Override
    public String buildSql(DataModel model) {
        AnalyseSql bean = this.getBean(model);
        BiDataSet dataSet = checkDataSet(model);
        model.setTableName(dataSet.getTableName());
        DataSetTypeEnum typeEnum = DataSetTypeEnum.getEnumByKey(dataSet.getType());

        String result = null;
        switch (typeEnum) {
            case DEFAULT:
            case MODEL:
                result = buildSelect(model, bean)
                        + buildFrom(model, bean)
                        + buildWhere(model, bean)
                        + buildGroupBy(model, bean)
                        + buildHaving(model, bean)
                        + buildOrder(model, bean)
                        + page(model, bean);
                break;
            default:
        }
        model.setTableName(dataSet.getTableDesc());
        return result;
    }

    @Override
    public Long getCount(DataModel model) {
        AnalyseSql bean = this.getBean(model);
        BiDataSet dataSet = checkDataSet(model);
        model.setTableName(dataSet.getTableName());
        DataSetTypeEnum typeEnum = DataSetTypeEnum.getEnumByKey(dataSet.getType());

        String querySql = null;
        switch (typeEnum) {
            case DEFAULT:
            case MODEL:
                querySql = buildSelect(model, bean)
                        + buildFrom(model, bean)
                        + buildWhere(model, bean)
                        + buildGroupBy(model, bean)
                        + buildHaving(model, bean)
                ;
                break;
            default:
        }
        Long total = buildCount(model, bean, querySql);
        model.setTableName(dataSet.getTableDesc());
        return total;
    }

    @Override
    public List<Map<String, Object>> execute(DataModel model, String querySql) {
        AnalyseSql sql = this.getBean(model);
        SqlContext context = new SqlContext();
        context.setModel(model);
        context.setMethod(AnalyseSql.Method.EXECUT);
        context.setQuerySql(querySql);
        Object result = sql.assembly(context);
        if (null == result) {
            return null;
        }
        return (List<Map<String, Object>>) result;
    }

    private BiDataSet checkDataSet(DataModel model) {
        BiDataSet dataSet = dataSetService.getOne(new LambdaQueryWrapper<BiDataSet>()
                .eq(BiDataSet::getTableDesc, model.getTableName()));

        if (null == dataSet) {
            throw new RuntimeException("未在数据集找到目标对象");
        }
        return dataSet;
    }

    private String buildSelect(DataModel dataModel, AnalyseSql bean) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.SELECT);
        return String.valueOf(bean.assembly(context));
    }

    private String buildFrom(DataModel dataModel, AnalyseSql bean) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.FROM);
        return String.valueOf(bean.assembly(context));
    }

    private String buildWhere(DataModel dataModel, AnalyseSql bean) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.WHERE);
        return String.valueOf(bean.assembly(context));
    }

    private String buildGroupBy(DataModel dataModel, AnalyseSql bean) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.GROUPBY);
        return String.valueOf(bean.assembly(context));
    }

    @Deprecated
    private String buildHaving(DataModel dataModel, AnalyseSql bean) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.HAVING);
        return String.valueOf(bean.assembly(context));
    }

    private String buildOrder(DataModel dataModel, AnalyseSql bean) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.ORDERBY);
        return String.valueOf(bean.assembly(context));
    }

    private String page(DataModel dataModel, AnalyseSql bean) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setMethod(AnalyseSql.Method.PAGE);
        return String.valueOf(bean.assembly(context));

    }

    private Long buildCount(DataModel dataModel, AnalyseSql bean, String querySql) {
        SqlContext context = new SqlContext();
        context.setModel(dataModel);
        context.setQuerySql(querySql);
        context.setMethod(AnalyseSql.Method.COUNT);
        Object result = bean.assembly(context);
        if (null == result) {
            return null;
        }
        return Long.parseLong((String) result);
    }
}
