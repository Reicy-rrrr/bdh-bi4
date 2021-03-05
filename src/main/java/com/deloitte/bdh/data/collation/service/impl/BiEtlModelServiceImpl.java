package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.cron.CronUtil;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.GetIpAndPortUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.controller.BiTenantConfigController;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlModelMapper;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.DataSetTypeEnum;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.service.NifiProcessService;
import com.deloitte.bdh.data.collation.service.XxJobService;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.model.request.CreateModelDto;
import com.deloitte.bdh.data.collation.model.request.EffectModelDto;
import com.deloitte.bdh.data.collation.model.request.GetModelPageDto;
import com.deloitte.bdh.data.collation.model.request.UpdateModelDto;
import com.deloitte.bdh.data.collation.model.resp.ModelResp;
import com.deloitte.bdh.data.collation.service.BiComponentService;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.deloitte.bdh.data.collation.service.BiEtlModelHandleService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import com.deloitte.bdh.data.collation.service.BiTenantConfigService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-09-24
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlModelServiceImpl extends AbstractService<BiEtlModelMapper, BiEtlModel> implements BiEtlModelService {
    private static final Logger logger = LoggerFactory.getLogger(BiEtlModelServiceImpl.class);
    @Resource
    private BiEtlModelMapper biEtlModelMapper;
    @Autowired
    private NifiProcessService nifiProcessService;
    @Autowired
    private XxJobService jobService;
    @Autowired
    private BiComponentService componentService;
    @Autowired
    private BiEtlSyncPlanService syncPlanService;
    @Autowired
    private BiEtlMappingConfigService mappingConfigService;
    @Autowired
    private DbHandler dbHandler;
    @Autowired
    private BiEtlModelHandleService modelHandleService;
    @Autowired
    private BiTenantConfigService biTenantConfigService;
    @Resource
    private BiDataSetService dataSetService;


    @Override
    public void initModelTree() {
        if (0 == this.count()) {
            //创建初始化模板
            BiEtlModel model = new BiEtlModel();
            String code = GenerateCodeUtil.generate();
            model.setCode(code);
            model.setName("默认文件夹");
            model.setComments("默认文件夹");
            model.setVersion("0");
            model.setParentCode("0");
            model.setRootCode(code);
            model.setIsFile(YesOrNoEnum.YES.getKey());
            model.setEffect(EffectEnum.ENABLE.getKey());
            model.setTenantId(ThreadLocalHolder.getTenantId());
            model.setProcessGroupId(code);
            biEtlModelMapper.insert(model);
        }
    }

    @Override
    public List<BiEtlModel> getModelTree(String superUserFlag) {
        List<String> userList = Lists.newArrayList(ThreadLocalHolder.getOperator(), BiTenantConfigController.OPERATOR);
        LambdaQueryWrapper<BiEtlModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BiEtlModel::getParentCode, "0");
        lambdaQueryWrapper.eq(BiEtlModel::getIsFile, YesOrNoEnum.YES.getKey());
        if (!StringUtils.equals(superUserFlag, YesOrNoEnum.YES.getKey())) {
            lambdaQueryWrapper.in(BiEtlModel::getCreateUser, userList);
        }
        lambdaQueryWrapper.orderByDesc(BiEtlModel::getCreateDate);
        return list(lambdaQueryWrapper);
    }

    @Override
    public PageResult<List<ModelResp>> getModelPage(GetModelPageDto dto) {
        List<BiEtlModel> list = getModelListByFileCode(dto.getFileCode(), dto.getSuperUserFlag());
        List<ModelResp> models = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (BiEtlModel model : list) {
                ModelResp resp = new ModelResp();
                BeanUtils.copyProperties(model, resp);
                if (!StringUtil.isEmpty(model.getCronData())) {
                    resp.setCronDesc(CronUtil.createDescription(model.getCronData()));
                }
                models.add(resp);
            }
        }

        PageInfo<BiEtlModel> pageInfo = new PageInfo(models);
        PageResult<List<ModelResp>> pageResult = new PageResult(pageInfo);
        return pageResult;
    }

    @Override
    public BiEtlModel createModel(CreateModelDto dto) throws Exception {
        String modelCode = GenerateCodeUtil.genModel();

        //处理文件夹
        if (YesOrNoEnum.YES.getKey().equals(dto.getIsFile())) {
            return doFile(modelCode, dto);
        }
        //处理模板
        return doModel(modelCode, dto);
    }

    @Override
    public BiEtlModel effectModel(EffectModelDto dto) {
        //此启用、停用不调用nifi，只是bi来控制，但操作状态前提是未运行状态
        BiEtlModel biEtlModel = biEtlModelMapper.selectById(dto.getId());
        if (YesOrNoEnum.YES.getKey().equals(biEtlModel.getSyncStatus())) {
            throw new BizException(ResourceMessageEnum.MODEL_1.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_1.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (RunStatusEnum.RUNNING.getKey().equals(biEtlModel.getStatus())) {
            throw new BizException(ResourceMessageEnum.MODEL_2.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_2.getMessage(), ThreadLocalHolder.getLang()));
        }
        biEtlModel.setEffect(dto.getEffect());
        biEtlModelMapper.updateById(biEtlModel);
        return biEtlModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delModel(String id) throws Exception {
        BiEtlModel inf = biEtlModelMapper.selectById(id);
        if (inf == null) {
            throw new BizException(ResourceMessageEnum.MODEL_3.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_3.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (YesOrNoEnum.YES.getKey().equals(inf.getIsFile())) {
            if (inf.getCreateUser().equals(BiTenantConfigController.OPERATOR)) {
                throw new BizException(ResourceMessageEnum.DEFAULT_DATA_LOCK.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.DEFAULT_DATA_LOCK.getMessage(), ThreadLocalHolder.getLang()));

            }
            //校验是否有子文件
            List<BiEtlModel> modelList = biEtlModelMapper.selectList(new LambdaQueryWrapper<BiEtlModel>()
                    .eq(BiEtlModel::getParentCode, inf.getCode())
                    .eq(BiEtlModel::getIsFile, YesOrNoEnum.NO)
            );
            if (CollectionUtils.isNotEmpty(modelList)) {
                throw new BizException(ResourceMessageEnum.MODEL_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.MODEL_4.getMessage(), ThreadLocalHolder.getLang()));
            }
        } else {
            if (RunStatusEnum.RUNNING.getKey().equals(inf.getStatus())) {
                throw new BizException(ResourceMessageEnum.MODEL_5.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.MODEL_5.getMessage(), ThreadLocalHolder.getLang()));
            }

            //获取模板下的组件集合
            List<BiComponent> componentList = componentService.list(new LambdaQueryWrapper<BiComponent>()
                    .eq(BiComponent::getRefModelCode, inf.getCode())
            );

            // 最终表名
            for (BiComponent component : componentList) {
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

            //删除model
            String processGroupId = inf.getProcessGroupId();
            nifiProcessService.delProcessGroup(processGroupId);
            jobService.remove(inf.getCode());
        }
        biEtlModelMapper.deleteById(id);
    }

    @Override
    public BiEtlModel updateModel(UpdateModelDto dto) throws Exception {
        BiEtlModel inf = biEtlModelMapper.selectById(dto.getId());
        if (inf == null) {
            throw new BizException(ResourceMessageEnum.MODEL_3.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_3.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (YesOrNoEnum.YES.getKey().equals(inf.getSyncStatus())) {
            throw new BizException(ResourceMessageEnum.MODEL_1.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_1.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (RunStatusEnum.RUNNING.getKey().equals(inf.getStatus())) {
            throw new BizException(ResourceMessageEnum.MODEL_5.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_5.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (!StringUtil.isEmpty(dto.getName())) {
//            BiEtlModel exitModel = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
//                    .eq(BiEtlModel::getName, dto.getName())
//                    .ne(BiEtlModel::getId, inf.getId()));
//            if (null != exitModel) {
//                throw new RuntimeException("名称已存在");
//            }
            inf.setName(dto.getName());
        }
        if (!StringUtil.isEmpty(dto.getComments())) {
            inf.setComments(dto.getComments());
        }
        if (YesOrNoEnum.NO.getKey().equals(inf.getIsFile())) {
            if (!StringUtil.isEmpty(dto.getCronData())) {
                inf.setCronData(dto.getCronData());
                dto.setCronExpression(CronUtil.createCronExpression(dto.getCronData()));
            }
            if (!StringUtil.isEmpty(dto.getCronExpression())) {
                inf.setCronExpression(dto.getCronExpression());
                //调用xxjob 设置调度任务
                Map<String, String> params = Maps.newHashMap();
                params.put("modelCode", inf.getCode());
                params.put("tenantId", ThreadLocalHolder.getTenantId());
                params.put("operator", ThreadLocalHolder.getOperator());
                jobService.addOrUpdate(inf.getCode(), GetIpAndPortUtil.getIpAndPort() + "/bi/biEtlSyncPlan/model",
                        dto.getCronExpression(), params);
            }

            //调用nifi
            if (!StringUtil.isEmpty(dto.getName())) {
                Map<String, Object> reqNifi = Maps.newHashMap();
                reqNifi.put("id", inf.getProcessGroupId());
                reqNifi.put("name", inf.getName());
                reqNifi.put("comments", inf.getComments());
                Map<String, Object> sourceMap = nifiProcessService.updProcessGroup(reqNifi);
                inf.setVersion(NifiProcessUtil.getVersion(sourceMap));
            }

            if (!StringUtil.isEmpty(dto.getFileCode())) {
                inf.setParentCode(dto.getFileCode());
            }
        } else {
            //默认文件夹不允许删除
            if (inf.getCreateUser().equals(BiTenantConfigController.OPERATOR)) {
                throw new BizException(ResourceMessageEnum.DEFAULT_DATA_LOCK.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.DEFAULT_DATA_LOCK.getMessage(), ThreadLocalHolder.getLang()));
            }
        }

        biEtlModelMapper.updateById(inf);
        return inf;
    }

    @Override
    public BiEtlModel runModel(String modelCode) throws Exception {
        BiEtlModel biEtlModel = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );
        if (null == biEtlModel) {
            throw new BizException(ResourceMessageEnum.MODEL_3.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_3.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new BizException(ResourceMessageEnum.MODEL_6.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_6.getMessage(), ThreadLocalHolder.getLang()));
        }

        RunStatusEnum runStatusEnum = RunStatusEnum.getEnum(biEtlModel.getStatus());
        if (RunStatusEnum.RUNNING == runStatusEnum) {
            //判断是否有执行计划正在执行中，有则无法停止
            List<BiEtlSyncPlan> planList = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                    .eq(BiEtlSyncPlan::getRefModelCode, modelCode)
                    .isNull(BiEtlSyncPlan::getPlanResult)
            );
            if (CollectionUtils.isNotEmpty(planList)) {
                throw new BizException(ResourceMessageEnum.MODEL_5.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.MODEL_5.getMessage(), ThreadLocalHolder.getLang()));
            }
            // 此时不会有待执行的执行计划，停止数据源组nifi 、停止与删除etl NIFI
            componentService.stopAndDelComponents(modelCode);
            // 停止 job
            jobService.stop(modelCode);

            biEtlModel.setStatus(RunStatusEnum.STOP.getKey());
            biEtlModel.setSyncStatus(YesOrNoEnum.NO.getKey());
        } else {
            //校验模板配置
            runValidate(modelCode);
            ComponentModel componentModel = modelHandleService.handleModel(modelCode);
            List<TableField> columns = componentModel.getFieldMappings().stream().map(FieldMappingModel::getTableField)
                    .collect(Collectors.toList());

            //创建输出组件的nifi配置
            componentService.addOutComponentForNifi(componentModel.getQuerySql(), componentModel.getTableName(), biEtlModel);
            //创建表
            dbHandler.createTable(componentModel.getTableName(), columns);
            //启动模板 ，启动xxjob，有job去生成执行计划
            jobService.start(modelCode);
            biEtlModel.setStatus(RunStatusEnum.RUNNING.getKey());
            biEtlModel.setValidate(YesOrNoEnum.YES.getKey());

            //设置数据集
            BiDataSet oldDateSet = dataSetService.getOne(new LambdaQueryWrapper<BiDataSet>().eq(BiDataSet::getRefModelCode, biEtlModel.getCode()));
            if (null != oldDateSet) {
                dataSetService.removeById(oldDateSet.getId());
            }
            BiDataSet dataSet = new BiDataSet();
            dataSet.setType(DataSetTypeEnum.MODEL.getKey());
            dataSet.setTableName(componentModel.getTableName());
            dataSet.setTableDesc(componentModel.getTableDesc());
            dataSet.setCode(componentModel.getCode());
            dataSet.setComments(componentModel.getComments());
            dataSet.setRefModelCode(biEtlModel.getCode());
            dataSet.setParentId(componentModel.getFolderId());
            dataSet.setIsFile(YesOrNoEnum.NO.getKey());
            dataSet.setTenantId(ThreadLocalHolder.getTenantId());
            dataSetService.save(dataSet);
        }
        biEtlModelMapper.updateById(biEtlModel);
        return biEtlModel;
    }

    @Override
    public void runValidate(String modelCode) throws Exception {
        //1：校验模板状态
        BiEtlModel biEtlModel = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );
        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new BizException(ResourceMessageEnum.MODEL_6.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_6.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (StringUtil.isEmpty(biEtlModel.getCronExpression())) {
            throw new BizException(ResourceMessageEnum.MODEL_7.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_7.getMessage(), ThreadLocalHolder.getLang()));
        }
        //2：校验是否配置cron
        CronUtil.validate(biEtlModel.getCronExpression());
        //3：校验输入组件，输出组件
        componentService.validate(modelCode);
        //4：校验数据源是否可用
        mappingConfigService.validateSource(modelCode);
        //5：校验nifi配置
        //todo
    }

    @Override
    public void trigger(String modelCode) throws Exception {
        //运行后才能促发，且当前没有正在执行的调度计划
        BiEtlModel biEtlModel = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );

        if (null == biEtlModel) {
            throw new BizException(ResourceMessageEnum.MODEL_3.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_3.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new BizException(ResourceMessageEnum.MODEL_6.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_6.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (YesOrNoEnum.NO.getKey().equals(biEtlModel.getValidate())) {
            throw new BizException(ResourceMessageEnum.MODEL_8.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_8.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (RunStatusEnum.STOP.getKey().equals(biEtlModel.getStatus())) {
            throw new BizException(ResourceMessageEnum.MODEL_9.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_9.getMessage(), ThreadLocalHolder.getLang()));
        }
        //未执行任务才执行
        if (YesOrNoEnum.NO.getKey().equals(biEtlModel.getSyncStatus())) {
            Map<String, String> req = Maps.newHashMap();
            req.put("isTrigger", YesOrNoEnum.YES.getKey());
            jobService.triggerParams(modelCode, req);
        }

    }

    private BiEtlModel doFile(String modelCode, CreateModelDto dto) {
        BiEtlModel model = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getName, dto.getName())
                .eq(BiEtlModel::getIsFile, YesOrNoEnum.YES.getKey())
        );
        if (null != model) {
            throw new BizException(ResourceMessageEnum.MODEL_10.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_10.getMessage(), ThreadLocalHolder.getLang()));
        }

        BiEtlModel inf = new BiEtlModel();
        BeanUtils.copyProperties(dto, inf);
        inf.setCode(modelCode);
        if ("0".equals(dto.getParentCode())) {
            inf.setRootCode(inf.getCode());
        } else {
            //查询上级是否是文件夹
            Map<String, Object> query = Maps.newHashMap();
            query.put("code", dto.getParentCode());
            List<BiEtlModel> modelList = biEtlModelMapper.selectByMap(query);
            if (CollectionUtils.isEmpty(modelList)) {
                throw new BizException(ResourceMessageEnum.MODEL_11.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.MODEL_11.getMessage(), ThreadLocalHolder.getLang()));
            }
            if (YesOrNoEnum.NO.getKey().equals(modelList.get(0).getIsFile())) {
                throw new BizException(ResourceMessageEnum.MODEL_12.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.MODEL_12.getMessage(), ThreadLocalHolder.getLang()));
            }
        }
        inf.setName(dto.getName());
        inf.setComments(dto.getComments());
        inf.setTenantId(ThreadLocalHolder.getTenantId());
        inf.setVersion("0");
        inf.setEffect(EffectEnum.ENABLE.getKey());
        inf.setProcessGroupId(modelCode);
        biEtlModelMapper.insert(inf);
        return inf;
    }

    private BiEtlModel doModel(String modelCode, CreateModelDto dto) throws Exception {
        BiEtlModel model = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getName, dto.getName())
                .eq(BiEtlModel::getIsFile, YesOrNoEnum.NO.getKey())
        );
        if (null != model) {
            throw new BizException(ResourceMessageEnum.MODEL_13.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_13.getMessage(), ThreadLocalHolder.getLang()));
        }

        BiEtlModel inf = new BiEtlModel();
        BeanUtils.copyProperties(dto, inf);
        inf.setCode(modelCode);
        if (StringUtil.isEmpty(inf.getPosition())) {
            inf.setPosition(NifiProcessUtil.randPosition());
        }
        if ("0".equals(dto.getParentCode())) {
            throw new BizException(ResourceMessageEnum.MODEL_14.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_14.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (StringUtil.isEmpty(dto.getCronExpression()) && StringUtil.isEmpty(dto.getCronData())) {
            throw new BizException(ResourceMessageEnum.MODEL_15.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_15.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (!StringUtil.isEmpty(dto.getCronExpression())) {
            CronUtil.validate(dto.getCronExpression());
        }
        //生效、失效的状态
        inf.setEffect(EffectEnum.ENABLE.getKey());
        //初始化 为未运行状态 对应nifi stopped RUNNIG
        inf.setStatus(RunStatusEnum.STOP.getKey());
        // 设置 validate
        inf.setValidate(YesOrNoEnum.NO.getKey());
        inf.setSyncStatus(YesOrNoEnum.NO.getKey());

        //调用NIFI 创建模板
        Map<String, Object> reqNifi = Maps.newHashMap();
        reqNifi.put("name", inf.getName());
        reqNifi.put("comments", inf.getComments());
        reqNifi.put("position", JsonUtil.JsonStrToMap(NifiProcessUtil.randPosition()));
        Map<String, Object> sourceMap = nifiProcessService.createProcessGroup(reqNifi, biTenantConfigService.getGroupId());

        if (!StringUtil.isEmpty(dto.getCronData())) {
            dto.setCronExpression(CronUtil.createCronExpression(dto.getCronData()));
        }
        if (!StringUtil.isEmpty(dto.getCronExpression())) {
            Map<String, String> params = Maps.newHashMap();
            params.put("modelCode", inf.getCode());
            params.put("tenantId", ThreadLocalHolder.getTenantId());
            params.put("operator", ThreadLocalHolder.getOperator());
            jobService.add(inf.getCode(), GetIpAndPortUtil.getIpAndPort() + "/bi/biEtlSyncPlan/model",
                    dto.getCronExpression(), params);
        }

        //nifi 返回后设置补充dto
        inf.setVersion(NifiProcessUtil.getVersion(sourceMap));
        inf.setProcessGroupId(MapUtils.getString(sourceMap, "id"));
        inf.setTenantId(ThreadLocalHolder.getTenantId());
        inf.setId(ThreadLocalHolder.getIp());
        biEtlModelMapper.insert(inf);
        return inf;
    }

    private List<BiEtlModel> getModelListByFileCode(String fileCode, String superFlag) {
        LambdaQueryWrapper<BiEtlModel> fUOLamQW = new LambdaQueryWrapper();
        fUOLamQW.select(BiEtlModel.class, model -> !("CONTENT").equals(model.getColumn()));
        if (!StringUtil.isEmpty(fileCode)) {
            fUOLamQW.eq(BiEtlModel::getParentCode, fileCode);
        }
        fUOLamQW.eq(BiEtlModel::getIsFile, YesOrNoEnum.NO.getKey());
        if (!StringUtils.equals(superFlag, YesOrNoEnum.YES.getKey())) {
            fUOLamQW.eq(BiEtlModel::getCreateUser, ThreadLocalHolder.getOperator());
        }
        fUOLamQW.orderByDesc(BiEtlModel::getCreateDate);
        return biEtlModelMapper.selectList(fUOLamQW);
    }

}
