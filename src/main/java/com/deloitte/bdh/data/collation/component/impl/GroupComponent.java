package com.deloitte.bdh.data.collation.component.impl;

import com.alibaba.fastjson.JSON;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.component.model.GroupModel;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 聚合组件实现
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("groupComponent")
public class GroupComponent implements ComponentHandler {

    private static final String param_key_group = "group";
    private static final String param_key_count = "count";
    private static final String param_key_max = "max";
    private static final String param_key_min = "min";
    private static final String param_key_sum = "sum";
    private static final String param_key_avg = "avg";

    @Override
    public void handle(ComponentModel component) {
        String componentCode = component.getCode();
        List<ComponentModel> fromComponents = component.getFrom();
        if (CollectionUtils.isEmpty(fromComponents)) {
            log.error("组件[{}]未查询到上层组件，处理失败！", componentCode);
            throw new BizException("聚合组件不能单独存在，处理失败！");
        }

        if (fromComponents.size() > 1) {
            log.error("组件[{}]查询到[{}]个上层组件，处理失败！", componentCode, fromComponents.size());
            throw new BizException("聚合组件只能有一个上层组件，处理失败！");
        }

        component.setTableName(component.getCode());
        component.setTableName(componentCode);
        buildQuerySql(component);
    }

    /**
     * 组装聚合组件的查询sql
     *
     * @param component 聚合组件
     */
    private void buildQuerySql(ComponentModel component) {
        // 从组件
        List<ComponentModel> froms = component.getFrom();
        if (CollectionUtils.isEmpty(froms)) {
            log.error("分组组件[{}]未查询到从组件，处理失败！", component.getCode());
            throw new BizException("分组组件不能作为源头组件，处理失败！");
        }
        if (froms.size() > 1) {
            log.error("分组组件[{}]查询到[{}]个从组件，处理失败！", component.getCode(), froms.size());
            throw new BizException("分组组件有且只能有一个从头组件，处理失败！");
        }
        ComponentModel fromComponent = froms.get(0);

        // 整体sql
        StringBuilder sqlBuilder = new StringBuilder();
        // group字段sql
        StringBuilder groupBuilder = new StringBuilder();

        buildSelectPart(sqlBuilder, groupBuilder, component);
        buildFromPart(sqlBuilder, fromComponent);

        // 组装group by部分sql
        sqlBuilder.append(sql_key_group_by);
        if (groupBuilder.toString().endsWith(sql_key_comma)) {
            groupBuilder.deleteCharAt(groupBuilder.lastIndexOf(sql_key_comma));
        }
        sqlBuilder.append(groupBuilder);
        component.setQuerySql(sqlBuilder.toString());
    }

    /**
     * 组装select部分sql
     *
     * @param sqlBuilder
     * @param groupBuilder
     * @param component
     */
    private void buildSelectPart(StringBuilder sqlBuilder, StringBuilder groupBuilder, ComponentModel component) {
        Map<String, Set<String>> paramMap = initGroupParams(component);
        // 被用做分组字段
        Set<String> groupFields = (Set<String>) MapUtils.getObject(paramMap, param_key_group);
        ComponentModel fromComponent = component.getFrom().get(0);
        // 从组件的表名
        String fromTableName = fromComponent.getTableName();
        List<String> fromFields = fromComponent.getFields();
        // 从组件类型
        ComponentTypeEnum fromType = fromComponent.getTypeEnum();
        // 从组件字段映射
        Map<String, FieldMappingModel> fromFieldMappings = fromComponent.getFieldMappings().stream()
                .collect(Collectors.toMap(FieldMappingModel::getTempFieldName, fieldMapping -> fieldMapping));
        // 用于记录当前组件的字段映射（因为聚合会产生新的字段，不能复用从组件的映射）
        List<FieldMappingModel> currFieldMappings = Lists.newArrayList();

        sqlBuilder.append(sql_key_select);
        // 用做组装group部分字段
        groupFields.forEach(groupField -> {
            if (!fromFields.contains(groupField)) {
                throw new BizException("从组件中不存在的字段，处理失败！");
            }

            if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
                sqlBuilder.append(fromFieldMappings.get(groupField).getOriginalFieldName());
                sqlBuilder.append(sql_key_blank);
                sqlBuilder.append(sql_key_as);
                sqlBuilder.append(fromFieldMappings.get(groupField).getTempFieldName());
                groupBuilder.append(fromFieldMappings.get(groupField).getOriginalFieldName());
            } else {
                sqlBuilder.append(fromTableName);
                sqlBuilder.append(sql_key_separator);
                sqlBuilder.append(groupField);
                groupBuilder.append(fromTableName);
                groupBuilder.append(sql_key_separator);
                groupBuilder.append(groupField);
            }
            sqlBuilder.append(sql_key_comma);
            groupBuilder.append(sql_key_comma);
            currFieldMappings.add(fromFieldMappings.get(groupField));
        });

