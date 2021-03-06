package com.deloitte.bdh.data.collation.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.deloitte.bdh.common.mq.MessageProducer;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Sets;
import com.deloitte.bdh.common.constant.CommonConstant;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.Md5Util;
import com.deloitte.bdh.common.util.SqlFormatUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.WildcardEnum;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFieldService;
import com.deloitte.bdh.data.analyse.sql.utils.RelaBaseBuildUtil;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import com.deloitte.bdh.data.collation.component.ExpressionHandler;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.ArrangeTypeEnum;
import com.deloitte.bdh.data.collation.enums.BiProcessorsTypeEnum;
import com.deloitte.bdh.data.collation.enums.CalculateOperatorEnum;
import com.deloitte.bdh.data.collation.enums.CalculateTypeEnum;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.MqTypeEnum;
import com.deloitte.bdh.data.collation.enums.PlanResultEnum;
import com.deloitte.bdh.data.collation.enums.PlanStageEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.service.EtlService;
import com.deloitte.bdh.data.collation.service.NifiProcessService;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiComponentConnection;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.model.BiEtlMappingField;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.deloitte.bdh.data.collation.model.RunPlan;
import com.deloitte.bdh.data.collation.model.request.ArrangeComponentDto;
import com.deloitte.bdh.data.collation.model.request.ComponentFormulaCheckDto;
import com.deloitte.bdh.data.collation.model.request.ComponentLinkDto;
import com.deloitte.bdh.data.collation.model.request.ComponentPreviewDto;
import com.deloitte.bdh.data.collation.model.request.ComponentPreviewFieldDto;
import com.deloitte.bdh.data.collation.model.request.ComponentPreviewNullDto;
import com.deloitte.bdh.data.collation.model.request.ConditionDto;
import com.deloitte.bdh.data.collation.model.request.GroupComponentDto;
import com.deloitte.bdh.data.collation.model.request.JoinComponentDto;
import com.deloitte.bdh.data.collation.model.request.OutComponentDto;
import com.deloitte.bdh.data.collation.model.request.ResourceComponentDto;
import com.deloitte.bdh.data.collation.model.request.UpdateArrangeComponentDto;
import com.deloitte.bdh.data.collation.model.request.UpdateGroupComponentDto;
import com.deloitte.bdh.data.collation.model.request.UpdateJoinComponentDto;
import com.deloitte.bdh.data.collation.model.request.UpdateOutComponentDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourceComponentDto;
import com.deloitte.bdh.data.collation.model.request.ViewFieldValueDto;
import com.deloitte.bdh.data.collation.model.resp.CalculateOperatorResp;
import com.deloitte.bdh.data.collation.model.resp.ComponentPreviewResp;
import com.deloitte.bdh.data.collation.model.resp.ComponentResp;
import com.deloitte.bdh.data.collation.model.resp.ResourceViewResp;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.mq.KafkaSyncDto;
import com.deloitte.bdh.data.collation.nifi.template.config.SyncSql;
import com.deloitte.bdh.data.collation.nifi.template.servie.Transfer;
import com.deloitte.bdh.data.collation.service.BiComponentConnectionService;
import com.deloitte.bdh.data.collation.service.BiComponentParamsService;
import com.deloitte.bdh.data.collation.service.BiComponentService;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingFieldService;
import com.deloitte.bdh.data.collation.service.BiEtlModelHandleService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import com.deloitte.bdh.data.collation.service.BiProcessorsService;
import com.deloitte.bdh.data.collation.service.BiTenantConfigService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Service
@DS(DSConstant.BI_DB)
@Slf4j
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
    @Autowired
    private BiTenantConfigService biTenantConfigService;
    @Autowired
    private AnalyseModelFieldService analyseModelFieldService;
    @Autowired
    private ExpressionHandler expressionHandler;
    @Autowired
    private BiDataSetService dataSetService;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private LocaleMessageService localeMessageService;


    @Override
    public List<Object> previewField(ViewFieldValueDto dto) throws Exception {
        List<Object> results = Lists.newArrayList();
        List<Map<String, Object>> rows;
        String sql = "SELECT DISTINCT(" + dto.getField() + ") FROM " + dto.getTableName();
        BiEtlDatabaseInf databaseInf = databaseInfService.getById(dto.getSourceId());
        if (SourceTypeEnum.File_Excel.getType().equals(databaseInf.getType())
                || SourceTypeEnum.File_Csv.getType().equals(databaseInf.getType())) {
            rows = dbHandler.executeQuery(sql);
        } else {
            DbContext context = new DbContext();
            context.setDbId(databaseInf.getId());
            context.setQuerySql(sql);
            rows = dbSelector.executeQuery(context);
        }
        rows.forEach(row -> results.addAll(row.values()));
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent resourceJoin(ResourceComponentDto dto) throws Exception {
        BiEtlDatabaseInf biEtlDatabaseInf = databaseInfService.getById(dto.getSourceId());
        if (null == biEtlDatabaseInf) {
            throw new BizException(ResourceMessageEnum.ETL_1.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_1.getMessage(), ThreadLocalHolder.getLang()));
        }

        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new BizException(ResourceMessageEnum.ETL_2.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_2.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (EffectEnum.DISABLE.getKey().equals(biEtlDatabaseInf.getEffect())) {
            throw new BizException(ResourceMessageEnum.ETL_3.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_3.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (StringUtils.isBlank(dto.getComponentName())) {
            dto.setComponentName(ComponentTypeEnum.DATASOURCE.getValue() + System.currentTimeMillis());
        }
        //step1:??????????????????????????????
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
        List<KafkaSyncDto> planList = new ArrayList<>();
        //?????????????????????
        if (YesOrNoEnum.YES.getKey().equals(dto.getDuplicate())) {
            //??????????????????
            if (CollectionUtils.isNotEmpty(dto.getConditions())) {
                params.put(ComponentCons.CONDITION, JsonUtil.obj2String(dto.getConditions()));
            }

            String mappingCode = GenerateCodeUtil.generate();
            component.setRefMappingCode(mappingCode);
            dto.setBelongMappingCode(mappingCode);

            //step2.0:????????????????????????????????????,??????????????????
            if (biEtlDatabaseInf.getType().equals(SourceTypeEnum.File_Excel.getType())
                    || biEtlDatabaseInf.getType().equals(SourceTypeEnum.File_Csv.getType())) {
                dto.setSyncType(SyncTypeEnum.LOCAL.getKey());
            }

            //step2.1:??????????????????????????????
            BiEtlMappingConfig mappingConfig = new BiEtlMappingConfig();
            mappingConfig.setCode(mappingCode);
            mappingConfig.setRefModelCode(biEtlModel.getCode());
            mappingConfig.setRefComponentCode(componentCode);
            mappingConfig.setType(SyncTypeEnum.getEnumByKey(dto.getSyncType()).getKey().toString());
            mappingConfig.setRefSourceId(biEtlDatabaseInf.getId());
            mappingConfig.setFromTableName(dto.getTableName());
            mappingConfig.setToTableName(dto.getTableName());
            mappingConfig.setTenantId(ThreadLocalHolder.getTenantId());


            //?????????????????????
            if (!SyncTypeEnum.DIRECT.getKey().equals(dto.getSyncType())
                    && !SyncTypeEnum.LOCAL.getKey().equals(dto.getSyncType())) {
                component.setEffect(EffectEnum.DISABLE.getKey());
                if (CollectionUtils.isEmpty(dto.getFields())) {
                    throw new BizException(ResourceMessageEnum.ETL_4.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.ETL_4.getMessage(), ThreadLocalHolder.getLang()));
                }

                if (StringUtils.isBlank(dto.getOffsetField())) {
                    throw new BizException(ResourceMessageEnum.ETL_5.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.ETL_5.getMessage(), ThreadLocalHolder.getLang()));
                }

                Optional<TableField> field = dto.getFields().stream().filter(s -> s.getName().equals(dto.getOffsetField())).findAny();
                if (!field.isPresent()) {
                    throw new BizException(ResourceMessageEnum.ETL_6.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.ETL_6.getMessage(), ThreadLocalHolder.getLang()));
                }

                //??????????????? ???????????????????????????
                String processorsCode = GenerateCodeUtil.genProcessors();
                mappingConfig.setOffsetField(dto.getOffsetField());
                mappingConfig.setOffsetValue(dto.getOffsetValue());
                //??????????????????0
                mappingConfig.setLocalCount("0");
                //?????????????????????+?????????
                String toTableName = componentCode + "_" + dto.getTableName();
                mappingConfig.setToTableName(toTableName);

                //step2.1.1?????????????????????count
                RunPlan runPlan = RunPlan.builder()
                        .groupCode("0")
                        .planName(dto.getComponentName())
                        .planType("0")
                        .first(YesOrNoEnum.YES.getKey())
                        .modelCode(biEtlModel.getCode())
                        .cronExpression(biEtlModel.getCronExpression())
                        .mappingConfigCode(mappingConfig)
                        .synCount(dto.getConditions());


                //step2.1.2: ??????NIFI??????processors
                transferNifiSource(dto, mappingConfig, biEtlDatabaseInf, biEtlModel, processorsCode);

                //step 2.1.3:???????????????
                dbHandler.createTable(biEtlDatabaseInf.getId(), toTableName, dto.getFields());

                //step2.1.4 ???????????????????????????????????????
                BiEtlSyncPlan synecPlan = syncPlanService.createPlan(runPlan);

                KafkaSyncDto kfs = new KafkaSyncDto();
                kfs.setCode(synecPlan.getCode());
                kfs.setGroupCode(synecPlan.getGroupCode());
                kfs.setType("group");
                planList.add(kfs);

                //step2.1.5 ???????????????processors
                params.put(ComponentCons.REF_PROCESSORS_CDOE, processorsCode);
            }

            configService.save(mappingConfig);

            //step2.2:?????? ????????????,?????????????????????
            List<BiEtlMappingField> fields = transferToFields(mappingCode, dto.getFields());
            fieldService.saveBatch(fields);
        } else {
            if (StringUtils.isBlank(dto.getBelongMappingCode())) {
                throw new BizException(ResourceMessageEnum.ETL_7.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.ETL_7.getMessage(), ThreadLocalHolder.getLang()));
            }
        }

        //step3:????????????
        List<BiComponentParams> biComponentParams = transferToParams(componentCode, biEtlModel.getCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        componentService.save(component);
        if (!SyncTypeEnum.DIRECT.getKey().equals(dto.getSyncType())
                && !SyncTypeEnum.LOCAL.getKey().equals(dto.getSyncType())) {
            KafkaMessage message = new KafkaMessage(UUID.randomUUID().toString().replaceAll("-", ""), planList, MqTypeEnum.Plan_start.getType());
            messageProducer.sendSyncMessage(message, 1);
        }


        return component;
    }

    @Override
    public BiComponent resourceUpdate(UpdateResourceComponentDto dto) throws Exception {
        //?????????????????????????????????????????????????????????????????????????????????????????????
        BiComponent oldComponent = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, dto.getComponentCode()));
        if (null == oldComponent) {
            throw new BizException(ResourceMessageEnum.ETL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_8.getMessage(), ThreadLocalHolder.getLang()));
        }

        BiEtlModel model = biEtlModelService.getOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, oldComponent.getRefModelCode()));
        if (null == model) {
            throw new BizException(ResourceMessageEnum.ETL_9.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_9.getMessage(), ThreadLocalHolder.getLang()));
        }
        // ???????????????????????????????????????model ?????????
        if (componentService.isSync(oldComponent.getCode())) {
            throw new BizException(ResourceMessageEnum.ETL_10.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_10.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (YesOrNoEnum.YES.getKey().equals(model.getSyncStatus()) || RunStatusEnum.RUNNING.getKey().equals(model.getStatus())) {
            throw new BizException(ResourceMessageEnum.ETL_11.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_11.getMessage(), ThreadLocalHolder.getLang()));
        }

        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
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
            //check ??????
            BiComponentConnection connection = connectionService.getOne(new LambdaQueryWrapper<BiComponentConnection>()
                    .eq(BiComponentConnection::getFromComponentCode, oldComponent.getCode()));

            //???????????????
            boolean noConnection = null == connection;
            if (!noConnection) {
                connectionService.removeById(connection);
            }

            //?????????????????????
            this.remove(oldComponent.getCode());

            //?????????????????????
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

            if (!noConnection) {
                //????????????
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

        //????????????????????????????????????????????????????????????????????????????????????
        BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, code));
        if (null == component) {
            throw new BizException(ResourceMessageEnum.ETL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_8.getMessage(), ThreadLocalHolder.getLang()));
        }
        resp.setEffect(component.getEffect());
        BiComponentParams dulicateParam = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, code)
                .eq(BiComponentParams::getParamKey, ComponentCons.DULICATE));

        //????????????
        if (dulicateParam.getParamValue().equals(YesOrNoEnum.NO.getKey())) {
            resp.setPlanStage(PlanStageEnum.NON_EXECUTE.getKey());
            resp.setPlanStageDesc(PlanStageEnum.NON_EXECUTE.getValue());
            resp.setProgressRate("100");
            return resp;
        }

        //????????????
        BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getCode, component.getRefMappingCode()));
        if (null == config) {
            throw new BizException(ResourceMessageEnum.ETL_12.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_12.getMessage(), ThreadLocalHolder.getLang()));
        }
        //????????????
        if (config.getType().equals(String.valueOf(SyncTypeEnum.LOCAL.getKey()))) {
            resp.setPlanStage(PlanStageEnum.NON_EXECUTE.getKey());
            resp.setPlanStageDesc(PlanStageEnum.NON_EXECUTE.getValue());
            resp.setProgressRate("100");
            return resp;
        }

        //?????????????????????????????????????????????????????????????????????
        BiEtlSyncPlan syncPlan = syncPlanService.getOne(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getRefMappingCode, config.getCode())
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
                .last("limit 1"));
        if (null == syncPlan) {
            throw new BizException(ResourceMessageEnum.ETL_13.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_13.getMessage(), ThreadLocalHolder.getLang()));
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
    public BiComponent outCreate(OutComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new BizException(ResourceMessageEnum.ETL_9.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_9.getMessage(), ThreadLocalHolder.getLang()));
        }

        //????????????????????????????????????
        int num = componentService.count(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefModelCode, biEtlModel.getCode())
                .eq(BiComponent::getType, ComponentTypeEnum.OUT.getKey())
        );
        if (num > 0) {
            throw new BizException(ResourceMessageEnum.ETL_14.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_14.getMessage(), ThreadLocalHolder.getLang()));
        }

        String componentCode = GenerateCodeUtil.getComponent();
        BiComponent component = new BiComponent();
        component.setCode(componentCode);
        component.setName(getComponentName(biEtlModel.getCode(), ComponentTypeEnum.OUT));
        component.setType(ComponentTypeEnum.OUT.getKey());
        // ????????????????????????
        component.setEffect(EffectEnum.ENABLE.getKey());
        component.setRefModelCode(biEtlModel.getCode());
        component.setVersion("1");
        component.setPosition(dto.getPosition());
        component.setTenantId(ThreadLocalHolder.getTenantId());
        component.setComments(dto.getComments());
        componentService.save(component);

        // ?????????????????????
        List<BiEtlMappingField> fields = transferFieldsByName(componentCode, dto.getFields());
        fieldService.saveBatch(fields);

        // ????????????????????????????????????,??????????????? ????????????_????????????
        String tableDesc = StringUtils.isBlank(dto.getTableName()) ? (biEtlModel.getName() + "_" + component.getName()) : dto.getTableName();

        String folderId = dto.getFolderId();
        if (StringUtils.isBlank(folderId)) {
            folderId = "1";
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.TO_TABLE_DESC, tableDesc);
        params.put(ComponentCons.FOLDER_ID, folderId);

        List<BiComponentParams> biComponentParams = transferToParams(componentCode, biEtlModel.getCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    public BiComponent outUpdate(UpdateOutComponentDto dto) throws Exception {
        // ?????????????????????
        BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, dto.getComponentCode()));
        if (null == component) {
            throw new BizException(ResourceMessageEnum.ETL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_8.getMessage(), ThreadLocalHolder.getLang()));
        }

        // ????????????????????????
        List<BiComponentParams> originalParams = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, dto.getComponentCode()));

        // ????????????
        String originalTableDesc = null;
        // ????????????????????????id
        String originalFolderId = null;
        for (BiComponentParams param : originalParams) {
            if (null == param) {
                continue;
            }
            if (ComponentCons.TO_TABLE_DESC.equals(param.getParamKey())) {
                originalTableDesc = param.getParamValue();
            }
            if (ComponentCons.FOLDER_ID.equals(param.getParamKey())) {
                originalFolderId = param.getParamValue();
            }
        }

        //????????????????????????????????????,????????????????????????????????????????????????
        boolean pass = checkAnalyseField(dto.getComponentCode(), dto.getFields());
        if (!pass) {
            dataSetService.delete(dto.getComponentCode(), true);
        }
        // ????????????????????????????????????????????????
        String tableDesc = StringUtils.isBlank(dto.getTableName()) ? originalTableDesc : dto.getTableName();

        // ???????????????????????????id????????????????????????id
        String folderId = StringUtils.isBlank(dto.getFolderId()) ? originalFolderId : dto.getFolderId();

        // ?????????????????????
        fieldService.remove(new LambdaQueryWrapper<BiEtlMappingField>()
                .eq(BiEtlMappingField::getRefCode, dto.getComponentCode()));
        // ??????????????????
        componentParamsService.remove((new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, dto.getComponentCode())));
        // ?????????????????????
        List<BiEtlMappingField> fields = transferFieldsByName(dto.getComponentCode(), dto.getFields());
        fieldService.saveBatch(fields);

        // ??????????????????
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.TO_TABLE_DESC, tableDesc);
        params.put(ComponentCons.FOLDER_ID, folderId);

        List<BiComponentParams> biComponentParams = transferToParams(dto.getComponentCode(), component.getRefModelCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        // ??????????????????
        if (StringUtils.isNotBlank(dto.getComponentName())
                || StringUtils.isNotBlank(dto.getComments())) {
            component.setName(dto.getComponentName());
            component.setComments(dto.getComments());
            componentService.updateById(component);
        }
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent joinCreate(JoinComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new BizException(ResourceMessageEnum.ETL_9.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_9.getMessage(), ThreadLocalHolder.getLang()));
        }

        // ??????????????????
        BiComponent component = saveComponent(biEtlModel.getCode(), ComponentTypeEnum.JOIN, dto.getPosition());

        // ?????????????????????
        List<BiEtlMappingField> fields = transferFieldsByName(component.getCode(), dto.getFields());
        fieldService.saveBatch(fields);

        // ??????????????????
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.JOIN_PARAM_KEY_TABLES, JSON.toJSONString(dto.getTables()));
        List<BiComponentParams> biComponentParams = transferToParams(component.getCode(), component.getRefModelCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent joinUpdate(UpdateJoinComponentDto dto) throws Exception {
        // ???????????????????????????????????????????????????
        BiComponent component = clearComponentParams(dto.getComponentCode());
        // ??????????????????
        if (CollectionUtils.isNotEmpty(dto.getFields())) {
            List<BiEtlMappingField> fields = transferFieldsByName(component.getCode(), dto.getFields());
            fieldService.saveBatch(fields);
        }
        // ????????????????????????
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.JOIN_PARAM_KEY_TABLES, JSON.toJSONString(dto.getTables()));
        List<BiComponentParams> biComponentParams = transferToParams(component.getCode(), component.getRefModelCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        // ??????????????????
        if (StringUtils.isNotBlank(dto.getComponentName())) {
            component.setName(dto.getComponentName());
            componentService.updateById(component);
        }
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent groupCreate(GroupComponentDto dto) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new BizException(ResourceMessageEnum.ETL_9.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_9.getMessage(), ThreadLocalHolder.getLang()));
        }

        // ??????????????????
        BiComponent component = saveComponent(biEtlModel.getCode(), ComponentTypeEnum.GROUP, dto.getPosition());

        // ?????????????????????
        List<BiEtlMappingField> fields = transferFieldsByName(component.getCode(), dto.getFields());
        fieldService.saveBatch(fields);

        // ??????????????????
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.GROUP_PARAM_KEY_GROUPS, JSON.toJSONString(dto.getGroups()));
        List<BiComponentParams> biComponentParams = transferToParams(component.getCode(), component.getRefModelCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent groupUpdate(UpdateGroupComponentDto dto) throws Exception {
        // ???????????????????????????????????????????????????
        BiComponent component = clearComponentParams(dto.getComponentCode());
        // ??????????????????
        if (CollectionUtils.isNotEmpty(dto.getFields())) {
            List<BiEtlMappingField> fields = transferFieldsByName(component.getCode(), dto.getFields());
            fieldService.saveBatch(fields);
        }
        // ????????????????????????
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.GROUP_PARAM_KEY_GROUPS, JSON.toJSONString(dto.getGroups()));
        List<BiComponentParams> biComponentParams = transferToParams(component.getCode(), component.getRefModelCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        // ??????????????????
        if (StringUtils.isNotBlank(dto.getComponentName())) {
            component.setName(dto.getComponentName());
            componentService.updateById(component);
        }
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent arrangeCreate(ArrangeComponentDto dto, ArrangeTypeEnum arrangeType) throws Exception {
        BiEtlModel biEtlModel = biEtlModelService.getById(dto.getModelId());
        if (null == biEtlModel) {
            throw new BizException(ResourceMessageEnum.ETL_9.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_9.getMessage(), ThreadLocalHolder.getLang()));
        }
        // ??????????????????
        BiComponent component = saveComponent(biEtlModel.getCode(), ComponentTypeEnum.ARRANGE, dto.getPosition());

        // ??????????????????
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.ARRANGE_PARAM_KEY_TYPE, arrangeType.getType());
        params.put(ComponentCons.ARRANGE_PARAM_KEY_CONTEXT, JSON.toJSONString(dto.getFields()));
        List<BiComponentParams> biComponentParams = transferToParams(component.getCode(), component.getRefModelCode(), params);
        componentParamsService.saveBatch(biComponentParams);
        return component;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BiComponent arrangeUpdate(UpdateArrangeComponentDto dto, ArrangeTypeEnum arrangeType) throws Exception {
        // ???????????????????????????????????????????????????
        BiComponent component = clearComponentParams(dto.getComponentCode());
        // ????????????????????????
        Map<String, Object> params = Maps.newHashMap();
        params.put(ComponentCons.ARRANGE_PARAM_KEY_TYPE, arrangeType.getType());
        params.put(ComponentCons.ARRANGE_PARAM_KEY_CONTEXT, JSON.toJSONString(dto.getFields()));
        List<BiComponentParams> biComponentParams = transferToParams(component.getCode(), component.getRefModelCode(), params);
        componentParamsService.saveBatch(biComponentParams);


        try {
            //handle??????????????????
            List<BiComponentConnection> connections = connectionService.list(new LambdaQueryWrapper<BiComponentConnection>()
                    .eq(BiComponentConnection::getRefModelCode, component.getRefModelCode()));
            String endCode = currentEndCode(connections, component.getCode());
            BiComponent endComponent = componentService.getOne(new LambdaQueryWrapper<BiComponent>().eq(BiComponent::getCode, endCode));
            BiEtlModel model = biEtlModelService.getOne(new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, endComponent.getRefModelCode()));

            ComponentPreviewDto previewDto = new ComponentPreviewDto();
            previewDto.setModelId(model.getId());
            previewDto.setComponentId(endComponent.getId());
            this.handle(previewDto);
        } catch (Exception e) {
            log.error("arrangeUpdate.error: ", e);
            throw new BizException(ResourceMessageEnum.ETL_15.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_15.getMessage(), ThreadLocalHolder.getLang()));
        }
        return component;
    }

    @Override
    public ComponentResp handle(ComponentPreviewDto dto) throws Exception {
        String componentId = dto.getComponentId();
        BiComponent component = componentService.getById(componentId);
        if (component == null) {
            throw new BizException(ResourceMessageEnum.ETL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_8.getMessage(), ThreadLocalHolder.getLang()));
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

        // ????????????sql
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

        // ????????????sql
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

        // ????????????sql
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
            throw new BizException(ResourceMessageEnum.ETL_9.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_9.getMessage(), ThreadLocalHolder.getLang()));
        }

        String componentId = dto.getComponentId();
        BiComponent component = componentService.getById(componentId);
        if (component == null) {
            throw new BizException(ResourceMessageEnum.ETL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_8.getMessage(), ThreadLocalHolder.getLang()));
        }

        String modelCode = model.getCode();
        String componentCode = component.getCode();
        ComponentModel componentModel = biEtlModelHandleService.handleComponent(modelCode, componentCode);
        return SqlFormatUtil.format(componentModel.getQuerySql());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(String code) throws Exception {
        //?????????????????????????????????????????????
        BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, code)
        );
        if (null == component) {
            throw new BizException(ResourceMessageEnum.ETL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_8.getMessage(), ThreadLocalHolder.getLang()));
        }
        BiEtlModel model = biEtlModelService.getOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, component.getRefModelCode()));
        if (RunStatusEnum.RUNNING.getKey().equals(model.getStatus())) {
            throw new BizException(ResourceMessageEnum.ETL_16.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_16.getMessage(), ThreadLocalHolder.getLang()));
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

    @Override
    public List<CalculateOperatorResp> getOperators() throws Exception {
        List<CalculateOperatorResp> operators = Lists.newArrayList();
        CalculateOperatorEnum[] operatorEnums = CalculateOperatorEnum.values();
        for (CalculateOperatorEnum operatorEnum : operatorEnums) {
            CalculateOperatorResp operator = new CalculateOperatorResp();
            operator.setOperator(operatorEnum.getOperator());
            operator.setName(operatorEnum.getName());
            operator.setDesc(operatorEnum.getDesc());
            String exampleStr = operatorEnum.getExample();
            if (StringUtils.isNotBlank(exampleStr) && exampleStr.contains(";")) {
                operator.setExamples(Lists.newArrayList(operatorEnum.getExample().split(";")));
            } else {
                operator.setExamples(Lists.newArrayList(operatorEnum.getExample()));
            }
            operators.add(operator);
        }
        return operators;
    }

    @Override
    public String checkFormula(ComponentFormulaCheckDto dto) throws Exception {
        String modelId = dto.getModelId();
        BiEtlModel model = biEtlModelService.getById(modelId);
        if (model == null) {
            throw new BizException(ResourceMessageEnum.ETL_9.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_9.getMessage(), ThreadLocalHolder.getLang()));
        }

        String componentId = dto.getComponentId();
        BiComponent component = componentService.getById(componentId);
        if (component == null) {
            throw new BizException(ResourceMessageEnum.ETL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_8.getMessage(), ThreadLocalHolder.getLang()));
        }

        String formula = dto.getFormula();
        if (StringUtils.isBlank(formula)) {
            throw new BizException(ResourceMessageEnum.ETL_17.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_17.getMessage(), ThreadLocalHolder.getLang()));
        }
        CalculateTypeEnum calculateType = expressionHandler.getCalculateType(formula);
        if (calculateType == null) {
            throw new BizException(ResourceMessageEnum.ETL_18.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_18.getMessage(), ThreadLocalHolder.getLang()));
        }

        Pair<Boolean, String> checkResult = null;
        if (CalculateTypeEnum.ORDINARY.equals(calculateType)) {
            if (formula.contains("%")) {
                throw new BizException(ResourceMessageEnum.ETL_19.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.ETL_19.getMessage(), ThreadLocalHolder.getLang()));
            }

            checkResult = expressionHandler.isParamArithmeticFormula(formula);
            if (!checkResult.getKey()) {
                throw new BizException(checkResult.getValue());
            }
        }

        if (CalculateTypeEnum.FUNCTION.equals(calculateType)) {
            checkResult = expressionHandler.isParamFunctionFormula(formula);
            if (!checkResult.getKey()) {
                throw new BizException(checkResult.getValue());
            }
        }

        if (CalculateTypeEnum.LOGICAL.equals(calculateType)) {
            checkResult = expressionHandler.isFormula(formula);
            if (!checkResult.getKey()) {
                throw new BizException(checkResult.getValue());
            }
        }

        List<String> params = expressionHandler.getUniqueParams(formula);
        if (CollectionUtils.isEmpty(params)) {
            return "????????????";
        }
        // ???????????????????????????????????????
        ComponentModel componentModel = biEtlModelHandleService.handleComponent(model.getCode(), component.getCode());
        List<String> fields = componentModel.getFields();

        List<String> unknownFields = Lists.newArrayList();
        for (String param : params) {
            if (!fields.contains(param)) {
                unknownFields.add(param);
            }
        }
        if (CollectionUtils.isNotEmpty(unknownFields)) {
            throw new BizException(ResourceMessageEnum.ETL_20.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_20.getMessage(), ThreadLocalHolder.getLang()));
        }
        return localeMessageService.getMessage(ResourceMessageEnum.ETL_21.getMessage(), ThreadLocalHolder.getLang());
    }

    private List<BiComponentParams> transferToParams(String componentCode, String modelCode, Map<String, Object> source) {
        List<BiComponentParams> list = Lists.newArrayList();
        for (Map.Entry<String, Object> var : source.entrySet()) {
            String key = var.getKey();
            Object value = var.getValue();
            BiComponentParams params = new BiComponentParams();
            params.setCode(GenerateCodeUtil.genParam());
            params.setName(key);
            params.setParamKey(key);
            params.setParamValue(JsonUtil.obj2String(value));
            params.setRefComponentCode(componentCode);
            params.setRefModelCode(modelCode);
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
                throw new BizException(ResourceMessageEnum.ETL_22.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.ETL_22.getMessage(), ThreadLocalHolder.getLang()));
        }
    }


    private void transferNifiSourceRel(ResourceComponentDto dto, BiEtlMappingConfig mappingConfig, BiEtlDatabaseInf
            biEtlDatabaseInf, BiEtlModel biEtlModel, String processorsCode) throws Exception {
        BiProcessors processors = new BiProcessors();
        processors.setCode(processorsCode);
        processors.setType(BiProcessorsTypeEnum.SYNC_SOURCE.getType());
        processors.setName(dto.getComponentName());
        processors.setTypeDesc(BiProcessorsTypeEnum.getTypeDesc(processors.getType()));
        processors.setStatus(YesOrNoEnum.YES.getKey());
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

            List<String> list = Lists.newArrayList();
            list.add(" 1=1 ");
            if (StringUtils.isNotBlank(dto.getOffsetValue())) {
                list.add(dto.getOffsetField() + " >= " + dto.getOffsetValue());
            }
            if (CollectionUtils.isNotEmpty(dto.getConditions())) {
                for (ConditionDto conditionDto : dto.getConditions()) {
                    WildcardEnum wildcardEnum = WildcardEnum.get(conditionDto.getSymbol());
                    String value = wildcardEnum.expression(conditionDto.getValues());
                    String symbol = wildcardEnum.getCode();
                    String express = RelaBaseBuildUtil.condition(conditionDto.getField(), symbol, value);
                    list.add(express);
                }
            }
            if (list.size() > 1) {
                String whereClause = AnalyseUtil.join(" AND ", list.toArray(new String[0]));
                syncSql.setDttWhereClause(whereClause);
            }
            syncSql.setDttMaxValueColumns(mappingConfig.getOffsetField());
            syncSql.setDttPutReader(biTenantConfigService.getReaderId());
            syncSql.setDttPutServiceId(biTenantConfigService.getControllerServiceId());
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
        List<ConditionDto> conditionDtos = dto.getConditions();
//        String offsetValue = dto.getOffsetValue();

        BiComponentParams dulicateParam = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, oldComponent.getCode())
                .eq(BiComponentParams::getParamKey, ComponentCons.DULICATE));

        BiComponentParams conditionParam = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, oldComponent.getCode())
                .eq(BiComponentParams::getParamKey, ComponentCons.CONDITION));

        //??????????????????
        if (!dulicateParam.getParamValue().equals(duplicate)) {
            return true;
        }

        //??????????????????
        String newConditionValue = CollectionUtils.isNotEmpty(conditionDtos) ? JsonUtil.obj2String(conditionDtos) : "null";
        String oldConditionValue = null != conditionParam && StringUtils.isNotBlank(conditionParam.getParamValue()) ? conditionParam.getParamValue() : "null";
        if (!Md5Util.getMD5(newConditionValue).equals(oldConditionValue)) {
            return true;
        }

        if (YesOrNoEnum.NO.getKey().equals(duplicate)) {
            //????????????????????????????????????
            return !belongMappingCode.equals(oldComponent.getRefMappingCode());
        } else {
            BiEtlMappingConfig config = etlMappingConfigService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>().eq(BiEtlMappingConfig::getCode, oldComponent.getRefMappingCode()));
            //??????value??????
