package com.deloitte.bdh.data.collation.database.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlDbMapper;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.convertor.DbConvertor;
import com.deloitte.bdh.data.collation.database.dto.CreateTableDto;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.service.BiComponentParamsService;
import com.deloitte.bdh.data.collation.service.BiComponentService;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库处理器实现
 *
 * @author chenghzhang
 * @date 2020/10/27
 */
@Service
@DS(DSConstant.BI_DB)
public class DbHandlerImpl implements DbHandler {

    @Autowired
    private DbConvertor dbConvertor;

    @Autowired
    private DbSelector dbSelector;

    @Resource
    private BiEtlDbMapper biEtlDbMapper;

    @Autowired
    private BiEtlDatabaseInfService biEtlDatabaseInfService;

    @Autowired
    private BiEtlModelService biEtlModelService;

    @Autowired
    private BiComponentService biComponentService;

    @Autowired
    private BiComponentParamsService biComponentParamsService;


    @Override
    public void createTable(CreateTableDto dto) throws Exception {
        String dbId = dto.getDbId();
        DbContext context = getDbContext(dbId);
        context.setTableName(dto.getSourceTableName());
        // 查询表结构
        TableSchema tableSchema = dbSelector.getTableSchema(context);
        // 转换表字段
        List<TableField> allFields = tableSchema.getColumns();
        dbConvertor.convertFieldType(allFields, context);
        String tableName = dto.getTargetTableName();

        List<String> targetColumns = dto.getFields();
        String createTableSql = buildCreateTableSql(tableName, allFields, targetColumns);
        biEtlDbMapper.createTable(createTableSql);
    }

    @Override
    public void createTable(String dbId, String targetTableName, List<TableField> targetFields) throws Exception {
        DbContext context = getDbContext(dbId);
        // 转换表字段
        dbConvertor.convertFieldType(targetFields, context);
        String createTableSql = buildCreateTableSql(targetTableName, targetFields, Lists.newArrayList());
        biEtlDbMapper.createTable(createTableSql);
    }

    @Override
    public void createTable(String targetTableName, List<TableField> targetFields) throws Exception {
        String createTableSql = buildCreateTableSql(targetTableName, targetFields, Lists.newArrayList());
        biEtlDbMapper.createTable(createTableSql);
    }

    @Override
    public List<String> getTables() {
        // TODO: 暂无数据，临时表测试
        if (true) {
            return Lists.newArrayList("ORDERS_USCA_BI");
        }
        // 查询当前租户下面所有模板
        List<BiEtlModel> models = biEtlModelService.list();
        if (CollectionUtils.isEmpty(models)) {
            return Lists.newArrayList();
        }
        List<String> modelCodes = models.stream().map(BiEtlModel::getCode).collect(Collectors.toList());

        // 查询所有模板下面的输出组件
        LambdaQueryWrapper<BiComponent> componentQuery = new LambdaQueryWrapper();
        componentQuery.in(BiComponent::getRefModelCode, modelCodes);
        componentQuery.eq(BiComponent::getType, ComponentTypeEnum.OUT.getKey());
        List<BiComponent> components = biComponentService.list(componentQuery);
        if (CollectionUtils.isEmpty(components)) {
            return Lists.newArrayList();
        }
        List<String> componentCodes = components.stream().map(BiComponent::getCode).collect(Collectors.toList());

        // 根据输出组件查询组件参数中的表名
        LambdaQueryWrapper<BiComponentParams> paramQuery = new LambdaQueryWrapper();
        paramQuery.in(BiComponentParams::getCode, componentCodes);
        // todo:需要修改成根据常量定义的参数查询
        paramQuery.eq(BiComponentParams::getParamKey, "table");
        List<BiComponentParams> params = biComponentParamsService.list(paramQuery);
        if (CollectionUtils.isEmpty(params)) {
            return Lists.newArrayList();
        }

        List<String> tableNames = params.stream().map(BiComponentParams::getParamValue).collect(Collectors.toList());
        return tableNames;
    }

    @Override
    public List<TableColumn> getColumns(String tableName) {
        String querySql = buildQueryColumnsSql(tableName);
        List<Map<String, Object>> results = biEtlDbMapper.selectColumns(querySql);
        return formatTableColumn(results);
    }

