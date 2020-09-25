package com.deloitte.bdh.data.integration.impl;

import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.integration.EtlService;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.model.BiEtlGroupDbRef;
import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.request.CreateProcessorDto;
import com.deloitte.bdh.data.model.request.JoinResourceDto;
import com.deloitte.bdh.data.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.data.service.BiEtlGroupDbRefService;
import com.deloitte.bdh.data.service.BiEtlModelService;
import com.deloitte.bdh.data.service.BiEtlProcessorService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
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


    @Override
    public void joinResource(JoinResourceDto dto) throws Exception {
        BiEtlDatabaseInf biEtlDatabaseInf = databaseInfService.getResource(dto.getSourceId());
        if (null == biEtlDatabaseInf) {
            throw new RuntimeException("joinResource.error:未找到目标");
        }
        BiEtlModel biEtlModel = biEtlModelService.getModel(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("joinResource.error:未找到目标");
        }

        //新建 ExecuteSQL processor 再关联数据源
        CreateProcessorDto createProcessorDto = new CreateProcessorDto();
        createProcessorDto.setName(ProcessorTypeEnum.ExecuteSQL.getTypeDesc() + System.currentTimeMillis());
        createProcessorDto.setType(ProcessorTypeEnum.ExecuteSQL.getType());
        createProcessorDto.setPosition(dto.getPosition());
        createProcessorDto.setCreateUser(dto.getCreateUser());
        createProcessorDto.setTenantId(dto.getTenantId());
        createProcessorDto.setProcessGroupId(biEtlModel.getProcessGroupId());
        BiEtlProcessor biEtlProcessor = processorService.createProcessor(createProcessorDto);
        logger.info("joinResource.db 返回数据:{}", JsonUtil.obj2String(biEtlProcessor));

        //关联数据源
        processorService.joinResource(biEtlProcessor.getProcessId(), dto.getSourceId(), dto.getCreateUser(),dto.getTableName());

        //模板和数据源关联
        BiEtlGroupDbRef groupDbRef = new BiEtlGroupDbRef();
        groupDbRef.setCode("REF" + System.currentTimeMillis());
        groupDbRef.setSourceId(dto.getModelId());
        groupDbRef.setTargetId(dto.getSourceId());
        groupDbRef.setCreateDate(LocalDateTime.now());
        groupDbRef.setCreateUser(dto.getCreateUser());
        groupDbRef.setIp("");
        groupDbRef.setTenantId(dto.getTenantId());
        groupDbRefService.save(groupDbRef);
    }
}
