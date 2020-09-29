package com.deloitte.bdh.data.integration.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.data.enums.BiProcessorsTypeEnum;
import com.deloitte.bdh.data.enums.EffectEnum;
import com.deloitte.bdh.data.enums.YesOrNoEnum;
import com.deloitte.bdh.data.model.*;
import com.deloitte.bdh.data.service.*;
import com.google.common.collect.Lists;
import com.deloitte.bdh.data.nifi.MethodEnum;
import com.google.common.collect.Maps;

import com.deloitte.bdh.data.integration.EtlService;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.request.JoinResourceDto;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.nifi.processors.BiEtlProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@DS(DSConstant.BI_DB)
public class EtlServiceImpl implements EtlService {
    private static final Logger logger = LoggerFactory.getLogger(EtlServiceImpl.class);

    @Autowired
    private NifiProcessService nifiProcessService;
    @Autowired
    private BiEtlDatabaseInfService databaseInfService;
    @Autowired
    private BiEtlGroupDbRefService groupDbRefService;
    @Autowired
    private BiEtlModelService biEtlModelService;
    @Autowired
    private BiEtlProcessorService processorService;
    @Autowired
    private BiEtlProcess biEtlProcess;
    @Autowired
    private BiProcessorsService processorsService;


    @Override
    @Transactional
    public void joinResource(JoinResourceDto dto) throws Exception {
        BiEtlDatabaseInf biEtlDatabaseInf = databaseInfService.getResource(dto.getSourceId());
        if (null == biEtlDatabaseInf) {
            throw new RuntimeException("EtlServiceImpl.joinResource.error:未找到目标 数据源");
        }
        BiEtlModel biEtlModel = biEtlModelService.getModel(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.joinResource.error:未找到目标 模型");
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
        processorsService.save(processors);

        //todo 判断数据源类型 ,创建processors ，找到对应需要创建的 process 集合
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

//        //模板和数据源关联
//        BiEtlGroupDbRef groupDbRef = new BiEtlGroupDbRef();
//        groupDbRef.setCode("REF" + System.currentTimeMillis());
//        groupDbRef.setSourceId(dto.getModelId());
//        groupDbRef.setTargetId(dto.getSourceId());
//        groupDbRef.setCreateDate(LocalDateTime.now());
//        groupDbRef.setCreateUser(dto.getCreateUser());
//        groupDbRef.setTenantId(dto.getTenantId());
//        groupDbRefService.save(groupDbRef);
    }
}
