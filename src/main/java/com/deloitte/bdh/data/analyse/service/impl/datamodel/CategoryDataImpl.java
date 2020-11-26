package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.datamodel.response.MaxMinDto;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 带图例的实现类
 */
@Service("categoryDataImpl")
public class CategoryDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        List<DataModelField> originalX = Lists.newArrayList(dataModel.getX());
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getY())) {
            dataModel.getY().forEach(field -> dataModel.getX().add(field));
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getY2())) {
            dataModel.getY2().forEach(field -> dataModel.getX().add(field));
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getCategory())) {
            dataModel.getCategory().forEach(field -> dataModel.getX().add(field));
        }

        BaseComponentDataResponse response = execute(buildSql(request.getDataConfig().getDataModel()));
        request.getDataConfig().getDataModel().setX(originalX);
        int modelSize = dataModel.getY().size() + dataModel.getY2().size();
        List<Map<String, Object>> y1 = buildCategory(request, response.getRows(), dataModel.getY(), modelSize);
        List<Map<String, Object>> y2 = buildCategory(request, response.getRows(), dataModel.getY2(), modelSize);
        Map<String, MaxMinDto> y1MaxMin = getMinMax(y1);
        Map<String, MaxMinDto> y2MaxMin = getMinMax(y2);
        Map<String, MaxMinDto> maxMinMap = Stream.concat(y1MaxMin.entrySet().stream(), y2MaxMin.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> new MaxMinDto(v1.getMin(), v1.getMax())));
        Map<String, Object> extra = Maps.newHashMap();
        extra.put("maxmin", maxMinMap);
        response.setExtra(extra);
        response.setRows(y1);
        response.setY2(y2);
        return response;
    }

    private List<Map<String, Object>> buildCategory(BaseComponentDataRequest request, List<Map<String, Object>> rows, List<DataModelField> yList, int modelSize) {

        List<Map<String, Object>> newRows = Lists.newArrayList();
        DataModel dataModel = request.getDataConfig().getDataModel();
        for (Map<String, Object> row : rows) {

            //x轴名称
            List<String> xList = Lists.newArrayList();
            for (DataModelField x : dataModel.getX()) {
                String colName = x.getId();
                if (StringUtils.isNotBlank(x.getAlias())) {
                    colName = x.getAlias();
                }
                xList.add(MapUtils.getString(row, colName));
            }
            //图例前缀
            List<String> categoryPrefix = Lists.newArrayList();
            for (DataModelField category : dataModel.getCategory()) {
                String colName = category.getId();
                if (StringUtils.isNotBlank(category.getAlias())) {
                    colName = category.getAlias();
                }
                categoryPrefix.add(MapUtils.getString(row, colName));
            }
            String categoryPrefixName = StringUtils.join(categoryPrefix, "-");
            //重新赋值
            for (DataModelField y : yList) {
                String colName = y.getId();
                if (StringUtils.isNotBlank(y.getAlias())) {
                    colName = y.getAlias();
                }
                Map<String, Object> newRow = Maps.newHashMap();
                newRow.put("name", StringUtils.join(xList, "-"));
                if (StringUtils.isNotBlank(categoryPrefixName)) {
                    if (modelSize > 1) {
                        newRow.put("category", categoryPrefixName + "-" + colName);
                    } else {
                        newRow.put("category", categoryPrefixName);
                    }
                } else {
                    newRow.put("category", colName);
                }

                //将度量的数据类型转为数字
                if (StringUtils.equals(y.getQuota(), DataModelTypeEnum.DL.getCode())) {
                    if (AnalyseConstants.MENSURE_DECIMAL_TYPE.contains(y.getDataType().toUpperCase())) {
                        newRow.put("value", MapUtils.getDouble(row, colName));
                    } else if (AnalyseConstants.MENSURE_TYPE.contains(y.getDataType().toUpperCase())) {
                        newRow.put("value", MapUtils.getIntValue(row, colName));
                    } else {
                        newRow.put("value", MapUtils.getString(row, colName));
                    }
                } else {
                    newRow.put("value", MapUtils.getString(row, colName));
                }
                newRows.add(newRow);
            }
        }
        return newRows;
    }

    private Map<String, MaxMinDto> getMinMax(List<Map<String, Object>> rows) {
        Map<String, MaxMinDto> result = Maps.newHashMap();
        Map<String, List<Object>> categoryMap = Maps.newHashMap();
        for (Map<String, Object> row : rows) {
            String categoryName = MapUtils.getString(row, "category");
            if (categoryMap.keySet().toString().contains(categoryName)) {
                categoryMap.get(categoryName).add(MapUtils.getDouble(row, "value"));
            } else {
                List<Object> valueList = Lists.newArrayList();
                valueList.add(MapUtils.getString(row, "value"));
                categoryMap.put(categoryName, valueList);
            }
        }

        for (Map.Entry<String, List<Object>> entry : categoryMap.entrySet()) {
            MaxMinDto maxMinDto = new MaxMinDto();
            List<Object> valueList = entry.getValue();
            Object max = null;
            Object min = null;
            for (Object ob : valueList) {
                if (null == max) {
                    max = ob;
                }
                if (null == min) {
                    min = ob;
                }
                Double temp = Double.parseDouble(ob.toString());
                if (temp > Double.parseDouble(max.toString())) {
                    max = ob;
                }
                if (temp < Double.parseDouble(min.toString())) {
                    min = ob;
                }
            }
            maxMinDto.setMin(min);
            maxMinDto.setMax(max);
            result.put(entry.getKey(), maxMinDto);
        }
        return result;
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException("维度不能为空");
        }
        if (CollectionUtils.isEmpty(dataModel.getY())) {
            throw new BizException("度量不能为空");

        } else {
            for (DataModelField field : dataModel.getY()) {
                if (!AnalyseConstants.MENSURE_TYPE.contains(field.getDataType().toUpperCase())) {
                    throw new BizException(field.getId() + "数据格式不正确");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(dataModel.getY2())) {
            for (DataModelField field : dataModel.getY2()) {
                if (!AnalyseConstants.MENSURE_TYPE.contains(field.getDataType().toUpperCase())) {
                    throw new BizException(field.getId() + "数据格式不正确");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(dataModel.getCategory())) {
            for (DataModelField field : dataModel.getCategory()) {
                if (!StringUtils.equals(field.getQuota(), DataModelTypeEnum.WD.getCode())) {
                    throw new BizException("图例只可放入维度");
                }
            }
        }
    }
}
