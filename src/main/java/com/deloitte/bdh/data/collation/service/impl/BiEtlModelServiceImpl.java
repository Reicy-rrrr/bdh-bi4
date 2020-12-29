package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.cron.CronUtil;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.GetIpAndPortUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourcesTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserResource;
import com.deloitte.bdh.data.analyse.model.request.SaveResourcePermissionDto;
import com.deloitte.bdh.data.analyse.service.AnalyseUserResourceService;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlModelMapper;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.DataSetTypeEnum;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.integration.XxJobService;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    @Resource
    AnalyseUserResourceService userResourceService;


    @Override
    public List<BiEtlModel> getModelTree() {
        List<BiEtlModel> models = biEtlModelMapper.selectList(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getParentCode, "0")
                .eq(BiEtlModel::getIsFile, YesOrNoEnum.YES.getKey())
                .orderByDesc(BiEtlModel::getCreateDate)
        );

        if (CollectionUtils.isEmpty(models)) {
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
            models.add(model);
        }
        return models;
    }

    @Override
    public PageResult<List<ModelResp>> getModelPage(GetModelPageDto dto) {
        List<BiEtlModel> list = getModelListByFileCode(dto.getFileCode());
        List<ModelResp> models = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (BiEtlModel model : list) {
                ModelResp resp = new ModelResp();
                BeanUtils.copyProperties(model, resp);
                if (!StringUtil.isEmpty(model.getCronData())) {
                    resp.setCronDesc(CronUtil.createDescription(model.getCronData()));
                }
                resp.setStatus(RunStatusEnum.getEnum(resp.getStatus()).getvalue());
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
            throw new RuntimeException("当前正在执行同步任务，不允许启、停操作");
        }
        if (RunStatusEnum.RUNNING.getKey().equals(biEtlModel.getStatus())) {
            throw new RuntimeException("运行中的模板，不允许启、停操作");
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
            throw new RuntimeException("未找到目标对象");
        }

        if (YesOrNoEnum.YES.getKey().equals(inf.getIsFile())) {
            //校验是否有子文件
            List<BiEtlModel> modelList = biEtlModelMapper.selectList(new LambdaQueryWrapper<BiEtlModel>()
                    .eq(BiEtlModel::getParentCode, inf.getCode())
                    .eq(BiEtlModel::getIsFile, YesOrNoEnum.NO)
            );
            if (CollectionUtils.isNotEmpty(modelList)) {
                throw new RuntimeException("文件夹下有文件,请先删除子文件");
            }
        } else {
            if (RunStatusEnum.RUNNING.getKey().equals(inf.getStatus())) {
                throw new RuntimeException("运行状态下,不允许删除");
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
            throw new RuntimeException("未找到目标对象");
        }
        if (YesOrNoEnum.YES.getKey().equals(inf.getSyncStatus())) {
            throw new RuntimeException("当前正在执行同步任务，不允许修改");
        }
        if (RunStatusEnum.RUNNING.getKey().equals(inf.getStatus())) {
            throw new RuntimeException("运行中的模型不允许修改");
        }

        if (!StringUtil.isEmpty(dto.getName())) {
            BiEtlModel exitModel = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                    .eq(BiEtlModel::getName, dto.getName())
                    .ne(BiEtlModel::getId, inf.getId()));
            if (null != exitModel) {
                throw new RuntimeException("名称已存在");
            }
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
            throw new RuntimeException("未找到目标对象");
        }
        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new RuntimeException("失效状态下无法发布");
        }

        RunStatusEnum runStatusEnum = RunStatusEnum.getEnum(biEtlModel.getStatus());
        if (RunStatusEnum.RUNNING == runStatusEnum) {
            //判断是否有执行计划正在执行中，有则无法停止
            List<BiEtlSyncPlan> planList = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                    .eq(BiEtlSyncPlan::getRefModelCode, modelCode)
                    .isNull(BiEtlSyncPlan::getPlanResult)
            );
            if (CollectionUtils.isNotEmpty(planList)) {
                throw new RuntimeException("有任务正在执行,不允许停止");
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

            //设置数据集 此处是否该删除维度和度量？todo
            BiDataSet oldDateSet = dataSetService.getOne(new LambdaQueryWrapper<BiDataSet>().eq(BiDataSet::getRefModelCode, biEtlModel.getCode()));
            if (null != oldDateSet) {
                dataSetService.removeById(oldDateSet.getId());
                //删除权限并更新
                userResourceService.remove(new LambdaQueryWrapper<BiUiAnalyseUserResource>()
                        .eq(BiUiAnalyseUserResource::getResourceType, ResourcesTypeEnum.DATA_SET.getCode())
                        .eq(BiUiAnalyseUserResource::getResourceId, oldDateSet.getId()));
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
            //删除权限并更新
            Optional<BiComponentParams> optional = componentModel.getParams().stream()
                    .filter(s -> s.getParamKey().equals(ComponentCons.PERMISSION)).findAny();
            if (optional.isPresent()) {
                SaveResourcePermissionDto permissionDto = JsonUtil.readJsonToObjectByFastjson(optional.get().getParamValue()
                        , SaveResourcePermissionDto.class);
                permissionDto.setId(dataSet.getId());
                permissionDto.setResourceType(ResourcesTypeEnum.DATA_SET.getCode());
                userResourceService.saveResourcePermission(permissionDto);
            }

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
            throw new RuntimeException("失效状态下无法发布");
        }

        if (StringUtil.isEmpty(biEtlModel.getCronExpression())) {
            throw new RuntimeException("请先配置模板调度时间");
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
            throw new RuntimeException("未找到目标模型");
        }
        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new RuntimeException("失效状态下无法执行");
        }
        if (YesOrNoEnum.NO.getKey().equals(biEtlModel.getValidate())) {
            throw new RuntimeException("校验失败下无法执行");
        }
        if (RunStatusEnum.STOP.getKey().equals(biEtlModel.getStatus())) {
            throw new RuntimeException("模型未运行状态下无法执行");
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
            throw new RuntimeException("文件夹名称已存在");
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
                throw new RuntimeException("未找到上级的文件夹信息");
            }
            if (YesOrNoEnum.NO.getKey().equals(modelList.get(0).getIsFile())) {
                throw new RuntimeException("只能在文件夹下面创建子文件");
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
            throw new RuntimeException("模型名称已存在");
        }

        BiEtlModel inf = new BiEtlModel();
        BeanUtils.copyProperties(dto, inf);
        inf.setCode(modelCode);
        if (StringUtil.isEmpty(inf.getPosition())) {
            inf.setPosition(NifiProcessUtil.randPosition());
        }
        if ("0".equals(dto.getParentCode())) {
            throw new RuntimeException("请在文件夹下创建ETL模板");
        }
        if (StringUtil.isEmpty(dto.getCronExpression()) && StringUtil.isEmpty(dto.getCronData())) {
            throw new RuntimeException("请配置调度时间");
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

    private List<BiEtlModel> getModelListByFileCode(String fileCode) {
        LambdaQueryWrapper<BiEtlModel> fUOLamQW = new LambdaQueryWrapper();
        fUOLamQW.select(BiEtlModel.class, model -> !("CONTENT").equals(model.getColumn()));
        if (!StringUtil.isEmpty(fileCode)) {
            fUOLamQW.eq(BiEtlModel::getParentCode, fileCode);
        }
        fUOLamQW.eq(BiEtlModel::getIsFile, YesOrNoEnum.NO.getKey());
        fUOLamQW.orderByDesc(BiEtlModel::getCreateDate);
        return biEtlModelMapper.selectList(fUOLamQW);
    }

}
