package com.deloitte.bdh.data.nifi.processor.impl;



import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.*;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.nifi.processor.AbstractProcessor;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.deloitte.bdh.data.nifi.Processor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("ExecuteSQL")
public class ExecuteSQL extends AbstractProcessor {

    private final static String QUERY = "select * from " + NifiProcessUtil.TEMP;

    @Override
    public Map<String, Object> save(ProcessorContext context) throws Exception {
        //配置数据源的
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("SQL select query", QUERY.replace(NifiProcessUtil.TEMP, MapUtils.getString(context.getReq(), "tableName")));
        properties.put("Database Connection Pooling Service", context.getBiEtlDatabaseInf().getControllerServiceId());
        //调度相关的默认值
        Map<String, Object> config = Maps.newHashMap();
        config.put("schedulingPeriod", "1 * * * * ?");
        config.put("schedulingStrategy", "CRON_DRIVEN");
        config.put("properties", properties);

        //processor 公共的
        Map<String, Object> component = Maps.newHashMap();
        component.put("name", MapUtils.getString(context.getReq(), "name"));
        component.put("type", ProcessorTypeEnum.ExecuteSQL.getvalue());
        component.put("config", config);

        //新建 processor
        BiEtlProcessor biEtlProcessor = createProcessor(context, component);
        // 新建 processor param
        List<BiEtlParams> paramsList = createParams(biEtlProcessor, context, component);
        //该组件有关联表的信息
        BiEtlDbRef dbRef = createDbRef(biEtlProcessor, context);

        Processor processor = new Processor();
        BeanUtils.copyProperties(biEtlProcessor, processor);
        processor.setList(paramsList);
        processor.setDbRef(dbRef);
        context.addProcessor(processor);
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

        //删除该组件有关联表的信息
        etlDbRefService.removeById(processor.getDbRef().getId());
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

        //删除该组件有关联表的信息
        etlDbRefService.removeById(processor.getDbRef().getId());
        return null;
    }

    @Override
    protected Map<String, Object> rDelete(ProcessorContext context) throws Exception {
        Processor processor = context.getTempProcessor();
//        processor.getList()

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
