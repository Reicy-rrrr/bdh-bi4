package com.deloitte.bdh.data.collation.database.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlDbMapper;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.convertor.DbConvertor;
import com.deloitte.bdh.data.collation.database.convertor.impl.DbConvertorImpl;
import com.deloitte.bdh.data.collation.database.dto.CreateTableDto;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.database.po.TableInfo;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
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

        drop(tableName);
        biEtlDbMapper.createTable(createTableSql);
    }

    @Override
    public void createTable(String dbId, String targetTableName, List<TableField> targetFields) {
        DbContext context = getDbContext(dbId);
        // 转换表字段
        dbConvertor.convertFieldType(targetFields, context);
        String createTableSql = buildCreateTableSql(targetTableName, targetFields, Lists.newArrayList());

        drop(targetTableName);
        biEtlDbMapper.createTable(createTableSql);
    }

    @Override
    public void createTable(String targetTableName, List<TableField> targetFields) throws Exception {
        String createTableSql = buildCreateTableSql(targetTableName, targetFields, Lists.newArrayList());

        drop(targetTableName);
        biEtlDbMapper.createTable(createTableSql);
    }

    @Override
    public List<String> getTableNameList() {
        List<String> results = Lists.newArrayList("ORDERS_USCA_BI", "TEST_CHINESE_ORDER", "TEST_CHINESE_REFUND", "TEST_CHINESE_SALESMAN", "TEST_GLOBAL_ORDER");

        // 查询所有输出组件
        LambdaQueryWrapper<BiComponent> componentQuery = new LambdaQueryWrapper();
        componentQuery.eq(BiComponent::getType, ComponentTypeEnum.OUT.getKey());
        componentQuery.eq(BiComponent::getEffect, EffectEnum.ENABLE.getKey());
        List<BiComponent> components = biComponentService.list(componentQuery);
        if (CollectionUtils.isEmpty(components)) {
            // todo: 待修改为返回空集合
            return results;
        }
        List<String> componentCodes = components.stream().map(BiComponent::getCode).collect(Collectors.toList());
        results.addAll(componentCodes);
        return results;
    }

    @Override
    public List<TableInfo> getTableList() {
        // 设定默认的表信息
        TableInfo defaultTable = new TableInfo("ORDERS_USCA_BI", "ORDERS_USCA_BI");
        TableInfo chineseOrder = new TableInfo("TEST_CHINESE_ORDER", "中国订单");
        TableInfo chineseRefund = new TableInfo("TEST_CHINESE_REFUND", "中国退货");
        TableInfo chineseSalesman = new TableInfo("TEST_CHINESE_SALESMAN", "中国销售员");
        TableInfo globalOrder = new TableInfo("TEST_GLOBAL_ORDER", "世界订单");
        List<TableInfo> results = Lists.newArrayList(defaultTable, chineseOrder, chineseRefund, chineseSalesman, globalOrder);

        // 查询所有输出组件
        LambdaQueryWrapper<BiComponent> componentQuery = new LambdaQueryWrapper();
        componentQuery.eq(BiComponent::getType, ComponentTypeEnum.OUT.getKey());
        componentQuery.eq(BiComponent::getEffect, EffectEnum.ENABLE.getKey());
        List<BiComponent> outComponents = biComponentService.list(componentQuery);
        if (CollectionUtils.isEmpty(outComponents)) {
            return results;
        }
        List<String> componentCodes = outComponents.stream().map(BiComponent::getCode).collect(Collectors.toList());

        // 根据输出组件查询组件参数中的表名(本地表的描述)
        LambdaQueryWrapper<BiComponentParams> paramQuery = new LambdaQueryWrapper();
        paramQuery.in(BiComponentParams::getRefComponentCode, componentCodes);
        paramQuery.eq(BiComponentParams::getParamKey, ComponentCons.TO_TABLE_DESC);
        List<BiComponentParams> params = biComponentParamsService.list(paramQuery);
        Map<String, String> descMap = params.stream().collect(Collectors.toMap(BiComponentParams::getRefComponentCode, param -> param.getParamValue()));
        componentCodes.forEach(componentCode -> {
            String desc = MapUtils.getString(descMap, componentCode);
            if (StringUtils.isBlank(desc)) {
                desc = componentCode;
            }
            results.add(new TableInfo(componentCode, desc));
        });
        return results;
    }

    @Override
    public List<TableColumn> getColumns(String tableName) {
        String querySql = buildQueryColumnsSql(tableName);
        List<Map<String, Object>> results = biEtlDbMapper.selectColumns(querySql);
        return formatTableColumn(results);
    }

    @Override
    public List<TableField> getTableFields(String tableName) {
        String querySql = buildQueryColumnsSql(tableName);
        List<Map<String, Object>> results = biEtlDbMapper.selectColumns(querySql);
        return formatTableField(results);
    }

    @Override
    public long getCount(String tableName, String condition) {
        String querySql = "SELECT COUNT(1) FROM `" + tableName + "`";
        if (StringUtils.isNotBlank(condition)) {
            querySql = querySql + " WHERE " + condition;
        }
        return biEtlDbMapper.selectCount(querySql);
    }

    @Override
    public long truncateTable(String tableName) {
        String truncateSql = "TRUNCATE TABLE `" + tableName + "`";
        return biEtlDbMapper.truncateTable(truncateSql);
    }

    @Override
    public long delete(String tableName, String condition) {
        if (StringUtils.isBlank(condition)) {
            return 0L;
        }
        String deleteSql = "DELETE FROM `" + tableName + "` WHERE " + condition;
        return biEtlDbMapper.truncateTable(deleteSql);
    }

    @Override
    public void drop(String tableName) {
        String deleteSql = "DROP TABLE IF EXISTS `" + tableName + "`";
        biEtlDbMapper.truncateTable(deleteSql);
    }

    @Override
    public void dropFields(String tableName, String... field) {
        for (String args : field) {
            String deleteSql = "ALTER table  `" + tableName + "` DROP " + args;
            biEtlDbMapper.truncateTable(deleteSql);
        }
    }

    @Override
    public boolean isTableExists(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            throw new BizException("Check table exists error: 表名不能为空！");
        }

        String result = biEtlDbMapper.checkTableExists(tableName);
        if (StringUtils.isBlank(result)) {
            return false;
        }
        return true;
    }

    @Override
    public List<Map<String, Object>> executeQuery(String querySql) {
        return biEtlDbMapper.executeQuery(querySql);
    }

    @Override
    public PageInfo<Map<String, Object>> executePageQuery(String querySql, Integer page, Integer size) {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        PageHelper.startPage(page, size);
        return new PageInfo(biEtlDbMapper.executeQuery(querySql));
    }

    @Override
    public long executeInsert(String tableName, List<LinkedHashMap<String, Object>> rows) {
        if (StringUtils.isBlank(tableName)) {
            throw new BizException("DbHandler execute insert error: 表名不能为空！");
        }

        if (!isTableExists(tableName)) {
            throw new BizException("DbHandler execute insert error: 表不存在！");
        }

        if (CollectionUtils.isEmpty(rows)) {
            return 0;
        }
        return biEtlDbMapper.executeInsert(tableName, rows);
    }

    private String buildQueryColumnsSql(String tableName) {
        // 获取数据落地本地的数据库类型：Hive/MySQL
        String localSourceType = "mysql";
        if ("mysql".equals(localSourceType)) {
            return "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE" +
                    " TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME='" + tableName + "' ORDER BY ORDINAL_POSITION";
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
                tableColumn.setType("Text");
                tableColumn.setDataType((String) rowData.get("DATA_TYPE"));
            } else {
                tableColumn.setName((String) rowData.get("col_name"));
                tableColumn.setType("Text");
            }
            result.add(tableColumn);
        });
        return result;
    }

    private List<TableField> formatTableField(List<Map<String, Object>> data) {
        List<TableField> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(data)) {
            return result;
        }

        // 获取数据落地本地的数据库类型(不同类型字段名不一样)：Hive/MySQL
        String localSourceType = "mysql";
        data.forEach(rowData -> {
            TableField tableField = new TableField();
            if ("mysql".equals(localSourceType)) {
                tableField.setName((String) rowData.get("COLUMN_NAME"));
                tableField.setDesc((String) rowData.get("COLUMN_COMMENT"));
                tableField.setType("Text");
                tableField.setDataType((String) rowData.get("DATA_TYPE"));
            } else {
                tableField.setName((String) rowData.get("col_name"));
                tableField.setType("Text");
            }
            result.add(tableField);
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
        if (CollectionUtils.isEmpty(allFields)) {
            throw new RuntimeException("建表字段不能为空");
        }
        List<TableField> newTableField = Lists.newArrayList();
        for (TableField tableField : allFields) {
            newTableField.add(tableField.clone());
        }

        DbConvertor convertor = new DbConvertorImpl();
        convertor.mysqlSchemaAdapter(newTableField);

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
        for (int index = 0; index < newTableField.size(); index++) {
            TableField field = newTableField.get(index);
            String fieldName = field.getName();
            String columnType = field.getColumnType();

            if (!CollectionUtils.isEmpty(targetColumns) && !targetColumns.contains(fieldName)) {
                continue;
            }
            sqlBuilder.append(fieldName).append(" ").append(columnType);
            if (StringUtils.isNotBlank(field.getDesc())) {
                sqlBuilder.append(" COMMENT '").append(field.getDesc()).append("'");
            }
            sqlBuilder.append(",");
        }
        // 删除SELECT中最后多余的“,”
        if (sqlBuilder.lastIndexOf(",") == (sqlBuilder.length() - 1)) {
            sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(","));
        }
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    @Override
    public DbContext getDbContext(String dbId) {
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
            // 初始化字段的备注：如果没有备注，使用字段名称作为备注
            columns.forEach(tableField -> {
                if (StringUtils.isBlank(tableField.getDesc())) {
                    tableField.setDesc(tableField.getName());
                }
            });
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
        String querySql = "SELECT COUNT(1) FROM (" + query + " ) count_temp";
        return biEtlDbMapper.selectCount(querySql);
    }

    private List<TableField> getTableFieldFromMysql(List<Map<String, Object>> results) {
        List<TableField> columns = Lists.newArrayList();
        results.forEach(columnMap -> {
            TableField field = new TableField();
            field.setName(MapUtils.getString(columnMap, "COLUMN_NAME"));

            if (StringUtils.isBlank(MapUtils.getString(columnMap, "COLUMN_COMMENT"))) {
                field.setDesc(field.getName());
            } else {
                field.setDesc(MapUtils.getString(columnMap, "COLUMN_COMMENT"));
            }
            field.setDataType(MapUtils.getString(columnMap, "DATA_TYPE"));
            // 将mysql数据类型转换成本系统的数据类型
            MysqlDataTypeEnum dataType = MysqlDataTypeEnum.values(field.getDataType().toLowerCase());
            field.setType(dataType.getValue().getType());
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
            field.setDesc(field.getName());
            field.setType("Text");
            field.setColumnType(MapUtils.getString(columnMap, "data_type"));
            columns.add(field);
        });
        return columns;
    }
}
