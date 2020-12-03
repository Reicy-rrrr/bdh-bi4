package com.deloitte.bdh.data.collation.integration.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Sets;
import com.deloitte.bdh.common.constant.CommonConstant;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.SqlFormatUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.integration.EtlService;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.ComponentPreviewResp;
import com.deloitte.bdh.data.collation.model.resp.ComponentResp;
import com.deloitte.bdh.data.collation.model.resp.ResourceViewResp;
import com.deloitte.bdh.data.collation.nifi.template.config.SyncSql;
import com.deloitte.bdh.data.collation.nifi.template.servie.Transfer;
import com.deloitte.bdh.data.collation.service.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@DS(DSConstant.BI_DB)
public class EtlServiceImpl implements EtlService {

    @Autowired
    private BiEtlDatabaseInfService databaseInfService;
    @Autowired
    private BiEtlModelService biEtlModelService;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private BiComponentService componentService;
    @Autowired
    private BiComponentParamsService componentParamsService;
    @Autowired
    private BiComponentConnectionService connectionService;
    @Autowired
    private BiEtlMappingConfigService configService;
    @Autowired
    private BiEtlMappingFieldService fieldService;
    @Autowired
    private BiEtlSyncPlanService syncPlanService;
    @Autowired
    private DbHandler dbHandler;
    @Autowired
    private DbSelector dbSelector;
    @Autowired
    private BiEtlModelHandleService biEtlModelHandleService;
    @Autowired
    private BiEtlMappingConfigService etlMappingConfigService;
    @Resource
    private Transfer transfer;
    @Autowired
    private NifiProcessService nifiProcessService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent resourceJoin(ResourceComponentDto dto) throws Exception {
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

        if (StringUtils.isBlank(dto.getComponentName())) {
            dto.setComponentName(ComponentTypeEnum.DATASOURCE.getValue() + System.currentTimeMillis());
        }
        //step1:新建数据源组件与参数
        String componentCode = GenerateCodeUtil.getComponent();
        BiComponent component = new BiComponent();
        component.setCode(componentCode);
        component.setName(dto.getComponentName());
        component.setType(ComponentTypeEnum.DATASOURCE.getKey());
        component.setEffect(EffectEnum.ENABLE.getKey());
        component.setRefModelCode(biEtlModel.getCode());
        component.setVersion("1");
        component.setPosition(dto.getPosition());
        component.setTenantId(ThreadLocalHolder.getTenantId());
        component.setRefMappingCode(dto.getBelongMappingCode());

        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.DULICATE, YesOrNoEnum.getEnum(dto.getDuplicate()).getKey());

