package com.deloitte.bdh.data.service.impl;

import java.time.LocalDateTime;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.enums.*;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.dao.bi.BiEtlProcessorMapper;
import com.deloitte.bdh.data.model.request.CreateProcessorDto;
import com.deloitte.bdh.data.model.request.RunModelDto;
import com.deloitte.bdh.data.model.request.UpdateModelDto;
import com.deloitte.bdh.data.service.BiEtlParamsService;
import com.deloitte.bdh.data.service.BiEtlProcessorService;
import com.deloitte.bdh.common.base.AbstractService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-09-25
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlProcessorServiceImpl extends AbstractService<BiEtlProcessorMapper, BiEtlProcessor> implements BiEtlProcessorService {
    @Resource
    private BiEtlProcessorMapper processorMapper;
    @Autowired
    NifiProcessService nifiProcessService;
    @Autowired
    BiEtlParamsService paramsService;

    @Override
    public Pair<BiEtlProcessor, List<BiEtlParams>> getProcessor(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("getProcessor error:processorId 不嫩为空");
        }
        BiEtlProcessor processor = processorMapper.selectById(id);
        if (null == processor) {
            throw new RuntimeException("getProcessor error:未找到对应的对象");
        }

        //获取对应processor 参数集合
        List<BiEtlParams> paramsList = paramsService.list(
                new LambdaQueryWrapper<BiEtlParams>()
                        .eq(BiEtlParams::getRelateCode, processor.getCode())
        );
        return new Pair(processor, paramsList);
    }

    @Override
    public BiEtlProcessor createProcessor(CreateProcessorDto dto) throws Exception {
        BiEtlProcessor processor = new BiEtlProcessor();
        BeanUtils.copyProperties(dto, processor);
        processor.setCode("Pro" + System.currentTimeMillis());
        processor.setTypeDesc(ProcessorTypeEnum.getTypeDesc(dto.getType()));
        processor.setStatus(RunStatusEnum.STOP.getKey());
        processor.setEffect(EffectEnum.DISABLE.getKey());
        processor.setValidate(YesOrNoEnum.NO.getKey());
        processor.setValidateMessage(YesOrNoEnum.NO.getvalue());
        processor.setCreateDate(LocalDateTime.now());

        //nifi 创建 processor
        Map<String, Object> reqNifi = Maps.newHashMap();
        reqNifi.put("name", dto.getName());
        //此处去nifi value
        reqNifi.put("type", ProcessorTypeEnum.getNifiValue(dto.getType()));
        reqNifi.put("position", JsonUtil.string2Obj(dto.getPosition(), Map.class));
        Map<String, Object> source = nifiProcessService.createProcessor(reqNifi, dto.getProcessGroupId());

        processor.setProcessId(MapUtils.getString(source, "id"));
        processor.setVersion(NifiProcessUtil.getVersion(source));

        processorMapper.insert(processor);
        return processor;
    }

    @Override
    public BiEtlProcessor updateProcessor(UpdateModelDto dto) throws Exception {
//        Map<String, Object> reqNifi = Maps.newHashMap();
//        Map<String, Object> source = nifiProcessService.updateProcessor(reqNifi);
        return null;
    }

    @Override
    public BiEtlProcessor runProcessor(RunModelDto dto) throws Exception {
        return null;
    }

    @Override
    public void delProcessor(String id) throws Exception {

    }

    @Override
    public Map<String, Object> joinResource(String processorId, String controllerServiceId, String userId, String tableName) throws Exception {
        String querySql = "select * from #";
        BiEtlProcessor processor = processorMapper.selectById(processorId);
        if (null == processor) {
            throw new RuntimeException("BiEtlProcessorServiceImpl.joinResource error:未找到目标");
        }

        Map<String, Object> properties = Maps.newHashMap();
        properties.put("Database Connection Pooling Service", controllerServiceId);
        properties.put("SQL select query", querySql.replace("#", tableName));

        Map<String, Object> config = Maps.newHashMap();
        config.put("concurrentlySchedulableTaskCount", "1");
        config.put("schedulingPeriod", "0 sec");
        config.put("executionNode", "ALL");
        config.put("penaltyDuration", "30 sec");
        config.put("yieldDuration", "1 sec");
        config.put("bulletinLevel", "WARN");
        config.put("schedulingStrategy", "TIMER_DRIVEN");
        config.put("properties", "properties");

        Map<String, Object> reqNifi = Maps.newHashMap();
        reqNifi.put("id", processorId);
        reqNifi.put("config", config);
        reqNifi.put("state", "STOPPED");

        Map<String, Object> source = nifiProcessService.updateProcessor(reqNifi);
        processor.setModifiedDate(LocalDateTime.now());
        processor.setModifiedUser(userId);
        processor.setVersion(NifiProcessUtil.getVersion(source));
        processorMapper.updateById(processor);

        //保存processor 的 配置信息，此处只有 properties
        BiEtlParams dcps = new BiEtlParams();
        dcps.setCode("Pr" + System.currentTimeMillis());
        dcps.setName("数据库连接池");
        dcps.setKey("Database Connection Pooling Service");
        dcps.setValue(controllerServiceId);
        dcps.setParamsGroup(ParamsGroupEnum.PROPERTIES.getKey());
        dcps.setParamsComponent(ParamsComponentEnum.PROCESSOR.getKey());
        dcps.setRelateCode(processor.getCode());
        dcps.setCreateDate(LocalDateTime.now());
        dcps.setCreateUser(userId);
        dcps.setIp("");
        dcps.setTenantId(processor.getTenantId());

        BiEtlParams ssq = new BiEtlParams();
        ssq.setCode("Pr" + System.currentTimeMillis());
        ssq.setName("查询语句");
        ssq.setKey("SQL select query");
        ssq.setValue(querySql.replace("#", tableName));
        ssq.setParamsGroup(ParamsGroupEnum.PROPERTIES.getKey());
        ssq.setParamsComponent(ParamsComponentEnum.PROCESSOR.getKey());
        ssq.setRelateCode(processor.getCode());
        ssq.setCreateDate(LocalDateTime.now());
        ssq.setCreateUser(userId);
        ssq.setIp("");
        ssq.setTenantId(processor.getTenantId());

        List<BiEtlParams> paramsList = Lists.newArrayList();
        paramsList.add(dcps);
        paramsList.add(ssq);
        paramsService.saveBatch(paramsList);
        return source;
    }


}
