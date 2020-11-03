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

@Service("QueryDatabaseTable")
public class QueryDatabaseTable extends AbstractProcessor {


    @Override
    public Map<String, Object> save(ProcessorContext context) throws Exception {
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
        //配置数据源的
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("Database Connection Pooling Service", MapUtils.getString(context.getReq(), "fromControllerServiceId"));
        properties.put("Table Name", MapUtils.getString(context.getReq(), "fromTableName"));
        properties.put("Columns to Return", MapUtils.getString(context.getReq(), "Columns to Return"));

        if (null != context.getReq().get("db-fetch-where-clause")) {
            properties.put("db-fetch-where-clause", MapUtils.getString(context.getReq(), "Additional WHERE clause"));
        }
        //自增字段
        properties.put("Maximum-value Columns", MapUtils.getString(context.getReq(), "Maximum-value Columns"));

        //默认的
        properties.put("db-fetch-db-type", "Generic");
        properties.put("dbf-normalize", true);
        properties.put("qdbt-max-rows", "40000");
        properties.put("dbf-default-precision", "100000000");
        properties.put("dbf-default-scale", "100000000");

        //调度相关的默认值
        Map<String, Object> config = Maps.newHashMap();
        config.put("schedulingPeriod", "129600 min");
        config.put("schedulingStrategy", "TIMER_DRIVEN");
        config.put("yieldDuration", "36000 sec");

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
        return ProcessorTypeEnum.QueryDatabaseTable;
    }

}
