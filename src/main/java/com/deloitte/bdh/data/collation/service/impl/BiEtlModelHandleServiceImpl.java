package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiComponentTree;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.service.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * etl模板处理Service实现
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service
@DS(DSConstant.BI_DB)
public class BiEtlModelHandleServiceImpl implements BiEtlModelHandleService {

    @Autowired
    private BiEtlDatabaseInfService biEtlDatabaseInfService;

    @Autowired
    private BiEtlModelService biEtlModelService;

    @Autowired
    private BiEtlMappingConfigService biEtlMappingConfigService;

    @Autowired
    private BiEtlMappingFieldService biEtlMappingFieldService;

    @Autowired
    private BiComponentService biComponentService;

    @Autowired
    private BiComponentParamsService biComponentParamsService;

    @Autowired
    private ComponentHandler componentHandler;

    @Override
    public ComponentModel handleComponent(String modelCode, String componentCode) {
        if (StringUtils.isBlank(modelCode)) {
            throw new BizException("模板code不能为空！");
        }
        if (StringUtils.isBlank(componentCode)) {
            throw new BizException("组件code不能为空");
        }
        // 根据组件查询组件树
        BiComponentTree componentTree = biComponentService.selectTree(modelCode, componentCode);
        // 根据组件查询组件树
        ComponentModel componentModel = new ComponentModel();
        convertToModel(componentTree, componentModel);
        componentModel.setLast(true);
        handleComponent(componentModel);
        return componentModel;
    }

    @Override
    public ComponentModel handleModel(String modelCode) {
        if (StringUtils.isBlank(modelCode)) {
            throw new BizException("模板code不能为空！");
        }

        LambdaQueryWrapper<BiEtlModel> modelWrapper = new LambdaQueryWrapper();
        modelWrapper.eq(BiEtlModel::getCode, modelCode);
        BiEtlModel model = biEtlModelService.getOne(modelWrapper);
        if (model == null) {
            log.error("根据模板id[{}]未查询到模板信息！", modelCode);
            throw new BizException("未查询到模板信息！");
        }
        // 根据模板查询组件树
        BiComponentTree componentTree = biComponentService.selectTree(model.getCode(), null);
        // 根据组件查询组件树
        ComponentModel componentModel = new ComponentModel();
        convertToModel(componentTree, componentModel);
        componentModel.setLast(true);
        handleComponent(componentModel);
        return componentModel;
    }

    @Override
    public void handlePreviewSql(ComponentModel componentModel) {
        if (!componentModel.isHandled()) {
            throw new BizException("当前组件还未处理，不支持预览sql！");
        }

        ComponentTypeEnum type = componentModel.getTypeEnum();
        // 输出组件直接使用查询语句
        if (ComponentTypeEnum.OUT.equals(type)) {
            componentModel.setPreviewSql(componentModel.getQuerySql() + " LIMIT 10");
            return;
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(ComponentHandler.sql_key_select);

        List<FieldMappingModel> mappings = componentModel.getFieldMappings();
        mappings.forEach(fieldMapping -> {
            if (ComponentTypeEnum.DATASOURCE.equals(type)) {
                sqlBuilder.append(fieldMapping.getOriginalFieldName());
                sqlBuilder.append(ComponentHandler.sql_key_comma);
            } else {
                sqlBuilder.append(componentModel.getTableName());
                sqlBuilder.append(ComponentHandler.sql_key_separator);
                sqlBuilder.append(fieldMapping.getTempFieldName());
                sqlBuilder.append(ComponentHandler.sql_key_blank);
                sqlBuilder.append(ComponentHandler.sql_key_as);
                sqlBuilder.append(fieldMapping.getFinalFieldName());
                sqlBuilder.append(ComponentHandler.sql_key_comma);
            }
        });
        // 删除SELECT中最后多余的“,”
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(ComponentHandler.sql_key_comma));
        sqlBuilder.append(ComponentHandler.sql_key_blank);
        sqlBuilder.append(ComponentHandler.sql_key_from);

        if (ComponentTypeEnum.DATASOURCE.equals(type)) {
            sqlBuilder.append(componentModel.getTableName());
            sqlBuilder.append(ComponentHandler.sql_key_blank);
            sqlBuilder.append("LIMIT 10");
        } else {
            sqlBuilder.append(ComponentHandler.sql_key_bracket_left);
            sqlBuilder.append(componentModel.getQuerySql());
            sqlBuilder.append(ComponentHandler.sql_key_blank);
            sqlBuilder.append("LIMIT 10");
            sqlBuilder.append(ComponentHandler.sql_key_bracket_right);
            sqlBuilder.append(ComponentHandler.sql_key_blank);
            sqlBuilder.append(componentModel.getTableName());
        }
        componentModel.setPreviewSql(sqlBuilder.toString());
    }

