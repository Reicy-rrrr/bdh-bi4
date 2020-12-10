package com.deloitte.bdh.data.collation.component.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.collation.component.ArrangerSelector;
import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.*;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 整理组件
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("arrangeComponent")
public class ArrangeComponent implements ComponentHandler {

    private static final String arranger_bean_suffix = "Arranger";

    private BiEtlMappingConfigService etlMappingConfigService;

    private BiEtlDatabaseInfService etlDatabaseInfService;

    private DbHandler dbHandler;

    private ComponentHandler componentHandler;

    private ArrangerSelector arranger;

    @Override
    public void handle(ComponentModel component) {
        String componentCode = component.getCode();
        List<ComponentModel> fromComponents = component.getFrom();
        if (org.apache.commons.collections.CollectionUtils.isEmpty(fromComponents)) {
            log.error("组件[{}]未查询到上层组件，处理失败！", componentCode);
            throw new BizException("整理组件不能单独存在，处理失败！");
        }
        if (fromComponents.size() > 1) {
            log.error("组件[{}]查询到[{}]个上层组件，处理失败！", componentCode, fromComponents.size());
            throw new BizException("整理组件有且只能有一个上层组件，处理失败！");
        }


        String modelCode = component.getRefModelCode();
        LambdaQueryWrapper<BiEtlMappingConfig> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(BiEtlMappingConfig::getRefModelCode, modelCode);
        queryWrapper.orderByDesc(BiEtlMappingConfig::getId);
        queryWrapper.last("limit 1");
        BiEtlMappingConfig mappingConfig = etlMappingConfigService.getOne(queryWrapper);
        if (mappingConfig == null) {
            throw new BizException("Arrange component handle error: 为查询到数据源组件配置映射信息！");
        }

        // todo：设置默认数据源类型
        SourceTypeEnum dbType = SourceTypeEnum.Mysql;
        // 直连时直接使用数据源
        SyncTypeEnum syncType = SyncTypeEnum.getEnumByKey(mappingConfig.getType());
        if (SyncTypeEnum.DIRECT.equals(syncType)) {
            String sourceId = mappingConfig.getRefSourceId();
            BiEtlDatabaseInf db = etlDatabaseInfService.getById(sourceId);
            dbType = SourceTypeEnum.values(db.getType());
        }

        arranger = SpringUtil.getBean(dbType.getTypeName() + arranger_bean_suffix, ArrangerSelector.class);
        component.setTableName(component.getCode());
        component.setTableName(componentCode);
        Map<String, BiComponentParams> params = component.getParams().stream().collect(Collectors.toMap(BiComponentParams::getParamKey, param -> param));

        BiComponentParams typeParam = MapUtils.getObject(params, ComponentCons.ARRANGE_PARAM_KEY_TYPE);
        if (typeParam == null) {
            log.error("整理组件[{}]未查询到[type]参数，处理失败！", componentCode);
            throw new BizException("Arrange component handle error: 未查询到type参数！");
        }

        BiComponentParams contextParam = MapUtils.getObject(params, ComponentCons.ARRANGE_PARAM_KEY_CONTEXT);
        if (contextParam == null) {
            log.error("整理组件[{}]未查询到[context]参数，处理失败！", componentCode);
            throw new BizException("Arrange component handle error: 未查询到context参数！");
        }

        build(component, ArrangeTypeEnum.get(typeParam.getParamValue()), contextParam.getParamValue());
    }

