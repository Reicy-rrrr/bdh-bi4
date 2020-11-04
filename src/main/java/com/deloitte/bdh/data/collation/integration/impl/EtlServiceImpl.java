package com.deloitte.bdh.data.collation.integration.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.ThreadLocalUtil;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.integration.EtlService;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.ComponentPreviewVo;
import com.deloitte.bdh.data.collation.model.resp.ComponentVo;
import com.deloitte.bdh.data.collation.nifi.EtlProcess;
import com.deloitte.bdh.data.collation.nifi.dto.ProcessorContext;
import com.deloitte.bdh.data.collation.nifi.enums.MethodEnum;
import com.deloitte.bdh.data.collation.service.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
import java.util.Optional;
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

    @Autowired
    private BiEtlModelHandleService biEtlModelHandleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent resource(ResourceComponentDto dto) throws Exception {
        BiEtlDatabaseInf biEtlDatabaseInf = databaseInfService.getById(dto.getSourceId());
        if (null == biEtlDatabaseInf) {
            throw new RuntimeException("EtlServiceImpl.joinResource.error : 未找到目标 数据源");
        }

        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
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
            dbRef.setCreateUser(ThreadLocalUtil.getOperator());
            dbRef.setTenantId(ThreadLocalUtil.getTenantId());
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
        component.setCreateUser(ThreadLocalUtil.getOperator());
        component.setTenantId(ThreadLocalUtil.getTenantId());
        component.setRefMappingCode(dto.getBelongMappingCode());

        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.DULICATE, YesOrNoEnum.getEnum(dto.getDuplicate()).getKey());

        //判断是独立副本
        if (YesOrNoEnum.YES.getKey().equals(dto.getDuplicate())) {
            String mappingCode = GenerateCodeUtil.generate();
            component.setRefMappingCode(mappingCode);
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
            mappingConfig.setCreateUser(ThreadLocalUtil.getOperator());
            mappingConfig.setTenantId(ThreadLocalUtil.getTenantId());
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
                List<BiEtlMappingField> fields = transferToFields(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), mappingCode, dto.getFields());
                fieldService.saveBatch(fields);

                //step2.1.2: 调用NIFI生成processors
                transferNifiSource(dto, mappingConfig, biEtlDatabaseInf, biEtlModel, processorsCode);

                //step 2.1.3:创建目标表
                dbHandler.createTable(biEtlDatabaseInf.getId(), toTableName, dto.getFields());

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
        List<BiComponentParams> biComponentParams = transferToParams(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), componentCode, params);
        componentParamsService.saveBatch(biComponentParams);
        componentService.save(component);
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent out(OutComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
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
        component.setCreateUser(ThreadLocalUtil.getOperator());
        component.setTenantId(ThreadLocalUtil.getTenantId());
        componentService.save(component);

        //创建最终表,表名默认为模板编码
        String tableName = StringUtils.isBlank(dto.getTableName()) ? biEtlModel.getCode() : dto.getTableName();
        String processorsCode = GenerateCodeUtil.genProcessors();

        //保存字段及属性
        List<BiEtlMappingField> fields = transferToFields(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), componentCode, dto.getFields());
        fieldService.saveBatch(fields);

        //设置组件参数
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.TO_TABLE_NAME, tableName);
        //关联组件与processors
        params.put(ComponentCons.REF_PROCESSORS_CDOE, processorsCode);
        params.put(ComponentCons.SQL_SELECT_QUERY, dto.getSqlSelectQuery());

        List<BiComponentParams> biComponentParams = transferToParams(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), componentCode, params);
        componentParamsService.saveBatch(biComponentParams);

        dbHandler.createTable(tableName, dto.getFields());
        //NIFI创建 etl processors
        transferNifiOut(dto, params, biEtlModel);
        return component;
    }

    @Override
    public BiComponent join(JoinComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.join.error : 未找到目标 模型");
        }

        // 保存组件信息
        String componentCode = GenerateCodeUtil.getComponent();
        BiComponent component = new BiComponent();
        component.setCode(componentCode);
        component.setName(componentCode);
        component.setType(ComponentTypeEnum.JOIN.getKey());
        component.setEffect(EffectEnum.ENABLE.getKey());
        component.setRefModelCode(biEtlModel.getCode());
        component.setVersion("1");
        component.setPosition(dto.getPosition());
        component.setCreateDate(LocalDateTime.now());
        component.setCreateUser(ThreadLocalUtil.getOperator());
        component.setTenantId(ThreadLocalUtil.getTenantId());
        componentService.save(component);

        // 保存字段及属性
        List<BiEtlMappingField> fields = transferFieldsByName(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), componentCode, dto.getFields());
        fieldService.saveBatch(fields);

        // 设置组件参数
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.JOIN_PARAM_KEY_TABLES, JSON.toJSONString(dto.getTables()));
        List<BiComponentParams> biComponentParams = transferToParams(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), componentCode, params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    public BiComponent group(GroupComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.join.error : 未找到目标 模型");
        }

        // 保存组件信息
        String componentCode = GenerateCodeUtil.getComponent();
        BiComponent component = new BiComponent();
        component.setCode(componentCode);
        component.setName(componentCode);
        component.setType(ComponentTypeEnum.GROUP.getKey());
        component.setEffect(EffectEnum.ENABLE.getKey());
        component.setRefModelCode(biEtlModel.getCode());
        component.setVersion("1");
        component.setPosition(dto.getPosition());
        component.setCreateDate(LocalDateTime.now());
        component.setCreateUser(ThreadLocalUtil.getOperator());
        component.setTenantId(ThreadLocalUtil.getTenantId());
        componentService.save(component);

        // 保存字段及属性
        List<BiEtlMappingField> fields = transferFieldsByName(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), componentCode, dto.getFields());
        fieldService.saveBatch(fields);

        // 设置组件参数
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.GROUP_PARAM_KEY_GROUPS, JSON.toJSONString(dto.getGroups()));
        List<BiComponentParams> biComponentParams = transferToParams(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), componentCode, params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    public BiComponent arrange(ArrangeComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.join.error : 未找到目标 模型");
        }

        // 保存组件信息
        String componentCode = GenerateCodeUtil.getComponent();
        BiComponent component = new BiComponent();
        component.setCode(componentCode);
        component.setName(componentCode);
        component.setType(ComponentTypeEnum.ARRANGE.getKey());
        component.setEffect(EffectEnum.ENABLE.getKey());
        component.setRefModelCode(biEtlModel.getCode());
        component.setVersion("1");
        component.setPosition(dto.getPosition());
        component.setCreateDate(LocalDateTime.now());
        component.setCreateUser(ThreadLocalUtil.getOperator());
        component.setTenantId(ThreadLocalUtil.getTenantId());
        componentService.save(component);

        // 保存字段及属性
        List<BiEtlMappingField> fields = transferFieldsByName(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), componentCode, dto.getFields());
        fieldService.saveBatch(fields);

        // 设置组件参数
        Map<String, Object> params = Maps.newHashMap();
