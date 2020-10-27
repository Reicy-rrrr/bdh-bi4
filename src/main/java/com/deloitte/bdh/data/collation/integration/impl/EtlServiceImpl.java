package com.deloitte.bdh.data.collation.integration.impl;

import com.deloitte.bdh.data.collation.database.DbSelector;

import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.EtlProcessorsResp;
import com.deloitte.bdh.data.collation.model.resp.EtlRunModelResp;
import com.deloitte.bdh.data.collation.nifi.EtlProcess;
import com.deloitte.bdh.data.collation.nifi.dto.*;
import com.deloitte.bdh.data.collation.nifi.enums.MethodEnum;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.service.*;
import com.google.common.collect.Lists;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.google.common.collect.Maps;

import com.deloitte.bdh.data.collation.integration.EtlService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    @Resource
    private EtlProcess<Nifi> etlProcess;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private BiEtlParamsService biEtlParamsService;
    @Autowired
    private BiEtlConnectionService biEtlConnectionService;
    @Autowired
    private BiConnectionsService connectionsService;

    @Autowired
    private BiComponentService componentService;
    @Autowired
    private BiComponentParamsService componentParamsService;
    @Autowired
    private BiEtlMappingConfigService configService;
    @Autowired
    private BiEtlMappingFieldService fieldService;
    @Autowired
    private BiEtlDbRefService refService;
    @Autowired
    private BiEtlSyncPlanService syncPlanService;
    @Autowired
    private DbHandler dbHandler;
    @Autowired
    private DbSelector dbSelector;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent joinResource(JoinResourceDto dto) throws Exception {
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

        //step1:创建数据源与model的关系
        BiEtlDbRef biEtlDbRef = refService.getOne(new LambdaQueryWrapper<BiEtlDbRef>()
                .eq(BiEtlDbRef::getSourceId, biEtlDatabaseInf.getId()).eq(BiEtlDbRef::getModelCode, biEtlModel.getCode())
        );

        String refCode;
        if (null == biEtlDbRef) {
            refCode = GenerateCodeUtil.genDbRef();
            BiEtlDbRef dbRef = new BiEtlDbRef();
            dbRef.setCode(refCode);
            dbRef.setSourceId(dto.getSourceId());
            dbRef.setModelCode(biEtlModel.getCode());
            dbRef.setCreateDate(LocalDateTime.now());
            dbRef.setCreateUser(dto.getOperator());
            dbRef.setTenantId(dto.getTenantId());
            refService.save(dbRef);
        } else {
            refCode = biEtlDbRef.getCode();
        }

        //step2:新建数据源组件
        String componentCode = GenerateCodeUtil.getComponent();
        BiComponent component = new BiComponent();
        component.setCode(componentCode);
        component.setName(ComponentTypeEnum.DATASOURCE.getValue());
        component.setType(ComponentTypeEnum.DATASOURCE.getKey());
        component.setEffect(EffectEnum.ENABLE.getKey());
        component.setRefModelCode(biEtlModel.getCode());
        component.setVersion("1");
        component.setPosition(dto.getPosition());
        component.setCreateDate(LocalDateTime.now());
        component.setCreateUser(dto.getOperator());
        component.setTenantId(dto.getTenantId());

        //判断独立副本
        if (YesOrNoEnum.YES.equals(dto.getIsDuplicate())) {
            String mappingCode = GenerateCodeUtil.generate();
            dto.setBelongMappingCode(mappingCode);

            //step2.1:是独立副本，创建映射
            BiEtlMappingConfig mappingConfig = new BiEtlMappingConfig();
            mappingConfig.setCode(mappingCode);
            mappingConfig.setRefCode(refCode);
            mappingConfig.setType(dto.getSyncType().getKey().toString());
            mappingConfig.setRefSourceId(biEtlDatabaseInf.getId());
            mappingConfig.setFromTableName(dto.getTableName());
            mappingConfig.setToTableName(dto.getTableName());
            mappingConfig.setCreateDate(LocalDateTime.now());
            mappingConfig.setCreateUser(dto.getOperator());
            mappingConfig.setTenantId(dto.getTenantId());

            if (!SyncTypeEnum.DIRECT.equals(dto.getSyncType())) {
                component.setEffect(EffectEnum.DISABLE.getKey());
                if (CollectionUtils.isEmpty(dto.getFields())) {
                    throw new RuntimeException("EtlServiceImpl.joinResource.error : 同步时,所选字段不能为空");
                }

                if (StringUtils.isBlank(dto.getOffsetField())) {
                    throw new RuntimeException("EtlServiceImpl.joinResource.error : 同步时,偏移字段不能为空");
                }
                //同步都涉及 偏移字段，方便同步
                String processorsCode = GenerateCodeUtil.genProcessors();
                mappingConfig.setRefProcessorsCode(processorsCode);
                mappingConfig.setOffsetField(dto.getOffsetField());
                mappingConfig.setOffsetValue(dto.getOffsetValue());
                //初次同步设置0
                mappingConfig.setLocalCount("0");
                //表名：组件编码+源表名
                String toTableName = componentCode + dto.getTableName();
                mappingConfig.setToTableName(toTableName);

                //step2.1.1:创建 字段列表,此处为映射编码
                List<BiEtlMappingField> fields = transferToFields(dto.getOperator(), dto.getTenantId(), mappingCode, dto.getFields());
                fieldService.saveBatch(fields);

                //step 2.1.2:创建目标表
                dbHandler.createTable(biEtlDatabaseInf.getId(), toTableName, dto.getFields());

                //step2.1.3:生成processors集合
                BiProcessors processors = new BiProcessors();
                processors.setCode(processorsCode);
                processors.setType(BiProcessorsTypeEnum.SYNC_SOURCE.getType());
                processors.setName(BiProcessorsTypeEnum.getTypeDesc(processors.getType()));
                processors.setTypeDesc(BiProcessorsTypeEnum.getTypeDesc(processors.getType()));
                processors.setStatus(YesOrNoEnum.NO.getKey());
                processors.setEffect(EffectEnum.ENABLE.getKey());
                processors.setValidate(YesOrNoEnum.NO.getKey());
                processors.setRelModelCode(biEtlModel.getCode());
                processors.setVersion("1");
                processors.setCreateDate(LocalDateTime.now());
                processors.setCreateUser(dto.getOperator());
                processors.setTenantId(dto.getTenantId());
                processorsService.save(processors);

                // step2.1.4 调用NIFI生成processor
                Map<String, Object> req = Maps.newHashMap();
                req.put("createUser", dto.getOperator());
                req.put("SQL select query", dto.getTableName());
                ProcessorContext context = new ProcessorContext();
                context.setEnumList(BiProcessorsTypeEnum.SYNC_SOURCE.includeProcessor(biEtlDatabaseInf.getType()));
                context.setReq(req);
                context.setMethod(MethodEnum.SAVE);
                context.setModel(biEtlModel);
                context.setBiEtlDatabaseInf(biEtlDatabaseInf);
                context.setProcessors(processors);
                etlProcess.process(context);

                //step2.1.5 生成调度计划
                BiEtlSyncPlan syncPlan = new BiEtlSyncPlan();
                syncPlan.setCode(GenerateCodeUtil.generate());
                syncPlan.setGroupCode("0");
                //0数据同步、1数据整理
                syncPlan.setPlanType("0");
                syncPlan.setRefMappingCode(mappingCode);
                syncPlan.setPlanStatus(PlanStatusEnum.TO_EXECUTE.getKey());
                //基于条件，获取元数据的总数，该执行效率较低下，建议 由配置时去执行
                syncPlan.setSqlCount(getSyncCountSql(biEtlDatabaseInf.getId(), mappingConfig));
                syncPlan.setSqlLocalCount("0");
                syncPlan.setCreateDate(LocalDateTime.now());
                syncPlan.setRefModelCode(biEtlModel.getCode());
                syncPlan.setCreateDate(LocalDateTime.now());
                syncPlan.setCreateUser(dto.getOperator());
                syncPlan.setTenantId(dto.getTenantId());
                syncPlanService.save(syncPlan);
            }
            configService.save(mappingConfig);
        } else {
            if (StringUtils.isBlank(dto.getBelongMappingCode())) {
                throw new RuntimeException("EtlServiceImpl.joinResource.error : 非独立副本时,引用的表不能为空");
            }
        }

        componentService.save(component);

        //step3:设置参数
        Map<String, Object> params = Maps.newHashMap();
        params.put("isDuplicate", dto.getIsDuplicate().getKey());
        params.put("belongMappingCode", dto.getBelongMappingCode());
        List<BiComponentParams> biComponentParams = transferToParams(dto.getOperator(), dto.getTenantId(), componentCode, params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProcessors(String processorsCode) throws Exception {
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
        context.setEnumList(BiProcessorsTypeEnum.getEnum(processors.getType()).includeProcessor(biEtlDatabaseInf.getType()));
        context.setMethod(MethodEnum.DELETE);
        context.setModel(biEtlModel);
        context.setBiEtlDatabaseInf(biEtlDatabaseInf);
        context.setProcessors(processors);
        context.addProcessorList(processorList);
        context.addConnectionList(connectionList);
        context.setReq(req);
        etlProcess.process(context);
        processorsService.removeById(processors.getId());

    }

    @Override
    public List<BiConnections> connectProcessors(CreateConnectionsDto dto) throws Exception {
        BiProcessors fromProcessors = processorsService.getOne(
                new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getCode, dto.getFromProcessorsCode()));
        if (null == fromProcessors) {
            throw new RuntimeException("EtlServiceImpl.removeResource.error : 未找到目标 fromProcessors");
        }

        BiProcessors toProcessors = processorsService.getOne(
                new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getCode, dto.getToProcessorsCode()));
        if (null == toProcessors) {
            throw new RuntimeException("EtlServiceImpl.removeResource.error : 未找到目标 toProcessors");
        }

        BiEtlModel biEtlModel = biEtlModelService.getOne(
                new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, fromProcessors.getRelModelCode()));
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.joinResource.error : 未找到目标 模型");
        }


        Map<String, Object> req = Maps.newHashMap();
        req.put("createUser", "lw");

        List<BiProcessors> from = Lists.newLinkedList();
        from.add(fromProcessors);
        List<BiProcessors> to = Lists.newLinkedList();
        to.add(toProcessors);
        ConnectionsContext context = new ConnectionsContext();
        context.setFromProcessorsList(from);
        context.setToProcessorsList(to);
        context.setMethod(MethodEnum.SAVE);
        context.setModel(biEtlModel);
        context.setReq(req);
        etlProcess.process(context);
        return context.getConnectionsList();
    }

    @Override
    public void cancelConnectProcessors(String connectionsCode) throws Exception {
        BiConnections connections = connectionsService.getOne(
                new LambdaQueryWrapper<BiConnections>().eq(BiConnections::getCode, connectionsCode));

        BiEtlConnection biEtlConnection = biEtlConnectionService.getOne(
                new LambdaQueryWrapper<BiEtlConnection>().eq(BiEtlConnection::getRelProcessorsCode, connections.getCode()));

        Map<String, Object> req = Maps.newHashMap();
        req.put("createUser", "lw");

        List<BiConnections> connectionsList = Lists.newLinkedList();
        connectionsList.add(connections);
        List<BiEtlConnection> connectionList = Lists.newLinkedList();
        connectionList.add(biEtlConnection);

        ConnectionsContext context = new ConnectionsContext();
        context.setConnectionsList(connectionsList);
        context.setConnectionList(connectionList);
        context.setMethod(MethodEnum.DELETE);
        context.setReq(req);
        etlProcess.process(context);
    }

    @Override
    public EtlProcessorsResp getProcessors(String processorsCode) {
        if (StringUtil.isEmpty(processorsCode)) {
            throw new RuntimeException("EtlServiceImpl.getProcessors.error : processorsCode 不能为空");
        }
        BiProcessors processors = processorsService.getOne(
                new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getCode, processorsCode));

        if (null == processors) {
            throw new RuntimeException("EtlServiceImpl.getProcessors.error : 未找到对应的目标");
        }

        EtlProcessorsResp resp = new EtlProcessorsResp();
        BeanUtils.copyProperties(processors, resp);
        List<BiEtlParams> paramsList = biEtlParamsService.list(
                new LambdaQueryWrapper<BiEtlParams>().eq(BiEtlParams::getRelProcessorsCode, processorsCode));

        List<BiConnections> preConnections = connectionsService.list(
                new LambdaQueryWrapper<BiConnections>().eq(BiConnections::getFromProcessorsCode, processorsCode));

        List<BiConnections> nextConnections = connectionsService.list(
                new LambdaQueryWrapper<BiConnections>().eq(BiConnections::getToProcessorsCode, processorsCode));

        resp.setParamsList(paramsList);
        resp.setPreConnections(preConnections);
        resp.setNextConnections(nextConnections);
        return resp;
    }

    @Override
    public List<EtlProcessorsResp> getProcessorsList(String modelCode) {
        BiEtlModel model = biEtlModelService.getOne(
                new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, modelCode));
        if (null == model) {
            throw new RuntimeException("EtlServiceImpl.getProcessorsList.error : 未找到对应的目标");
        }

        List<EtlProcessorsResp> resps = Lists.newLinkedList();
        List<BiProcessors> processorsList = processorsService.list(
                new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getRelModelCode, model).orderByAsc(BiProcessors::getCode));
        if (CollectionUtils.isNotEmpty(processorsList)) {
            processorsList.forEach(v -> resps.add(getProcessors(v.getCode())));
        }
        return resps;

    }

    @Override
    public BiProcessors outProcessors(CreateOutProcessorsDto dto) throws Exception {
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
        processors.setType(BiProcessorsTypeEnum.OUT_SOURCE.getType());
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
        //todo 待设置坐标
        processorsService.save(processors);

        // 判断数据源类型 ,创建processors ，找到对应需要创建的 process 集合
        Map<String, Object> req = Maps.newHashMap();
        req.put("createUser", dto.getCreateUser());
        req.put("Table Name", dto.getTableName());
        req.put("JDBC Connection Pool", biEtlDatabaseInf.getControllerServiceId());

        ProcessorContext context = new ProcessorContext();
        context.setEnumList(BiProcessorsTypeEnum.OUT_SOURCE.includeProcessor(biEtlDatabaseInf.getType()));
        context.setReq(req);
        context.setMethod(MethodEnum.SAVE);
        context.setModel(biEtlModel);
        context.setBiEtlDatabaseInf(biEtlDatabaseInf);
        context.setProcessors(processors);
        etlProcess.process(context);

        return context.getProcessors();
    }

    @Override
    public EtlRunModelResp runModel(RunModelDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getModel(dto.getId());
        if (biEtlModel.getStatus().equals(dto.getRunStatus())) {
            throw new RuntimeException("EtlServiceImpl.runModel.error : 请勿重复执行");
        }

        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new RuntimeException("EtlServiceImpl.runModel.error : 模板失效下不允许操作");
        }

        Map<String, Object> req = Maps.newHashMap();
        req.put("modifiedUser", dto.getModifiedUser());

        MethodEnum methodEnum = MethodEnum.RUN;
        if (RunStatusEnum.STOP.getKey().equals(dto.getRunStatus())) {
            methodEnum = MethodEnum.STOP;
        }
        BiEtlModel model = biEtlModelService.getById(dto.getId());
        RunContext runContext = new RunContext();
        runContext.setMethod(methodEnum);
        runContext.setReq(req);
        runContext.setModel(model);
        etlProcess.process(runContext);

        model.setModifiedDate(LocalDateTime.now());
        model.setModifiedUser(dto.getModifiedUser());
        model.setStatus(dto.getRunStatus());
        biEtlModelService.updateById(model);
        return null;
    }

    @Override
    public String preview(PreviewDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getModel(dto.getId());

        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new RuntimeException("EtlServiceImpl.preview.error : 模板失效下不允许操作");
        }

        Map<String, Object> req = Maps.newHashMap();
        req.put("modifiedUser", dto.getModifiedUser());

        RunContext runContext = new RunContext();
        runContext.setMethod(MethodEnum.VIEW);
        runContext.setReq(req);
        runContext.setModel(biEtlModel);
        runContext.setPreviewCode(dto.getPreviewCode());
        etlProcess.process(runContext);
        return runContext.getResult();
    }

    private List<BiComponentParams> transferToParams(String operator, String tenantId, String code, Map<String, Object> source) {
        List<BiComponentParams> list = Lists.newArrayList();
        for (Map.Entry<String, Object> var : source.entrySet()) {
            String key = var.getKey();
            Object value = var.getValue();
            BiComponentParams params = new BiComponentParams();
            params.setCode(GenerateCodeUtil.genParam());
            params.setName(key);
            params.setParamKey(key);
            params.setParamValue(JsonUtil.obj2String(value));
            params.setRefComponentCode(code);
            params.setCreateDate(LocalDateTime.now());
            params.setCreateUser(operator);
            params.setTenantId(tenantId);
            list.add(params);
        }
        return list;
    }

    private List<BiEtlMappingField> transferToFields(String operator, String tenantId, String code, List<TableField> list) {
        List<BiEtlMappingField> result = Lists.newArrayList();
        for (TableField var : list) {
            BiEtlMappingField params = new BiEtlMappingField();
            params.setCode(GenerateCodeUtil.generate());
            params.setFieldName(var.getName());
            params.setFieldType(var.getColumnType());
            params.setRefCode(code);
            params.setCreateDate(LocalDateTime.now());
            params.setCreateUser(operator);
            params.setTenantId(tenantId);
            result.add(params);
        }
        return result;
    }

    public String getSyncCountSql(String sourceId, BiEtlMappingConfig dto) throws Exception {
        //创建表条件
        DbContext context = new DbContext();
        context.setDbId(sourceId);
        context.setTableName(dto.getFromTableName());

        //当前肯定是需要同步的
        String offsetField = dto.getOffsetField();
        String offsetValue = dto.getOffsetValue();
        if (StringUtils.isNotBlank(offsetValue)) {
            String condition = "'" + offsetField + "' > =" + "'" + offsetValue + "'";
            context.setCondition(condition);
        }

        return String.valueOf(dbSelector.getTableCount(context));
    }

}
