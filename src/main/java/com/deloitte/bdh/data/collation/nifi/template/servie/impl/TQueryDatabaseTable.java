package com.deloitte.bdh.data.collation.nifi.template.servie.impl;

import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.nifi.template.BiParamsEnum;
import com.deloitte.bdh.data.collation.nifi.template.servie.TProcessor;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

//        "properties": {
//            ""Database Connection Pooling Service"": "67b6920b-5f10-3f72-aae8-71b5c624a51c",
//                    ""db-fetch-db-type"": "Generic",
//                    ""Table Name"": "aut20190308",
//                    ""Columns to Return"": "123,123",
//                    ""db-fetch-where-clause"": "11",
//                    ""db-fetch-sql-query"": null,
//                    ""Maximum-value Columns"": "id",
//                    ""Max Wait Time"": "0 seconds",
//                    ""Fetch Size"": "123333",
//                    ""qdbt-max-rows"": "40000",
//                    ""qdbt-output-batch-size"": "0",
//                    ""qdbt-max-frags"": "0",
//                    ""dbf-normalize"": "true",
//                    ""transaction-isolation-level"": null,
//                    ""dbf-user-logical-types"": "false",
//                    ""dbf-default-precision"": "100000000",
//                    ""dbf-default-scale"": "100000000"

@Service("tQueryDatabaseTable")
public class TQueryDatabaseTable implements TProcessor {
    @Resource
    private NifiProcessService processService;

    @Override
    public boolean update(String processorId, Map<String, Object> var) throws Exception {

        ////        "id":"9fdef751-51d6-38ba-bf17-7b1bb0ebf55f",
        ////        "config": {
        ////        "properties": {
        ////        "Table Name": "test"
        ////        }
        ////        }
        //配置数据源的
        Map<String, Object> properties = Maps.newHashMap();
        properties.put(BiParamsEnum.DCPS.getKey(), MapUtils.getString(var, BiParamsEnum.DCPS.getKey()));
        properties.put(BiParamsEnum.TableName.getKey(), MapUtils.getString(var, BiParamsEnum.TableName.getKey()));
        properties.put(BiParamsEnum.ColumnstoReturn.getKey(), MapUtils.getString(var, BiParamsEnum.ColumnstoReturn.getKey()));

        if (null != var.get(BiParamsEnum.dbfetchwhereclause.getKey())) {
            properties.put(BiParamsEnum.dbfetchwhereclause.getKey(), MapUtils.getString(var, BiParamsEnum.dbfetchwhereclause.getKey()));
        }
        properties.put(BiParamsEnum.MaximumvalueColumns.getKey(), MapUtils.getString(var, BiParamsEnum.MaximumvalueColumns.getKey()));

        Map<String, Object> config = Maps.newHashMap();
        config.put("properties", properties);

        Map<String, Object> component = Maps.newHashMap();
        component.put("id", processorId);
        component.put("config", config);
        processService.updateProcessor(processorId, component);
        return false;
    }
}
