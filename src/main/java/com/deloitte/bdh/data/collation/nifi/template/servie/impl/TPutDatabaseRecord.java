package com.deloitte.bdh.data.collation.nifi.template.servie.impl;

import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.nifi.template.BiParamsEnum;
import com.deloitte.bdh.data.collation.nifi.template.servie.TProcessor;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

//        "config": {
//            "properties": {
//                ""put-db-record-record-reader"": "df4d475b-f256-3ff4-a6b7-1aed7df826c9",
//                        ""put-db-record-statement-type"": "INSERT",
//                        ""put-db-record-dcbp-service"": "c678acab-e9e0-3354-819a-11bd015da8cc",
//                        ""put-db-record-catalog-name"": null,
//                        ""put-db-record-schema-name"": null,
//                        ""put-db-record-table-name"": "aut20190308_test9",
//                        ""put-db-record-translate-field-names"": "true",
//                        ""put-db-record-unmatched-field-behavior"": "Fail on Unmatched Fields",
//                        ""put-db-record-unmatched-column-behavior"": "Fail on Unmatched Columns",
//                        ""put-db-record-update-keys"": null,
//                        ""put-db-record-field-containing-sql"": null,
//                        ""put-db-record-allow-multiple-statements"": "true",
//                        ""put-db-record-quoted-identifiers"": "true",
//                        ""put-db-record-quoted-table-identifiers"": "false",
//                        ""put-db-record-query-timeout"": "0 seconds",
//                        ""rollback-on-failure"": "false",
//                        ""table-schema-cache-size"": "10000",
//                        ""put-db-record-max-batch-size"": "0"
//            },

@Service("tPutDatabaseRecord")
public class TPutDatabaseRecord implements TProcessor {
    @Resource
    private NifiProcessService processService;
    @Value("${nifi.putdbrecordrecordreader}")
    protected String putdbrecordrecordreader = "a5994ef0-0174-1000-0000-00006d114be3";

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
        properties.put(BiParamsEnum.putdbrecordtablename.getKey(), MapUtils.getString(var, BiParamsEnum.putdbrecordtablename.getKey()));
        properties.put(BiParamsEnum.putdbrecordrecordreader.getKey(), putdbrecordrecordreader);
        properties.put(BiParamsEnum.putdbrecorddcbpservice.getKey(), "a5b9fc8e-0174-1000-0000-000039bf90cc");

        Map<String, Object> config = Maps.newHashMap();
        config.put("properties", properties);

        Map<String, Object> component = Maps.newHashMap();
        component.put("id", processorId);
        component.put("config", config);
        processService.updateProcessor(processorId, component);
        return false;
    }
}