        //判断是独立副本
        if (YesOrNoEnum.YES.getKey().equals(dto.getDuplicate())) {
            String mappingCode = GenerateCodeUtil.generate();
            component.setRefMappingCode(mappingCode);
            dto.setBelongMappingCode(mappingCode);

            //step2.0:判断数据源是否是文件类型,是则设置类型
            if (biEtlDatabaseInf.getType().equals(SourceTypeEnum.File_Excel.getType())
                    || biEtlDatabaseInf.getType().equals(SourceTypeEnum.File_Csv.getType())) {
                dto.setSyncType(SyncTypeEnum.LOCAL.getKey());
            }

            //step2.1:是独立副本，创建映射
            BiEtlMappingConfig mappingConfig = new BiEtlMappingConfig();
            mappingConfig.setCode(mappingCode);
            mappingConfig.setRefModelCode(biEtlModel.getCode());
            mappingConfig.setRefComponentCode(componentCode);
            mappingConfig.setType(SyncTypeEnum.getEnumByKey(dto.getSyncType()).getKey().toString());
            mappingConfig.setRefSourceId(biEtlDatabaseInf.getId());
            mappingConfig.setFromTableName(dto.getTableName());
            mappingConfig.setToTableName(dto.getTableName());
            mappingConfig.setTenantId(ThreadLocalHolder.getTenantId());

            //非直连且非本地
            if (!SyncTypeEnum.DIRECT.getKey().equals(dto.getSyncType())
                    && !SyncTypeEnum.LOCAL.getKey().equals(dto.getSyncType())) {
                component.setEffect(EffectEnum.DISABLE.getKey());
                if (CollectionUtils.isEmpty(dto.getFields())) {
                    throw new RuntimeException("EtlServiceImpl.joinResource.error : 同步时,所选字段不能为空");
                }

                if (StringUtils.isBlank(dto.getOffsetField())) {
                    throw new RuntimeException("EtlServiceImpl.joinResource.error : 独立副本时，偏移字段不能为空");
                }

                Optional<TableField> field = dto.getFields().stream().filter(s -> s.getName().equals(dto.getOffsetField())).findAny();
                if (!field.isPresent()) {
                    throw new RuntimeException("EtlServiceImpl.joinResource.error : 同步时,偏移字段必须在同步的字段列表以内");
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

                //step2.1.1：获取数据源的count
                RunPlan runPlan = RunPlan.builder()
                        .groupCode("0")
                        .planName(dto.getComponentName())
                        .planType("0")
                        .first(YesOrNoEnum.YES.getKey())
                        .modelCode(biEtlModel.getCode())
                        .cronExpression(biEtlModel.getCronExpression())
                        .mappingConfigCode(mappingConfig)
                        .synCount();


                //step2.1.2: 调用NIFI生成processors
                transferNifiSource(dto, mappingConfig, biEtlDatabaseInf, biEtlModel, processorsCode);

                //step 2.1.3:创建目标表
                dbHandler.createTable(biEtlDatabaseInf.getId(), toTableName, dto.getFields());

                //step2.1.4 生成同步的第一次的调度计划
                syncPlanService.createFirstPlan(runPlan);

                //step2.1.5 关联组件与processors
                params.put(ComponentCons.REF_PROCESSORS_CDOE, processorsCode);
            }

            configService.save(mappingConfig);

            //step2.2:创建 字段列表,此处为映射编码
            List<BiEtlMappingField> fields = transferToFields(mappingCode, dto.getFields());
            fieldService.saveBatch(fields);
        } else {
            if (StringUtils.isBlank(dto.getBelongMappingCode())) {
                throw new RuntimeException("EtlServiceImpl.joinResource.error : 非独立副本时,引用的表不能为空");
            }
        }

        //step3:保存组件
        List<BiComponentParams> biComponentParams = transferToParams(componentCode, params);
        componentParamsService.saveBatch(biComponentParams);
        componentService.save(component);
        return component;
    }

