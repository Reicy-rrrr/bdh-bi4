package com.deloitte.bdh.data.collation.component.impl;

import com.deloitte.bdh.data.collation.component.ArrangerSelector;
import com.deloitte.bdh.data.collation.component.model.*;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.DataTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 整理组件
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("hiveArranger")
public class HiveArranger implements ArrangerSelector {
    @Override
    public List<ArrangeResultModel> split(FieldMappingModel fromFieldMapping, String separator, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public List<ArrangeResultModel> split(FieldMappingModel fromFieldMapping, int length, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public ArrangeResultModel replace(FieldMappingModel fromFieldMapping, List<ArrangeReplaceContentModel> contents, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public ArrangeResultModel combine(FieldMappingModel leftMapping, FieldMappingModel rightMapping, String connector, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public List<String> nonNull(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public List<ArrangeResultModel> toUpperCase(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public List<ArrangeResultModel> toLowerCase(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public List<ArrangeResultModel> trim(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public ArrangeResultModel blank(FieldMappingModel fromFieldMapping, ArrangeBlankModel blankModel, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public ArrangeResultModel enumGroup(FieldMappingModel fromFieldMapping, ArrangeGroupEnumModel groupModel, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public ArrangeResultModel sectGroup(FieldMappingModel fromFieldMapping, ArrangeGroupSectModel groupModel, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public ArrangeResultModel modify(FieldMappingModel fromFieldMapping, String targetDesc, DataTypeEnum targetType, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public ArrangeResultModel fill(FieldMappingModel fromFieldMapping, String fillValue, String fromTable, ComponentTypeEnum fromType) {
        return null;
    }

    @Override
    public String calculateCeil(String field) {
        return null;
    }

    @Override
    public String calculateIfNull(String field, String value) {
        return null;
    }
}
