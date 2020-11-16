package com.deloitte.bdh.data.collation.component.impl;

import com.deloitte.bdh.data.collation.component.ArrangerSelector;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.*;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 整理组件
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("mysqlArranger")
public class MysqlArranger implements ArrangerSelector {
    @Override
    public List<ArrangeResultModel> split(FieldMappingModel fromFieldMapping, String separator, String fromTable, ComponentTypeEnum fromType) {
        String leftField = fromFieldMapping.getFinalFieldName() + "_left";
        String leftFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + leftField);
        FieldMappingModel leftMapping = fromFieldMapping.clone();
        leftMapping.setTempFieldName(leftFieldTemp);
        leftMapping.setFinalFieldName(leftField);
        leftMapping.getTableField().setName(leftField);

        String rightField = fromFieldMapping.getFinalFieldName() + "_right";
        String rightFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + rightField);
        FieldMappingModel rightMapping = fromFieldMapping.clone();
        rightMapping.setTempFieldName(rightFieldTemp);
        rightMapping.setFinalFieldName(rightField);
        rightMapping.getTableField().setName(rightField);

        String leftSql;
        String rightSql;
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            leftSql = "SUBSTRING_INDEX(" + fromFieldMapping.getOriginalFieldName() + ", '" + separator + "', 1) AS " + leftFieldTemp;
            rightSql = "SUBSTRING(" + fromFieldMapping.getOriginalFieldName() + ", IF(INSTR(" + fromFieldMapping.getOriginalFieldName() + ", '" + separator + "') > 0, INSTR(" + fromFieldMapping.getOriginalFieldName() + ", '" + separator + "'), LENGTH(" + fromFieldMapping.getOriginalFieldName() + ")) + 1) AS " + rightFieldTemp;
            // 以下sql为没有匹配到分隔符，右边字段使用全与左边一致
            // rightSql = "SUBSTRING(" + fromFieldMapping.getOriginalFieldName() + ", INSTR(" + fromFieldMapping.getOriginalFieldName() + ", '" + separator + "') + 1) AS " + rightFieldTemp;

        } else {
            leftSql = "SUBSTRING_INDEX(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ", '" + separator + "', 1) AS " + leftFieldTemp;
            rightSql = "SUBSTRING(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ", IF(INSTR(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ", '" + separator + "') > 0, INSTR(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ", '" + separator + "'), LENGTH(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ")) + 1) AS " + rightFieldTemp;
            // 以下sql为没有匹配到分隔符，右边字段使用全与左边一致
            // rightSql = "SUBSTRING(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ", INSTR(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ", '" + separator + "') + 1) AS " + rightFieldTemp;
        }

        List<ArrangeResultModel> result = Lists.newArrayList();
        result.add(new ArrangeResultModel(leftMapping.getTempFieldName(), leftSql, true, leftMapping));
        result.add(new ArrangeResultModel(rightMapping.getTempFieldName(), rightSql, true, rightMapping));
        return result;
    }

    @Override
    public List<ArrangeResultModel> split(FieldMappingModel fromFieldMapping, int length, String fromTable, ComponentTypeEnum fromType) {
        String leftField = fromFieldMapping.getFinalFieldName() + "_left";
        String leftFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + leftField);
        FieldMappingModel leftMapping = fromFieldMapping.clone();
        leftMapping.setTempFieldName(leftFieldTemp);
        leftMapping.setFinalFieldName(leftField);
        leftMapping.getTableField().setName(leftField);

        String rightField = fromFieldMapping.getFinalFieldName() + "_right";
        String rightFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + rightField);
        FieldMappingModel rightMapping = fromFieldMapping.clone();
        rightMapping.setTempFieldName(rightFieldTemp);
        rightMapping.setFinalFieldName(rightField);
        rightMapping.getTableField().setName(rightField);

        String leftSql;
        String rightSql;
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            leftSql = "SUBSTRING(" + fromFieldMapping.getOriginalFieldName() + ", 1, " + length + ") AS " + leftFieldTemp;
            rightSql = "SUBSTRING(" + fromFieldMapping.getOriginalFieldName() + ", " + (length + 1) + ", LENGTH(" + fromFieldMapping.getOriginalFieldName() + ") - " + length + ") AS " + rightFieldTemp;
        } else {
            leftSql = "SUBSTRING(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ", 1, " + length + ") AS " + leftFieldTemp;
            rightSql = "SUBSTRING(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ", " + (length + 1) + ", LENGTH(" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ") - " + length + ") AS " + rightFieldTemp;
        }

        List<ArrangeResultModel> result = Lists.newArrayList();
        result.add(new ArrangeResultModel(leftMapping.getTempFieldName(), leftSql, true, leftMapping));
        result.add(new ArrangeResultModel(rightMapping.getTempFieldName(), rightSql, true, rightMapping));
        return result;
    }

    @Override
    public ArrangeResultModel replace(FieldMappingModel fromFieldMapping, String source, String target, String fromTable, ComponentTypeEnum fromType) {
        String segment;
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            segment = "REPLACE (" + fromFieldMapping.getOriginalFieldName() + ", '" + source + "', '" + target + "' ) AS " + fromFieldMapping.getTempFieldName();
        } else {
            segment = "REPLACE (" + fromTable + sql_key_separator + fromFieldMapping.getTempFieldName() + ", '" + source + "', '" + target + "' ) AS " + fromFieldMapping.getTempFieldName();
        }
        return new ArrangeResultModel(fromFieldMapping.getTempFieldName(), segment, false, fromFieldMapping);
    }

    @Override
    public ArrangeResultModel combine(List<FieldMappingModel> fromFieldMapping, String fromTable, ComponentTypeEnum fromType) {
        String fieldName = fromFieldMapping.get(0).getFinalFieldName() + "_combine";
        String tempName = getColumnAlias(fromFieldMapping.get(0).getOriginalTableName() + sql_key_separator + fieldName);
        StringBuilder fieldBuilder = new StringBuilder();
        fieldBuilder.append("CONCAT(");

        fromFieldMapping.forEach(fromMapping -> {
            if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
                fieldBuilder.append("IFNULL(");
                fieldBuilder.append(fromMapping.getOriginalFieldName());
                fieldBuilder.append(", '')");
                fieldBuilder.append(sql_key_comma);
            } else {
                fieldBuilder.append("IFNULL(");
                fieldBuilder.append(fromTable);
                fieldBuilder.append(sql_key_separator);
                fieldBuilder.append(fromMapping.getTempFieldName());
                fieldBuilder.append(", '')");
                fieldBuilder.append(sql_key_comma);
            }
        });
        // 删除SELECT中最后多余的“,”
        fieldBuilder.deleteCharAt(fieldBuilder.lastIndexOf(sql_key_comma));
        fieldBuilder.append(") AS ");
        fieldBuilder.append(tempName);

        // 新字段的属性
        Integer length = getCombineColumnLength(fromFieldMapping);
        String columnType = "varchar(" + length + ")";
        TableField tableField = new TableField(null, fieldName, null, columnType, "varchar", String.valueOf(length));
        FieldMappingModel newMapping = fromFieldMapping.get(0).clone();
        newMapping.setTempFieldName(tempName);
        newMapping.setFinalFieldName(fieldName);
        newMapping.setOriginalColumnType(columnType);
        newMapping.setTableField(tableField);
        return new ArrangeResultModel(newMapping.getTempFieldName(), fieldBuilder.toString(), true, newMapping);
    }

    @Override
    public List<String> nonNull(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        List<String> results = Lists.newArrayList();
        fromFieldMappings.forEach(fromMapping -> {
            if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
                results.add(fromMapping.getOriginalFieldName() + " IS NOT NULL");
            } else {
                results.add(fromTable + sql_key_separator + fromMapping.getTempFieldName() + " IS NOT NULL");
            }
        });
        return results;
    }

    @Override
    public List<ArrangeResultModel> toUpperCase(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        List<ArrangeResultModel> results = Lists.newArrayList();
        fromFieldMappings.forEach(fromMapping -> {
            String segment;
            if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
                segment = "UPPER(" + fromMapping.getOriginalFieldName() + ") AS " + fromMapping.getTempFieldName();
            } else {
                segment = "UPPER(" + fromTable + sql_key_separator + fromMapping.getTempFieldName() + ") AS " + fromMapping.getTempFieldName();
            }

            results.add(new ArrangeResultModel(fromMapping.getTempFieldName(), segment, false, fromMapping));
        });
        return results;
    }

    @Override
    public List<ArrangeResultModel> toLowerCase(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        List<ArrangeResultModel> results = Lists.newArrayList();
        fromFieldMappings.forEach(fromMapping -> {
            String segment;
            if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
                segment = "LOWER(" + fromMapping.getOriginalFieldName() + ") AS " + fromMapping.getTempFieldName();
            } else {
                segment = "LOWER(" + fromTable + sql_key_separator + fromMapping.getTempFieldName() + ") AS " + fromMapping.getTempFieldName();
            }

            results.add(new ArrangeResultModel(fromMapping.getTempFieldName(), segment, false, fromMapping));
        });
        return results;
    }

    @Override
    public List<ArrangeResultModel> trim(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        List<ArrangeResultModel> results = Lists.newArrayList();
        fromFieldMappings.forEach(fromMapping -> {
            String segment;
            if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
                segment = "TRIM(" + fromMapping.getOriginalFieldName() + ") AS " + fromMapping.getTempFieldName();
            } else {
                segment = "TRIM(" + fromTable + sql_key_separator + fromMapping.getTempFieldName() + ") AS " + fromMapping.getTempFieldName();
            }

            results.add(new ArrangeResultModel(fromMapping.getTempFieldName(), segment, false, fromMapping));
        });
        return results;
    }

    @Override
    public ArrangeResultModel blank(FieldMappingModel fromMapping, ArrangeBlankModel blankModel, String fromTable, ComponentTypeEnum fromType) {
        // sql片段
        String segment = null;
        // 去除空格的类型：left, right, all
        String type = blankModel.getType();
        // 去除空格长度
        Integer length = blankModel.getLength();
        String fieldName;
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            fieldName = fromMapping.getOriginalFieldName();
        } else {
            fieldName = fromTable + sql_key_separator + fromMapping.getTempFieldName();
        }

        if (ComponentCons.ARRANGE_PARAM_KEY_SPACE_LEFT.equals(type) && length != null && length != 0) {
            // 从左侧开始，去除在长度为length的范围内的空字符
            segment = "CONCAT(REPLACE(SUBSTRING(" + fieldName + ", 1, " + length + "), ' ', ''), SUBSTRING(" + fieldName + ", 11)) AS " + fromMapping.getTempFieldName();
        } else if (ComponentCons.ARRANGE_PARAM_KEY_SPACE_RIGHT.equals(type) && length != null && length != 0) {
            // 从右侧开始，去除在长度为length的范围内的空字符
            segment = "CONCAT(SUBSTRING(" + fieldName + ", 1, LENGTH(" + fieldName + ") - " + length + "), REPLACE(SUBSTRING(" + fieldName + ", -" + length + "), ' ', ''))" + fromMapping.getTempFieldName();
        } else {
            // 去除字段内的全部空格
            segment = "REPLACE(" + fieldName + ", ' ', '') AS " + fromMapping.getTempFieldName();
        }
        return new ArrangeResultModel(fromMapping.getTempFieldName(), segment, false, fromMapping);
    }

    @Override
    public ArrangeResultModel enumGroup(FieldMappingModel fromFieldMapping, ArrangeGroupEnumModel groupModel, String fromTable, ComponentTypeEnum fromType) {
        List<ArrangeGroupEnumFieldModel> groups = groupModel.getGroups();
        String otherValue = groupModel.getOther();
        StringBuilder fieldBuilder = new StringBuilder();
        fieldBuilder.append("CASE ");
        // 原字段（要进行分组的字段）
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            fieldBuilder.append(fromFieldMapping.getOriginalFieldName());
        } else {
            fieldBuilder.append(fromTable);
            fieldBuilder.append(sql_key_separator);
            fieldBuilder.append(fromFieldMapping.getTempFieldName());
        }
        fieldBuilder.append(sql_key_blank);

        // 初始化分组后的字段信息
        String newField = fromFieldMapping.getFinalFieldName() + "_group";
        String newFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + newField);
        FieldMappingModel newMapping = fromFieldMapping.clone();
        newMapping.setFinalFieldName(newField);
        newMapping.setTempFieldName(newFieldTemp);
        newMapping.getTableField().setName(newField);
        newMapping.getTableField().setColumnType("varchar(64)");
        newMapping.getTableField().setDataType("varchar");

        // 遍历生成条件
        groups.forEach(group -> {
            String target = group.getTarget();
            List<String> source = group.getSources();
            source.forEach(s -> {
                fieldBuilder.append("WHEN ");
                if (NumberUtils.isDigits(s)) {
                    fieldBuilder.append(s);
                } else {
                    fieldBuilder.append("'" + s + "'");
                }
                fieldBuilder.append(sql_key_blank);
                fieldBuilder.append("THEN ");
                if (NumberUtils.isDigits(target)) {
                    fieldBuilder.append(target);
                } else {
                    fieldBuilder.append("'" + target + "'");
                }
                fieldBuilder.append(sql_key_blank);
            });
        });

        fieldBuilder.append("ELSE '").append(otherValue).append("'");
        fieldBuilder.append(sql_key_blank);
        fieldBuilder.append("END AS ");
        fieldBuilder.append(newFieldTemp);
        fieldBuilder.append(sql_key_blank);
        return new ArrangeResultModel(newMapping.getTempFieldName(), fieldBuilder.toString(), true, newMapping);
    }

    @Override
    public ArrangeResultModel sectGroup(FieldMappingModel fromFieldMapping, ArrangeGroupSectModel groupModel, String fromTable, ComponentTypeEnum fromType) {
        List<ArrangeGroupSectFieldModel> groups = groupModel.getGroups();
        String otherValue = groupModel.getOther();
        // 原字段（要进行分组的字段）
        String sourceField;
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            sourceField = fromFieldMapping.getOriginalFieldName();
        } else {
            sourceField = fromTable + sql_key_separator + fromFieldMapping.getTempFieldName();
        }

        // 初始化分组后的字段信息
        String newField = fromFieldMapping.getFinalFieldName() + "_group";
        String newFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + newField);
        FieldMappingModel newMapping = fromFieldMapping.clone();
        newMapping.setFinalFieldName(newField);
        newMapping.setTempFieldName(newFieldTemp);
        newMapping.getTableField().setName(newField);
        newMapping.getTableField().setColumnType("varchar(64)");
        newMapping.getTableField().setDataType("varchar");

        StringBuilder fieldBuilder = new StringBuilder();
        fieldBuilder.append("CASE ");
        // 遍历生成条件
        groups.forEach(group -> {
            String minSource = group.getMinSource();
            String maxSource = group.getMaxSource();
            String target = group.getTarget();

            fieldBuilder.append("WHEN ");
            fieldBuilder.append(sourceField);
            fieldBuilder.append(sql_key_blank);
            fieldBuilder.append(sql_key_between);
            if (NumberUtils.isDigits(minSource)) {
                fieldBuilder.append(minSource);
            } else {
                fieldBuilder.append("'" + minSource + "'");
            }

            fieldBuilder.append(sql_key_blank);
            fieldBuilder.append(sql_key_and);
            if (NumberUtils.isDigits(maxSource)) {
                fieldBuilder.append(maxSource);
            } else {
                fieldBuilder.append("'" + maxSource + "'");
            }

            fieldBuilder.append(sql_key_blank);
            fieldBuilder.append("THEN ");
            if (NumberUtils.isDigits(target)) {
                fieldBuilder.append(target);
            } else {
                fieldBuilder.append("'" + target + "'");
            }
            fieldBuilder.append(sql_key_blank);
        });

        fieldBuilder.append("ELSE '").append(otherValue).append("'");
        fieldBuilder.append("END AS ");
        fieldBuilder.append(newFieldTemp);
        fieldBuilder.append(sql_key_blank);
        return new ArrangeResultModel(newMapping.getTempFieldName(), fieldBuilder.toString(), true, newMapping);
    }
}