    @Override
    public void handlePreviewNullSql(ComponentModel componentModel, List<String> nullFields) {
        if (!componentModel.isHandled()) {
            throw new BizException("当前组件还未处理，不支持预览sql！");
        }
        ComponentTypeEnum type = componentModel.getTypeEnum();

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(ComponentHandler.sql_key_select);
        List<FieldMappingModel> mappings = componentModel.getFieldMappings();
        mappings.forEach(fieldMapping -> {
            if (ComponentTypeEnum.DATASOURCE.equals(type)) {
                sqlBuilder.append(fieldMapping.getOriginalFieldName());
                sqlBuilder.append(ComponentHandler.sql_key_comma);
            } else {
                sqlBuilder.append(componentModel.getTableName());
                sqlBuilder.append(ComponentHandler.sql_key_separator);
                sqlBuilder.append(fieldMapping.getTempFieldName());
                sqlBuilder.append(ComponentHandler.sql_key_blank);
                sqlBuilder.append(ComponentHandler.sql_key_as);
                sqlBuilder.append(fieldMapping.getFinalFieldName());
                sqlBuilder.append(ComponentHandler.sql_key_comma);
            }
        });
        // 删除SELECT中最后多余的“,”
        sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(ComponentHandler.sql_key_comma));
        sqlBuilder.append(ComponentHandler.sql_key_blank);
        sqlBuilder.append(ComponentHandler.sql_key_from);

