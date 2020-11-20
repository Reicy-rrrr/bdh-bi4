package com.deloitte.bdh.data.collation.component.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.component.model.JoinFieldModel;
import com.deloitte.bdh.data.collation.component.model.JoinModel;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.JoinTypeEnum;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiEtlMappingField;
import com.deloitte.bdh.data.collation.service.BiEtlMappingFieldService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 关联组件实现
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("joinComponent")
public class JoinComponent implements ComponentHandler {

    @Autowired
    private BiEtlMappingFieldService biEtlMappingFieldService;

    @Override
    public void handle(ComponentModel component) {
        String componentCode = component.getCode();
        // 转换组件参数为map结构
        List<BiComponentParams> params = component.getParams();
        Map<String, BiComponentParams> paramsMap = params.stream()
                .collect(Collectors.toMap(BiComponentParams::getParamKey, param -> param));

        JoinModel joinModel = buildJoinModel(componentCode, paramsMap);

        // 查询设定的需要的字段信息
        LambdaQueryWrapper<BiEtlMappingField> fieldWrapper = new LambdaQueryWrapper();
        fieldWrapper.eq(BiEtlMappingField::getRefCode, componentCode);
        List<BiEtlMappingField> setMappingFields = biEtlMappingFieldService.list(fieldWrapper);
        List<String> setFields = setMappingFields.stream().map(BiEtlMappingField::getFieldName)
                .collect(Collectors.toList());
        buildQuerySql(component, joinModel, setFields);
    }

    /**
     * 组装关联模型（树形结构）
     *
     * @param componentCode
     * @param paramsMap
     * @return
     */
    private JoinModel buildJoinModel(String componentCode,
                                     Map<String, BiComponentParams> paramsMap) {
        BiComponentParams tablesParam =
                (BiComponentParams) MapUtils.getObject(paramsMap, ComponentCons.JOIN_PARAM_KEY_TABLES);
        if (tablesParam == null) {
            log.error("关联组件[{}]未查询到[tables]参数，处理组件失败！", componentCode);
            throw new BizException("关联组件的tables参数不能为空！");
        }

        String tablesStr = tablesParam.getParamValue();
        if (StringUtils.isBlank(tablesStr)) {
            log.error("关联组件[{}]查询到[tables]参数为空，处理组件失败！", componentCode);
            throw new BizException("关联组件的tables参数不能为空！");
        }

        List<JoinModel> tables = JSON.parseArray(tablesStr, JoinModel.class);
        JoinModel joinModel = null;
        for (JoinModel table : tables) {
            // 找到连接查询最左表
            if (StringUtils.isBlank(table.getLeftTableName())) {
                joinModel = table;
                break;
            }
        }

        buildModelTree(joinModel, tables);
        return joinModel;
    }

    /**
     * 组装sql
     *
     * @param component 组件模型
     * @param joinModel 关联模型
     */
    private void buildQuerySql(ComponentModel component, JoinModel joinModel, List<String> setFields) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(sql_key_select).append(sql_key_blank);
        List<ComponentModel> fromComponents = component.getFrom();
        Map<String, ComponentModel> fromCompMap = fromComponents.stream()
                .collect(Collectors.toMap(ComponentModel::getCode, fromModel -> fromModel));

