package com.deloitte.bdh.data.nifi.processor;


import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.request.CreateProcessorDto;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.deloitte.bdh.data.model.resp.Processor;

import java.util.Map;

@Service("ExecuteSQL")
public class ExecuteSQL extends AbstractProcessor {

    private final static String QUERY = "select * from " + NifiProcessUtil.TEMP;

    @Override
    public Map<String, Object> save(ProcessorContext context) throws Exception {
        //配置数据源的
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("SQL select query", QUERY.replace(NifiProcessUtil.TEMP, MapUtils.getString(context.getReq(), "SQL select query")));
        properties.put("Database Connection Pooling Service", MapUtils.getString(context.getReq(), "Database Connection Pooling Service"));
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
        CreateProcessorDto createProcessorDto = new CreateProcessorDto();
        createProcessorDto.setName(ProcessorTypeEnum.ExecuteSQL.getTypeDesc() + System.currentTimeMillis());
        createProcessorDto.setType(ProcessorTypeEnum.ExecuteSQL.getType());
        createProcessorDto.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
        createProcessorDto.setTenantId(context.getModel().getTenantId());
        createProcessorDto.setProcessorsCode(context.getProcessors().getCode());
        createProcessorDto.setParams(component);
        BiEtlProcessor biEtlProcessor = processorService.createProcessor(createProcessorDto);

        Processor processor = new Processor();
        BeanUtils.copyProperties(biEtlProcessor, processor);

        context.getProcessorList().add(processor);
        return null;
    }


    @Override
    public Map<String, Object> update(ProcessorContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> validate(ProcessorContext context) throws Exception {
        return null;

    }

}
