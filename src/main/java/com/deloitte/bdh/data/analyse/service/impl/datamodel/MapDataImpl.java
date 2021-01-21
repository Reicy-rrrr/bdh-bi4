package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.DataImplEnum;
import com.deloitte.bdh.data.analyse.enums.DataUnitEnum;
import com.deloitte.bdh.data.analyse.enums.MapEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service("mapDataImpl")
public class MapDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
        DataConfig dataConfig = request.getDataConfig();
        DataModel dataModel = dataConfig.getDataModel();
        String tableName = dataModel.getTableName();
        //如果直接List<DataModelField> originalX = dataModel.getX();
        //会发生深拷贝现象，导致下方赋值的时候也会跟着变化
        List<DataModelField> originalX = Lists.newArrayList();
        originalX.addAll(dataModel.getX());
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getY())) {
            dataModel.getY().forEach(field -> dataModel.getX().add(field));
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getCategory())) {
            dataModel.getCategory().forEach(field -> dataModel.getX().add(field));
        }
        Map<String, Object> customParams = dataModel.getCustomParams();
        if (MapUtils.isNotEmpty(customParams)) {
            Object symbolS = MapUtils.getObject(customParams, CustomParamsConstants.SYMBOL_SIZE);
            if (Objects.nonNull(symbolS)) {
                DataModelField symbolSize = JSONObject.parseObject(JSON.toJSONString(symbolS), DataModelField.class);
                if (CollectionUtils.isNotEmpty(dataModel.getX()) && Objects.nonNull(symbolSize)) {
                    dataModel.getX().add(symbolSize);
                }
            }
        }
        BaseComponentDataResponse response = execute(dataModel, buildSql(request.getDataConfig().getDataModel()));
        if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
            String viewDetail = MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.VIEW_DETAIL);
            if (StringUtils.equals(viewDetail, "true")) {
                return response;
            }
        }
        request.getDataConfig().getDataModel().setX(originalX);
        request.getDataConfig().getDataModel().setTableName(tableName);
        response.setExtra(getMinMax(customParams, response.getRows()));
        response.setRows(buildCategory(request, response.getRows(), dataModel.getY()));
        return response;
    }

    private List<Map<String, Object>> buildCategory(ComponentDataRequest request, List<Map<String, Object>> rows, List<DataModelField> yList) {

        List<Map<String, Object>> newRows = Lists.newArrayList();
        DataModel dataModel = request.getDataConfig().getDataModel();
        //查询出所有的经纬度数据
        Map<String, Map<String, String>> longitudeLatitudeMap = queryLongitudeLatitude(dataModel);

        Map<String, Object> customParams = dataModel.getCustomParams();
        DataModelField symbolSizeField = null;
        if (MapUtils.isNotEmpty(customParams)) {
            Object symbolS = MapUtils.getObject(customParams, CustomParamsConstants.SYMBOL_SIZE);
            if (Objects.nonNull(symbolS)) {
                symbolSizeField = JSONObject.parseObject(JSON.toJSONString(symbolS), DataModelField.class);
            }
        }
        Map<String, String> precisionMap = Maps.newHashMap();
        Map<String, String> dataUnitMap = Maps.newHashMap();
        for (DataModelField y : yList) {
            String colName = y.getId();
            if (StringUtils.isNotBlank(y.getAlias())) {
                colName = y.getAlias();
            }
            if (null != y.getPrecision()) {
                precisionMap.put(colName, y.getPrecision().toString());
            }
            if (StringUtils.isNotBlank(y.getDataUnit())) {
                dataUnitMap.put(colName, y.getDataUnit());
            }
        }

        DataModelField xField = dataModel.getX().get(0);
        String xName = StringUtils.isBlank(xField.getAlias()) ? xField.getId() : xField.getAlias();
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
            //其他参数
            String symbolSizeName = getColName(symbolSizeField);

            //图例参数
            List<String> categoryPrefix = Lists.newArrayList();
            for (DataModelField category : dataModel.getCategory()) {
                String colName = category.getId();
                if (StringUtils.isNotBlank(category.getAlias())) {
                    colName = category.getAlias();
                }
                String categoryName = StringUtils.join(colName, ": ", MapUtils.getString(row, colName));
                categoryPrefix.add(categoryName);
            }
            //重新赋值
            for (DataModelField y : yList) {
                String colName = y.getId();
                if (StringUtils.isNotBlank(y.getAlias())) {
                    colName = y.getAlias();
                }
                Map<String, Object> newRow = Maps.newHashMap();
                newRow.put("name", StringUtils.join(xList, "-"));
                if (CollectionUtils.isNotEmpty(categoryPrefix)) {
                    newRow.put("category", categoryPrefix);
                }

                if (request.getDataConfig().getTableType().equals(DataImplEnum.MAP_SYMBOL.getTableType())) {
                    List<Object> valueList = Lists.newArrayList();
                    //获取当前地方CODE
                    String place = MapUtils.getString(row, xName);
                    if (StringUtils.isNotEmpty(place)) {
                        //获取当前地方的经纬度数据
                        Map<String, String> longLanMap = longitudeLatitudeMap.get(place);
                        if (Objects.nonNull(longLanMap)) {
                            valueList.add(longLanMap.get(MapEnum.LONGITUDE.getCode()));//经度
                            valueList.add(longLanMap.get(MapEnum.LATITUDE.getCode()));//纬度
                        } else {
                            valueList.add("");//经度
                            valueList.add("");//纬度
                        }
                    } else {
                        valueList.add("");//经度
                        valueList.add("");//纬度
                    }
                    valueList.add(MapUtils.getObject(row, colName));
                    newRow.put("value", valueList);
                } else {
                    newRow.put("value", MapUtils.getObject(row, colName));
                }
                if (StringUtils.isNotEmpty(symbolSizeName)) {
                    double symbolSize = MapUtils.getDouble(row, symbolSizeName);
                    if (!Double.isNaN(symbolSize)) {
                        newRow.put("symbolSize", StringUtils.join(symbolSizeName, ": ", symbolSize));
                    }
                }

                //设置精度和数据单位
                if (null != MapUtils.getObject(precisionMap, colName)) {
                    newRow.put("precision", MapUtils.getObject(precisionMap, colName));
                }
                if (null != MapUtils.getObject(dataUnitMap, colName)) {
                    newRow.put("dataUnit", DataUnitEnum.getDesc(MapUtils.getObject(dataUnitMap, colName)));
                }
                newRows.add(newRow);
            }
        }
        return newRows;
    }

    private Map<String, Map<String, String>> queryLongitudeLatitude(DataModel dataModel) {
        String sql = "select * from LONGITUDE_LATITUDE";
        List<Map<String, Object>> rows = execute(dataModel, sql).getRows();
        Map<String, Map<String, String>> returnMap = Maps.newHashMap();
        for (Map<String, Object> row : rows) {
            Map<String, String> longLatitude = Maps.newHashMap();
            longLatitude.put(MapEnum.LONGITUDE.getCode(), MapUtils.getString(row, MapEnum.LONGITUDE.getCode()));
            longLatitude.put(MapEnum.LATITUDE.getCode(), MapUtils.getString(row, MapEnum.LATITUDE.getCode()));
            returnMap.put(MapUtils.getString(row, "PLACE_CODE"), longLatitude);
        }
        return returnMap;
    }

    private Map<String, Object> getMinMax(Map<String, Object> customParams, List<Map<String, Object>> rows) {

        Map<String, Object> result = Maps.newHashMap();
        DataModelField symbolSizeField = null;
        if (MapUtils.isNotEmpty(customParams)) {
            Object symbolS = MapUtils.getObject(customParams, CustomParamsConstants.SYMBOL_SIZE);
            if (Objects.nonNull(symbolS)) {
                symbolSizeField = JSONObject.parseObject(JSON.toJSONString(symbolS), DataModelField.class);
            }
        }
        if (Objects.isNull(symbolSizeField)){
            return result;
        }
        String symbolSizeName = getColName(symbolSizeField);
        List<Double> symbolSizeList = Lists.newArrayList();
        for (Map<String, Object> row : rows) {
            double value = MapUtils.getDouble(row, symbolSizeName);
            symbolSizeList.add(value);
        }
        Map<String, Object> minMaxMap = Maps.newHashMap();
        minMaxMap.put("min", Collections.min(symbolSizeList));
        minMaxMap.put("max", Collections.max(symbolSizeList));
        result.put("minMax",minMaxMap);
        return result;
    }

    @Override
    protected void validate(DataModel dataModel) {
    }
}