    @Override
    public long getCount(String tableName, String condition) {
        String querySql = "SELECT COUNT(1) FROM " + tableName;
        if (StringUtils.isNotBlank(condition)) {
            querySql = querySql + " WHERE " + condition;
        }
        return biEtlDbMapper.selectCount(querySql);
    }

    @Override
    public long truncateTable(String tableName) {
        String truncateSql = "TRUNCATE TABLE " + tableName;
        return biEtlDbMapper.truncateTable(truncateSql);
    }

    @Override
    public long delete(String tableName, String condition) {
        if (StringUtils.isBlank(condition)) {
            return 0L;
        }
        String deleteSql = "DELETE FROM " + tableName + " WHERE " + condition;
        return biEtlDbMapper.truncateTable(deleteSql);
    }

    @Override
    public void drop(String tableName) {
        String deleteSql = "DROP FROM " + tableName;
        biEtlDbMapper.truncateTable(deleteSql);
    }

    @Override
    public List<Map<String, Object>> executeQuery(String querySql) {
        return biEtlDbMapper.executeQuery(querySql);
    }

    private String buildQueryColumnsSql(String tableName) {
        // 获取数据落地本地的数据库类型：Hive/MySQL
        String localSourceType = "mysql";
        if ("mysql".equals(localSourceType)) {
            return "select * from information_schema.COLUMNS where" +
                    " TABLE_SCHEMA = (select database()) and TABLE_NAME='" + tableName + "'";
        }
        return "desc " + tableName;
    }

    private List<TableColumn> formatTableColumn(List<Map<String, Object>> data) {
        List<TableColumn> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(data)) {
            return result;
        }

        // 获取数据落地本地的数据库类型(不同类型字段名不一样)：Hive/MySQL
        String localSourceType = "mysql";
        data.forEach(rowData -> {
            TableColumn tableColumn = new TableColumn();
            if ("mysql".equals(localSourceType)) {
                tableColumn.setName((String) rowData.get("COLUMN_NAME"));
                tableColumn.setDesc((String) rowData.get("COLUMN_COMMENT"));
                tableColumn.setType("String");
                tableColumn.setDataType((String) rowData.get("DATA_TYPE"));
            } else {
                tableColumn.setName((String) rowData.get("col_name"));
                tableColumn.setType("String");
            }
            result.add(tableColumn);
        });
        return result;
    }

    /**
     * 组装建表SQL
     * 如果targetColumns不为空，表字段以targetColumns为准，
     * 如果targetColumns为空，建表字段为全量
     *
     * @param tableName     表名
     * @param allFields     源数据表字段
     * @param targetColumns 目标表字段
     * @return
     */
    private String buildCreateTableSql(String tableName, List<TableField> allFields, List<String> targetColumns) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE ").append(tableName).append("(");
        for (int index = 0; index < allFields.size(); index++) {
            TableField field = allFields.get(index);
            String fieldName = field.getName();
            String columnType = field.getColumnType();

            if (!CollectionUtils.isEmpty(targetColumns) && !targetColumns.contains(fieldName)) {
                continue;
            }
            sqlBuilder.append(fieldName).append(" ").append(columnType).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(","));
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    private DbContext getDbContext(String dbId) {
        DbContext context = new DbContext();
        BiEtlDatabaseInf inf = biEtlDatabaseInfService.getById(dbId);
        context.setDbId(dbId);
        context.setSourceTypeEnum(SourceTypeEnum.values(inf.getType()));
        if (!context.getSourceTypeEnum().equals(SourceTypeEnum.File_Csv)
                && !context.getSourceTypeEnum().equals(SourceTypeEnum.File_Excel)) {
            String url = NifiProcessUtil.getDbUrl(inf.getType(), inf.getAddress(), inf.getPort(), inf.getDbName());
            context.setDbUrl(url);
        }
        context.setDbUserName(inf.getDbUser());
        context.setDbPassword(inf.getDbPassword());
        context.setDriverName(inf.getDriverName());
        context.setDbName(inf.getDbName());
        return context;
    }
}