        // 组装用做聚合计算的字段(count, max, min, sum, avg)
        for (Map.Entry<String, Set<String>> entry : paramMap.entrySet()) {
            String paramKey = entry.getKey();
            if (param_key_group.equals(paramKey)) {
                continue;
            }
            Set<String> fields = entry.getValue();
            for (String field : fields) {
                // 字段名称为空，不做处理
                if (StringUtils.isBlank(field)) {
                    continue;
                }
                if (!fromFields.contains(field)) {
                    throw new BizException("从组件中不存在的字段，处理失败！");
                }

                FieldMappingModel fromMapping = fromFieldMappings.get(field);
                // 聚合后的字段名称默认为：原名 + "_" + 聚合函数
                String newFinalName = fromMapping.getFinalFieldName() + "_" + paramKey;
                // 使用最终名称生成临时别名
                String newTempName = getColumnAlias(fromMapping.getOriginalTableName()
                        + sql_key_separator + newFinalName);

                sqlBuilder.append(paramKey.toUpperCase());
                sqlBuilder.append(sql_key_bracket_left);
                if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
                    sqlBuilder.append(fromMapping.getOriginalFieldName());
                } else {
                    sqlBuilder.append(fromTableName);
                    sqlBuilder.append(sql_key_separator);
                    sqlBuilder.append(field);
                }
                sqlBuilder.append(sql_key_bracket_right);
                sqlBuilder.append(sql_key_as);
                sqlBuilder.append(newTempName);
                sqlBuilder.append(sql_key_comma);

                FieldMappingModel currMapping = fromMapping.clone();
                currMapping.setTempFieldName(newTempName);
                currMapping.setFinalFieldName(newFinalName);
                currFieldMappings.add(currMapping);
            }
        }
        // 删除SELECT中最后多余的“,”
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(sql_key_comma));
        sqlBuilder.append(sql_key_blank);

        component.setFieldMappings(currFieldMappings);
        List<String> currFields = currFieldMappings.stream()
                .map(FieldMappingModel::getTempFieldName).collect(Collectors.toList());
        component.setFields(currFields);
    }

    /**
     * 组织from部分sql
     *
     * @param sqlBuilder
     * @param fromComponent
     */
    private void buildFromPart(StringBuilder sqlBuilder, ComponentModel fromComponent) {
        sqlBuilder.append(sql_key_from);
        if (ComponentTypeEnum.DATASOURCE.equals(fromComponent.getTypeEnum())) {
            sqlBuilder.append(fromComponent.getTableName());
        } else {
            sqlBuilder.append(sql_key_bracket_left);
            sqlBuilder.append(fromComponent.getQuerySql());
            sqlBuilder.append(sql_key_bracket_right);
            sqlBuilder.append(sql_key_blank);
            sqlBuilder.append(fromComponent.getTableName());
        }
        sqlBuilder.append(sql_key_blank);
    }

    /**
     * 初始化分组参数
     *
     * @param component 分组组件
     * @return
     */
    private Map<String, Set<String>> initGroupParams(ComponentModel component) {
        List<BiComponentParams> params = component.getParams();
        Map<String, Set<String>> result = Maps.newLinkedHashMap();
        if (CollectionUtils.isEmpty(params)) {
            throw new BizException("未找到聚合组件参数，处理失败！");
        }
        BiComponentParams groupParam = null;
        for (BiComponentParams componentParams : params) {
            if (componentParams == null) {
                continue;
            }
            if (ComponentCons.GROUP_PARAM_KEY_GROUPS.equals(componentParams.getParamKey())) {
                groupParam = componentParams;
                break;
            }
        }

        if (groupParam == null) {
            throw new BizException("未找到聚合组件字段参数，处理失败！");
        }

        GroupModel model = JSON.parseObject(groupParam.getParamValue(), GroupModel.class);
        result.put(param_key_group, Sets.newLinkedHashSet(model.getGroup()));
        LinkedHashSet<String> countFields = Sets.newLinkedHashSet();
        countFields.add(model.getCount());
        result.put(param_key_count, countFields);
        result.put(param_key_max, Sets.newLinkedHashSet(model.getMax()));
        result.put(param_key_min, Sets.newLinkedHashSet(model.getMin()));
        result.put(param_key_sum, Sets.newLinkedHashSet(model.getSum()));
        result.put(param_key_avg, Sets.newLinkedHashSet(model.getAvg()));
        return result;
    }
}
