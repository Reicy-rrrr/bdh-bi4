package com.deloitte.bdh.data.nifi.processor;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.request.CreateProcessorDto;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.service.BiEtlParamsService;
import com.deloitte.bdh.data.service.BiEtlProcessorService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractProcessor implements Processor {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

    @Autowired
    protected BiEtlProcessorService processorService;
    @Autowired
    protected BiEtlParamsService paramsService;

    @Override
    final public Map<String, Object> pProcess(ProcessorContext context) throws Exception {
        Map<String, Object> result = Maps.newHashMap();
        switch (context.getMethod()) {
            case SAVE:
                save(context);
                break;
            case DELETE:
                delete(context);
                break;
            case UPDATE:
                update(context);
                break;
            case VALIDATE:
                validate(context);
                break;
            default:
                logger.error("未找到正确的 Processor 处理器");
        }
        return result;
    }

    @Override
    final public Map<String, Object> rProcess(ProcessorContext context) throws Exception {
        Map<String, Object> result = Maps.newHashMap();
        switch (context.getMethod()) {
            case SAVE:
                delete(context);
                break;
            case DELETE:
                save(context);
                break;
            case UPDATE:
                update(context);
                break;
            case VALIDATE:
                validate(context);
                break;
            default:
                logger.error("未找到正确的 Processor 处理器");
        }
        return result;
    }

    protected abstract Map<String, Object> save(ProcessorContext context) throws Exception;

    protected Map<String, Object> delete(ProcessorContext context) throws Exception {
        com.deloitte.bdh.data.nifi.Processor processor = context.getTempProcessor();
        processorService.delProcessor(processor.getId());
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

    protected abstract Map<String, Object> update(ProcessorContext context) throws Exception;

    protected abstract Map<String, Object> validate(ProcessorContext context) throws Exception;

    final protected BiEtlProcessor createProcessor(ProcessorContext context, Map<String, Object> component) throws Exception {
        CreateProcessorDto createProcessorDto = new CreateProcessorDto();
        createProcessorDto.setName(processorType().getTypeDesc() + System.currentTimeMillis());
        createProcessorDto.setType(processorType().getType());
        createProcessorDto.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
        createProcessorDto.setTenantId(context.getModel().getTenantId());
        createProcessorDto.setProcessorsCode(context.getProcessors().getCode());
        createProcessorDto.setParams(component);
        BiEtlProcessor biEtlProcessor = processorService.createProcessor(createProcessorDto);
        return biEtlProcessor;
    }

    final protected List<BiEtlParams> transferToParams(ProcessorContext context, Map<String, Object> source, BiEtlProcessor biEtlProcessor) throws Exception {
        List<BiEtlParams> list = Lists.newArrayList();
        for (Map.Entry<String, Object> var : source.entrySet()) {
            String key = var.getKey();
            Object value = var.getValue();
            if ("position".equals(key)) {
                continue;
            }

            if (value instanceof Map) {
                list.addAll(transferToParams(context, (Map<String, Object>) value, biEtlProcessor));
            } else {
                BiEtlParams params = new BiEtlParams();
                params.setCode(GenerateCodeUtil.genParam());
                params.setName(key);
                params.setParamKey(key);
                params.setParamValue(JsonUtil.obj2String(value));
                params.setParamsComponent("PROCESSOR");
                params.setRelCode(biEtlProcessor.getCode());
                params.setRelProcessorsCode(context.getProcessors().getCode());
                params.setCreateDate(LocalDateTime.now());
                params.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
                params.setTenantId(context.getProcessors().getTenantId());
                list.add(params);
            }

        }
        return list;
    }

    protected abstract ProcessorTypeEnum processorType();

}
