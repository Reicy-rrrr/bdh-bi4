package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
        return response;
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
                row.put("category", cateName);
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
            throw new BizException("最多可拖入1个字段");
        }
    }

}
