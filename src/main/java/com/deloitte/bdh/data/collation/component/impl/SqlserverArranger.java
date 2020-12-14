package com.deloitte.bdh.data.collation.component.impl;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.component.ArrangerSelector;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.*;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.DataTypeEnum;
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

        String fromField = getFromField(fromFieldMapping, fromType);
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

        String fromField = getFromField(fromFieldMapping, fromType);
        String leftSql = "LEFT (" + fromField + ", " + length + ") AS " + leftFieldTemp;
        String rightSql = "RIGHT (" + fromField + ", LEN(" + fromField + ") - " + length + ") AS " + rightFieldTemp;

        List<ArrangeResultModel> result = Lists.newArrayList();
        result.add(new ArrangeResultModel(leftMapping.getTempFieldName(), leftSql, true, leftMapping));
        result.add(new ArrangeResultModel(rightMapping.getTempFieldName(), rightSql, true, rightMapping));
        return result;
    }

    @Override
    public ArrangeResultModel replace(FieldMappingModel fromFieldMapping, List<ArrangeReplaceContentModel> contents, String fromTable, ComponentTypeEnum fromType) {
        String fromField = getFromField(fromFieldMapping, fromType);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < contents.size(); i++) {
            ArrangeReplaceContentModel replaceContent = contents.get(i);
            String source = replaceContent.getSource();
            String target = replaceContent.getTarget();
            if (i == 0) {
                builder.append("REPLACE (" + fromField + ", '" + source + "', '" + target + "' )");
            } else {
                builder.append("REPLACE (" + builder.toString() + ", '" + source + "', '" + target + "' )");
            }
        }
        builder.append(" AS ");
        builder.append(fromFieldMapping.getTempFieldName());
        return new ArrangeResultModel(fromFieldMapping.getTempFieldName(), builder.toString(), false, fromFieldMapping);
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
        String leftField = getFromField(leftMapping, fromType);
        String rightField = getFromField(rightMapping, fromType);

        StringBuilder fieldBuilder = new StringBuilder();
        fieldBuilder.append("CONCAT(");
        fieldBuilder.append(leftField);
        fieldBuilder.append(",");
        fieldBuilder.append(connector);
        fieldBuilder.append(",");
        fieldBuilder.append(rightField);
        fieldBuilder.append(") AS ");
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
        TableField tableField = new TableField(DataTypeEnum.Text.getType(), fieldName, columnDesc, columnType, "varchar", String.valueOf(length));
        FieldMappingModel newMapping = leftMapping.clone();
        newMapping.setTempFieldName(tempName);
        newMapping.setFinalFieldName(fieldName);
        newMapping.setFinalFieldType(DataTypeEnum.Text.getType());
        newMapping.setFinalFieldDesc(columnDesc);
        newMapping.setOriginalColumnType(columnType);
        newMapping.setTableField(tableField);
        return new ArrangeResultModel(newMapping.getTempFieldName(), fieldBuilder.toString(), true, newMapping);
    }

    @Override
    public List<String> nonNull(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        List<String> results = Lists.newArrayList();
        fromFieldMappings.forEach(fromMapping -> {
            String fromField = getFromField(fromMapping, fromType);
            // 日期类型不能用 ='' 判断
            if (DataTypeEnum.Date.getType().equals(fromMapping.getFinalFieldType()) || DataTypeEnum.DateTime.getType().equals(fromMapping.getFinalFieldType())) {
                results.add(fromField + " IS NOT NULL");
            } else {
                results.add(fromField + " IS NOT NULL AND " + fromField + " != ''");
            }
        });
        return results;
    }

    @Override
    public List<ArrangeResultModel> toUpperCase(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        List<ArrangeResultModel> results = Lists.newArrayList();
        fromFieldMappings.forEach(fromMapping -> {
            String fromField = getFromField(fromMapping, fromType);
            String segment = "UPPER(" + fromField + ") AS " + fromMapping.getTempFieldName();
            results.add(new ArrangeResultModel(fromMapping.getTempFieldName(), segment, false, fromMapping));
        });
        return results;
    }

    @Override
    public List<ArrangeResultModel> toLowerCase(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        List<ArrangeResultModel> results = Lists.newArrayList();
        fromFieldMappings.forEach(fromMapping -> {
            String fromField = getFromField(fromMapping, fromType);
            String segment = "LOWER(" + fromField + ") AS " + fromMapping.getTempFieldName();
            results.add(new ArrangeResultModel(fromMapping.getTempFieldName(), segment, false, fromMapping));
        });
        return results;
    }

    @Override
    public List<ArrangeResultModel> trim(List<FieldMappingModel> fromFieldMappings, String fromTable, ComponentTypeEnum fromType) {
        List<ArrangeResultModel> results = Lists.newArrayList();
        fromFieldMappings.forEach(fromMapping -> {
            String fromField = getFromField(fromMapping, fromType);
            String segment = "LTRIM(RTRIM((" + fromField + ")) AS " + fromMapping.getTempFieldName();
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
        String fromField = getFromField(fromMapping, fromType);
        if (ComponentCons.ARRANGE_PARAM_KEY_SPACE_LEFT.equals(type) && length != null && length != 0) {
            // 从左侧开始，去除在长度为length的范围内的空字符
            segment = "CONCAT(REPLACE(LEFT(" + fromField + ", " + length + "), ' ', ''), RIGHT(" + fromField + ", LEN(" + fromField + ") - " + length + "))";
        } else if (ComponentCons.ARRANGE_PARAM_KEY_SPACE_RIGHT.equals(type) && length != null && length != 0) {
            // 从右侧开始，去除在长度为length的范围内的空字符
            segment = "CONCAT(LEFT(" + fromField + ", LEN(" + fromField + ") - " + length + "), REPLACE(RIGHT(" + fromField + ", " + length + "), ' ', ''))";
        } else {
            // 去除字段内的全部空格
            segment = "REPLACE(" + fromField + ", ' ', '') AS " + fromMapping.getTempFieldName();
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
            fieldBuilder.append(fromFieldMapping.getTempFieldName());
        }
        fieldBuilder.append(sql_key_blank);

        // 初始化分组后的字段信息
        String newField = fromFieldMapping.getFinalFieldName() + "_group";
        String newFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + newField);
        FieldMappingModel newMapping = fromFieldMapping.clone();
        newMapping.setFinalFieldType(DataTypeEnum.Text.getType());
        newMapping.setFinalFieldName(newField);
        newMapping.setTempFieldName(newFieldTemp);
        String desc = fromFieldMapping.getTableField().getDesc();
        if (StringUtils.isBlank(desc)) {
            newMapping.setFinalFieldDesc(newField);
        } else {
            newMapping.setFinalFieldDesc(desc + "(group)");
        }
        newMapping.getTableField().setType(DataTypeEnum.Text.getType());
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
            sourceField = fromFieldMapping.getTempFieldName();
        }

        // 初始化分组后的字段信息
        String newField = fromFieldMapping.getFinalFieldName() + "_group";
        String newFieldTemp = getColumnAlias(fromFieldMapping.getOriginalTableName() + sql_key_separator + newField);
        FieldMappingModel newMapping = fromFieldMapping.clone();
        newMapping.setFinalFieldName(newField);
        newMapping.setFinalFieldType(DataTypeEnum.Text.getType());
        newMapping.setTempFieldName(newFieldTemp);
        String desc = fromFieldMapping.getTableField().getDesc();
        if (StringUtils.isBlank(desc)) {
            newMapping.setFinalFieldDesc(newField);
        } else {
            newMapping.setFinalFieldDesc(desc + "(group)");
        }
        newMapping.getTableField().setType(DataTypeEnum.Text.getType());
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

    @Override
    public ArrangeResultModel modify(FieldMappingModel fromFieldMapping, String targetDesc, DataTypeEnum targetType, String fromTable, ComponentTypeEnum fromType) {
        String fromField = fromFieldMapping.getOriginalFieldName();
        String tempSegment = fromField + " AS " + fromFieldMapping.getTempFieldName();
        if (!ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            fromField = fromFieldMapping.getTempFieldName();
            tempSegment = fromField;
        }

        FieldMappingModel mapping = fromFieldMapping.clone();
        TableField field = mapping.getTableField();
        // 修改字段
        if (StringUtils.isNotBlank(targetDesc)) {
            mapping.setFinalFieldDesc(targetDesc);
            field.setDesc(targetDesc);
        }

        // 原始字段类型
        String type = field.getType();
        // 前后字段类型一致，不转换
        if (targetType.getType().equals(type)) {
            return new ArrangeResultModel(mapping.getTempFieldName(), tempSegment, false, mapping);
        }
        ArrangeResultModel result = null;
        switch (targetType) {
            case Integer:
                result = toInteger(fromFieldMapping, fromType);
                break;
            case Float:
                result = toFloat(fromFieldMapping, fromType);
                break;
            case Date:
                result = toDate(fromFieldMapping, fromType);
                break;
            case DateTime:
                result = toDateTime(fromFieldMapping, fromType);
                break;
            case Text:
                result = toText(fromFieldMapping, fromType);
                break;
            default:
                throw new BizException("转换类型失败，暂不支持的类型！");
        }
        return result;
    }

    @Override
    public ArrangeResultModel fill(FieldMappingModel fromFieldMapping, String fillValue, String fromTable, ComponentTypeEnum fromType) {
        if (fromFieldMapping == null) {
            return new ArrangeResultModel();
        }

        String fieldType = fromFieldMapping.getFinalFieldType();
        DataTypeEnum dataType = DataTypeEnum.valueOf(fieldType);
        // 设置默认值
        if (StringUtils.isBlank(fillValue)) {
            switch (dataType) {
                case Integer:
                    fillValue = "0";
                    break;
                case Float:
                    fillValue = "0";
                    break;
                case DateTime:
                    fillValue = "GETDATE()";
                    break;
                case Date:
                    fillValue = "CONVERT(DATE, GETDATE())";
                    break;
                case Text:
                    fillValue = "'NULL'";
                    break;
                default:
            }
        } else {
            // 转换数据格式
            if (DataTypeEnum.DateTime.equals(dataType)) {
                fillValue = "CONVERT(DATETIME, '" + fillValue +  "')";
            } else if (DataTypeEnum.Date.equals(dataType)) {
                fillValue = "CONVERT(DATE, '" + fillValue +  "'";
            } else if (DataTypeEnum.Text.equals(dataType)) {
                fillValue = "'" + fillValue +  "'";
            }
        }

        String fromField = getFromField(fromFieldMapping, fromType);
        StringBuilder segmentBuilder = new StringBuilder();
        segmentBuilder.append("CASE WHEN ");
        segmentBuilder.append(fromField);
        segmentBuilder.append(" IS NULL OR ");
        segmentBuilder.append(fromField);
        segmentBuilder.append("='' THEN ");
        segmentBuilder.append(fillValue);
        segmentBuilder.append(" ELSE ");
        segmentBuilder.append(fromField);
        segmentBuilder.append(" END AS ");
        segmentBuilder.append(fromFieldMapping.getTempFieldName());
        return new ArrangeResultModel(fromFieldMapping.getTempFieldName(), segmentBuilder.toString(), false, fromFieldMapping.clone());
    }

    /**
     * 转换字段为整数类型
     *
     * @param fromMapping 从字段映射对象
     * @param fromType    从组件类型
     * @return ArrangeResultModel
     */
    private ArrangeResultModel toInteger(FieldMappingModel fromMapping, ComponentTypeEnum fromType) {
        String fromField = getFromField(fromMapping, fromType);
        String type = fromMapping.getTableField().getType();
        DataTypeEnum sourceType = DataTypeEnum.valueOf(type);
        StringBuilder segmentBuilder = new StringBuilder();
        switch (sourceType) {
            case Float:
                segmentBuilder.append("CONVERT(NUMERIC, ").append(fromField).append(")");
                break;
            case Text:
                // CASE WHEN ISNUMERIC('123.1') = 1 AND PATINDEX('%[^0-9+-.%]','123.1') = 0 THEN CONVERT(NUMERIC, '123.1') ELSE NULL END;
                segmentBuilder.append("CASE WHEN ISNUMERIC(");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") = 1 AND PATINDEX('%[^0-9+-.%]',");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") = 0 THEN CONVERT(NUMERIC, ");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") ELSE NULL END");
                break;
            default:
                return defaultModify(fromMapping, fromType);
        }

        FieldMappingModel mapping = fromMapping.clone();
        mapping.setFinalFieldType(DataTypeEnum.Integer.getType());
        mapping.getTableField().setType(DataTypeEnum.Integer.getType());
        mapping.getTableField().setColumnType("bigint(32)");
        mapping.getTableField().setDataType("bigint");
        mapping.getTableField().setDataScope("32");
        segmentBuilder.append(" AS ");
        segmentBuilder.append(mapping.getTempFieldName());
        return new ArrangeResultModel(mapping.getTempFieldName(), segmentBuilder.toString(), false, mapping);
    }

    /**
     * 转换字段为浮点数类型
     *
     * @param fromMapping 从字段映射对象
     * @param fromType    从组件类型
     * @return ArrangeResultModel
     */
    private ArrangeResultModel toFloat(FieldMappingModel fromMapping, ComponentTypeEnum fromType) {
        String fromField = getFromField(fromMapping, fromType);
        String type = fromMapping.getTableField().getType();
        DataTypeEnum sourceType = DataTypeEnum.valueOf(type);
        StringBuilder segmentBuilder = new StringBuilder();
        switch (sourceType) {
            case Integer:
                segmentBuilder.append("CAST(").append(fromField).append(" AS DECIMAL (32,8))");
                break;
            case Text:
                // CASE WHEN ISNUMERIC('123.1') = 1 AND PATINDEX('%[^0-9+-.%]','123.1') = 0 THEN CONVERT(NUMERIC(32,8), '123.1') ELSE NULL END;
                segmentBuilder.append("CASE WHEN ISNUMERIC(");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") = 1 AND PATINDEX('%[^0-9+-.%]',");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") = 0 THEN CONVERT(NUMERIC(32,8), ");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") ELSE NULL END");
                break;
            default:
                return defaultModify(fromMapping, fromType);
        }

        FieldMappingModel mapping = fromMapping.clone();
        mapping.setFinalFieldType(DataTypeEnum.Float.getType());
        mapping.getTableField().setType(DataTypeEnum.Float.getType());
        mapping.getTableField().setColumnType("DECIMAL(32,8)");
        mapping.getTableField().setDataType("DECIMAL");
        mapping.getTableField().setDataScope("32,8");
        segmentBuilder.append(" AS ");
        segmentBuilder.append(mapping.getTempFieldName());
        return new ArrangeResultModel(mapping.getTempFieldName(), segmentBuilder.toString(), false, mapping);
    }

    /**
     * 转换字段为日期类型
     *
     * @param fromMapping 从字段映射对象
     * @param fromType    从组件类型
     * @return ArrangeResultModel
     */
    private ArrangeResultModel toDate(FieldMappingModel fromMapping, ComponentTypeEnum fromType) {
        String fromField = getFromField(fromMapping, fromType);
        String type = fromMapping.getTableField().getType();
        FieldMappingModel mapping = fromMapping.clone();
        DataTypeEnum sourceType = DataTypeEnum.valueOf(type);
        StringBuilder segmentBuilder = new StringBuilder();
        switch (sourceType) {
            case DateTime:
                segmentBuilder.append("CONVERT(DATE, ").append(fromField).append(");");
                break;
            case Text:
                // CASE WHEN ISDATE('2020/01/22') = 1 THEN CONVERT(DATE, '2020/01/22 12:00:12') ELSE NULL END
                segmentBuilder.append("CASE WHEN ISDATE(");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") = 1 THEN CONVERT(DATE, ");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") ELSE NULL END");
                break;
            default:
                return defaultModify(fromMapping, fromType);
        }

        mapping.setFinalFieldType(DataTypeEnum.Date.getType());
        mapping.getTableField().setType(DataTypeEnum.Date.getType());
        mapping.getTableField().setColumnType("date");
        mapping.getTableField().setDataType("date");
        mapping.getTableField().setDataScope("");
        segmentBuilder.append(" AS ");
        segmentBuilder.append(mapping.getTempFieldName());
        return new ArrangeResultModel(mapping.getTempFieldName(), segmentBuilder.toString(), false, mapping);
    }

    /**
     * 转换字段为日期时间类型
     *
     * @param fromMapping 从字段映射对象
     * @param fromType    从组件类型
     * @return ArrangeResultModel
     */
    private ArrangeResultModel toDateTime(FieldMappingModel fromMapping, ComponentTypeEnum fromType) {
        String fromField = getFromField(fromMapping, fromType);
        String type = fromMapping.getTableField().getType();
        FieldMappingModel mapping = fromMapping.clone();
        DataTypeEnum sourceType = DataTypeEnum.valueOf(type);
        StringBuilder segmentBuilder = new StringBuilder();
        switch (sourceType) {
            case DateTime:
                segmentBuilder.append("CONVERT(DATETIME, ").append(fromField).append(");");
                break;
            case Text:
                // CASE WHEN ISDATE('2020/01/22') = 1 THEN CONVERT(DATETIME, '2020/01/22 12:00:12') ELSE NULL END
                segmentBuilder.append("CASE WHEN ISDATE(");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") = 1 THEN CONVERT(DATETIME, ");
                segmentBuilder.append(fromField);
                segmentBuilder.append(") ELSE NULL END");
                break;
            default:
                return defaultModify(fromMapping, fromType);
        }

        mapping.setFinalFieldType(DataTypeEnum.DateTime.getType());
        mapping.getTableField().setType(DataTypeEnum.DateTime.getType());
        mapping.getTableField().setColumnType("datetime");
        mapping.getTableField().setDataType("datetime");
        mapping.getTableField().setDataScope("");
        segmentBuilder.append("AS ");
        segmentBuilder.append(mapping.getTempFieldName());
        return new ArrangeResultModel(mapping.getTempFieldName(), segmentBuilder.toString(), false, mapping);
    }

    /**
     * 转换字段为文本类型
     *
     * @param fromMapping 从字段映射对象
     * @param fromType    从组件类型
     * @return ArrangeResultModel
     */
    private ArrangeResultModel toText(FieldMappingModel fromMapping, ComponentTypeEnum fromType) {
        String fromField = getFromField(fromMapping, fromType);
        String type = fromMapping.getTableField().getType();
        FieldMappingModel mapping = fromMapping.clone();
        DataTypeEnum sourceType = DataTypeEnum.valueOf(type);
        StringBuilder segmentBuilder = new StringBuilder();
        switch (sourceType) {
            case Date:
                // SUBSTRING(CONVERT(VARCHAR, GETDATE(), 120), 1, 10)
                segmentBuilder.append("SUBSTRING(CONVERT(VARCHAR, ");
                segmentBuilder.append(fromField);
                segmentBuilder.append(", 120), 1, 10)");
                break;
            case DateTime:
                // CONVERT(VARCHAR, GETDATE(), 120)
                segmentBuilder.append("CONVERT(VARCHAR, ");
                segmentBuilder.append(fromField);
                segmentBuilder.append(", 120)");
                break;
            default:
                // CONVERT(VARCHAR, 1233.1);
                segmentBuilder.append("CONVERT(VARCHAR, ");
                segmentBuilder.append(fromField);
                segmentBuilder.append(")");
        }

        mapping.setFinalFieldType(DataTypeEnum.Text.getType());
        mapping.getTableField().setType(DataTypeEnum.Text.getType());
        mapping.getTableField().setColumnType("varchar(255)");
        mapping.getTableField().setDataType("varchar");
        mapping.getTableField().setDataScope("255");
        segmentBuilder.append(" AS ");
        segmentBuilder.append(mapping.getTempFieldName());
        return new ArrangeResultModel(mapping.getTempFieldName(), segmentBuilder.toString(), false, mapping);
    }

    /**
     * 默认不做任何处理
     *
     * @param fromMapping 从组件字段映射
     * @param fromType    从组件类型
     * @return ArrangeResultModel
     */
    private ArrangeResultModel defaultModify(FieldMappingModel fromMapping, ComponentTypeEnum fromType) {
        String fromField = fromMapping.getOriginalFieldName();
        String tempSegment = fromField + " AS " + fromMapping.getTempFieldName();
        if (!ComponentTypeEnum.DATASOURCE.equals(fromType)) {
            fromField = fromMapping.getTempFieldName();
            tempSegment = fromField;
        }

        FieldMappingModel mapping = fromMapping.clone();
        return new ArrangeResultModel(mapping.getTempFieldName(), tempSegment, false, mapping);
    }
}