//            if (!config.getOffsetValue().equals(offsetValue)) {
//                return true;
//            }
            //????????????????????????true
            if (config.getType().equals(String.valueOf(SyncTypeEnum.LOCAL.getKey()))
                    || SyncTypeEnum.DIRECT.getKey().equals(config.getType())) {
                return true;
            }
            //???????????????
            if (!config.getRefSourceId().equals(sourceId)) {
                return true;
            }
            //?????????
            if (!config.getFromTableName().equals(tableName)) {
                return true;
            }
            //?????????????????????
            if (fieldsChange(dto, oldComponent)) {
                return true;
            }
            //??????????????????
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
                    //?????????????????????
                    BiEtlMappingConfig config = etlMappingConfigService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>().eq(BiEtlMappingConfig::getCode, oldComponent.getRefMappingCode()));
                    dbHandler.dropFields(config.getToTableName(), oldFieldSet.toArray(new String[oldFieldSet.size()]));
                    //??????field??????
                    List<BiEtlMappingField> fieldList = fieldService.list(new LambdaQueryWrapper<BiEtlMappingField>()
                            .eq(BiEtlMappingField::getRefCode, config.getCode()));
                    List<String> delFields = Lists.newArrayList();
                    for (BiEtlMappingField mappingField : fieldList) {
                        if (oldFieldSet.contains(mappingField.getFieldName())) {
                            delFields.add(mappingField.getId());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(delFields)) {
                        fieldService.removeByIds(delFields);
                    }
                }
            }
        }
    }

    /**
     * ??????????????????
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
     * ?????????????????????
     *
     * @param type ????????????
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
     * ????????????
     *
     * @param modelId     ??????id
     * @param componentId ??????id
     * @return ComponentModel
     */
    private ComponentModel handleComponent(String modelId, String componentId) {
        BiEtlModel model = biEtlModelService.getById(modelId);
        if (model == null) {
            throw new BizException(ResourceMessageEnum.ETL_9.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_9.getMessage(), ThreadLocalHolder.getLang()));
        }

        BiComponent component = componentService.getById(componentId);
        if (component == null) {
            throw new BizException(ResourceMessageEnum.ETL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_8.getMessage(), ThreadLocalHolder.getLang()));
        }

        ComponentModel componentModel = biEtlModelHandleService.handleComponent(
                model.getCode(), component.getCode());
        return componentModel;
    }

    /**
     * ??????????????????
     *
     * @param componentModel ????????????
     * @param page           ?????????
     * @param size           ???????????????
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
        // ?????????????????????????????????
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

    /**
     * ??????????????????
     *
     * @param componentCode ??????code
     * @return BiComponent
     */
    private BiComponent clearComponentParams(String componentCode) {
        // ?????????????????????
        BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getCode, componentCode));
        if (null == component) {
            throw new BizException(ResourceMessageEnum.ETL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ETL_8.getMessage(), ThreadLocalHolder.getLang()));
        }
        // ?????????????????????
        fieldService.remove(new LambdaQueryWrapper<BiEtlMappingField>()
                .eq(BiEtlMappingField::getRefCode, componentCode));
        // ??????????????????
        componentParamsService.remove((new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, componentCode)));
        return component;
    }

    private boolean checkAnalyseField(String queryTableName, List<String> columns) {
        Map<String, List<String>> analyseTable = analyseModelFieldService.getTables(queryTableName);
        if (null != analyseTable) {
            List<String> analyseFields = analyseTable.get(queryTableName);
            if (CollectionUtils.isEmpty(analyseFields) && CollectionUtils.isEmpty(columns)) {
                return true;
            }
            if (CollectionUtils.isNotEmpty(analyseFields) && CollectionUtils.isNotEmpty(columns)
                    && analyseFields.size() != columns.size()) {
                return false;
            }
            for (String field : analyseFields) {
                boolean exist = false;
                for (String newFieldName : columns) {
                    if (field.equals(newFieldName)) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    return false;
                }
            }
        }
        return true;
    }

    private String currentEndCode(List<BiComponentConnection> connections, String code) {
        String nextCode = code;
        if (CollectionUtils.isNotEmpty(connections)) {
            for (BiComponentConnection con : connections) {
                if (con.getFromComponentCode().equals(code)) {
                    nextCode = currentEndCode(connections, con.getToComponentCode());
                    break;
                }
            }
        }
        return nextCode;
    }
}
