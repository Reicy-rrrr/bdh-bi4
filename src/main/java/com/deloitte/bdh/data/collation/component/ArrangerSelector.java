package com.deloitte.bdh.data.collation.component;

import com.deloitte.bdh.data.collation.component.model.ArrangeGroupFieldModel;
import com.deloitte.bdh.data.collation.component.model.ArrangeResultModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

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
     * @param source           替换内容
     * @param target           替换目标
     * @param fromTable        源表名（上一个组件）
     * @param fromType         从组件类型
     * @return
     */
    ArrangeResultModel replace(FieldMappingModel fromFieldMapping, String source, String target, String fromTable, ComponentTypeEnum fromType);

    /**
     * 合并字段
     *
     * @param fieldMappings 字段映射
     * @param fromTable     源表名（上一个组件）
     * @param fromType      从组件类型
     * @return Pair
     */
    ArrangeResultModel combine(List<FieldMappingModel> fieldMappings, String fromTable, ComponentTypeEnum fromType);

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
     * 字段分组（新增字段）
     *
     * @param fromFieldMapping
     * @param groups           分组属性
     * @param fromTable        源表名（上一个组件）
     * @param fromType         从组件类型
     * @return
     */
    ArrangeResultModel group(FieldMappingModel fromFieldMapping, List<ArrangeGroupFieldModel> groups, String fromTable, ComponentTypeEnum fromType);

    /**
     * 获取合并字段后新字段的长度
     *
     * @param fromFieldMappings 待合并的字段映射
     * @return Integer
     */
    default Integer getCombineColumnLength(List<FieldMappingModel> fromFieldMappings) {
        List<String> columnTypes = fromFieldMappings.stream().map(mapping -> mapping.getTableField().getColumnType()).collect(Collectors.toList());

        List<Integer> numbers = Lists.newArrayList();
        columnTypes.forEach(columnType -> {
            numbers.addAll(getNumbers(columnType));
        });
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
}
