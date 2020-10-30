package com.deloitte.bdh.data.collation.nifi.processor.impl;


import com.deloitte.bdh.data.collation.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.collation.model.BiEtlParams;
import com.deloitte.bdh.data.collation.model.BiEtlProcessor;
import com.deloitte.bdh.data.collation.nifi.dto.Processor;
import com.deloitte.bdh.data.collation.nifi.dto.ProcessorContext;
import com.deloitte.bdh.data.collation.nifi.processor.AbstractProcessor;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("PutDatabaseRecord")
public class PutDatabaseRecord extends AbstractProcessor {


    @Override
    public Map<String, Object> save(ProcessorContext context) throws Exception {
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

        //配置数据源的
        Map<String, Object> properties = Maps.newHashMap();
//        properties.put("put-db-record-dcbp-service", MapUtils.getString(context.getReq(), "toControllerServiceId"));
        properties.put("put-db-record-table-name", MapUtils.getString(context.getReq(), "toTableName"));
        //todo 基于租户前置创建，此处默认设置
        properties.put("put-db-record-record-reader", "a5994ef0-0174-1000-0000-00006d114be3");
        properties.put("put-db-record-dcbp-service", "a5b9fc8e-0174-1000-0000-000039bf90cc");


        //默认的
        properties.put("put-db-record-statement-type", "INSERT");
        properties.put("put-db-record-allow-multiple-statements", true);
        properties.put("put-db-record-quoted-identifiers", true);
        properties.put("table-schema-cache-size", "10000");

        //调度相关的默认值
        Map<String, Object> config = Maps.newHashMap();
        config.put("schedulingPeriod", "0 sec");
        config.put("schedulingStrategy", "TIMER_DRIVEN");
        config.put("autoTerminatedRelationships", new String[]{"success"});
        //多线程8个
        config.put("concurrentlySchedulableTaskCount", "8");

        config.put("properties", properties);

        //processor 公共的
        Map<String, Object> component = Maps.newHashMap();
        component.put("name", processorType().getTypeDesc() + System.currentTimeMillis());
        component.put("type", processorType().getvalue());
        component.put("config", config);

        //新建 processor
        BiEtlProcessor biEtlProcessor = super.createProcessor(context, component);
        // 新建 processor param
        List<BiEtlParams> paramsList = super.createParams(biEtlProcessor, context, component);

        Processor processor = new Processor();
        BeanUtils.copyProperties(biEtlProcessor, processor);
        processor.setList(paramsList);
        context.addProcessorList(processor);
        return null;
    }

    @Override
    protected Map<String, Object> rSave(ProcessorContext context) throws Exception {
        Processor processor = context.getTempProcessor();
        processorService.delProcessor(processor.getId());

        List<BiEtlParams> paramsList = processor.getList();
        if (CollectionUtils.isNotEmpty(paramsList)) {
            List<String> list = paramsList
                    .stream()
                    .map(BiEtlParams::getId)
                    .collect(Collectors.toList());
            paramsService.removeByIds(list);
        }

        return null;
    }

    @Override
    protected Map<String, Object> delete(ProcessorContext context) throws Exception {
        Processor processor = context.getTempProcessor();
        processorService.delProcessor(processor.getId());
        List<BiEtlParams> paramsList = processor.getList();
        if (CollectionUtils.isNotEmpty(paramsList)) {
            List<String> list = paramsList
                    .stream()
                    .map(BiEtlParams::getId)
                    .collect(Collectors.toList());
            paramsService.removeByIds(list);
        }

        return null;
    }

    @Override
    protected Map<String, Object> rDelete(ProcessorContext context) throws Exception {
        List<BiEtlParams> sourceParamList = context.getTempProcessor().getList();
        //获取删除前的参数 map
        Map<String, Object> sourceParam = transferToMap(sourceParamList);
        //新建 删除的 processor
        BiEtlProcessor biEtlProcessor = createProcessor(context, sourceParam);
        //新建 processor param
        createParams(biEtlProcessor, context, sourceParam);
        //补偿删除必须要调用该方法
        setTempForRdelete(biEtlProcessor, context);
        return null;
    }


    @Override
    public Map<String, Object> update(ProcessorContext context) throws Exception {
        return null;
    }

    @Override
    protected Map<String, Object> rUpdate(ProcessorContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> validate(ProcessorContext context) throws Exception {
        return null;

    }

    @Override
    protected ProcessorTypeEnum processorType() {
        return ProcessorTypeEnum.PutDatabaseRecord;
    }

}
