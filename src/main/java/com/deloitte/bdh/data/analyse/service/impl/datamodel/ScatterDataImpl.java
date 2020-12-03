package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service("scatterDataImpl")
public class ScatterDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getY())) {
            dataModel.getY().forEach(y -> dataModel.getX().add(y));
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getCategory())) {
            dataModel.getCategory().forEach(y -> dataModel.getX().add(y));
        }
        Map<String, Object> customParams = dataModel.getCustomParams();
        DataModelField scatterName = JSONObject.parseObject(JSON.toJSONString(customParams.get("scatterName")), DataModelField.class);
        DataModelField scatterSize = JSONObject.parseObject(JSON.toJSONString(customParams.get("scatterSize")), DataModelField.class);
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && Objects.nonNull(scatterName)) {
            dataModel.getX().add(scatterName);
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && Objects.nonNull(scatterSize)) {
            dataModel.getX().add(scatterSize);
        }

        BaseComponentDataResponse response = execute(buildSql(dataModel));
        response.setRows(buildCategory(request, response.getRows()));
//        Map<String, Object> extra = Maps.newHashMap();
//        extra.put("maxmin", getMinMax(dataModel, response.getRows()));
//        response.setExtra(extra);
        return response;
    }


    private Map<String, Object> getMinMax(DataModel dataModel, List<Map<String, Object>> rows) {

        Map<String, Object> result = Maps.newHashMap();

        List<DataModelField> cateModelList = dataModel.getCategory();
        DataModelField yModel = dataModel.getY().get(0);
        //获取Y轴
        String yColName = getColName(yModel);

        //维度正常处理
        if (CollectionUtils.isNotEmpty(cateModelList) && cateModelList.get(0).getQuota().equals(DataModelTypeEnum.WD.getCode())) {
            Map<String, List<Object>> categoryMap = Maps.newHashMap();
            //获取所有的category
            for (Map<String, Object> row : rows) {
                String categoryName = MapUtils.getString(row, "category");
                if (categoryMap.keySet().toString().contains(categoryName)) {
                    categoryMap.get(categoryName).add(MapUtils.getObject(row, yColName));
                } else {
                    List<Object> valueList = Lists.newArrayList();
                    valueList.add(MapUtils.getObject(row, yColName));
                    categoryMap.put(categoryName, valueList);
                }
            }

            //比较选取最大最小
            for (Map.Entry<String, List<Object>> entry : categoryMap.entrySet()) {

                List<Object> list = entry.getValue();
                double first = Double.parseDouble(list.get(0).toString());
                double min = first;
                double max = first;
                for (Object o : list) {
                    if (min > Double.parseDouble(o.toString())) {
                        min = Double.parseDouble(o.toString());
                    }
                    if (max < Double.parseDouble(o.toString())) {
                        max = Double.parseDouble(o.toString());
                    }
                }
                Map<String, Object> minMaxMap = Maps.newHashMap();
                minMaxMap.put("min", min);
                minMaxMap.put("max", max);
                result.put(entry.getKey(), minMaxMap);
            }
        }
        //度量取Y轴的最大最小
        else {

            List<Double> yList = Lists.newArrayList();
            for (Map<String, Object> row : rows) {
                double value = MapUtils.getDouble(row, yColName);
                yList.add(value);
            }
            Map<String, Object> minMaxMap = Maps.newHashMap();
            minMaxMap.put("min", Collections.min(yList));
            minMaxMap.put("max", Collections.max(yList));
            result.put(yColName, minMaxMap);
        }
        return result;
    }


    private List<Map<String, Object>> buildCategory(BaseComponentDataRequest request, List<Map<String, Object>> rows) {

        DataModel dataModel = request.getDataConfig().getDataModel();
        Map<String, Object> customParams = dataModel.getCustomParams();
        DataModelField scatterNameField = JSONObject.parseObject(JSON.toJSONString(customParams.get("scatterName")), DataModelField.class);

        for (Map<String, Object> row : rows) {

            //散点名称
            String scatterColName = getColName(scatterNameField);
            String scatterName = "";
            if (StringUtils.isNotEmpty(scatterColName)) {
                scatterName = MapUtils.getString(row, scatterColName);
            }
            //图例前缀
            List<DataModelField> category = dataModel.getCategory();
            if (CollectionUtils.isNotEmpty(category)) {
                DataModelField cateModel = category.get(0);
                String cateColName = getColName(cateModel);
                String cateName = MapUtils.getString(row, cateColName);
                if (cateModel.getQuota().equals(DataModelTypeEnum.WD.getCode())) {
                    row.put("category", cateName);
                }
                if (StringUtils.isNotEmpty(scatterName)) {
                    if (cateModel.getQuota().equals(DataModelTypeEnum.DL.getCode())) {
                        row.put("name", scatterName);
                    } else if (cateModel.getQuota().equals(DataModelTypeEnum.WD.getCode())) {
                        row.put("name", cateName + "-" + scatterName);
                    }
                }
            } else {
                if (StringUtils.isNotEmpty(scatterName)) {
                    row.put("name", scatterName);
                }
            }
        }
        return rows;
    }

    private String getColName(DataModelField dataModelField) {

        if (Objects.isNull(dataModelField)) {
            return "";
        }
        String colName = dataModelField.getId();
        if (StringUtils.isNotBlank(dataModelField.getAlias())) {
            colName = dataModelField.getAlias();
        }
        return colName;
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (dataModel.getY().size() > 1) {
            throw new BizException("Y轴最多可拖入1个字段");
        }
        if (dataModel.getCategory().size() > 1) {
            throw new BizException("颜色最多可拖入1个字段");
        }
    }

}
