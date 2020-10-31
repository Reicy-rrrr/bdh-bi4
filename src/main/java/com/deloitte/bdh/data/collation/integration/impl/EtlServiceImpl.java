package com.deloitte.bdh.data.collation.integration.impl;

import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.database.DbSelector;

import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.model.request.*;
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
import com.google.common.collect.Maps;

import com.deloitte.bdh.data.collation.integration.EtlService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@DS(DSConstant.BI_DB)
public class EtlServiceImpl implements EtlService {
    private static final Logger logger = LoggerFactory.getLogger(EtlServiceImpl.class);

    @Autowired
    private BiEtlDatabaseInfService databaseInfService;
    @Autowired
    private BiEtlModelService biEtlModelService;
    @Resource
    private EtlProcess etlProcess;
    @Autowired
    private BiProcessorsService processorsService;

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
    public BiComponent joinResource(JoinComponentDto dto) throws Exception {
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

        //step2:新建数据源组件与参数
        String componentCode = GenerateCodeUtil.getComponent();
        BiComponent component = new BiComponent();
        component.setCode(componentCode);
        component.setName(dto.getComponentName());
        component.setType(ComponentTypeEnum.DATASOURCE.getKey());
        component.setEffect(EffectEnum.DISABLE.getKey());
        component.setRefModelCode(biEtlModel.getCode());
        component.setVersion("1");
        component.setPosition(dto.getPosition());
        component.setCreateDate(LocalDateTime.now());
        component.setCreateUser(dto.getOperator());
        component.setTenantId(dto.getTenantId());

        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.DULICATE, YesOrNoEnum.getValue(dto.getDuplicate()).getKey());
        params.put(ComponentCons.BELONG_MAPPING_CODE, dto.getBelongMappingCode());

        //判断是独立副本
        if (YesOrNoEnum.YES.getKey().equals(dto.getDuplicate())) {
            String mappingCode = GenerateCodeUtil.generate();
            params.put(ComponentCons.BELONG_MAPPING_CODE, mappingCode);
            dto.setBelongMappingCode(mappingCode);

            //step2.1:是独立副本，创建映射
            BiEtlMappingConfig mappingConfig = new BiEtlMappingConfig();
            mappingConfig.setCode(mappingCode);
            mappingConfig.setRefCode(refCode);
            mappingConfig.setType(SyncTypeEnum.getEnumByKey(dto.getSyncType()).getKey().toString());
            mappingConfig.setRefSourceId(biEtlDatabaseInf.getId());
            mappingConfig.setFromTableName(dto.getTableName());
            mappingConfig.setToTableName(dto.getTableName());
            mappingConfig.setCreateDate(LocalDateTime.now());
            mappingConfig.setCreateUser(dto.getOperator());
            mappingConfig.setTenantId(dto.getTenantId());
            mappingConfig.setRefComponentCode(componentCode);

            if (!SyncTypeEnum.DIRECT.getKey().equals(dto.getSyncType())) {
                component.setEffect(EffectEnum.DISABLE.getKey());
                if (CollectionUtils.isEmpty(dto.getFields())) {
                    throw new RuntimeException("EtlServiceImpl.joinResource.error : 同步时,所选字段不能为空");
                }

                if (StringUtils.isBlank(dto.getOffsetField())) {
                    throw new RuntimeException("EtlServiceImpl.joinResource.error : 同步时,偏移字段不能为空");
                }
                //同步都涉及 偏移字段，方便同步
                String processorsCode = GenerateCodeUtil.genProcessors();
                mappingConfig.setOffsetField(dto.getOffsetField());
                mappingConfig.setOffsetValue(dto.getOffsetValue());
                //初次同步设置0
                mappingConfig.setLocalCount("0");
                //表名：组件编码+源表名
                String toTableName = componentCode + "_" + dto.getTableName();
                mappingConfig.setToTableName(toTableName);

                //step2.1.1:创建 字段列表,此处为映射编码
                List<BiEtlMappingField> fields = transferToFields(dto.getOperator(), dto.getTenantId(), mappingCode, dto.getFields());
                fieldService.saveBatch(fields);

                //step 2.1.2:创建目标表
                dbHandler.createTable(biEtlDatabaseInf.getId(), toTableName, dto.getFields());

                //step2.1.3: 调用NIFI生成processors
                transferNifiSync(dto, mappingConfig, biEtlDatabaseInf, biEtlModel, processorsCode);

                //step2.1.4 生成第一次的调度计划
                createFirstPlan(dto, biEtlModel, biEtlDatabaseInf, mappingConfig);

                //step2.1.5 关联组件与processors
                params.put(ComponentCons.REF_PROCESSORS_CDOE, processorsCode);
            }
            configService.save(mappingConfig);
        } else {
            if (StringUtils.isBlank(dto.getBelongMappingCode())) {
                throw new RuntimeException("EtlServiceImpl.joinResource.error : 非独立副本时,引用的表不能为空");
            }
        }