//        params.put();
        List<BiComponentParams> biComponentParams = transferToParams(ThreadLocalUtil.getOperator(), ThreadLocalUtil.getTenantId(), componentCode, params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    public ComponentVo handle(ComponentPreviewDto dto) throws Exception {
        String modelId = dto.getModelId();
        BiEtlModel model = biEtlModelService.getById(modelId);
        if (model == null) {
            throw new BizException("未找到模板信息！");
        }

        String componentId = dto.getComponentId();
        BiComponent component = componentService.getById(componentId);
        if (component == null) {
            throw new BizException("未找到组件信息！");
        }

        ComponentVo vo = new ComponentVo();
        BeanUtils.copyProperties(component, vo);

        ComponentModel componentModel = biEtlModelHandleService.handleComponent(
                model.getCode(), component.getCode());
        vo.setModel(componentModel);
        return vo;
    }

    @Override
    public ComponentPreviewVo previewData(ComponentPreviewDto dto) throws Exception {
        String modelId = dto.getModelId();
        BiEtlModel model = biEtlModelService.getById(modelId);
        if (model == null) {
            throw new BizException("未找到模板信息！");
        }

        String componentId = dto.getComponentId();
        BiComponent component = componentService.getById(componentId);
        if (component == null) {
            throw new BizException("未找到组件信息！");
        }

        String modelCode = model.getCode();
        String componentCode = component.getCode();
        ComponentModel componentModel = biEtlModelHandleService.handleComponent(modelCode, componentCode);
        biEtlModelHandleService.handlePreviewSql(componentModel);
        List<Map<String, Object>> rows = dbHandler.executeQuery(componentModel.getPreviewSql());

        ComponentPreviewVo previewVo = new ComponentPreviewVo();
        previewVo.setRows(rows);

        List<String> columns = componentModel.getFieldMappings().stream()
                .map(FieldMappingModel::getFinalFieldName).collect(Collectors.toList());

        previewVo.setColumns(columns);
        return previewVo;
    }

    @Override
    public String previewSql(ComponentPreviewDto dto) throws Exception {
        String modelId = dto.getModelId();
        BiEtlModel model = biEtlModelService.getById(modelId);
        if (model == null) {
            throw new BizException("未找到模板信息！");
        }

        String componentId = dto.getComponentId();
        BiComponent component = componentService.getById(componentId);
        if (component == null) {
            throw new BizException("未找到组件信息！");
        }

        String modelCode = model.getCode();
        String componentCode = component.getCode();
        ComponentModel componentModel = biEtlModelHandleService.handleComponent(modelCode, componentCode);
//        String querySql = SQLUtils.formatMySql(componentModel.getQuerySql(), SQLUtils.DEFAULT_FORMAT_OPTION);
        return componentModel.getQuerySql();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(String code) throws Exception {
        //获取组件信息，判断组件是否存在
        BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, code)
        );
        if (null == component) {
            throw new RuntimeException("EtlServiceImpl.remove.error : 未找到目标 组件对象");
        }

        switch (ComponentTypeEnum.values(component.getType())) {
            case DATASOURCE:
                removeResource(component);
                break;
            case OUT:
                removeOut(component);
                break;
            default:

        }
    }

    private void removeResource(BiComponent component) throws Exception {
        //获取组件参数
        List<BiComponentParams> paramsList = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
        );

        //是否独立的数据源组件
        String dulicate = paramsList.stream()
                .filter(p -> p.getParamKey().equals(ComponentCons.DULICATE)).findAny().get().getParamValue();
        if (YesOrNoEnum.NO.getKey().equals(dulicate)) {
            //非独立副本可以直接删除返回
            componentService.removeById(component.getId());
            componentParamsService.remove(new LambdaQueryWrapper<BiComponentParams>()
                    .eq(BiComponentParams::getRefComponentCode, component.getCode())
            );
            return;
        }

        //独立副本时，该组件是否被其他模板的组件引用
        String mappingCode = component.getRefMappingCode();
        List<BiComponent> sameRefList = componentService.list(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefMappingCode, mappingCode)
                .ne(BiComponent::getCode, component.getCode())
        );
        if (CollectionUtils.isNotEmpty(sameRefList)) {
            //todo 待确定是否还能删除
            throw new RuntimeException("EtlServiceImpl.removeResource.error : 该组件不能移除，已经被其他模板引用，请先取消其他被引用的组件。");
        }

        //判断当前组件同步类型，"直连" 则直接删除
        BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getCode, mappingCode)
        );
        if (SyncTypeEnum.DIRECT.getKey().toString().equals(config.getType())) {
            componentService.removeById(component.getId());
            componentParamsService.remove(new LambdaQueryWrapper<BiComponentParams>()
                    .eq(BiComponentParams::getRefComponentCode, component.getCode())
            );
            configService.removeById(config.getId());
        }

        //当前是 "非直连"
        //不管当前是 第一次同步还是定时调度，是待同步还是同步中还是同步完成，都一致操作
        //1：若当前调度计划未完成，2： 停止清空NIFI，修改状态为取消，3：删除本地表，4：删除本地组件配置，5： 删除NIFI配置
        BiComponentParams processorsCodeParam = paramsList.stream()
                .filter(p -> p.getParamKey().equals(ComponentCons.REF_PROCESSORS_CDOE)).findAny().get();
        BiEtlSyncPlan syncPlan = syncPlanService.getOne(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getRefMappingCode, mappingCode)
                .orderByDesc(BiEtlSyncPlan::getCreateDate)
                .last("limit 1")
        );
        if (StringUtils.isBlank(syncPlan.getPlanResult())) {
            syncPlan.setPlanResult(PlanResultEnum.CANCEL.getKey());
            syncPlan.setResultDesc(PlanResultEnum.CANCEL.getValue());
            syncPlanService.updateById(syncPlan);
        }
        processorsService.runState(processorsCodeParam.getParamValue(), RunStatusEnum.STOP, true);
        processorsService.removeProcessors(processorsCodeParam.getParamValue(), config.getRefSourceId());
        dbHandler.drop(config.getToTableName());
        componentService.removeById(component.getId());
        componentParamsService.remove(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
        );
        configService.removeById(config.getId());
        fieldService.remove(new LambdaQueryWrapper<BiEtlMappingField>()
                .eq(BiEtlMappingField::getRefCode, config.getCode())
        );
    }

    private void removeOut(BiComponent component) throws Exception {
        List<BiComponentParams> paramsList = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
        );

        Optional<BiComponentParams> optionalProcessorsCode = paramsList.stream()
                .filter(p -> p.getParamKey().equals(ComponentCons.REF_PROCESSORS_CDOE)).findAny();
        if (optionalProcessorsCode.isPresent()) {
            processorsService.removeProcessors(optionalProcessorsCode.get().getParamValue(), null);
        }

        Optional<BiComponentParams> optionalTableName = paramsList.stream()
                .filter(p -> p.getParamKey().equals(ComponentCons.TO_TABLE_NAME)).findAny();
        optionalTableName.ifPresent(biComponentParams -> dbHandler.drop(biComponentParams.getParamValue()));

        componentService.removeById(component.getId());
        componentParamsService.remove(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
        );
        fieldService.remove(new LambdaQueryWrapper<BiEtlMappingField>()
                .eq(BiEtlMappingField::getRefCode, component.getCode())
        );
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

    private List<BiEtlMappingField> transferFieldsByName(String operator, String tenantId, String code, List<String> list) {
        List<BiEtlMappingField> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (String field : list) {
            BiEtlMappingField params = new BiEtlMappingField();
            params.setCode(GenerateCodeUtil.generate());
            params.setFieldName(field);
            params.setFieldType(null);
            params.setRefCode(code);
            params.setCreateDate(LocalDateTime.now());
            params.setCreateUser(operator);
            params.setTenantId(tenantId);
            result.add(params);
        }
        return result;
    }

    private ProcessorContext transferNifiSource(ResourceComponentDto dto, BiEtlMappingConfig mappingConfig, BiEtlDatabaseInf
            biEtlDatabaseInf, BiEtlModel biEtlModel, String processorsCode) throws Exception {

        switch (SourceTypeEnum.values(biEtlDatabaseInf.getType())) {
            case Mysql:
            case Oracle:
            case SQLServer:
            case Hana:
                return transferNifiSourceRel(dto, mappingConfig, biEtlDatabaseInf, biEtlModel, processorsCode);
            case Hive:
            default:
                throw new RuntimeException("暂不支持的类型");
        }
    }

    private ProcessorContext transferNifiSourceRel(ResourceComponentDto dto, BiEtlMappingConfig mappingConfig, BiEtlDatabaseInf
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
        processors.setCreateUser(ThreadLocalUtil.getOperator());
        processors.setTenantId(ThreadLocalUtil.getTenantId());

        //调用NIFI准备
        Map<String, Object> reqNifi = Maps.newHashMap();
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

    private void createFirstPlan(ResourceComponentDto dto, BiEtlModel biEtlModel, BiEtlDatabaseInf biEtlDatabaseInf, BiEtlMappingConfig mappingConfig) throws Exception {
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
        syncPlan.setCreateUser(ThreadLocalUtil.getOperator());
        syncPlan.setTenantId(ThreadLocalUtil.getTenantId());
        syncPlan.setIsFirst(YesOrNoEnum.YES.getKey());
        //设置已处理初始值为0
        syncPlan.setProcessCount("0");
        syncPlan.setPlanResult(null);
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

    private ProcessorContext transferNifiOut(OutComponentDto dto, Map<String, Object> params, BiEtlModel biEtlModel) throws Exception {
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
        processors.setCreateUser(ThreadLocalUtil.getOperator());
        processors.setTenantId(ThreadLocalUtil.getTenantId());

        //调用NIFI准备
        Map<String, Object> reqNifi = Maps.newHashMap();
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