    private void build(ComponentModel component, ArrangeTypeEnum arrangeType, String context) {
        List<ArrangeResultModel> arrangeCases = Lists.newArrayList();
        List<String> whereCases = Lists.newArrayList();
        Set<String> excludeFields = Sets.newHashSet();
        switch (arrangeType) {
            case REMOVE:
                excludeFields.addAll(remove(context));
                break;
            case SPLIT:
                arrangeCases.addAll(split(component, context));
                break;
            case COMBINE:
                arrangeCases.addAll(combine(component, context));
                break;
            case NON_NULL:
                whereCases.addAll(nonNull(component, context));
                break;
            case MODIFY:
                arrangeCases.addAll(modify(component, context));
                break;
            case RENAME:
                System.out.println("RENAME");
                break;
            case GROUP:
                arrangeCases.addAll(group(component, context));
                break;
            case CONVERT_CASE:
                arrangeCases.addAll(caseConvert(component, context));
                break;
            case TRIM:
                arrangeCases.addAll(trim(component, context));
                break;
            case BLANK:
                arrangeCases.addAll(blank(component, context));
                break;
            case REPLACE:
                arrangeCases.addAll(replace(component, context));
                break;
            case FILL:
                System.out.println("FILL");
                break;
            case CALCULATE:
                System.out.println("CALCULATE");
                break;
            case SYNC_STRUCTURE:
                System.out.println("SYNC_STRUCTURE");
                break;
            case CONVERT_STRUCTURE:
                System.out.println("CONVERT_STRUCTURE");
                break;
            default:
                throw new BizException("Arrange component handle error: 暂不支持的整理组件类型！");
        }

        ComponentModel fromComponent = component.getFrom().get(0);
        List<FieldMappingModel> currMappings = Lists.newArrayList();

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(buildSelect(fromComponent, arrangeCases, currMappings, excludeFields));
        sqlBuilder.append(buildFrom(fromComponent));
        if (!CollectionUtils.isEmpty(whereCases)) {
            sqlBuilder.append(buildWhere(whereCases));
        }

        component.setQuerySql(sqlBuilder.toString());
        List<String> finalFields = currMappings.stream()
                .map(FieldMappingModel::getTempFieldName).collect(Collectors.toList());
        component.setFields(finalFields);
        component.setFieldMappings(currMappings);
    }

    private StringBuilder buildSelect(ComponentModel fromComponent, List<ArrangeResultModel> arrangeCases,
                                      List<FieldMappingModel> currMappings, Set<String> excludeFields) {
        String fromTableName = fromComponent.getTableName();
        ComponentTypeEnum fromType = fromComponent.getTypeEnum();
        List<FieldMappingModel> fromMappings = fromComponent.getFieldMappings();

        Map<String, Boolean> newFlags = arrangeCases.stream()
                .collect(Collectors.toMap(ArrangeResultModel::getField, resultModel -> resultModel.getIsNew()));
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(sql_key_select);

        for (FieldMappingModel fromMapping : fromMappings) {
            // 要排除的字段不查询
            if (excludeFields.contains(fromMapping.getTempFieldName())) {
                continue;
            }
            // 如果在原字段进行整理（转大小写等），则查询语句使用整理后的
            if (newFlags.containsKey(fromMapping.getTempFieldName())
                    && !newFlags.get(fromMapping.getTempFieldName())) {
                continue;
            }

            if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
                selectBuilder.append(fromMapping.getOriginalFieldName());
                selectBuilder.append(sql_key_blank);
                selectBuilder.append(sql_key_as);
                selectBuilder.append(fromMapping.getTempFieldName());
                selectBuilder.append(sql_key_comma);
            } else {
                selectBuilder.append(fromMapping.getTempFieldName());
                selectBuilder.append(sql_key_comma);
            }

            FieldMappingModel clone = fromMapping.clone();
            currMappings.add(clone);
        }

