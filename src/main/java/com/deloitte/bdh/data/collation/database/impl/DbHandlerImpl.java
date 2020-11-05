package com.deloitte.bdh.data.collation.database.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
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
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.service.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
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
@Slf4j
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

    @Autowired
    private BiEtlMappingConfigService biEtlMappingConfigService;

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
        String deleteSql = "DROP TABLE " + tableName;
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
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
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

    @Override
    public List<TableField> getTargetTableFields(String mappingConfigCode) {
        if (StringUtils.isBlank(mappingConfigCode)) {
            throw new BizException("映射配置code不能为空！");
        }
        LambdaQueryWrapper<BiEtlMappingConfig> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(BiEtlMappingConfig::getCode, mappingConfigCode);
        BiEtlMappingConfig mappingConfig = biEtlMappingConfigService.getOne(queryWrapper);
        if (mappingConfig == null) {
            throw new BizException("数据源组件配置信息未查询到！");
        }

        // 根据同步方式处理：直连需要根据数据源查询，其他的直接在本地库查询
        String dbId = mappingConfig.getRefSourceId();
        SyncTypeEnum syncType = SyncTypeEnum.getEnumByKey(mappingConfig.getType());
        String tableName = null;
        if (SyncTypeEnum.DIRECT.equals(syncType)) {
            tableName = mappingConfig.getFromTableName();
            DbContext context = new DbContext();
            context.setDbId(dbId);
            context.setTableName(tableName);
            TableSchema tableSchema = null;
            try {
                tableSchema = dbSelector.getTableSchema(context);
            } catch (Exception e) {
                log.error("查询远程数据表字段信息错误", e);
                throw new BizException("数据源组件配置信息未查询到！");
            }
            List<TableField> columns = tableSchema.getColumns();
            dbConvertor.convertFieldType(columns, context);
            return columns;
        }

        tableName = mappingConfig.getToTableName();
        String querySql = buildQueryColumnsSql(tableName);
        List<Map<String, Object>> results = biEtlDbMapper.selectColumns(querySql);
        // TODO： 查询当前落地库为mysql？hive？，根据类型获取字段类型
        String localDbType = "mysql";
        if ("mysql".equals(localDbType)) {
            return getTableFieldFromMysql(results);
        } else {
            return getTableFieldFromHive(results, tableName);
        }
    }

    @Override
    public long getCountLocal(String query) {
        String querySql = "SELECT COUNT(1) FROM (" + query + " )";
        return biEtlDbMapper.selectCount(querySql);
    }

    private List<TableField> getTableFieldFromMysql(List<Map<String, Object>> results) {
        List<TableField> columns = Lists.newArrayList();
        results.forEach(columnMap -> {
            TableField field = new TableField();
            field.setName(MapUtils.getString(columnMap, "COLUMN_NAME"));
            field.setType("String");
            field.setDesc(MapUtils.getString(columnMap, "COLUMN_COMMENT"));
            field.setDataType(MapUtils.getString(columnMap, "DATA_TYPE"));
            field.setColumnType(MapUtils.getString(columnMap, "COLUMN_TYPE"));
            columns.add(field);
        });
        return columns;
    }

    private List<TableField> getTableFieldFromHive(List<Map<String, Object>> results, String tableName) {
        List<TableField> columns = Lists.newArrayList();
        results.forEach(columnMap -> {
            TableField field = new TableField();
            field.setName(MapUtils.getString(columnMap, "col_name")
                    .replace(tableName + ".", ""));
            field.setType("String");
            field.setColumnType(MapUtils.getString(columnMap, "data_type"));
            columns.add(field);
        });
        return columns;
    }
}