        //step3:保存组件
        List<BiComponentParams> biComponentParams = transferToParams(dto.getOperator(), dto.getTenantId(), componentCode, params);
        componentParamsService.saveBatch(biComponentParams);
        componentService.save(component);
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeResource(String code) throws Exception {
        //获取组件信息，判断组件是否存在
        BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, code)
        );
        if (null == component) {
            throw new RuntimeException("EtlServiceImpl.removeResource.error : 未找到目标 组件");
        }

        //获取组件参数
        List<BiComponentParams> paramsList = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, code)
        );

        //是否独立的数据源组件
        String dulicate = paramsList.stream()
                .filter(p -> p.getParamKey().equals(ComponentCons.DULICATE)).findAny().get().getParamValue();
        if (YesOrNoEnum.NO.getKey().equals(dulicate)) {
            //非独立副本可以直接删除返回
            componentService.remove(component.getId());
            return;
        }

        //独立副本时，该组件是否被其他模板的组件引用
        String mappingCode = paramsList.stream()
                .filter(p -> p.getParamKey().equals(ComponentCons.BELONG_MAPPING_CODE)).findAny().get().getParamValue();

        List<BiComponentParams> refParamsList = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getParamKey, ComponentCons.BELONG_MAPPING_CODE)
                .eq(BiComponentParams::getParamValue, mappingCode)
                .ne(BiComponentParams::getRefComponentCode, code)
        );
        if (CollectionUtils.isNotEmpty(refParamsList)) {
            //todo 待确定是否还能删除
            throw new RuntimeException("EtlServiceImpl.removeResource.error : 该组件不能移除，已经被其他模板引用，请先取消其他被引用的组件。");
        }

        //判断当前组件同步类型，"直连" 则直接删除
        BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getCode, mappingCode)
        );
        if (SyncTypeEnum.DIRECT.getKey().toString().equals(config.getType())) {
            componentService.remove(component.getId());
        }

        //当前是 "非直连"
        BiEtlSyncPlan syncPlan = syncPlanService.getOne(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getRefMappingCode, mappingCode)
                .orderByDesc(BiEtlSyncPlan::getCreateDate)
                .last("limit 1")
        );

        //不管当前是 第一次同步还是定时调度，是待同步还是同步中还是同步完成，都一致操作
        //1：停止清空NIFI，2：删除NIFI配置，3：删除本地表，4：删除本地组件配置，5：若当前调度计划未完成，修改状态为取消
        componentService.remove(component.getId());
        if (StringUtils.isBlank(syncPlan.getPlanResult())) {
            syncPlan.setPlanResult(PlanResultEnum.CANCEL.getKey());
            syncPlanService.updateById(syncPlan);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent out(OutComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getModel(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.out.error : 未找到目标 模型");
        }

        String componentCode = GenerateCodeUtil.getComponent();
        BiComponent component = new BiComponent();
        component.setCode(componentCode);
        component.setName(ComponentTypeEnum.OUT.getValue());
        component.setType(ComponentTypeEnum.OUT.getKey());
        //输出组件默认是成功
        component.setEffect(EffectEnum.ENABLE.getKey());
        component.setRefModelCode(biEtlModel.getCode());
        component.setVersion("1");
        component.setPosition(dto.getPosition());
        component.setCreateDate(LocalDateTime.now());
        component.setCreateUser(dto.getOperator());
        component.setTenantId(dto.getTenantId());
        componentService.save(component);

        //创建最终表,表名默认为模板编码
        String tableName = StringUtils.isBlank(dto.getTableName()) ? biEtlModel.getCode() : dto.getTableName();
        String processorsCode = GenerateCodeUtil.genProcessors();

        //保存字段及属性
        List<BiEtlMappingField> fields = transferToFields(dto.getOperator(), dto.getTenantId(), componentCode, dto.getFields());
        fieldService.saveBatch(fields);

        //设置组件参数
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.TO_TABLE_NAME, tableName);
        //关联组件与processors
        params.put(ComponentCons.REF_PROCESSORS_CDOE, processorsCode);
        params.put(ComponentCons.SQL_SELECT_QUERY, dto.getSqlSelectQuery());

        List<BiComponentParams> biComponentParams = transferToParams(dto.getOperator(), dto.getTenantId(), componentCode, params);
        componentParamsService.saveBatch(biComponentParams);

        dbHandler.createTable(tableName, dto.getFields());
        //NIFI创建 etl processors
        transferNifiEtl(dto, params, biEtlModel);
        return component;
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
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
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

    private ProcessorContext transferNifiSync(JoinComponentDto dto, BiEtlMappingConfig mappingConfig, BiEtlDatabaseInf
            biEtlDatabaseInf, BiEtlModel biEtlModel, String processorsCode) throws Exception {

        switch (SourceTypeEnum.values(biEtlDatabaseInf.getType())) {
            case Mysql:
            case Oracle:
            case SQLServer:
            case Hana:
                return transferNifiTaskRel(dto, mappingConfig, biEtlDatabaseInf, biEtlModel, processorsCode);
            case Hive:
            default:
                throw new RuntimeException("暂不支持的类型");
        }
    }

    private ProcessorContext transferNifiTaskRel(JoinComponentDto dto, BiEtlMappingConfig mappingConfig, BiEtlDatabaseInf
            biEtlDatabaseInf, BiEtlModel biEtlModel, String processorsCode) throws Exception {
        ProcessorContext context = new ProcessorContext();

        BiProcessors processors = new BiProcessors();
        processors.setCode(processorsCode);
        processors.setType(BiProcessorsTypeEnum.SYNC_SOURCE.getType());
        processors.setName(dto.getComponentName());
        processors.setTypeDesc(BiProcessorsTypeEnum.getTypeDesc(processors.getType()));
        processors.setStatus(YesOrNoEnum.NO.getKey());
        processors.setEffect(EffectEnum.ENABLE.getKey());
        processors.setValidate(YesOrNoEnum.NO.getKey());
        processors.setRelModelCode(biEtlModel.getCode());
        processors.setVersion("1");
        processors.setCreateDate(LocalDateTime.now());
        processors.setCreateUser(dto.getOperator());
        processors.setTenantId(dto.getTenantId());

        //调用NIFI准备
        Map<String, Object> reqNifi = Maps.newHashMap();
        reqNifi.put("createUser", dto.getOperator());
        reqNifi.put("tenantId", dto.getTenantId());
        //QueryDatabaseTable
        reqNifi.put("fromControllerServiceId", biEtlDatabaseInf.getControllerServiceId());
        reqNifi.put("fromTableName", mappingConfig.getFromTableName());
        String fileds = dto.getFields().stream().map(TableField::getName).collect(Collectors.joining(","));
        reqNifi.put("Columns to Return", JsonUtil.obj2String(fileds));
        if (StringUtils.isNotBlank(dto.getOffsetValue())) {
            reqNifi.put("db-fetch-where-clause", dto.getOffsetField() + " > " + dto.getOffsetValue());
        }
        reqNifi.put("Maximum-value Columns", mappingConfig.getOffsetField());
        //PutDatabaseRecord
        reqNifi.put("toTableName", mappingConfig.getToTableName());
        context.setEnumList(BiProcessorsTypeEnum.SYNC_SOURCE.includeProcessor(biEtlDatabaseInf.getType()));
        context.setReq(reqNifi);
        context.setMethod(MethodEnum.SAVE);
        context.setModel(biEtlModel);
        context.setProcessors(processors);
        etlProcess.operateProcessorGroup(context);
        processorsService.save(context.getProcessors());
        return context;
    }

    private void createFirstPlan(JoinComponentDto dto, BiEtlModel biEtlModel, BiEtlDatabaseInf biEtlDatabaseInf, BiEtlMappingConfig mappingConfig) throws Exception {
        BiEtlSyncPlan syncPlan = new BiEtlSyncPlan();
        syncPlan.setCode(GenerateCodeUtil.generate());
        syncPlan.setGroupCode("0");
        //0数据同步、1数据整理
        syncPlan.setPlanType("0");
        syncPlan.setRefMappingCode(mappingConfig.getCode());
        syncPlan.setPlanStage(PlanStageEnum.TO_EXECUTE.getKey());
        syncPlan.setSqlLocalCount("0");
        syncPlan.setCreateDate(LocalDateTime.now());
        syncPlan.setRefModelCode(biEtlModel.getCode());
        syncPlan.setCreateDate(LocalDateTime.now());
        syncPlan.setCreateUser(dto.getOperator());
        syncPlan.setTenantId(dto.getTenantId());
        syncPlan.setIsFirst(YesOrNoEnum.YES.getKey());
        //设置已处理初始值为0
        syncPlan.setProcessCount("0");
        //基于条件，获取元数据的总数，该执行效率较低下，建议 由配置时去执行
        DbContext context = new DbContext();
        context.setDbId(biEtlDatabaseInf.getId());
        context.setTableName(mappingConfig.getFromTableName());
        if (StringUtils.isNotBlank(dto.getOffsetValue())) {
            String condition = "'" + dto.getOffsetField() + "' > =" + "'" + dto.getOffsetValue() + "'";
            context.setCondition(condition);
        }
        syncPlan.setSqlCount(String.valueOf(dbSelector.getTableCount(context)));
        syncPlanService.save(syncPlan);
    }

    private ProcessorContext transferNifiEtl(OutComponentDto dto, Map<String, Object> params, BiEtlModel biEtlModel) throws Exception {
        ProcessorContext context = new ProcessorContext();

        BiProcessors processors = new BiProcessors();
        processors.setCode(MapUtils.getString(params, ComponentCons.REF_PROCESSORS_CDOE));
        processors.setType(BiProcessorsTypeEnum.ETL_SOURCE.getType());
        processors.setName(BiProcessorsTypeEnum.getTypeDesc(processors.getType()) + System.currentTimeMillis());
        processors.setTypeDesc(BiProcessorsTypeEnum.getTypeDesc(processors.getType()));
        processors.setStatus(YesOrNoEnum.NO.getKey());
        processors.setEffect(EffectEnum.ENABLE.getKey());
        processors.setValidate(YesOrNoEnum.NO.getKey());
        processors.setRelModelCode(biEtlModel.getCode());
        processors.setVersion("1");
        processors.setCreateDate(LocalDateTime.now());
        processors.setCreateUser(dto.getOperator());
        processors.setTenantId(dto.getTenantId());

        //调用NIFI准备
        Map<String, Object> reqNifi = Maps.newHashMap();
        reqNifi.put("createUser", dto.getOperator());
        reqNifi.put("tenantId", dto.getTenantId());
        //ExecuteSQL
//        reqNifi.put("fromControllerServiceId", null);
        reqNifi.put("sqlSelectQuery", MapUtils.getString(params, ComponentCons.SQL_SELECT_QUERY));
        //PutDatabaseRecord
        reqNifi.put("toTableName", MapUtils.getString(params, ComponentCons.TO_TABLE_NAME));
        context.setEnumList(BiProcessorsTypeEnum.SYNC_SOURCE.includeProcessor(null));
        context.setReq(reqNifi);
        context.setMethod(MethodEnum.SAVE);
        context.setModel(biEtlModel);
        context.setProcessors(processors);
        etlProcess.operateProcessorGroup(context);
        processorsService.save(context.getProcessors());
        return context;
    }

}
