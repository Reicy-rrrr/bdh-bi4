package com.deloitte.bdh.data.collation.component.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.model.BiEtlMappingField;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingFieldService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据源组件实现
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("sourceComponent")
public class SourceComponent implements ComponentHandler {

    @Autowired
    private BiEtlMappingConfigService biEtlMappingConfigService;

    @Autowired
    private BiEtlMappingFieldService biEtlMappingFieldService;

    @Autowired
    private DbHandler dbHandler;

    @Override
    public void handle(ComponentModel component) {
        // 查询配置映射（表名）
        LambdaQueryWrapper<BiEtlMappingConfig> configWrapper = new LambdaQueryWrapper();
        configWrapper.eq(BiEtlMappingConfig::getCode, component.getRefMappingCode());
        List<BiEtlMappingConfig> configs = biEtlMappingConfigService.list(configWrapper);
        if (CollectionUtils.isEmpty(configs)) {
            throw new BizException("源表组件配置映射信息不能为空！");
        }
        BiEtlMappingConfig config = configs.get(0);
        // 使用目标表名（落库后的表）
        String tableName = config.getToTableName();
        component.setTableName(tableName);

        // 查询映射字段
        LambdaQueryWrapper<BiEtlMappingField> fieldWrapper = new LambdaQueryWrapper();
        fieldWrapper.eq(BiEtlMappingField::getRefCode, component.getRefMappingCode());
        fieldWrapper.orderByAsc(BiEtlMappingField::getId);
        List<BiEtlMappingField> fields = biEtlMappingFieldService.list(fieldWrapper);
        // 如果映射字段为空，直接查询表结构中的所有字段
        Map<String, String> fieldNames = Maps.newLinkedHashMap();
        if (!CollectionUtils.isEmpty(fields)) {
            fields.forEach(field -> {
                fieldNames.put(field.getFieldName(), field.getFieldDesc());
            });
        } else {
            List<TableColumn> columns = dbHandler.getColumns(tableName);
            columns.forEach(tableColumn -> {
                fieldNames.put(tableColumn.getName(), tableColumn.getDesc());
            });
        }

        List<FieldMappingModel> fieldMappings = Lists.newArrayList();
        Map<String, TableField> columnTypes = getColumnTypes(component.getRefMappingCode());

        for (Map.Entry<String, String> entry : fieldNames.entrySet()) {
            String fieldName = entry.getKey();
            String fieldDesc = entry.getValue();
            // fullName: table.column
            String fullName = tableName + sql_key_separator + fieldName;
            // 使用全名进行编码获取到字段别名（全名可以避免重复）
            String tempName = getColumnAlias(fullName);
            TableField tableField = MapUtils.getObject(columnTypes, fieldName);
            // 字段描述为空时，使用字段名称作为描述
            if (StringUtils.isBlank(fieldDesc)) {
                if (StringUtils.isBlank(tableField.getDesc())) {
                    fieldDesc = fieldName;
                } else {
                    fieldDesc = tableField.getDesc();
                }
            }
            tableField.setDesc(fieldDesc);
            FieldMappingModel mapping = new FieldMappingModel(tempName, fieldName, tableField.getType(), fieldDesc, fieldName,
                    tableName, tableField.getColumnType(), tableField);
            fieldMappings.add(mapping);
        }

        List<String> tempFields = fieldMappings.stream().map(FieldMappingModel::getTempFieldName).collect(Collectors.toList());
        component.setFields(tempFields);
        component.setFieldMappings(fieldMappings);
        buildQuerySql(component);
    }

    /**
     * 初始化sql
     *
     * @param component
     */
    private void buildQuerySql(ComponentModel component) {
        String tableName = component.getTableName();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(sql_key_select);
        List<FieldMappingModel> fieldMappings = component.getFieldMappings();
        fieldMappings.forEach(fieldMapping -> {
            String fullName = fieldMapping.getOriginalTableName()
                    + sql_key_separator + fieldMapping.getOriginalFieldName();
            sqlBuilder.append(fullName);
            sqlBuilder.append(sql_key_blank);
            sqlBuilder.append(sql_key_as);
            sqlBuilder.append(fieldMapping.getTempFieldName());
            sqlBuilder.append(sql_key_comma);
        });
        // 删除SELECT中最后多余的“,”
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(sql_key_comma));
        sqlBuilder.append(sql_key_blank);
        sqlBuilder.append(sql_key_from);
        sqlBuilder.append(tableName);
        component.setQuerySql(sqlBuilder.toString());
    }

    /**
     * 获取字段类型
     *
     * @param mappingCode 映射code
     * @return
     */
    private Map<String, TableField> getColumnTypes(String mappingCode) {
        List<TableField> tableFields = dbHandler.getTargetTableFields(mappingCode);
        if (CollectionUtils.isEmpty(tableFields)) {
            return Maps.newHashMap();
        }

        Map<String, TableField> columnTypes = tableFields.stream()
                .collect(Collectors.toMap(TableField::getName, tableField -> tableField));
        return columnTypes;
    }
}
