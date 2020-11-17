package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.enums.CustomParamsEnum;
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

import java.util.Map;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:普通表格
 */
@Service("tableNormalDataImpl")
public class TableNormalDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        Map<String, String> customParams = request.getDataConfig().getDataModel().getCustomParams();
        if (MapUtils.isNotEmpty(customParams)) {
            String tableAggregate = MapUtils.getString(customParams, CustomParamsEnum.TABLE_AGGREGATE.getCode());
            if (StringUtils.equals(tableAggregate, "true")) {
                if (CollectionUtils.isNotEmpty(request.getDataConfig().getDataModel().getX())) {
                    for (DataModelField field : request.getDataConfig().getDataModel().getX()) {
                        field.setQuota(DataModelTypeEnum.WD.getCode());
                    }
                }
            }
        }
        String sql = buildSql(request.getDataConfig().getDataModel());
        return execute(sql);
//        DataConfig dataConfig = request.getDataConfig();
//        DataModel dataModel = dataConfig.getDataModel();
//        List<DataModelField> x = dataModel.getX();
//        Integer pageIndex = dataModel.getPage();
//        Integer pageSize = dataModel.getPageSize();
//        String tableName = dataModel.getTableName();
//        String[] fields = new String[x.size()];
//        List<String> aggregateField = Lists.newArrayList();
//        if (dataConfig.getTableAggregate()) {
//            for (int i = 0; i < x.size(); i++) {
//                if (StringUtils.equals(x.get(i).getQuota(), DataModelTypeEnum.DL.getCode())) {
//                    if (StringUtils.isNotBlank(x.get(i).getAlias())) {
//                        fields[i] = "sum(" + x.get(i).getId() + ") as " + x.get(i).getAlias();
//                    } else {
//                        fields[i] = "sum(" + x.get(i).getId() + ") as " + x.get(i).getId();
//                    }
//                } else {
//                    if (StringUtils.isNotBlank(x.get(i).getAlias())) {
//                        fields[i] = x.get(i).getId() + " as " + x.get(i).getAlias();
//                    } else {
//                        fields[i] = x.get(i).getId() + " as " + x.get(i).getId();
//                    }
//                    aggregateField.add(x.get(i).getId());
//                }
//
//            }
//        } else {
//            for (int i = 0; i < x.size(); i++) {
//                if (StringUtils.isNotBlank(x.get(i).getAlias())) {
//                    fields[i] = x.get(i).getId() + " as " + x.get(i).getAlias();
//                } else {
//                    fields[i] = x.get(i).getId() + " as " + x.get(i).getId();
//                }
//            }
//        }
//
//        String select = "select " + AnalyseUtil.join(",", fields);
//        String querySql = select + " from " + tableName;
//        if (CollectionUtils.isNotEmpty(aggregateField)) {
//            querySql += " group by " + StringUtils.join(aggregateField, ",");
//        }
//        if (pageIndex != null && pageSize != null && pageSize > 0) {
//            querySql = querySql + " limit " + (pageIndex - 1) * pageSize + "," + pageIndex * pageSize;
//        }
//        return execute(querySql);
    }

    @Override
    protected void validate(DataModel dataModel) {
        //todo
    }
}
