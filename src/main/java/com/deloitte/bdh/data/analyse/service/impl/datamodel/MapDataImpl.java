package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.enums.DataImplEnum;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.MapEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.collation.enums.DataTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("mapDataImpl")
public class MapDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
        DataConfig dataConfig = request.getDataConfig();
        DataModel dataModel = dataConfig.getDataModel();
        //如果直接List<DataModelField> originalX = dataModel.getX();
        //会发生深拷贝现象，导致下方赋值的时候也会跟着变化
        List<DataModelField> originalX = Lists.newArrayList();
        originalX.addAll(dataModel.getX());
        //符号地图增加经纬度
        if (dataConfig.getTableType().equals(DataImplEnum.MAP_SYMBOL.getTableType())) {
            if (!isExistLongLanField(request)) {
                throw new BizException("当前表数据无经纬度字段,请换个有经纬度的表拖拽字段");
            }
            //经度
            DataModelField longitude = new DataModelField();
            longitude.setType(DataTypeEnum.Text.getType());
            longitude.setQuota(DataModelTypeEnum.WD.getCode());
            longitude.setDataType(DataTypeEnum.Text.getValue());
            longitude.setId(MapEnum.LONGITUDE.getCode());
            longitude.setAlias(MapEnum.LONGITUDE.getDesc());
            dataModel.getX().add(longitude);
            //纬度
            DataModelField lantitude = new DataModelField();
            lantitude.setType(DataTypeEnum.Text.getType());
            lantitude.setQuota(DataModelTypeEnum.WD.getCode());
            lantitude.setDataType(DataTypeEnum.Text.getValue());
            lantitude.setId(MapEnum.LANTITUDE.getCode());
            lantitude.setAlias(MapEnum.LANTITUDE.getDesc());
            dataModel.getX().add(lantitude);
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getY())) {
            dataModel.getY().forEach(field -> dataModel.getX().add(field));
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getCategory())) {
            dataModel.getCategory().forEach(field -> dataModel.getX().add(field));
        }
        BaseComponentDataResponse response = execute(buildSql(request.getDataConfig().getDataModel()));
        request.getDataConfig().getDataModel().setX(originalX);
        response.setRows(buildCategory(request, response.getRows(), dataModel.getY()));
        return response;
    }

    private List<Map<String, Object>> buildCategory(ComponentDataRequest request, List<Map<String, Object>> rows, List<DataModelField> yList) {

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
            //其他参数
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
                    valueList.add(MapUtils.getObject(row, MapEnum.LONGITUDE.getDesc()));//经度
                    valueList.add(MapUtils.getObject(row, MapEnum.LANTITUDE.getDesc()));//纬度
                    valueList.add(MapUtils.getObject(row, colName));
                    newRow.put("value", valueList);
                } else {
                    newRow.put("value", MapUtils.getObject(row, colName));
                }
                newRows.add(newRow);
            }
        }
        return newRows;
    }

    private Boolean isExistLongLanField(ComponentDataRequest request) {
        String sql = "SELECT 1 from information_schema.columns where table_name = '" +
                request.getDataConfig().getDataModel().getTableName() + "' and column_name = 'longitude' and column_name = 'lantitude'";

        BaseComponentDataResponse response = execute(sql);
        return !CollectionUtils.isEmpty(response.getRows());
    }


    @Override
    protected void validate(DataModel dataModel) {

    }
}
