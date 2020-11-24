package com.deloitte.bdh.data.analyse.service.impl.datamodel;

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
 * 带图例的实现类
 */
@Service("categoryDataImpl")
public class CategoryDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getY())) {
            dataModel.getY().forEach(field -> dataModel.getX().add(field));
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getCategory())) {
            dataModel.getCategory().forEach(field -> dataModel.getX().add(field));
        }
        BaseComponentDataResponse response = execute(buildSql(request.getDataConfig().getDataModel()));
        buildCategory(request, response);
        return response;
    }

    private void buildCategory(BaseComponentDataRequest request, BaseComponentDataResponse response) {
        List<Map<String, Object>> rows = response.getRows();
        DataModel dataModel = request.getDataConfig().getDataModel();
        if (CollectionUtils.isNotEmpty(rows) && CollectionUtils.isNotEmpty(dataModel.getCategory())) {
            for (Map<String, Object> row : rows) {
                List<String> categoryValue = Lists.newArrayList();
                for (DataModelField category : dataModel.getCategory()) {
                    categoryValue.add(MapUtils.getString(row,category.getId()));
                }
                row.put("category", StringUtils.join(categoryValue, "-"));
            }
        }
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException("维度不能为空");
        }
        if (CollectionUtils.isEmpty(dataModel.getY())) {
            throw new BizException("度量不能为空");
        }
        if (dataModel.getY().size() > 2) {
            throw new BizException("最多可放入两个度量");
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