        arrangeCases.forEach(field -> {
            currMappings.add(field.getMapping());
            selectBuilder.append(field.getSegment());
            selectBuilder.append(sql_key_comma);
        });
        // 删除SELECT中最后多余的“,”
        if (selectBuilder.lastIndexOf(sql_key_comma) == (selectBuilder.length() - 1)) {
            selectBuilder.deleteCharAt(selectBuilder.lastIndexOf(sql_key_comma));
        }
        selectBuilder.append(sql_key_blank);
        return selectBuilder;
    }

    private StringBuilder buildFrom(ComponentModel fromComponent) {
        String fromTableName = fromComponent.getTableName();
        ComponentTypeEnum fromType = fromComponent.getTypeEnum();

        StringBuilder fromBuilder = new StringBuilder();
        fromBuilder.append(sql_key_from);
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            fromBuilder.append(fromTableName);
        } else {
            fromBuilder.append(sql_key_bracket_left);
            fromBuilder.append(fromComponent.getQuerySql());
            fromBuilder.append(sql_key_bracket_right);
            fromBuilder.append(sql_key_blank);
            fromBuilder.append(fromTableName);
        }
        fromBuilder.append(sql_key_blank);
        return fromBuilder;
    }

    private StringBuilder buildWhere(List<String> whereCases) {
        StringBuilder whereBuilder = new StringBuilder();
        if (CollectionUtils.isEmpty(whereCases)) {
            return whereBuilder;
        }
        whereBuilder.append(sql_key_where);
        for (int i = 0; i < whereCases.size(); i++) {
            if (i == 0) {
                whereBuilder.append(whereCases.get(i));
                whereBuilder.append(sql_key_blank);
                continue;
            }

            if (i >= 1) {
                whereBuilder.append(sql_key_and);
                whereBuilder.append(whereCases.get(i));
                whereBuilder.append(sql_key_blank);
            }
        }
        return whereBuilder;
    }

    /**
     * 移除字段（基于从组件操作）
     *
     * @param context 移除字段
     */
    private Set<String> remove(String context) {
        List<String> fields = JSONArray.parseArray(context, String.class);
        return Sets.newHashSet(fields);
    }

    /**
     * 拆分字段
     *
     * @param component 整理组件
     * @param context   拆分内容
     * @return
     */
    private List<ArrangeResultModel> split(ComponentModel component, String context) {
        List<ArrangeSplitModel> splitCases = JSONArray.parseArray(context, ArrangeSplitModel.class);
        if (CollectionUtils.isEmpty(splitCases)) {
            throw new BizException("Arrange component split error: 拆分内容不能为空！");
        }

        // 从组件信息
        ComponentModel fromComponent = component.getFrom().get(0);
        String fromTableName = fromComponent.getTableName();
        ComponentTypeEnum fromType = fromComponent.getTypeEnum();
        List<FieldMappingModel> fromMappings = fromComponent.getFieldMappings();

        Map<String, FieldMappingModel> fromMappingMap = fromMappings.stream()
                .collect(Collectors.toMap(FieldMappingModel::getTempFieldName, mapping -> mapping));
        List<ArrangeResultModel> resultModels = Lists.newArrayList();
        splitCases.forEach(splitModel -> {
            String splitField = splitModel.getName();
            String splitType = splitModel.getType();
            String splitValue = splitModel.getValue();
            FieldMappingModel originalMapping = MapUtils.getObject(fromMappingMap, splitField);
            if (ComponentCons.ARRANGE_PARAM_KEY_SPLIT_LENGTH.equals(splitType)) {
                resultModels.addAll(arranger.split(originalMapping, Integer.valueOf(splitValue), fromTableName, fromType));
            } else {
                resultModels.addAll(arranger.split(originalMapping, splitValue, fromTableName, fromType));
            }
        });

        return resultModels;
    }

    private List<ArrangeResultModel> replace(ComponentModel component, String context) {
        List<ArrangeReplaceModel> replaceCases = JSONArray.parseArray(context, ArrangeReplaceModel.class);
        if (CollectionUtils.isEmpty(replaceCases)) {
            throw new BizException("Arrange component replace error: 替换内容不能为空！");
        }

        // 从组件信息
        ComponentModel fromComponent = component.getFrom().get(0);
        String fromTableName = fromComponent.getTableName();
        ComponentTypeEnum fromType = fromComponent.getTypeEnum();
        List<FieldMappingModel> fromMappings = fromComponent.getFieldMappings();

        Map<String, FieldMappingModel> fromMappingMap = fromMappings.stream()
                .collect(Collectors.toMap(FieldMappingModel::getTempFieldName, mapping -> mapping));

        List<ArrangeResultModel> resultModels = Lists.newArrayList();
        replaceCases.forEach(replaceCase -> {
            String fieldName = replaceCase.getName();
            List<ArrangeReplaceContentModel> contents = replaceCase.getContents();
            FieldMappingModel fromMapping = MapUtils.getObject(fromMappingMap, fieldName);
            ArrangeResultModel resultModel = arranger.replace(fromMapping, contents, fromTableName, fromType);
            resultModels.add(resultModel);
        });
        return resultModels;
    }

    private List<ArrangeResultModel> combine(ComponentModel component, String context) {
        List<ArrangeCombineModel> combineFields = JSONArray.parseArray(context, ArrangeCombineModel.class);
        if (CollectionUtils.isEmpty(combineFields)) {
            throw new BizException("Arrange component combine error: 合并字段不能为空！");
        }
        // 从组件信息
        ComponentModel fromComponent = component.getFrom().get(0);
        Map<String, FieldMappingModel> mappings = fromComponent.getFieldMappings().stream().collect(Collectors.toMap(FieldMappingModel::getTempFieldName, mapping -> mapping));

        List<ArrangeResultModel> resultModels = Lists.newArrayList();
        combineFields.forEach(combineModel -> {
            String leftField = combineModel.getLeft();
            String rightField = combineModel.getRight();
            String connector = combineModel.getConnector();

            FieldMappingModel leftMapping = MapUtils.getObject(mappings, leftField);
            FieldMappingModel rightMapping = MapUtils.getObject(mappings, rightField);
            resultModels.add(arranger.combine(leftMapping, rightMapping, connector, fromComponent.getTableName(), fromComponent.getTypeEnum()));
        });
        return resultModels;
    }

    private List<String> nonNull(ComponentModel component, String context) {
        List<String> nonNullFields = JSONArray.parseArray(context, String.class);
        if (CollectionUtils.isEmpty(nonNullFields)) {
            throw new BizException("Arrange component non null error: 排空字段不能为空！");
        }

        // 从组件信息
        ComponentModel fromComponent = component.getFrom().get(0);
        List<FieldMappingModel> nonNullMappings = fromComponent.getFieldMappings().stream()
                .filter(mappingModel -> nonNullFields.contains(mappingModel.getTempFieldName())).collect(Collectors.toList());
        List<String> result = arranger.nonNull(nonNullMappings, fromComponent.getTableName(), fromComponent.getTypeEnum());
        return result;
    }

    private List<ArrangeResultModel> caseConvert(ComponentModel component, String context) {
        List<ArrangeCaseConvertModel> convertCases = JSONArray.parseArray(context, ArrangeCaseConvertModel.class);
        if (CollectionUtils.isEmpty(convertCases)) {
            throw new BizException("Arrange component case convert error: 转换字段不能为空！");
        }
        List<ArrangeResultModel> resultModels = Lists.newArrayList();
        // 从组件信息
        ComponentModel fromComponent = component.getFrom().get(0);
        Map<String, FieldMappingModel> fromMappingMap = fromComponent.getFieldMappings().stream()
                .collect(Collectors.toMap(FieldMappingModel::getTempFieldName, mapping -> mapping));

        // 转大写字段
        List<FieldMappingModel> toUpperCaseMappings = Lists.newArrayList();
        // 转小写字段
        List<FieldMappingModel> toLowerCaseMappings = Lists.newArrayList();
        convertCases.forEach(convertCase -> {
            String fieldName = convertCase.getName();
            String type = convertCase.getType();
            FieldMappingModel mapping = MapUtils.getObject(fromMappingMap, fieldName);
            if (ComponentCons.ARRANGE_PARAM_KEY_CASE_UPPER.equals(type)) {
                toUpperCaseMappings.add(mapping);
            } else {
                toLowerCaseMappings.add(mapping);
            }
        });

        if (!CollectionUtils.isEmpty(toUpperCaseMappings)) {
            resultModels.addAll(arranger.toUpperCase(toUpperCaseMappings, fromComponent.getTableName(), fromComponent.getTypeEnum()));
        }
        if (!CollectionUtils.isEmpty(toLowerCaseMappings)) {
            resultModels.addAll(arranger.toLowerCase(toLowerCaseMappings, fromComponent.getTableName(), fromComponent.getTypeEnum()));
        }
        return resultModels;
    }

    private List<ArrangeResultModel> trim(ComponentModel component, String context) {
        List<String> trimFields = JSONArray.parseArray(context, String.class);
        if (CollectionUtils.isEmpty(trimFields)) {
            throw new BizException("Arrange component trim error: 前后去空格字段不能为空！");
        }
        // 从组件信息
        ComponentModel fromComponent = component.getFrom().get(0);
        List<FieldMappingModel> trimMappings = fromComponent.getFieldMappings().stream()
                .filter(mappingModel -> trimFields.contains(mappingModel.getTempFieldName())).collect(Collectors.toList());

        List<ArrangeResultModel> resultModels = arranger.trim(trimMappings, fromComponent.getTableName(), fromComponent.getTypeEnum());
        return resultModels;
    }

    private List<ArrangeResultModel> blank(ComponentModel component, String context) {
        List<ArrangeBlankModel> blankModels = JSONArray.parseArray(context, ArrangeBlankModel.class);
        if (CollectionUtils.isEmpty(blankModels)) {
            throw new BizException("Arrange component remove blank error: 去除空格字段不能为空！");
        }
        // 从组件信息
        ComponentModel fromComponent = component.getFrom().get(0);
        Map<String, FieldMappingModel> fromMappingMap = fromComponent.getFieldMappings().stream()
                .collect(Collectors.toMap(FieldMappingModel::getTempFieldName, mapping -> mapping));

        List<ArrangeResultModel> resultModels = Lists.newArrayList();
        blankModels.forEach(blankModel -> {
            String fieldName = blankModel.getName();
            FieldMappingModel fromMapping = MapUtils.getObject(fromMappingMap, fieldName);
            if (fromMapping != null) {
                ArrangeResultModel resultModel = arranger.blank(fromMapping, blankModel, fromComponent.getTableName(), fromComponent.getTypeEnum());
                resultModels.add(resultModel);
            }
        });
        return resultModels;
    }

    private List<ArrangeResultModel> group(ComponentModel component, String context) {
        ArrangeGroupModel groupModel = JSON.parseObject(context, ArrangeGroupModel.class);
        if (groupModel == null) {
            throw new BizException("Arrange component group error: 分组字段不能为空！");
        }
        // 从组件信息
        ComponentModel fromComponent = component.getFrom().get(0);
        Map<String, FieldMappingModel> fromMappingMap = fromComponent.getFieldMappings().stream()
                .collect(Collectors.toMap(FieldMappingModel::getTempFieldName, mapping -> mapping));

        List<ArrangeResultModel> resultModels = Lists.newArrayList();
        // 字符类型的分组字段
        List<ArrangeGroupEnumModel> stringGroups = groupModel.getEnumFields();
        stringGroups.forEach(stringModel -> {
            String fieldName = stringModel.getName();
            FieldMappingModel mapping = MapUtils.getObject(fromMappingMap, fieldName);
            ArrangeResultModel resultModel = arranger.enumGroup(mapping, stringModel, fromComponent.getTableName(), fromComponent.getTypeEnum());
            resultModels.add(resultModel);
        });

        // 数字类型的分组字段
        List<ArrangeGroupSectModel> numberGroups = groupModel.getSectFields();
        numberGroups.forEach(numberModel -> {
            String fieldName = numberModel.getName();
            FieldMappingModel mapping = MapUtils.getObject(fromMappingMap, fieldName);
            ArrangeResultModel resultModel = arranger.sectGroup(mapping, numberModel, fromComponent.getTableName(), fromComponent.getTypeEnum());
            resultModels.add(resultModel);
        });
        return resultModels;
    }

    private List<ArrangeResultModel> modify(ComponentModel component, String context) {
        List<ArrangeModifyModel> modifyModels = JSONArray.parseArray(context, ArrangeModifyModel.class);
        if (CollectionUtils.isEmpty(modifyModels)) {
            throw new BizException("Arrange component group error: 修改的字段不能为空！");
        }
        // 从组件信息
        ComponentModel fromComponent = component.getFrom().get(0);
        Map<String, FieldMappingModel> fromMappingMap = fromComponent.getFieldMappings().stream()
                .collect(Collectors.toMap(FieldMappingModel::getTempFieldName, mapping -> mapping));

        List<ArrangeResultModel> resultModels = Lists.newArrayList();
        modifyModels.forEach(modifyModel -> {
            String fieldName = modifyModel.getName();
            DataTypeEnum targetType = DataTypeEnum.values(modifyModel.getType());
            String targetDesc = modifyModel.getDesc();
            FieldMappingModel fromMapping = MapUtils.getObject(fromMappingMap, fieldName);

            DataTypeEnum sourceType = DataTypeEnum.values(fromMapping.getFinalFieldType());
            switch (targetType) {
                case Integer:
                    if (DataTypeEnum.Date.equals(sourceType) || DataTypeEnum.DateTime.equals(sourceType)) {
                        throw new BizException("不支持从日期或者日期时间类型转换为整数类型！");
                    }
                    break;
                case Float:
                    if (DataTypeEnum.Date.equals(sourceType) || DataTypeEnum.DateTime.equals(sourceType)) {
                        throw new BizException("不支持从日期或者日期时间类型转换为浮点类型！");
                    }
                    break;
                case Date:
                    if (DataTypeEnum.Integer.equals(sourceType) || DataTypeEnum.Float.equals(sourceType)) {
                        throw new BizException("不支持从整数或者浮点类型转换为日期类型！");
                    }
                    break;
                case DateTime:
                    if (DataTypeEnum.Integer.equals(sourceType) || DataTypeEnum.Float.equals(sourceType)) {
                        throw new BizException("不支持从整数或者浮点类型转换为日期时间类型！");
                    }
                    break;
                case Text:
                    break;
                default:
            }
            ArrangeResultModel resultModel = arranger.modify(fromMapping, targetDesc, targetType, fromComponent.getTableName(), fromComponent.getTypeEnum());
            resultModels.add(resultModel);
        });
        return resultModels;
    }

    @Autowired
    public void setEtlMappingConfigService(BiEtlMappingConfigService etlMappingConfigService) {
        this.etlMappingConfigService = etlMappingConfigService;
    }

    @Autowired
    public void setEtlDatabaseInfService(BiEtlDatabaseInfService etlDatabaseInfService) {
        this.etlDatabaseInfService = etlDatabaseInfService;
    }

    @Autowired
    public void setDbHandler(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Autowired
    public void setComponentHandler(ComponentHandler componentHandler) {
        this.componentHandler = componentHandler;
    }
}
