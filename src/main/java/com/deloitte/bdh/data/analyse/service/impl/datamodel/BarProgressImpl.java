package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.beust.jcommander.internal.Maps;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 基础格式返回实现类
 */
@Service("barProgressImpl")
public class BarProgressImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {

        DataModel dataModel = request.getDataConfig().getDataModel();
        List<DataModelField> originalX = Lists.newArrayList(dataModel.getX());
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getY())) {
            dataModel.getY().forEach(field -> dataModel.getX().add(field));
        }

        BaseComponentDataResponse response = execute(buildSql(request.getDataConfig().getDataModel()));
        request.getDataConfig().getDataModel().setX(originalX);
        buildRows(request, response);
        return response;
    }

    private void buildRows(BaseComponentDataRequest request, BaseComponentDataResponse response) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        List<Map<String, Object>> rows = response.getRows();

        //X
        DataModelField x = dataModel.getX().get(0);
        String xName = getColName(x);
        //Y
        DataModelField y = dataModel.getY().get(0);
        String yName = getColName(y);

        Map<String, Object> newRow = Maps.newHashMap();
        for (Map<String, Object> row : rows) {
            int xValue = MapUtils.getIntValue(row, xName);
            int yValue = MapUtils.getIntValue(row, yName);
            newRow.put("title", yName);
            newRow.put("target", xValue);
            newRow.put("measures", yValue);
            if (xValue > yValue){
                newRow.put("ranges", (int) (xValue*1.2));
            }
            else{
                newRow.put("ranges", (int) (yValue*1.2));
            }
        }
        rows.clear();
        rows.add(newRow);
    }


    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException("度量不能为空");
        }
        if (dataModel.getY().size() > 1) {
            throw new BizException("最多可放入一个度量");
        }
        if (!StringUtils.equals(DataModelTypeEnum.DL.getCode(), dataModel.getX().get(0).getQuota())) {
            throw new BizException("只可放入度量");
        }
    }
}
