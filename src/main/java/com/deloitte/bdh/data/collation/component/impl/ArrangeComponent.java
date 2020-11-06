package com.deloitte.bdh.data.collation.component.impl;

import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 整理组件
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("arrangeComponent")
public abstract class ArrangeComponent implements ComponentHandler {

    private DbHandler dbHandler;

    private ComponentHandler componentHandler;

    @Override
    public void handle(ComponentModel component) {

    }

    /**
     * 移除字段（基于从组件操作）
     *
     * @param component
     * @param fields
     */
    protected void remove(ComponentModel component, List<String> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        List<ComponentModel> froms = component.getFrom();
        if (CollectionUtils.isEmpty(froms) || froms.size() > 1) {
            return;
        }
        ComponentModel fromComponent = froms.get(0);
        List<FieldMappingModel> originalMappings = fromComponent.getFieldMappings();
        List<FieldMappingModel> finalMappings = Lists.newArrayList();
        originalMappings.forEach(fieldMapping -> {
            if (!fields.contains(fieldMapping.getTempFieldName())) {
                finalMappings.add(fieldMapping);
            }
        });

        List<String> finalFields = finalMappings.stream().map(FieldMappingModel::getTempFieldName).collect(Collectors.toList());
        component.setFieldMappings(finalMappings);
        component.setFields(finalFields);
    }

    /**
     * 根据分隔符拆分字段
     *
     * @param fieldMapping 字段映射
     * @param separator    分隔符
     * @return List
     */
    abstract List<Pair<String, FieldMappingModel>> split(FieldMappingModel fieldMapping, String separator, ComponentTypeEnum type);

    /**
     * 根据长度拆分字段
     *
     * @param fieldMapping 字段映射
     * @param length       拆分长度
     * @return List
     */
    abstract List<Pair<String, FieldMappingModel>> split(FieldMappingModel fieldMapping, int length, ComponentTypeEnum type);

    /**
     * 替换字段内容
     *
     * @param fieldMapping 字段映射
     * @param source       替换内容
     * @param target       替换目标
     * @return
     */
    abstract String replace(FieldMappingModel fieldMapping, String source, String target, ComponentTypeEnum type);

    /**
     * 合并字段
     *
     * @param fieldMappings 字段映射
     * @return Pair
     */
    abstract Pair<String, FieldMappingModel> combine(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type);

    /**
     * 字段排空
     *
     * @param fieldMappings 字段映射
     * @return List
     */
    abstract List<String> nonNull(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type);

    /**
     * 转大写
     *
     * @param fieldMappings 字段映射
     * @return List
     */
    abstract List<String> toUpperCase(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type);

    /**
     * 转小写
     *
     * @param fieldMappings 字段映射
     * @return List
     */
    abstract List<String> toLowerCase(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type);

    /**
     * 去除前后空格
     *
     * @param fieldMappings 字段映射
     * @return List
     */
    abstract List<String> trim(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type);

    /**
     * 字段分组（新增字段）
     *
     * @param fieldMapping
     * @param values
     * @return
     */
    abstract Pair<String, FieldMappingModel> group(FieldMappingModel fieldMapping, List<Object> values, ComponentTypeEnum type);

    @Autowired
    public void setDbHandler(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Autowired
    public void setComponentHandler(ComponentHandler componentHandler) {
        this.componentHandler = componentHandler;
    }
}
