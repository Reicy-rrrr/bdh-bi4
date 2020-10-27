package com.deloitte.bdh.data.collation.database.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlDbMapper;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.convertor.DbConvertor;
import com.deloitte.bdh.data.collation.database.dto.CreateTableDto;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.vo.TableField;
import com.deloitte.bdh.data.collation.database.vo.TableSchema;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

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
        String createTableSql = buildCreateSql(tableName, allFields, targetColumns);
        biEtlDbMapper.createTable(createTableSql);
    }

    @Override
    public void createTable(String dbId, String targetTableName, List<TableField> targetFields) throws Exception {
        DbContext context = getDbContext(dbId);
        // 转换表字段
        dbConvertor.convertFieldType(targetFields, context);
        String createTableSql = buildCreateSql(targetTableName, targetFields, Lists.newArrayList());
        biEtlDbMapper.createTable(createTableSql);
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
    private String buildCreateSql(String tableName, List<TableField> allFields, List<String> targetColumns) {
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
