package com.deloitte.bdh.data.collation.component.impl;

import com.deloitte.bdh.data.collation.component.ArrangerSelector;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.*;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Service("sqlserverArranger")
public class SqlserverArranger implements ArrangerSelector {
    @Override
    public List<ArrangeResultModel> split(FieldMappingModel fromFieldMapping, String separator, String fromTable, ComponentTypeEnum fromType) {
        String desc = fromFieldMapping.getTableField().getDesc();
        if (StringUtils.isBlank(desc)) {
            desc = fromFieldMapping.getFinalFieldName();
        }

        String leftField = fromFieldMapping.getFinalFieldName() + "_left";
        String leftFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + leftField);
        FieldMappingModel leftMapping = fromFieldMapping.clone();
        leftMapping.setFinalFieldDesc(desc + "(left)");
        leftMapping.setTempFieldName(leftFieldTemp);
        leftMapping.setFinalFieldName(leftField);
        leftMapping.getTableField().setName(leftField);
        leftMapping.getTableField().setDesc(leftMapping.getFinalFieldDesc());

        String rightField = fromFieldMapping.getFinalFieldName() + "_right";
        String rightFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + rightField);
        FieldMappingModel rightMapping = fromFieldMapping.clone();
        rightMapping.setFinalFieldDesc(desc + "(right)");
        rightMapping.setTempFieldName(rightFieldTemp);
        rightMapping.setFinalFieldName(rightField);
        rightMapping.getTableField().setName(rightField);
        rightMapping.getTableField().setDesc(rightMapping.getFinalFieldDesc());

        String fromField = fromTable + sql_key_separator + fromFieldMapping.getTempFieldName();
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            fromField = fromFieldMapping.getOriginalFieldName();
        }
        String leftSql = "LEFT (" + fromField + ", CHARINDEX('" + separator + "', " + fromField + ", 0) - 1) AS " + leftFieldTemp;
        String rightSql = "RIGHT (" + fromField + ", LEN(" + fromField + ") - CHARINDEX('" + separator + "', " + fromField + ", 0)) AS " + rightFieldTemp;

        List<ArrangeResultModel> result = Lists.newArrayList();
        result.add(new ArrangeResultModel(leftMapping.getTempFieldName(), leftSql, true, leftMapping));
        result.add(new ArrangeResultModel(rightMapping.getTempFieldName(), rightSql, true, rightMapping));
        return result;
    }

    @Override
    public List<ArrangeResultModel> split(FieldMappingModel fromFieldMapping, int length, String fromTable, ComponentTypeEnum fromType) {
        String desc = fromFieldMapping.getTableField().getDesc();
        if (StringUtils.isBlank(desc)) {
            desc = fromFieldMapping.getFinalFieldName();
        }

        String leftField = fromFieldMapping.getFinalFieldName() + "_left";
        String leftFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + leftField);
        FieldMappingModel leftMapping = fromFieldMapping.clone();
        leftMapping.setFinalFieldDesc(desc + "(left)");
        leftMapping.setTempFieldName(leftFieldTemp);
        leftMapping.setFinalFieldName(leftField);
        leftMapping.getTableField().setName(leftField);
        leftMapping.getTableField().setDesc(leftMapping.getFinalFieldDesc());

        String rightField = fromFieldMapping.getFinalFieldName() + "_right";
        String rightFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + rightField);
        FieldMappingModel rightMapping = fromFieldMapping.clone();
        rightMapping.setFinalFieldDesc(desc + "(right)");
        rightMapping.setTempFieldName(rightFieldTemp);
        rightMapping.setFinalFieldName(rightField);
        rightMapping.getTableField().setName(rightField);
        rightMapping.getTableField().setDesc(rightMapping.getFinalFieldDesc());

        String fromField = fromTable + sql_key_separator + fromFieldMapping.getTempFieldName();
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            fromField = fromFieldMapping.getOriginalFieldName();
        }

        String leftSql = "LEFT (" + fromField + ", " + length + ") AS " + leftFieldTemp;
        String rightSql = "RIGHT (" + fromField + ", LEN(" + fromField + ") - " + length + ") AS " + rightFieldTemp;

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
    public ArrangeResultModel combine(FieldMappingModel leftMapping, FieldMappingModel rightMapping, String connector, String fromTable, ComponentTypeEnum fromType) {
        String fieldName = leftMapping.getFinalFieldName() + "_combine";
        String tempName = getColumnAlias(leftMapping.getOriginalTableName() + sql_key_separator + fieldName);
        // 连接符为空
        if (StringUtils.isEmpty(connector)) {
            connector = "''";
        } else {
            connector = "'" + connector + "'";
        }

        String leftField = leftMapping.getTempFieldName();
        String rightField = rightMapping.getTempFieldName();
        if (ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            leftField = leftMapping.getOriginalFieldName();
            rightField = rightMapping.getOriginalFieldName();

        }

        StringBuilder fieldBuilder = new StringBuilder();
        fieldBuilder.append("CONCAT(");
        fieldBuilder.append("IFNULL(");
        fieldBuilder.append(leftField);
        fieldBuilder.append(", '')");
        fieldBuilder.append(sql_key_comma);
        fieldBuilder.append(connector);
        fieldBuilder.append(sql_key_comma);
        fieldBuilder.append("IFNULL(");
        fieldBuilder.append(fromTable);
        fieldBuilder.append(sql_key_separator);
        fieldBuilder.append(rightField);
        fieldBuilder.append(", '')) AS ");
        fieldBuilder.append(tempName);

        // 新字段的属性
        Integer length = getCombineColumnLength(leftMapping, rightMapping);
        String columnType = "varchar(" + length + ")";
        // 新字段描述
        String desc = leftMapping.getTableField().getDesc();
        String columnDesc = null;
        if (StringUtils.isBlank(desc)) {
            columnDesc = fieldName;
        } else {
            columnDesc = desc + "(combine)";
        }
        TableField tableField = new TableField(null, fieldName, columnDesc, columnType, "varchar", String.valueOf(length));
        FieldMappingModel newMapping = leftMapping.clone();
        newMapping.setTempFieldName(tempName);
        newMapping.setFinalFieldName(fieldName);
        newMapping.setFinalFieldDesc(columnDesc);
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
        String desc = fromFieldMapping.getTableField().getDesc();
        if (StringUtils.isBlank(desc)) {
            newMapping.setFinalFieldDesc(newField);
        } else {
            newMapping.setFinalFieldDesc(desc + "(group)");
        }
        newMapping.getTableField().setName(newField);
        newMapping.getTableField().setColumnType("varchar(255)");
        newMapping.getTableField().setDataType("varchar");
        newMapping.getTableField().setDesc(newMapping.getFinalFieldDesc());

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
        String desc = fromFieldMapping.getTableField().getDesc();
        if (StringUtils.isBlank(desc)) {
            newMapping.setFinalFieldDesc(newField);
        } else {
            newMapping.setFinalFieldDesc(desc + "(group)");
        }
        newMapping.getTableField().setName(newField);
        newMapping.getTableField().setColumnType("varchar(255)");
        newMapping.getTableField().setDataType("varchar");
        newMapping.getTableField().setDesc(newMapping.getFinalFieldDesc());

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
