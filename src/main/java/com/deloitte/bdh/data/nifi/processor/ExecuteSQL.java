package com.deloitte.bdh.data.nifi.processor;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.model.request.CreateProcessorDto;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.deloitte.bdh.data.model.resp.Processor;

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

        // 新建 processor param
        if (MapUtils.isNotEmpty(component)) {
            List<BiEtlParams> paramsList = transferToParams(context, component, biEtlProcessor);
            paramsService.saveBatch(paramsList);
            processor.setList(paramsList);
        }
        context.getProcessorList().add(processor);

        return null;
    }

    @Override
    protected Map<String, Object> delete(ProcessorContext context) throws Exception {
        Processor processor = context.getTempProcessor();
        processorService.delProcessor(processor);

        List<BiEtlParams> paramsList = paramsService.list(new LambdaQueryWrapper<BiEtlParams>().eq(BiEtlParams::getRelCode, processor.getCode()));
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
    public Map<String, Object> update(ProcessorContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> validate(ProcessorContext context) throws Exception {
        return null;

    }

}
