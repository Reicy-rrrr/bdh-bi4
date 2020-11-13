package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.enums.DataImplEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
@Service("graphicsDataImpl")
public class GraphicsDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        String querySql = null;
        if (DataImplEnum.GRAPHICS_PIE.getType().equals(request.getType())) {
            querySql = doPie(request);
        }

        List<Map<String, Object>> result = biUiDemoMapper.selectDemoList(querySql);
        result.forEach(item -> item.put("key", UUID.randomUUID().toString()));
        BaseComponentDataResponse response = new BaseComponentDataResponse();
        response.setRows(result);
        return response;
    }

    private String doPie(BaseComponentDataRequest request) {
        String querySql;
        DataConfig dataConfig = request.getDataConfig();
        DataModel dataModel = dataConfig.getDataModel();
        List<DataModelField> x = dataModel.getX();
        Integer pageIndex = dataModel.getPage();
        Integer pageSize = dataModel.getPageSize();
        String tableName = dataModel.getTableName();
        String[] fields = new String[x.size()];
        for (int i = 0; i < x.size(); i++) {
            fields[i] = x.get(i).getId().replace("O_", "") + " as " + x.get(i).getId();
        }
        String select = "select " + AnalyseUtil.join(",", fields);
        querySql = select + " from " + tableName;
        if (pageIndex != null && pageSize != null && pageSize > 0) {
            querySql = querySql + " limit " + (pageIndex - 1) * pageSize + "," + pageIndex * pageSize;
        }
        return querySql;
    }
}
