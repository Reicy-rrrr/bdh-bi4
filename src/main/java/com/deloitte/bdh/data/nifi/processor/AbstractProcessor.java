package com.deloitte.bdh.data.nifi.processor;

import java.time.LocalDateTime;

import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.BiEtlDbRef;
import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.model.request.CreateProcessorDto;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.service.BiEtlDbRefService;
import com.deloitte.bdh.data.service.BiEtlParamsService;
import com.deloitte.bdh.data.service.BiEtlProcessorService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public abstract class AbstractProcessor extends AbstractCurdProcessor implements Processor {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

    @Autowired
    protected BiEtlProcessorService processorService;
    @Autowired
    protected BiEtlParamsService paramsService;
    @Autowired
    protected BiEtlDbRefService etlDbRefService;

    protected abstract ProcessorTypeEnum processorType();

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
                rSave(context);
                break;
            case DELETE:
                rDelete(context);
                break;
            case UPDATE:
                rUpdate(context);
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
    protected Map<String, Object> rDelete(ProcessorContext context) throws Exception {
        List<BiEtlParams> sourceParamList = context.getTempProcessor().getList();
        Map<String, Object> sourceParam = transferToMap(sourceParamList);
        return sourceParam;
    }


    final protected BiEtlProcessor createProcessor(ProcessorContext context, Map<String, Object> component) throws Exception {
        CreateProcessorDto createProcessorDto = new CreateProcessorDto();
        createProcessorDto.setName(processorType().getTypeDesc() + System.currentTimeMillis());
        createProcessorDto.setType(processorType().getType());
        createProcessorDto.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
        createProcessorDto.setTenantId(context.getModel().getTenantId());
        createProcessorDto.setProcessors(context.getProcessors());
        createProcessorDto.setParams(component);
        BiEtlProcessor biEtlProcessor = processorService.createProcessor(createProcessorDto);
        return biEtlProcessor;
    }

    final protected List<BiEtlParams> createParams(BiEtlProcessor etlProcessor, ProcessorContext context, Map<String, Object> component) {
        List<BiEtlParams> paramsList = null;
        if (MapUtils.isNotEmpty(component)) {
            paramsList = transferToParams(context, etlProcessor.getCode(), component, null, true);
            paramsService.saveBatch(paramsList);
        }
        return paramsList;
    }

    final protected BiEtlDbRef createDbRef(BiEtlProcessor etlProcessor, ProcessorContext context) {
        BiEtlDbRef dbRef = new BiEtlDbRef();
        dbRef.setCode(GenerateCodeUtil.genDbRef());
        dbRef.setSourceId(context.getBiEtlDatabaseInf().getId());
        dbRef.setProcessorCode(etlProcessor.getCode());
        dbRef.setProcessorsCode(context.getProcessors().getCode());
        dbRef.setModelCode(context.getModel().getCode());
        dbRef.setCreateDate(LocalDateTime.now());
        dbRef.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
        dbRef.setTenantId(context.getModel().getTenantId());
        etlDbRefService.save(dbRef);
        return dbRef;
    }

    final protected void setTempForRdelete(BiEtlProcessor etlProcessor, ProcessorContext context) {
        com.deloitte.bdh.data.nifi.Processor processor = new com.deloitte.bdh.data.nifi.Processor();
        BeanUtils.copyProperties(etlProcessor, processor);
        context.removeProcessorTemp();
        context.addProcessorTemp(processor);
    }

    private List<BiEtlParams> transferToParams(ProcessorContext context, String processorCode, Map<String, Object> source, String refParamCode, boolean levelOne) {
        List<BiEtlParams> list = Lists.newArrayList();
        for (Map.Entry<String, Object> var : source.entrySet()) {
            String key = var.getKey();
            Object value = var.getValue();
            String code = GenerateCodeUtil.genParam();

            if (refParamCode == null) {
                refParamCode = code;
            }

            BiEtlParams params = new BiEtlParams();
            params.setCode(code);
            params.setName(key);
            params.setParamKey(key);
            params.setParamValue((value instanceof Map) ? "null" : JsonUtil.obj2String(value));
            params.setParamsComponent("PROCESSOR");
            params.setRelCode(processorCode);
            params.setRelProcessorsCode(context.getProcessors().getCode());
//            params.setRelProcessorsCode("context.getProcessors().getCode()");
            params.setCreateDate(LocalDateTime.now());
            params.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
//            params.setCreateUser("MapUtils.getString(context.getReq())");
            params.setTenantId(context.getProcessors().getTenantId());
//            params.setTenantId("context.getProcessors().getTenantId()");
            params.setParentCode(refParamCode);
            list.add(params);

            if (value instanceof Map) {
                list.addAll(transferToParams(context, processorCode, (Map<String, Object>) value, code, false));
            } else {
                if (levelOne) {
                    refParamCode = null;
                }
            }
        }
        return list;
    }

    final protected Map<String, Object> transferToMap(List<BiEtlParams> list) {
        List<BiEtlParams> out = Lists.newArrayList();
        for (BiEtlParams params : list) {
            if (params.getCode().equals(params.getParentCode())) {
                out.add(params);
            }
        }
        Map<String, Object> map = Maps.newHashMap();
        for (BiEtlParams params : out) {
            boolean hasNext = false;
            for (BiEtlParams var : list) {
                if (var.getParentCode().equals(params.getCode()) && !var.getParentCode().equals(var.getCode())) {
                    hasNext = true;
                    break;
                }
            }
            if (!hasNext) {
                if (!params.getCode().equals(params.getParentCode())) {
                    continue;
                }
                map.put(params.getParamKey(), params.getParamValue());
            } else {
                map.put(params.getParamKey(), listTransferToParams(list, params.getCode()));
            }
        }
        return map;
    }

    private Map<String, Object> listTransferToParams(List<BiEtlParams> list, String sourceCode) {
        Map<String, Object> source = Maps.newHashMap();
        for (BiEtlParams params : list) {
            if (params.getParentCode().equals(sourceCode) && !params.getParentCode().equals(params.getCode())) {
                boolean hasNext = false;
                for (BiEtlParams var : list) {
                    if (var.getParentCode().equals(params.getCode()) && !var.getParentCode().equals(var.getCode())) {
                        hasNext = true;
                        break;
                    }
                }
                if (!hasNext) {
                    source.put(params.getParamKey(), params.getParamValue());
                } else {
                    source.put(params.getParamKey(), listTransferToParams(list, params.getCode()));

                }

            }
        }
        return source;
    }

//    public static void main(String[] args) {
//        Map<String, Object> properties = Maps.newHashMap();
//        properties.put("SQL select query", "SQL select query");
//        properties.put("Database Connection Pooling Service", "SQL 333 query");
//        //调度相关的默认值
//        Map<String, Object> config = Maps.newHashMap();
//        config.put("schedulingPeriod", "1 * * * * ?");
//        config.put("schedulingStrategy", "CRON_DRIVEN");
//        config.put("properties", properties);
//
//        //processor 公共的
//        Map<String, Object> component = Maps.newHashMap();
//        component.put("name", "SQL select 3333");
//        component.put("type", ProcessorTypeEnum.ExecuteSQL.getvalue());
//        component.put("config", config);
//
//        List<BiEtlParams> list=AbstractProcessor.transferToParams(null, "123", component, null, true);
//        Map<String, Object> map= transferToMap(list);
//        System.out.println(JsonUtil.obj2String(map));
//    }
}
