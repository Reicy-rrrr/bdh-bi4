package com.deloitte.bdh.data.analyse.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import com.deloitte.bdh.data.collation.enums.DataSetTypeEnum;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DataSourceSelectionImpl implements DataSourceSelection {
    private static final String ANALYSE_PREFIX = "analyse";

    @Resource
    private BiDataSetService dataSetService;
    @Resource
    private BiEtlDatabaseInfService databaseInfService;

    @Override
    public AnalyseSql getBean(DataModel model) {
        BiDataSet dataSet = checkDataSet(model);
        return this.getBean(dataSet);
    }

    @Override
    public AnalyseSql getBean(BiDataSet dataSet) {
        AnalyseSql bean;
        DataSetTypeEnum typeEnum = DataSetTypeEnum.getEnumByKey(dataSet.getType());
        switch (typeEnum) {
            case DEFAULT:
            case MODEL:
                //本地
                bean = SpringUtil.getBean(ANALYSE_PREFIX + "Local", AnalyseSql.class);
                break;
            default:
                //获取数据源类型
                BiEtlDatabaseInf databaseInf = databaseInfService.getById(dataSet.getRefSourceId());
                if (null == databaseInf) {
                    throw new RuntimeException("未找到数据源目标对象");
                }
                SourceTypeEnum sourceTypeEnum = SourceTypeEnum.values(databaseInf.getType());
                switch (sourceTypeEnum) {
                    case Mysql:
                    case Oracle:
                    case SQLServer:
                    case Hana:
                        bean = SpringUtil.getBean(ANALYSE_PREFIX + sourceTypeEnum.getTypeName(), AnalyseSql.class);
                        break;
                    case File_Excel:
                    case File_Csv:
                        bean = SpringUtil.getBean(ANALYSE_PREFIX + "Local", AnalyseSql.class);
                        break;
                    default:
                        throw new RuntimeException("数据集不支持的类型");
                }

        }
        return bean;
    }

    @Override
    public String buildSql(DataModel model) {
        BiDataSet dataSet = checkDataSet(model);
        AnalyseSql bean = this.getBean(dataSet);
        model.setTableName(dataSet.getTableName());

        SqlContext context = new SqlContext();
        context.setModel(model);
        context.setMethod(AnalyseSql.Method.ASSEMBLYQUERYSQL);
        String sql = (String) bean.assembly(context);
        model.setTableName(dataSet.getTableDesc());
        return sql;
    }

    @Override
    public Long getCount(DataModel model) {
        BiDataSet dataSet = checkDataSet(model);
        AnalyseSql bean = this.getBean(dataSet);
        model.setTableName(dataSet.getTableName());

        SqlContext context = new SqlContext();
        context.setModel(model);
        context.setMethod(AnalyseSql.Method.COUNT);
        context.setDbId(dataSet.getRefSourceId());
        Object result = bean.assembly(context);
        if (null == result) {
            return null;
        }
        model.setTableName(dataSet.getTableDesc());
        return (Long) result;
    }

    @Override
    public List<Map<String, Object>> execute(DataModel model, String querySql) {
        return execute(model, querySql, AnalyseSql.Method.EXECUTE);
    }

    @Override
    public List<Map<String, Object>> customizeExecute(DataModel model, String querySql) {
        return execute(model, querySql, AnalyseSql.Method.CUSTOMIZE_EXECUTE);
    }

    private List<Map<String, Object>> execute(DataModel model, String querySql, AnalyseSql.Method method) {
        BiDataSet dataSet = checkDataSet(model);
        AnalyseSql sql = this.getBean(dataSet);
        SqlContext context = new SqlContext();
        context.setModel(model);
        context.setMethod(method);
        context.setQuerySql(querySql);
        context.setDbId(dataSet.getRefSourceId());
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

}