    @Override
    public BiComponent resourceUpdate(UpdateResourceComponentDto dto) throws Exception {
        //获取数据源相关信息，先判断是否只删除了字段，只删除则修改表结构
        BiComponent oldComponent = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, dto.getComponentCode()));
        if (null == oldComponent) {
            throw new RuntimeException("EtlServiceImpl.resourceUpdate.error : 未找到目标");
        }

        BiEtlModel model = biEtlModelService.getOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, oldComponent.getRefModelCode()));
        if (null == model) {
            throw new RuntimeException("EtlServiceImpl.resourceUpdate.error : 未找到目标");
        }
        // 校验当前组件未同步，且当前model 未运行
        if (componentService.isSync(oldComponent.getCode())) {
            throw new RuntimeException("EtlServiceImpl.resourceUpdate.error : 数据源组件当前正在同步中，不允许修改");
        }
        if (YesOrNoEnum.YES.getKey().equals(model.getSyncStatus()) || RunStatusEnum.RUNNING.getKey().equals(model.getStatus())) {
            throw new RuntimeException("EtlServiceImpl.resourceUpdate.error : 模板状态非法");
        }

        // 若只是删除了个别字段，或组件名字变更，不需要重构同步
        if (!isRefactor(dto, oldComponent)) {
            if (!oldComponent.getName().equals(dto.getComponentName())) {
                Map<String, Object> reqNifi = Maps.newHashMap();
                reqNifi.put("id", componentService.getProcessorsGroupId(oldComponent.getCode()));
                reqNifi.put("name", dto.getComponentName());
                nifiProcessService.updProcessGroup(reqNifi);

                oldComponent.setName(dto.getComponentName());
                componentService.updateById(oldComponent);
            }
            updateForFields(dto, oldComponent);
            return oldComponent;
        } else {
            //check 连接
            BiComponentConnection connection = connectionService.getOne(new LambdaQueryWrapper<BiComponentConnection>()
                    .eq(BiComponentConnection::getFromComponentCode, oldComponent.getCode()));

            //是否有连线
            boolean noConnection = null == connection;
            if(!noConnection){
                connectionService.removeById(connection);
            }

            //删除数据源组件
            this.remove(oldComponent.getCode());

            //新增数据源数据
            ResourceComponentDto componentDto = new ResourceComponentDto();
            componentDto.setModelId(model.getId());
            componentDto.setComponentName(dto.getComponentName());
            componentDto.setSourceId(dto.getSourceId());
            componentDto.setTableName(dto.getTableName());
            componentDto.setDuplicate(dto.getDuplicate());
            componentDto.setBelongMappingCode(dto.getBelongMappingCode());
            componentDto.setSyncType(dto.getSyncType());
            componentDto.setOffsetField(dto.getOffsetField());
            componentDto.setOffsetValue(dto.getOffsetValue());
            componentDto.setFields(dto.getFields());
            BiComponent newComponent = this.resourceJoin(componentDto);

            if(!noConnection){
                //创建连接
                ComponentLinkDto linkDto = new ComponentLinkDto();
                linkDto.setModelId(model.getId());
                linkDto.setFromComponentCode(newComponent.getCode());
                linkDto.setToComponentCode(connection.getToComponentCode());
                connectionService.link(linkDto);
            }
            return newComponent;
        }
    }

    @Override
    public ResourceViewResp realTimeView(String code) {
        ResourceViewResp resp = new ResourceViewResp();

        //获取数据源配置信息，当为同步类型的时候，获取调度任务信息
        BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, code));
        if (null == component) {
            throw new RuntimeException("EtlServiceImpl.realTimeView.error : 未找到目标");
        }
        resp.setEffect(component.getEffect());
        BiComponentParams dulicateParam = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, code)
                .eq(BiComponentParams::getParamKey, ComponentCons.DULICATE));

        //引用副本
        if (dulicateParam.getParamValue().equals(YesOrNoEnum.NO.getKey())) {
            resp.setPlanStage(PlanStageEnum.NON_EXECUTE.getKey());
            resp.setPlanStageDesc(PlanStageEnum.NON_EXECUTE.getValue());
            resp.setProgressRate("100");
            return resp;
        }

        //独立副本
        BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getCode, component.getRefMappingCode()));
        if (null == config) {
            throw new RuntimeException("EtlServiceImpl.realTimeView.error : 未找到目标");
        }
        //文件类型
        if (config.getType().equals(String.valueOf(SyncTypeEnum.LOCAL.getKey()))) {
            resp.setPlanStage(PlanStageEnum.NON_EXECUTE.getKey());
            resp.setPlanStageDesc(PlanStageEnum.NON_EXECUTE.getValue());
            resp.setProgressRate("100");
            return resp;
        }

        //针对数据源主键被修改，则取最新创建的那一条数据
        BiEtlSyncPlan syncPlan = syncPlanService.getOne(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getRefMappingCode, config.getCode())
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
                .last("limit 1"));
        if (null == syncPlan) {
            throw new RuntimeException("EtlServiceImpl.realTimeView.error : 未找到目标");
        }

        resp.setPlanStage(syncPlan.getPlanStage());
        resp.setPlanStageDesc(PlanStageEnum.getValue(syncPlan.getPlanStage()));
        resp.setPlanResult(syncPlan.getPlanResult());
        resp.setResultDesc(PlanResultEnum.getValue(syncPlan.getPlanResult()));
        resp.setSqlCount(syncPlan.getSqlCount());
        resp.setSqlLocalCount(syncPlan.getSqlLocalCount());
        resp.setProgressRate("100.00");
        if (new BigDecimal(syncPlan.getSqlCount()).compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = new BigDecimal(syncPlan.getSqlLocalCount())
                    .divide(new BigDecimal(syncPlan.getSqlCount()), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            resp.setProgressRate(rate.toString());
        }
        return resp;
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
        component.setName(getComponentName(biEtlModel.getCode(), ComponentTypeEnum.OUT));
        component.setType(ComponentTypeEnum.OUT.getKey());
        // 输出组件默认启用
        component.setEffect(EffectEnum.ENABLE.getKey());
        component.setRefModelCode(biEtlModel.getCode());
        component.setVersion("1");
        component.setPosition(dto.getPosition());
        component.setTenantId(ThreadLocalHolder.getTenantId());
        componentService.save(component);

        // 保存字段及属性
        List<BiEtlMappingField> fields = transferFieldsByName(componentCode, dto.getFields());
        fieldService.saveBatch(fields);

        // 设置组件参数：创建最终表,表名默认为模板编码
        String tableName = StringUtils.isBlank(dto.getTableName()) ? biEtlModel.getCode() : dto.getTableName();
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.TO_TABLE_NAME, tableName);

        // 校验表名是否重复
        List<String> tables = dbHandler.getTables();
        if (CollectionUtils.isNotEmpty(tables)) {
            Optional<String> optional = tables.stream().filter(s -> s.equalsIgnoreCase(tableName)).findAny();
            if (optional.isPresent()) {
                throw new RuntimeException("EtlServiceImpl.out.error : 表名已存在");
            }
        }

        List<BiComponentParams> biComponentParams = transferToParams(componentCode, params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent join(JoinComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.join.error : 未找到目标 模型");
        }

        // 保存组件信息
        BiComponent component = saveComponent(biEtlModel.getCode(), ComponentTypeEnum.JOIN, dto.getPosition());

        // 保存字段及属性
        List<BiEtlMappingField> fields = transferFieldsByName(component.getCode(), dto.getFields());
        fieldService.saveBatch(fields);

        // 设置组件参数
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.JOIN_PARAM_KEY_TABLES, JSON.toJSONString(dto.getTables()));
        List<BiComponentParams> biComponentParams = transferToParams(component.getCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent group(GroupComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.join.error : 未找到目标 模型");
        }

        // 保存组件信息
        BiComponent component = saveComponent(biEtlModel.getCode(), ComponentTypeEnum.GROUP, dto.getPosition());

        // 保存字段及属性
        List<BiEtlMappingField> fields = transferFieldsByName(component.getCode(), dto.getFields());
        fieldService.saveBatch(fields);

        // 设置组件参数
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.GROUP_PARAM_KEY_GROUPS, JSON.toJSONString(dto.getGroups()));
        List<BiComponentParams> biComponentParams = transferToParams(component.getCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent arrange(ArrangeComponentDto dto, ArrangeTypeEnum arrangeType) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.join.error : 未找到目标 模型");
        }
        // 保存组件信息
        BiComponent component = saveComponent(biEtlModel.getCode(), ComponentTypeEnum.ARRANGE, dto.getPosition());

        // 设置组件参数
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.ARRANGE_PARAM_KEY_TYPE, arrangeType.getType());
        params.put(ComponentCons.ARRANGE_PARAM_KEY_CONTEXT, JSON.toJSONString(dto.getFields()));
        List<BiComponentParams> biComponentParams = transferToParams(component.getCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    public ComponentResp handle(ComponentPreviewDto dto) throws Exception {
        String componentId = dto.getComponentId();
        BiComponent component = componentService.getById(componentId);
        if (component == null) {
            throw new BizException("未找到组件信息！");
        }

        ComponentResp vo = new ComponentResp();
        BeanUtils.copyProperties(component, vo);

        ComponentModel componentModel = handleComponent(dto.getModelId(), dto.getComponentId());
        vo.setModel(componentModel);
        return vo;
    }

    @Override
    public ComponentPreviewResp previewData(ComponentPreviewDto dto) throws Exception {
        ComponentModel componentModel = handleComponent(dto.getModelId(), dto.getComponentId());
        biEtlModelHandleService.handlePreviewSql(componentModel);

        // 执行预览sql
        List<Map<String, Object>> rows = executePreviewQuery(componentModel, CommonConstant.DEFAULT_PAGE, CommonConstant.DEFAULT_PAGE_SIZE);
        ComponentPreviewResp previewVo = new ComponentPreviewResp();
        previewVo.setRows(rows);

        List<TableField> columns = componentModel.getFieldMappings().stream()
                .map(FieldMappingModel::getTableField).collect(Collectors.toList());

        previewVo.setColumns(columns);
        return previewVo;
    }

    @Override
    public ComponentPreviewResp previewNullData(ComponentPreviewNullDto dto) throws Exception {
        ComponentModel componentModel = handleComponent(dto.getModelId(), dto.getComponentId());
        biEtlModelHandleService.handlePreviewNullSql(componentModel, dto.getFields());

        // 执行预览sql
        List<Map<String, Object>> rows = executePreviewQuery(componentModel, CommonConstant.DEFAULT_PAGE, CommonConstant.DEFAULT_PAGE_SIZE);
        ComponentPreviewResp previewVo = new ComponentPreviewResp();
        previewVo.setRows(rows);

        List<TableField> columns = componentModel.getFieldMappings().stream()
                .map(FieldMappingModel::getTableField).collect(Collectors.toList());
        previewVo.setColumns(columns);
        return previewVo;
    }

    @Override
    public List<Object> previewFieldData(ComponentPreviewFieldDto dto) throws Exception {
        ComponentModel componentModel = handleComponent(dto.getModelId(), dto.getComponentId());
        biEtlModelHandleService.handlePreviewFieldSql(componentModel, dto.getField());

        // 执行预览sql
        List<Map<String, Object>> rows = executePreviewQuery(componentModel, CommonConstant.DEFAULT_PAGE, 100);
        List<Object> results = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return results;
        }
        rows.forEach(row -> {
            results.addAll(row.values());
        });
        return results;
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
        return SqlFormatUtil.format(componentModel.getQuerySql());
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
        BiEtlModel model = biEtlModelService.getOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, component.getRefModelCode()));
        if (RunStatusEnum.RUNNING.getKey().equals(model.getStatus())) {
            throw new RuntimeException("EtlServiceImpl.remove.error : 运行中的模板，不允许删除组件");
        }

        switch (ComponentTypeEnum.values(component.getType())) {
            case DATASOURCE:
                componentService.removeResourceComponent(component);
                break;
            case OUT:
                componentService.removeOut(component);
                break;
            default:
                componentService.remove(component);
        }
    }

    private List<BiComponentParams> transferToParams(String code, Map<String, Object> source) {
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
            params.setTenantId(ThreadLocalHolder.getTenantId());
            list.add(params);
        }
        return list;
    }

    private List<BiEtlMappingField> transferToFields(String code, List<TableField> list) {
        List<BiEtlMappingField> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (TableField var : list) {
            BiEtlMappingField params = new BiEtlMappingField();
            params.setCode(GenerateCodeUtil.generate());
            params.setFieldName(var.getName());
            params.setFieldType(var.getColumnType());
            params.setFieldDesc(var.getDesc());
            params.setRefCode(code);
            params.setTenantId(ThreadLocalHolder.getTenantId());
            result.add(params);
        }
        return result;
    }

    private List<BiEtlMappingField> transferFieldsByName(String code, List<String> list) {
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
            params.setTenantId(ThreadLocalHolder.getTenantId());
            result.add(params);
        }
        return result;
    }

    private void transferNifiSource(ResourceComponentDto dto, BiEtlMappingConfig mappingConfig, BiEtlDatabaseInf
            biEtlDatabaseInf, BiEtlModel biEtlModel, String processorsCode) throws Exception {

        switch (SourceTypeEnum.values(biEtlDatabaseInf.getType())) {
            case Mysql:
            case Oracle:
            case SQLServer:
            case Hana:
                transferNifiSourceRel(dto, mappingConfig, biEtlDatabaseInf, biEtlModel, processorsCode);
                break;
            case Hive:
            default:
                throw new RuntimeException("暂不支持的类型");
        }
    }


    private void transferNifiSourceRel(ResourceComponentDto dto, BiEtlMappingConfig mappingConfig, BiEtlDatabaseInf
            biEtlDatabaseInf, BiEtlModel biEtlModel, String processorsCode) throws Exception {
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
        processors.setTenantId(ThreadLocalHolder.getTenantId());

        String processGroupId = transfer.add(biEtlModel.getProcessGroupId(), BiProcessorsTypeEnum.SYNC_SOURCE.includeProcessor(biEtlDatabaseInf.getType()).getKey(), () -> {
            SyncSql syncSql = new SyncSql();
            syncSql.setDttComponentName(dto.getComponentName());
            syncSql.setDttDatabaseServieId(biEtlDatabaseInf.getControllerServiceId());
            syncSql.setDttTableName(mappingConfig.getFromTableName());
            String fileds = dto.getFields().stream().map(TableField::getName).collect(Collectors.joining(","));
            syncSql.setDttColumnsToReturn(JsonUtil.obj2String(fileds));
            if (StringUtils.isNotBlank(dto.getOffsetValue())) {
                syncSql.setDttWhereClause(dto.getOffsetField() + " > " + dto.getOffsetValue());
            }
            syncSql.setDttMaxValueColumns(mappingConfig.getOffsetField());
            syncSql.setDttPutReader("a5994ef0-0174-1000-0000-00006d114be3");
            syncSql.setDttPutServiceId("a5b9fc8e-0174-1000-0000-000039bf90cc");
            syncSql.setDttPutTableName(mappingConfig.getToTableName());
            return syncSql;
        });
        processors.setProcessGroupId(processGroupId);
        processorsService.save(processors);
    }

    private boolean isRefactor(UpdateResourceComponentDto dto, BiComponent oldComponent) {
        String sourceId = dto.getSourceId();
        String tableName = dto.getTableName();
        String duplicate = dto.getDuplicate();
        String belongMappingCode = dto.getBelongMappingCode();
        String offsetField = dto.getOffsetField();
//        String offsetValue = dto.getOffsetValue();

        BiComponentParams dulicateParam = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, oldComponent.getCode())
                .eq(BiComponentParams::getParamKey, ComponentCons.DULICATE));

        //副本类型变更
        if (!dulicateParam.getParamValue().equals(duplicate)) {
            return true;
        }

        if (YesOrNoEnum.NO.getKey().equals(duplicate)) {
            //引用副本时，引用编码变更
            return !belongMappingCode.equals(oldComponent.getRefMappingCode());
        } else {
            BiEtlMappingConfig config = etlMappingConfigService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>().eq(BiEtlMappingConfig::getCode, oldComponent.getRefMappingCode()));
            //增量value变更
