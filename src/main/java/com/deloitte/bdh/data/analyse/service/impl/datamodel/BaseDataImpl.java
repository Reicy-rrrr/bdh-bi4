package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:18/11/2020
 * Description:
 */
@Service("baseDataImpl")
public class BaseDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        DataModelField field = request.getDataConfig().getDataModel().getX().get(0);
        String sql = buildSql(request.getDataConfig().getDataModel());
        BaseComponentDataResponse response = execute(sql);
        List<Map<String, Object>> rows = response.getRows();
        List<Map<String, Object>> newRows = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(rows)) {
            for (Map<String, Object> map : rows) {
                Map<String, Object> newMap = Maps.newHashMap();
                newMap.put("value", MapUtils.getString(map, field.getId()));
                newRows.add(newMap);
            }
            response.setRows(newRows);
        }
        return response;
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException("维度不能为空");
        }
        if (dataModel.getX().size() > 1) {
            throw new BizException("维度数量只能为1");
        }
    }
}
