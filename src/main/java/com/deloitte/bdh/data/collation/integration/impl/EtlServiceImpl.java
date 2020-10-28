package com.deloitte.bdh.data.collation.integration.impl;

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
    private EtlProcess etlProcess;
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

                // step2.1.4 调用NIFI生成processors
                Map<String, Object> req = Maps.newHashMap();
                req.put("createUser", dto.getOperator());
                req.put("tenantId", dto.getTenantId());
                req.put("SQL select query", mappingConfig.getFromTableName());
                req.put("Table Name", mappingConfig.getToTableName());
                req.put("JDBC Connection Pool", biEtlDatabaseInf.getControllerServiceId());

                ProcessorContext context = new ProcessorContext();
                context.setEnumList(BiProcessorsTypeEnum.SYNC_SOURCE.includeProcessor(biEtlDatabaseInf.getType()));
                context.setReq(req);
                context.setMethod(MethodEnum.SAVE);
                context.setModel(biEtlModel);
                context.setBiEtlDatabaseInf(biEtlDatabaseInf);
                context.setProcessors(processors);
                etlProcess.operateProcessorGroup(context);
                processorsService.save(context.getProcessors());

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
                syncPlan.setIsFirst(YesOrNoEnum.YES.getKey());
                //设置已处理初始值为0
                syncPlan.setProcessCount("0");
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
