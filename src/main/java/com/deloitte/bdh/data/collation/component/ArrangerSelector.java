package com.deloitte.bdh.data.collation.component;

import com.deloitte.bdh.data.collation.component.model.*;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.DataTypeEnum;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组件接口
 *
 * @author chenghzhang
 * @date 2020/11/09
 */
public interface ArrangerSelector extends Component {
    /**
     * 根据分隔符拆分字段
     *
     * @param fromFieldMapping 字段映射
     * @param fromTable        源表名（上一个组件）
     * @param separator        分隔符
     * @param fromType         从组件类型
     * @return List
     */
    List<ArrangeResultModel> split(FieldMappingModel fromFieldMapping, String separator, String fromTable, ComponentTypeEnum fromType);

    /**
     * 根据长度拆分字段
     *
     * @param fromFieldMapping 字段映射
     * @param length           拆分长度
     * @param fromTable        源表名（上一个组件）
     * @param fromType         从组件类型
     * @return List
     */
    List<ArrangeResultModel> split(FieldMappingModel fromFieldMapping, int length, String fromTable, ComponentTypeEnum fromType);

    /**
     * 替换字段内容
     *
     * @param fromFieldMapping 字段映射
     * @param contents         替换内容
     * @param fromTable        源表名（上一个组件）
     * @param fromType         从组件类型
     * @return
     */
    ArrangeResultModel replace(FieldMappingModel fromFieldMapping, List<ArrangeReplaceContentModel> contents, String fromTable, ComponentTypeEnum fromType);

    /**
     * 合并字段
     *
     * @param leftMapping  左侧字段映射
     * @param rightMapping 右侧字段映射
     * @param connector    连接符
     * @param fromTable    源表名（上一个组件）
     * @param fromType     从组件类型
     * @return ArrangeResultModel
     */
    ArrangeResultModel combine(FieldMappingModel leftMapping, FieldMappingModel rightMapping, String connector, String fromTable, ComponentTypeEnum fromType);

    /**
     * 字段排空
     *
     * @param fromFieldMappings 字段映射
     * @param fromTable         源表名（上一个组件）
     * @param fromType          从组件类型
     * @return List
     */
    List<String> nonNull(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType);

    /**
     * 转大写
     *
     * @param fromFieldMappings 字段映射
     * @param fromTable         源表名（上一个组件）
     * @param fromType          从组件类型
     * @return List
     */
    List<ArrangeResultModel> toUpperCase(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType);

    /**
     * 转小写
     *
     * @param fromFieldMappings 字段映射
     * @param fromTable         源表名（上一个组件）
     * @param fromType          从组件类型
     * @return List
     */
    List<ArrangeResultModel> toLowerCase(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType);

    /**
     * 去除前后空格
     *
     * @param fromFieldMappings 字段映射
     * @param fromTable         源表名（上一个组件）
     * @param fromType          从组件类型
     * @return List
     */
    List<ArrangeResultModel> trim(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType);

    /**
     * 去除空格
     *
     * @param fromFieldMapping 字段映射
     * @param blankModel
     * @param fromTable        源表名（上一个组件）
     * @param fromType         从组件类型
     * @return List
     */
    ArrangeResultModel blank(FieldMappingModel fromFieldMapping, ArrangeBlankModel blankModel, String fromTable, ComponentTypeEnum fromType);

    /**
     * 字段根据列举类型分组（新增字段）
     * 字段值按照列举进行分组
     *
     * @param fromFieldMapping
     * @param groupModel       分组属性
     * @param fromTable        源表名（上一个组件）
     * @param fromType         从组件类型
     * @return ArrangeResultModel
     */
    ArrangeResultModel enumGroup(FieldMappingModel fromFieldMapping, ArrangeGroupEnumModel groupModel, String fromTable, ComponentTypeEnum fromType);

    /**
     * 字段根据区间类型分组（新增字段）
     *
     * @param fromFieldMapping
     * @param groupModel       分组属性
     * @param fromTable        源表名（上一个组件）
     * @param fromType         从组件类型
     * @return ArrangeResultModel
     */
    ArrangeResultModel sectGroup(FieldMappingModel fromFieldMapping, ArrangeGroupSectModel groupModel, String fromTable, ComponentTypeEnum fromType);

    /**
     * 修改字段
     *
     * @param fromFieldMapping 字段映射
     * @param targetDesc       字段描述
     * @param targetType       字段转换后类型
     * @param fromTable        源表名（上一个组件）
     * @param fromType         从组件类型
     * @return ArrangeResultModel
     */
    ArrangeResultModel modify(FieldMappingModel fromFieldMapping, String targetDesc, DataTypeEnum targetType, String fromTable, ComponentTypeEnum fromType);

    /**
     * 字段填充
     *
     * @param fromFieldMapping 字段映射
     * @param fillValue        填充值
     * @param fromTable        源表名（上一个组件）
     * @param fromType         从组件类型
     * @return ArrangeResultModel
     */
    ArrangeResultModel fill(FieldMappingModel fromFieldMapping, String fillValue, String fromTable, ComponentTypeEnum fromType);

    /**
     * 获取合并字段后新字段的长度
     *
     * @param leftMapping  左侧字段映射
     * @param rightMapping 右侧字段映射
     * @return Integer
     */
    default Integer getCombineColumnLength(FieldMappingModel leftMapping, FieldMappingModel rightMapping) {
        List<Integer> numbers = Lists.newArrayList();
        numbers.addAll(getNumbers(leftMapping.getTableField().getColumnType()));
        numbers.addAll(getNumbers(rightMapping.getTableField().getColumnType()));
        return numbers.stream().mapToInt(value -> value).sum();
    }

    /**
     * 获取字符串中所以数值
     *
     * @param source 源字符串
     * @return List<Integer>
     */
    default List<Integer> getNumbers(String source) {
        if (StringUtils.isBlank(source)) {
            return Lists.newArrayList();
        }

        String temp = source.trim().replaceAll("\\D", "_");
        String[] nums = temp.split("_");
        List<Integer> numbers = Arrays.stream(nums).filter(num -> StringUtils.isNotBlank(num))
                .map(Integer::valueOf).collect(Collectors.toList());
        return numbers;
    }

    /**
     * 获取从组件字段
     *
     * @param fromMapping 从组件映射
     * @param fromType    从组件类型
     * @return
     */
    default String getFromField(FieldMappingModel fromMapping, ComponentTypeEnum fromType) {
        String fromField = fromMapping.getTempFieldName();
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            fromField = fromMapping.getOriginalFieldName();
        }
        return fromField;
    }
}
