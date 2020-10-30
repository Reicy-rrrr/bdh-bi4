package com.deloitte.bdh.data.collation.component.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiEtlMappingField;
import com.deloitte.bdh.data.collation.service.BiEtlMappingFieldService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 输出组件实现
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("outComponent")
public class OutComponent implements ComponentHandler {
    /**
     * 输出目标表名
     */
    private static final String param_key_target_table = ComponentCons.TO_TABLE_NAME;

    @Autowired
    private BiEtlMappingFieldService biEtlMappingFieldService;

    @Override
    public void handle(ComponentModel component) {
        String componentCode = component.getCode();
        List<ComponentModel> fromComponents = component.getFrom();
        if (CollectionUtils.isEmpty(fromComponents)) {
            log.error("组件[{}]未查询到上层组件，处理失败！", componentCode);
            throw new BizException("输出组件不能单独存在，处理失败！");
        }

        if (fromComponents.size() > 1) {
            log.error("组件[{}]查询到[{}]个上层组件，处理失败！", componentCode, fromComponents.size());
            throw new BizException("输出组件只能有一个上层组件，处理失败！");
        }

        component.setTableName(component.getCode());
        initFields(component);
        String targetTableName = initTargetTableName(component);
        buildQuerySql(component);
        buildCreateSql(component, targetTableName);
        buildInsertSql(component, targetTableName);
    }

    /**
     * 组装输出组件的最终查询sql语句
     * SELECT xxx, yyy, ... FROM ()
     *
     * @param component 当前输出组件模型
     */
    private void buildQuerySql(ComponentModel component) {
        StringBuilder sqlBuilder = new StringBuilder(sql_key_select);
        component.getFieldMappings().forEach(fieldMapping -> {
            sqlBuilder.append(component.getTableName());
            sqlBuilder.append(sql_key_separator);
            sqlBuilder.append(fieldMapping.getLeft());
            sqlBuilder.append(sql_key_blank);
            sqlBuilder.append(sql_key_as);
            sqlBuilder.append(fieldMapping.getMiddle());
            sqlBuilder.append(sql_key_comma);
        });
        // 删除SELECT中最后多余的“,”
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(sql_key_comma));
        sqlBuilder.append(sql_key_blank);
        sqlBuilder.append(sql_key_from);
        sqlBuilder.append(sql_key_bracket_left);
        // 从上一个组件中获取查询sql作为子查询
        sqlBuilder.append(component.getFrom().get(0).getQuerySql());
        sqlBuilder.append(sql_key_bracket_right);
        sqlBuilder.append(sql_key_as);
        sqlBuilder.append(component.getTableName());
        component.setQuerySql(sqlBuilder.toString());
    }

    /**
     * 组装输出组件的最终CREATE语句
     * CREATE IF NOT EXISTS tableName FROM SELECT xxx, yyy, ... FROM ()
     *
     * @param component       当前输出组件模型
     * @param targetTableName 输出目标表名称
     */
    private void buildCreateSql(ComponentModel component, String targetTableName) {
        StringBuilder sqlBuilder = new StringBuilder(sql_key_create);
        sqlBuilder.append(targetTableName);
        sqlBuilder.append(sql_key_blank);
        sqlBuilder.append(sql_key_as);
        sqlBuilder.append(component.getQuerySql());
        component.setCreateSql(sqlBuilder.toString());
    }

    /**
     * 组装输出组件的最终INSERT语句
     * INSERT INTO tableName (xxx,yyy,...) SELECT xxx, yyy, ... FROM ()
     *
     * @param component       当前输出组件模型
     * @param targetTableName 输出目标表名称
     */
    private void buildInsertSql(ComponentModel component, String targetTableName) {
        StringBuilder sqlBuilder = new StringBuilder(sql_key_insert);
        sqlBuilder.append(targetTableName);
        sqlBuilder.append(sql_key_blank);
        sqlBuilder.append(sql_key_bracket_left);
        component.getFieldMappings().forEach(fieldMapping -> {
            sqlBuilder.append(fieldMapping.getMiddle());
            sqlBuilder.append(sql_key_comma);
        });
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(sql_key_comma));
        sqlBuilder.append(sql_key_bracket_right);
        sqlBuilder.append(sql_key_blank);
        sqlBuilder.append(component.getQuerySql());
        component.setInsertSql(sqlBuilder.toString());
    }

    /**
     * 初始化组件字段（创建和插入时需要使用）
     *
     * @param component 当前输出组件模型
     */
    private void initFields(ComponentModel component) {
        String componentCode = component.getCode();
        ComponentModel fromComponent = component.getFrom().get(0);
        // 查询设定的需要的字段信息
        LambdaQueryWrapper<BiEtlMappingField> fieldWrapper = new LambdaQueryWrapper();
        fieldWrapper.eq(BiEtlMappingField::getRefCode, componentCode);
        List<BiEtlMappingField> setMappingFields = biEtlMappingFieldService.list(fieldWrapper);
        // 获取到从组件的字段映射
        List<Triple> fromMappings = fromComponent.getFieldMappings();
        List<Triple> currMappings = Lists.newArrayList();
        if (CollectionUtils.isEmpty(setMappingFields)) {
            currMappings = fromMappings;
        } else {
            List<String> setFields = setMappingFields.stream().map(BiEtlMappingField::getFieldName)
                    .collect(Collectors.toList());
            currMappings = fromMappings.stream()
                    .filter(fromMapping -> setFields.contains(fromMapping.getLeft())).collect(Collectors.toList());
        }

        // 根据字段名称去重（建表或者插入字段名不能有重复）
        Set<String> uniqueFields = Sets.newHashSet();
        List<Triple> finalMappings = Lists.newArrayList();
        currMappings.forEach(fieldMapping -> {
            String fieldName = (String) fieldMapping.getMiddle();
            if (uniqueFields.add(fieldName)) {
                finalMappings.add(fieldMapping);
            }
        });
        component.setFieldMappings(finalMappings);
        // 最终字段
        List<String> finalFields = finalMappings.stream().map(Triple<String, String, String>::getLeft)
                .collect(Collectors.toList());
        component.setFields(finalFields);
    }

    /**
     * 初始化输出组件输出的目标表名称
     * 如果组件设置了表名，直接使用，未设置默认使用当前组件所在模板的模板code
     *
     * @param component 当前输出组件模型
     * @return String: targetTableName
     */
    private String initTargetTableName(ComponentModel component) {
        String targetTableName = null;
        List<BiComponentParams> params = component.getParams();
        if (CollectionUtils.isEmpty(params)) {
            targetTableName = component.getRefModelCode();
        } else {
            for (BiComponentParams param : params) {
                if (param_key_target_table.equals(param.getParamKey())) {
                    targetTableName = param.getParamValue();
                    break;
                }
            }
            if (StringUtils.isBlank(targetTableName)) {
                targetTableName = component.getRefModelCode();
            }
        }
        return targetTableName;
    }
}