        List<FieldMappingModel> currMappings = Lists.newArrayList();
        for (ComponentModel fromComponent : fromComponents) {
            if (fromComponent == null) {
                continue;
            }

            String fromTableName = fromComponent.getTableName();
            ComponentTypeEnum fromType = fromComponent.getTypeEnum();
            List<FieldMappingModel> fromMappings = fromComponent.getFieldMappings();
            if (CollectionUtils.isEmpty(fromMappings)) {
                continue;
            }
            for (FieldMappingModel fieldMapping : fromMappings) {
                String tempFieldName = fieldMapping.getTempFieldName();
                // 全名 = tableName + "." + fieldName
                String fullName = fieldMapping.getOriginalTableName() + sql_key_separator
                        + fieldMapping.getFinalFieldName();
                // 如果设置了字段，查询结果集为已设置字段，未设置则全量
                if (!CollectionUtils.isEmpty(setFields) && !setFields.contains(tempFieldName)) {
                    continue;
                }
                // 将从组件的字段添加到连接组件中
                currMappings.add(fieldMapping.clone());
                if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
                    sqlBuilder.append(sql_key_blank);
                    sqlBuilder.append(fullName);
                    sqlBuilder.append(sql_key_blank);
                    sqlBuilder.append(sql_key_as);
                    sqlBuilder.append(tempFieldName);
                    sqlBuilder.append(sql_key_comma);
                } else {
                    sqlBuilder.append(sql_key_blank);
                    sqlBuilder.append(fromTableName);
                    sqlBuilder.append(sql_key_separator);
                    sqlBuilder.append(tempFieldName);
                    sqlBuilder.append(sql_key_comma);
                }
            }
        }
        // 删除SELECT中最后多余的“,”
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(sql_key_comma));
        sqlBuilder.append(sql_key_blank);
        sqlBuilder.append(sql_key_from);
        buildTableSql(sqlBuilder, joinModel, fromCompMap);
        component.setTableName(component.getCode());
        component.setQuerySql(sqlBuilder.toString());
        component.setFieldMappings(currMappings);
        // 组装连接组件的字段
        List<String> fields = currMappings.stream().map(FieldMappingModel::getTempFieldName)
                .collect(Collectors.toList());
        component.setFields(fields);
    }

    /**
     * 组装查询表部分sql语句（from 与 where 之间部分）
     *
     * @param sqlBuilder
     * @param joinModel
     * @param componentModels
     */
    private void buildTableSql(StringBuilder sqlBuilder, JoinModel joinModel,
                               Map<String, ComponentModel> componentModels) {
        // 当前组件模型
        ComponentModel currComponent = componentModels.get(joinModel.getTableName());
        if (StringUtils.isBlank(joinModel.getLeftTableName())) {
            String leftTableName = joinModel.getTableName();
            ComponentModel leftComp = componentModels.get(leftTableName);
            // 如果为数据源组件，直接使用表名查询；其他组件使用子查询
            if (ComponentTypeEnum.DATASOURCE.equals(leftComp.getTypeEnum())) {
                sqlBuilder.append(currComponent.getTableName());
                sqlBuilder.append(sql_key_blank);
            } else {
                sqlBuilder.append(sql_key_bracket_left);
                sqlBuilder.append(leftComp.getQuerySql());
                // 子查询使用组件code作为别名
                sqlBuilder.append(sql_key_bracket_right);
                sqlBuilder.append(leftComp.getCode());
                sqlBuilder.append(sql_key_blank);
            }
        }
        List<JoinModel> rightModels = joinModel.getRight();
        if (CollectionUtils.isEmpty(rightModels)) {
            return;
        }

        Map<String, FieldMappingModel> currMappings = currComponent.getFieldMappings().stream()
                .collect(Collectors.toMap(FieldMappingModel::getTempFieldName, fieldMapping -> fieldMapping));

        for (JoinModel rightModel : rightModels) {
            String rightTableName = rightModel.getTableName();
            ComponentModel rightComponent = componentModels.get(rightTableName);

            Map<String, FieldMappingModel> rightMappings = rightComponent.getFieldMappings().stream()
                    .collect(Collectors.toMap(FieldMappingModel::getTempFieldName, fieldMapping -> fieldMapping));

            JoinTypeEnum joinType = JoinTypeEnum.values(rightModel.getJoinType());
            sqlBuilder.append(joinType.getValue());
            sqlBuilder.append(sql_key_blank);
            if (ComponentTypeEnum.DATASOURCE.equals(rightComponent.getTypeEnum())) {
                sqlBuilder.append(rightComponent.getTableName());
                sqlBuilder.append(sql_key_blank);
            } else {
                sqlBuilder.append(sql_key_bracket_left);
                sqlBuilder.append(sql_key_blank);
                sqlBuilder.append(rightComponent.getQuerySql());
                // 子查询使用组件code作为别名
                sqlBuilder.append(sql_key_bracket_right);
                sqlBuilder.append(sql_key_blank);
                sqlBuilder.append(rightComponent.getTableName());
                sqlBuilder.append(sql_key_blank);
            }

            sqlBuilder.append(sql_key_on);
            List<JoinFieldModel> joinFields = rightModel.getJoinFields();
            if (CollectionUtils.isEmpty(joinFields)) {
                throw new BizException("关联的右侧表关联字段不能为空，处理失败！");
            }
            for (int index = 0; index < joinFields.size(); index++) {
                JoinFieldModel joinField = joinFields.get(index);
                if (index != 0) {
                    sqlBuilder.append(sql_key_and);
                }
                sqlBuilder.append(currComponent.getTableName());
                sqlBuilder.append(sql_key_separator);
                if (ComponentTypeEnum.DATASOURCE.equals(currComponent.getTypeEnum())) {
                    sqlBuilder.append(currMappings.get(joinField.getLeftField()).getOriginalFieldName());
                } else {
                    sqlBuilder.append(joinField.getLeftField());
                }

                sqlBuilder.append(sql_key_equal);
                sqlBuilder.append(rightComponent.getTableName());
                sqlBuilder.append(sql_key_separator);
                if (ComponentTypeEnum.DATASOURCE.equals(rightComponent.getTypeEnum())) {
                    sqlBuilder.append(rightMappings.get(joinField.getRightField()).getOriginalFieldName());
                } else {
                    sqlBuilder.append(joinField.getRightField());
                }
                sqlBuilder.append(sql_key_blank);
            }

            if (!CollectionUtils.isEmpty(rightModel.getRight())) {
                buildTableSql(sqlBuilder, rightModel, componentModels);
            }
        }
    }

    private void buildModelTree(JoinModel joinModel, List<JoinModel> tables) {
        List<JoinModel> rights = Lists.newArrayList();
        // 为每一个父节点增加子树（List形式，没有则为空的list）
        for (JoinModel table : tables) {
            if (table.getLeftTableName().equals(joinModel.getTableName())) {
                JoinModel rightModel = new JoinModel();
                BeanUtils.copyProperties(table, rightModel, "right");
                rights.add(rightModel);
                buildModelTree(rightModel, tables);
            }
        }
        joinModel.setRight(rights);
    }
}