        Map<String, FieldMappingModel> mappingMap = mappings.stream().collect(Collectors.toMap(FieldMappingModel::getTempFieldName, mapping -> mapping));
        if (ComponentTypeEnum.DATASOURCE.equals(type)) {
            sqlBuilder.append(componentModel.getTableName());
            sqlBuilder.append(ComponentHandler.sql_key_blank);
            if (!CollectionUtils.isEmpty(nullFields)) {
                sqlBuilder.append(ComponentHandler.sql_key_where);
                for (int index = 0; index < nullFields.size(); index++) {
                    String nullField = nullFields.get(index);
                    FieldMappingModel mapping = MapUtils.getObject(mappingMap, nullField);
                    if (index > 0) {
                        sqlBuilder.append(ComponentHandler.sql_key_and);
                    }
                    sqlBuilder.append(mapping.getFinalFieldName());
                    sqlBuilder.append(" IS NULL ");
                }
            }
            sqlBuilder.append("LIMIT 10");
        } else {
            sqlBuilder.append(ComponentHandler.sql_key_bracket_left);
            sqlBuilder.append(componentModel.getQuerySql());
            sqlBuilder.append(ComponentHandler.sql_key_blank);
            if (!CollectionUtils.isEmpty(nullFields)) {
                sqlBuilder.append(ComponentHandler.sql_key_where);
                for (int index = 0; index < nullFields.size(); index++) {
                    String nullField = nullFields.get(index);
                    FieldMappingModel mapping = MapUtils.getObject(mappingMap, nullField);
                    if (index > 0) {
                        sqlBuilder.append(ComponentHandler.sql_key_and);
                    }
                    sqlBuilder.append(mapping.getTempFieldName());
                    sqlBuilder.append(" IS NULL ");
                }
            }
            sqlBuilder.append("LIMIT 10");
            sqlBuilder.append(ComponentHandler.sql_key_bracket_right);
            sqlBuilder.append(ComponentHandler.sql_key_blank);
            sqlBuilder.append(componentModel.getTableName());
        }
        componentModel.setPreviewSql(sqlBuilder.toString());
    }

    /**
     * 处理组件
     *
     * @param component
     */
    private void handleComponent(ComponentModel component) {
        while (true) {
            List<ComponentModel> untreatedModels = Lists.newArrayList();
            getUnhandledComponent(component, untreatedModels);
            if (CollectionUtils.isEmpty(untreatedModels)) {
                break;
            }

            // 批量查询组件参数
            List<String> componentCodes = untreatedModels.stream().map(ComponentModel::getCode).collect(Collectors.toList());
            LambdaQueryWrapper<BiComponentParams> paramWrapper = new LambdaQueryWrapper();
            paramWrapper.in(BiComponentParams::getRefComponentCode, componentCodes);
            List<BiComponentParams> list = biComponentParamsService.list(paramWrapper);

            for (ComponentModel untreatedModel : untreatedModels) {
                ComponentTypeEnum componentType = ComponentTypeEnum.values(untreatedModel.getType());
                untreatedModel.setTypeEnum(componentType);
                // 获取当前组件参数
                List<BiComponentParams> currParams = list.stream()
                        .filter(param -> param.getRefComponentCode().equals(untreatedModel.getCode()))
                        .collect(Collectors.toList());
                untreatedModel.setParams(currParams);
                componentHandler.handle(untreatedModel);
                untreatedModel.getFieldMappings().forEach(fieldMapping -> {
                    fieldMapping.getTableField().setName(fieldMapping.getFinalFieldName());
                });
                untreatedModel.setHandled(true);
            }
            untreatedModels.clear();
        }
    }

    /**
     * 递归转换组件
     *
     * @param componentTree  组件树
     * @param componentModel 组件模型
     */
    private void convertToModel(BiComponentTree componentTree, ComponentModel componentModel) {
        BeanUtils.copyProperties(componentTree, componentModel, "from");
        List<BiComponentTree> fromComponents = componentTree.getFrom();
        // 没有从组件：结束转换
        if (CollectionUtils.isEmpty(fromComponents)) {
            return;
        }

        List<ComponentModel> fromModels = Lists.newArrayList();
        for (BiComponentTree currComponent : fromComponents) {
            ComponentModel fromModel = new ComponentModel();
            BeanUtils.copyProperties(currComponent, fromModel, "from");
            fromModels.add(fromModel);
            if (CollectionUtils.isEmpty(currComponent.getFrom())) {
                continue;
            }
            // 递归调用
            convertToModel(currComponent, fromModel);
        }
        componentModel.setFrom(fromModels);
    }

    /**
     * 递归获取组件模型最外层还未处理的组件集合（not all）
     *
     * @param component           组件树模型
     * @param unhandledComponents 未处理的组件模型集合
     */
    private void getUnhandledComponent(ComponentModel component,
                                       List<ComponentModel> unhandledComponents) {
        if (unhandledComponents == null) {
            unhandledComponents = Lists.newArrayList();
        }
        // 若当前组件已经处理，则无需再往下查询
        if (component.isHandled()) {
            return;
        }

        // 如果当前组件没有从组件，则当前组件需要处理
        List<ComponentModel> fromModels = component.getFrom();
        if (CollectionUtils.isEmpty(fromModels)) {
            unhandledComponents.add(component);
            return;
        }

        // 所有从组件已经处理完
        boolean allFromProcessed = true;
        for (ComponentModel fromModel : fromModels) {
            // 当前从组件已经处理，直接跳过
            if (fromModel.isHandled()) {
                continue;
            }
            // 当前从组件没有处理，继续递归向下搜索
            allFromProcessed = false;
            getUnhandledComponent(fromModel, unhandledComponents);
        }

        // 所有从组件已经处理完，则轮到当前处理当前组件
        if (allFromProcessed) {
            unhandledComponents.add(component);
        }
    }
}
