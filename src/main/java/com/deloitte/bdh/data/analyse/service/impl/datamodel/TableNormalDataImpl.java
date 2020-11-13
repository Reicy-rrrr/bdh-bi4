package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.analyse.constants.AnalyseTypeConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
@Service("tableNormalDataImpl")
public class TableNormalDataImpl extends AbstractDataService implements AnalyseDataService {

    @Resource
    BiUiDemoMapper biUiDemoMapper;

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        String type = request.getType();
        if (AnalyseTypeConstants.TABLE.equals(type)) {
//            GridComponentDataRequest request = JSONObject.parseObject(JSONObject.toJSONString(data), GridComponentDataRequest.class);
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
            String querySql = select + " from " + tableName;
            if (pageIndex != null && pageSize != null && pageSize > 0) {
                querySql = querySql + " limit " + (pageIndex - 1) * pageSize + "," + pageIndex * pageSize;
            }
            List<Map<String, Object>> result = biUiDemoMapper.selectDemoList(querySql);
            //todo 需要知道那个列是主键,然后加到上面的sql中作为一定查询的列 as key
            result.forEach(item -> {
                item.put("key", UUID.randomUUID().toString());
            });
            BaseComponentDataResponse response = new BaseComponentDataResponse();
            response.setRows(result);
            return response;
        }
        return null;
    }
}
