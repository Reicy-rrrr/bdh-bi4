package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        List<Map<String, Object>> y1 = buildCategory(request, response.getRows(), dataModel.getY());
        List<Map<String, Object>> y2 = buildCategory(request, response.getRows(), dataModel.getY2());
        response.setRows(y1);
        response.setY2(y2);
        return response;
    }

    private List<Map<String, Object>> buildCategory(BaseComponentDataRequest request, List<Map<String, Object>> rows, List<DataModelField> yList) {
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
                    if (yList.size() > 1) {
                        newRow.put("category", categoryPrefixName + "-" + colName);
                    } else {
                        newRow.put("category", categoryPrefixName);
                    }
                } else {
                    newRow.put("category", colName);
                }

                newRow.put("value", MapUtils.getString(row, colName));
                newRows.add(newRow);
            }
        }
        return newRows;
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException("维度不能为空");
        }
        if (CollectionUtils.isEmpty(dataModel.getY())) {
            throw new BizException("度量不能为空");
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
