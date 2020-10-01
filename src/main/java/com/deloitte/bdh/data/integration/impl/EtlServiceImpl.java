package com.deloitte.bdh.data.integration.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.enums.BiProcessorsTypeEnum;
import com.deloitte.bdh.data.enums.EffectEnum;
import com.deloitte.bdh.data.enums.YesOrNoEnum;
import com.deloitte.bdh.data.model.*;
import com.deloitte.bdh.data.model.resp.EtlProcessorsResp;
import com.deloitte.bdh.data.nifi.Processor;
import com.deloitte.bdh.data.service.*;
import com.deloitte.bdh.data.nifi.enums.MethodEnum;
import com.google.common.collect.Maps;

import com.deloitte.bdh.data.integration.EtlService;
import com.deloitte.bdh.data.model.request.JoinResourceDto;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.nifi.processors.BiEtlProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@DS(DSConstant.BI_DB)
public class EtlServiceImpl implements EtlService {
    private static final Logger logger = LoggerFactory.getLogger(EtlServiceImpl.class);

    @Autowired
    private BiEtlDatabaseInfService databaseInfService;
    @Autowired
    private BiEtlModelService biEtlModelService;
    @Autowired
    private BiEtlProcessorService processorService;
    @Autowired
    private BiEtlProcess biEtlProcess;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private BiEtlParamsService biEtlParamsService;
    @Autowired
    private BiEtlConnectionService biEtlConnectionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiProcessors joinResource(JoinResourceDto dto) throws Exception {
        BiEtlDatabaseInf biEtlDatabaseInf = databaseInfService.getResource(dto.getSourceId());
        if (null == biEtlDatabaseInf) {
            throw new RuntimeException("EtlServiceImpl.joinResource.error : 未找到目标 数据源");
        }
        BiEtlModel biEtlModel = biEtlModelService.getModel(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.joinResource.error : 未找到目标 模型");
        }

        if (EffectEnum.DISABLE.getKey().equals(biEtlDatabaseInf.getEffect())) {
            throw new RuntimeException("EtlServiceImpl.joinResource.error : 数据源状态不合法");
        }

        //新建processors
        BiProcessors processors = new BiProcessors();
        processors.setCode(GenerateCodeUtil.genProcessors());
        processors.setType(BiProcessorsTypeEnum.JOIN_SOURCE.getType());
        processors.setName(BiProcessorsTypeEnum.getTypeDesc(processors.getType()));
        processors.setTypeDesc(BiProcessorsTypeEnum.getTypeDesc(processors.getType()));
        processors.setStatus(YesOrNoEnum.NO.getKey());
        processors.setEffect(EffectEnum.ENABLE.getKey());
        processors.setValidate(YesOrNoEnum.NO.getKey());
        processors.setRelModelCode(biEtlModel.getCode());
        processors.setVersion("1");
        processors.setCreateDate(LocalDateTime.now());
        processors.setCreateUser(dto.getCreateUser());
        processors.setTenantId(dto.getTenantId());
        processors.setRelSourceId(biEtlDatabaseInf.getId());
        processorsService.save(processors);

        // 判断数据源类型 ,创建processors ，找到对应需要创建的 process 集合
        Map<String, Object> req = Maps.newHashMap();
        req.put("name", "引入数据:" + System.currentTimeMillis());
        req.put("createUser", dto.getCreateUser());
        req.put("tableName", dto.getTableName());
        ProcessorContext context = new ProcessorContext();
        context.setEnumList(BiProcessorsTypeEnum.JOIN_SOURCE.includeProcessor(biEtlDatabaseInf.getType()));
        context.setReq(req);
        context.setMethod(MethodEnum.SAVE);
        context.setModel(biEtlModel);
        context.setBiEtlDatabaseInf(biEtlDatabaseInf);
        context.setProcessors(processors);
        biEtlProcess.etl(context);

        //关联数据源
        return context.getProcessors();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeResource(String processorsCode) throws Exception {
        BiProcessors processors = processorsService.getOne(
                new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getCode, processorsCode));
        if (null == processors) {
            throw new RuntimeException("EtlServiceImpl.removeResource.error : 未找到目标 processors");
        }

        BiEtlDatabaseInf biEtlDatabaseInf = databaseInfService.getResource(processors.getRelSourceId());
        if (null == biEtlDatabaseInf) {
            throw new RuntimeException("EtlServiceImpl.joinResource.error : 未找到目标 数据源");
        }

        BiEtlModel biEtlModel = biEtlModelService.getOne(
                new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, processors.getRelModelCode()));
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.joinResource.error : 未找到目标 模型");
        }

        List<Processor> processorList = processorService.invokeProcessorList(processorsCode);

        List<BiEtlConnection> connectionList = biEtlConnectionService.list(new LambdaQueryWrapper<BiEtlConnection>()
                .eq(BiEtlConnection::getRelProcessorsCode, processorsCode));

        Map<String, Object> req = Maps.newHashMap();
        req.put("createUser", "lw");

        ProcessorContext context = new ProcessorContext();
        context.setEnumList(BiProcessorsTypeEnum.JOIN_SOURCE.includeProcessor(biEtlDatabaseInf.getType()));
        context.setMethod(MethodEnum.DELETE);
        context.setModel(biEtlModel);
        context.setBiEtlDatabaseInf(biEtlDatabaseInf);
        context.setProcessors(processors);
        context.addProcessorList(processorList);
        context.addConnectionList(connectionList);
        context.setReq(req);
        biEtlProcess.etl(context);
        processorsService.removeById(processors.getId());

    }

    @Override
    public EtlProcessorsResp getProcessors(String processorsCode) {
        if (StringUtil.isEmpty(processorsCode)) {
            throw new RuntimeException("EtlServiceImpl.getProcessors.error : processorsCode 不能为空");
        }
        EtlProcessorsResp resp = new EtlProcessorsResp();
        BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getCode, processorsCode));

        if (null != processors) {
            BeanUtils.copyProperties(processors, resp);
            List<BiEtlParams> paramsList = biEtlParamsService.list(
                    new LambdaQueryWrapper<BiEtlParams>().eq(BiEtlParams::getRelProcessorsCode, processorsCode)
            );
            resp.setList(paramsList);
        }
        return resp;
    }
}
