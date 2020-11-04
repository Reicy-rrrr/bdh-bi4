package com.deloitte.bdh.data.collation.nifi.processor.impl;


import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.collation.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.collation.model.BiEtlDbRef;
import com.deloitte.bdh.data.collation.model.BiEtlParams;
import com.deloitte.bdh.data.collation.model.BiEtlProcessor;
import com.deloitte.bdh.data.collation.nifi.dto.ProcessorContext;
import com.deloitte.bdh.data.collation.nifi.processor.AbstractProcessor;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.deloitte.bdh.data.collation.nifi.dto.Processor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("ExecuteSQL")
public class ExecuteSQL extends AbstractProcessor {

    @Override
    public Map<String, Object> save(ProcessorContext context) throws Exception {
//        ""Database Connection Pooling Service"": "b8f52732-0174-1000-ffff-ffffeba67f67",
//                ""sql-pre-query"": null,
//                ""SQL select query"": "select * from oracle_data",
//                ""sql-post-query"": null,
//                ""Max Wait Time"": "0 seconds",
//                ""dbf-normalize"": "true",
//                ""dbf-user-logical-types"": "false",
//                ""compression-format"": "NONE",
//                ""dbf-default-precision"": "10",
//                ""dbf-default-scale"": "0",
//                ""esql-max-rows"": "0",
//                ""esql-output-batch-size"": "11111",
//                ""esql-fetch-size"": "11111"
        //配置数据源的
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("SQL select query", MapUtils.getString(context.getReq(), "sqlSelectQuery"));
        //todo a5b9fc8e-0174-1000-0000-000039bf90cc
//        properties.put("Database Connection Pooling Service", MapUtils.getString(context.getReq(), "fromControllerServiceId"));
        properties.put("Database Connection Pooling Service", "a5b9fc8e-0174-1000-0000-000039bf90cc");

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
        return ProcessorTypeEnum.ExecuteSQL;
    }

}