//            if (!config.getOffsetValue().equals(offsetValue)) {
//                return true;
//            }
            //数据源变更
            if (!config.getRefSourceId().equals(sourceId)) {
                return true;
            }
            //表变更
            if (!config.getFromTableName().equals(tableName)) {
                return true;
            }
            //字段变更或增加
            if (fieldsChange(dto, oldComponent)) {
                return true;
            }
            //增量标识变更
            return !config.getOffsetField().equals(offsetField);
        }
    }

    private boolean fieldsChange(UpdateResourceComponentDto dto, BiComponent oldComponent) {
        ComponentModel componentModel = biEtlModelHandleService.handleComponent(oldComponent.getRefModelCode(), oldComponent.getCode());
        Set<String> newFieldSet = Sets.newHashSet();
        for (TableField newField : dto.getFields()) {
            newFieldSet.add(newField.getName());
        }
        for (FieldMappingModel fieldMappingModel : componentModel.getFieldMappings()) {
            newFieldSet.remove(fieldMappingModel.getTableField().getName());
        }
        return !newFieldSet.isEmpty();
    }

    private void updateForFields(UpdateResourceComponentDto dto, BiComponent oldComponent) {
        if (YesOrNoEnum.YES.getKey().equals(dto.getDuplicate())) {
            ComponentModel componentModel = biEtlModelHandleService.handleComponent(oldComponent.getRefModelCode(), oldComponent.getCode());
            Set<String> newFieldSet = Sets.newHashSet();
            Set<String> oldFieldSet = Sets.newHashSet();

            for (TableField newField : dto.getFields()) {
                newFieldSet.add(newField.getName());
            }
            for (FieldMappingModel fieldMappingModel : componentModel.getFieldMappings()) {
                if (newFieldSet.contains(fieldMappingModel.getTableField().getName())) {
                    newFieldSet.remove(fieldMappingModel.getTableField().getName());
                } else {
                    oldFieldSet.add(fieldMappingModel.getTableField().getName());
                }
            }
            if (newFieldSet.isEmpty()) {
                if (oldFieldSet.size() > 0) {
                    //删除多余的字段
                    BiEtlMappingConfig config = etlMappingConfigService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>().eq(BiEtlMappingConfig::getCode, oldComponent.getRefMappingCode()));
                    dbHandler.dropFields(config.getToTableName(), oldFieldSet.toArray(new String[oldFieldSet.size()]));
                    //删除field集合
                    List<BiEtlMappingField> fieldList = fieldService.list(new LambdaQueryWrapper<BiEtlMappingField>()
                            .eq(BiEtlMappingField::getRefCode, config.getCode()));
                    List<BiEtlMappingField> delFields = Lists.newArrayList();
                    for (BiEtlMappingField mappingField : fieldList) {
                        if (oldFieldSet.contains(mappingField.getFieldName())) {
                            delFields.add(mappingField);
                        }
                    }
                    fieldService.removeByIds(delFields);
                }
            }
        }
    }

    /**
     * 保存组件信息
     *
     * @param modelCode
     * @param type
     * @param position
     * @return
     */
    private BiComponent saveComponent(String modelCode, ComponentTypeEnum type, String position) {
        String componentCode = GenerateCodeUtil.getComponent();
        BiComponent component = new BiComponent();
        component.setCode(componentCode);
        component.setName(getComponentName(modelCode, type));
        component.setType(type.getKey());
        component.setEffect(EffectEnum.ENABLE.getKey());
        component.setRefModelCode(modelCode);
        component.setVersion("1");
        component.setPosition(position);
        component.setTenantId(ThreadLocalHolder.getTenantId());
        componentService.save(component);
        return component;
    }

    /**
     * 初始化组件名称
     *
     * @param type 组件类型
     * @return
     */
    private String getComponentName(String modelCode, ComponentTypeEnum type) {
        int number = 1;
        LambdaQueryWrapper<BiComponent> wrapper = new LambdaQueryWrapper();
        wrapper.eq(BiComponent::getRefModelCode, modelCode);
        wrapper.eq(BiComponent::getType, type.getKey());
        wrapper.orderByDesc(BiComponent::getId);
        wrapper.last("limit 1");
        BiComponent component = componentService.getOne(wrapper);
        if (component != null) {
            String name = component.getName();
            String nameNum = name.substring(type.getValue().length());
            try {
                number = Integer.valueOf(nameNum) + 1;
            } catch (NumberFormatException e) {
                number = 1;
            }
        }
        return type.getValue() + number;
    }

    /**
     * 处理组件
     *
     * @param modelId     模型id
     * @param componentId 组件id
     * @return ComponentModel
     */
    private ComponentModel handleComponent(String modelId, String componentId) {
        BiEtlModel model = biEtlModelService.getById(modelId);
        if (model == null) {
            throw new BizException("未找到模板信息！");
        }

        BiComponent component = componentService.getById(componentId);
        if (component == null) {
            throw new BizException("未找到组件信息！");
        }

        ComponentModel componentModel = biEtlModelHandleService.handleComponent(
                model.getCode(), component.getCode());
        return componentModel;
    }

    /**
     * 执行预览查询
     *
     * @param componentModel 组件模型
     * @param page           查询页
     * @param size           每页记录数
     * @return List<Map < String, Object>>
     * @throws Exception
     */
    private List<Map<String, Object>> executePreviewQuery(ComponentModel componentModel, Integer page, Integer size) throws Exception {
        LambdaQueryWrapper<BiEtlMappingConfig> configWrapper = new LambdaQueryWrapper();
        configWrapper.eq(BiEtlMappingConfig::getRefModelCode, componentModel.getRefModelCode());
        configWrapper.orderByDesc(BiEtlMappingConfig::getId);
        configWrapper.last("limit 1");
        BiEtlMappingConfig mappingConfig = etlMappingConfigService.getOne(configWrapper);

        List<Map<String, Object>> rows = null;
        // 直连方式直接查询数据源
        SyncTypeEnum syncType = SyncTypeEnum.getEnumByKey(mappingConfig.getType());
        if (SyncTypeEnum.DIRECT.equals(syncType)) {
            String sourceId = mappingConfig.getRefSourceId();
            DbContext context = new DbContext();
            context.setDbId(sourceId);
            context.setQuerySql(componentModel.getPreviewSql());
            context.setPage(page);
            context.setSize(size);
            rows = dbSelector.executePageQuery(context).getList();
        } else {
            rows = dbHandler.executePageQuery(componentModel.getPreviewSql(), page, size).getList();
        }

        if (rows == null) {
            return Lists.newArrayList();
        }
        return rows;
    }
}
