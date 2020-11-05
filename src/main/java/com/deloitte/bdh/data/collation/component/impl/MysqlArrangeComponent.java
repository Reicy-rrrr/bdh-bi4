package com.deloitte.bdh.data.collation.component.impl;

import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 整理组件
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("mysql")
public abstract class MysqlArrangeComponent extends ArrangeComponent {

    @Override
    public void handle(ComponentModel component) {

    }

    @Override
    List<Pair<String, FieldMappingModel>> split(FieldMappingModel fieldMapping, String separator, ComponentTypeEnum type) {
        String newField1 = fieldMapping.getFinalFieldName() + "_" + "split1";
        FieldMappingModel newMapping1 = new FieldMappingModel();
        BeanUtils.copyProperties(fieldMapping, fieldMapping);

        String newField2 = fieldMapping.getFinalFieldName() + "_" + "split2";
//        substring_index(substring_index(promotion_name,'：',1),'：',-1) temp1,
//                substring_index(substring_index(promotion_name,'：',2),'：',-1) temp2
        return null;
    }

    @Override
    List<Pair<String, FieldMappingModel>> split(FieldMappingModel fieldMapping, int length, ComponentTypeEnum type) {
        return null;
    }

    @Override
    String replace(FieldMappingModel fieldMapping, String source, String target, ComponentTypeEnum type) {
        return null;
    }

    @Override
    Pair<String, FieldMappingModel> combine(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type) {
        return null;
    }

    @Override
    List<String> nonNull(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type) {
        return null;
    }

    @Override
    List<String> toUpperCase(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type) {
        return null;
    }

    @Override
    List<String> toLowerCase(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type) {
        return null;
    }

    @Override
    List<String> trim(List<FieldMappingModel> fieldMappings, ComponentTypeEnum type) {
        return null;
    }

    @Override
    Pair<String, FieldMappingModel> group(FieldMappingModel fieldMapping, List<Object> values, ComponentTypeEnum type) {
        return null;
    }
}
