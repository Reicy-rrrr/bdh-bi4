package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.*;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.component.model.FieldMappingModel;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlModelMapper;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.integration.XxJobService;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.model.request.CreateModelDto;
import com.deloitte.bdh.data.collation.model.request.EffectModelDto;
import com.deloitte.bdh.data.collation.model.request.GetModelPageDto;
import com.deloitte.bdh.data.collation.model.request.UpdateModelDto;
import com.deloitte.bdh.data.collation.service.*;
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
    @Autowired
    private BiEtlDatabaseInfService databaseInfService;
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


    @Override
    public PageResult<List<BiEtlModel>> getModelPage(GetModelPageDto dto) {
        LambdaQueryWrapper<BiEtlModel> fUOLamQW = new LambdaQueryWrapper();
        fUOLamQW.select(BiEtlModel.class, model -> !("CONTENT").equals(model.getColumn()));
        if (!StringUtil.isEmpty(ThreadLocalHolder.getTenantId())) {
            fUOLamQW.eq(BiEtlModel::getTenantId, ThreadLocalHolder.getTenantId());
        }
        fUOLamQW.orderByDesc(BiEtlModel::getCreateDate);
        PageInfo<BiEtlModel> pageInfo = new PageInfo(this.list(fUOLamQW));
        PageResult<List<BiEtlModel>> pageResult = new PageResult(pageInfo);
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
    public BiEtlModel effectModel(EffectModelDto dto) throws Exception {
        //此次启用、停用 不调用nifi，只是bi来空则，但操作状态前提是未运行状态
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
            return;
        }
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
        Map<String, Object> sourceMap = nifiProcessService.delProcessGroup(processGroupId);

        jobService.remove(inf.getCode());
        biEtlModelMapper.deleteById(id);
        logger.info("删除数据成功:{}", JsonUtil.obj2String(sourceMap));
    }

    @Override
    public BiEtlModel updateModel(UpdateModelDto dto) throws Exception {
        BiEtlModel inf = biEtlModelMapper.selectById(dto.getId());
        if (YesOrNoEnum.YES.getKey().equals(inf.getSyncStatus())) {
            throw new RuntimeException("当前正在执行同步任务，不允许修改");
        }
        if (RunStatusEnum.RUNNING.getKey().equals(inf.getStatus())) {
            throw new RuntimeException("运行中的 model 不允许修改");
        }

        if (!StringUtil.isEmpty(dto.getName())) {
            inf.setName(dto.getName());
        }
        if (!StringUtil.isEmpty(dto.getComments())) {
            inf.setComments(dto.getComments());
        }
        if (!StringUtil.isEmpty(dto.getContent())) {
            inf.setContent(dto.getContent());
        }
        if (!StringUtil.isEmpty(dto.getCronExpression())) {
            inf.setCornExpression(dto.getCronExpression());
            //调用xxjob 设置调度任务
            Map<String, String> params = Maps.newHashMap();
            params.put("modelCode", inf.getCode());
            params.put("tenantCode", ThreadLocalHolder.getTenantCode());
            params.put("tenantId", ThreadLocalHolder.getTenantId());
            params.put("operator", ThreadLocalHolder.getOperator());
            jobService.update(inf.getCode(), GetIpAndPortUtil.getIpAndPort() + "/bi/biEtlSyncPlan/model",
                    dto.getCronExpression(), params);
        }
        //调用nifi
        Map<String, Object> reqNifi = Maps.newHashMap();
        reqNifi.put("id", inf.getProcessGroupId());
        reqNifi.put("name", inf.getName());
        reqNifi.put("comments", inf.getComments());

        Map<String, Object> sourceMap = nifiProcessService.updProcessGroup(reqNifi);
        inf.setVersion(NifiProcessUtil.getVersion(sourceMap));
        biEtlModelMapper.updateById(inf);
        return inf;
    }

    @Override
    public void runModel(String modelCode) throws Exception {
        BiEtlModel biEtlModel = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );
        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.runModel.error : 未找到目标 模型");
        }
        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new RuntimeException("EtlServiceImpl.runModel.error : 失效状态下无法发布");
        }

        RunStatusEnum runStatusEnum = RunStatusEnum.getEnum(biEtlModel.getStatus());
        if (RunStatusEnum.RUNNING == runStatusEnum) {
            // 停止数据源组nifi 、停止与删除etl NIFI
            componentService.stopAndDelComponents(modelCode);
            // 停止 job
            jobService.stop(modelCode);
            //判断是否有执行计划正在执行中，有则取消
            List<BiEtlSyncPlan> planList = syncPlanService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                    .eq(BiEtlSyncPlan::getRefModelCode, modelCode)
                    .isNull(BiEtlSyncPlan::getPlanResult)
            );
            if (CollectionUtils.isNotEmpty(planList)) {
                planList.forEach(s -> s.setPlanResult(PlanResultEnum.CANCEL.getValue()));
                syncPlanService.updateBatchById(planList);
            }
            biEtlModel.setStatus(RunStatusEnum.STOP.getKey());
        } else {
            validate(modelCode);
            ComponentModel componentModel = modelHandleService.handleModel(modelCode);
            List<TableField> columns = componentModel.getFieldMappings().stream().map(FieldMappingModel::getTableField)
                    .collect(Collectors.toList());
            //创建nifi 配置
            componentService.addOutComponent(componentModel.getQuerySql(), componentModel.getTableName(), biEtlModel);
            //创建表
            dbHandler.createTable(componentModel.getTableName(), columns);
            //启动模板 ，启动xxjob，有job去生成执行计划
            jobService.start(modelCode);
            biEtlModel.setStatus(RunStatusEnum.RUNNING.getKey());
            biEtlModel.setValidate(YesOrNoEnum.YES.getKey());
        }
        biEtlModelMapper.updateById(biEtlModel);
    }

    @Override
    public void validate(String modelCode) {
        //1：校验数据源是否可用，2：校验是否配置cron 表达式，3：校验输入组件，输出组件，4：校验nifi配置
        List<BiEtlMappingConfig> mappingConfigs = mappingConfigService.list(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getRefModelCode, modelCode)
        );

        if (CollectionUtils.isEmpty(mappingConfigs)) {
            throw new RuntimeException("校验失败:该模板未关联数据源");
        }

        mappingConfigs.forEach(s -> {
            BiEtlDatabaseInf databaseInf = databaseInfService.getById(s.getRefSourceId());
            if (!databaseInf.getEffect().equals(EffectEnum.ENABLE.getKey())) {
                throw new RuntimeException("校验失败:该模板关联的数据源状态异常");
            }
        });

        BiEtlModel biEtlModel = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );
        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new RuntimeException("EtlServiceImpl.runModel.validate : 失效状态下无法发布");
        }

        if (StringUtil.isEmpty(biEtlModel.getCornExpression())) {
            throw new RuntimeException("EtlServiceImpl.runModel.validate : 请先配置模板调度时间");
        }
        componentService.validate(modelCode);
    }

    @Override
    public void trigger(String modelCode) throws Exception {
        //运行后才能促发，且当前没有正在执行的调度计划
        BiEtlModel biEtlModel = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getCode, modelCode)
        );

        if (null == biEtlModel) {
            throw new RuntimeException("EtlServiceImpl.trigger.error : 未找到目标 模型");
        }
        if (EffectEnum.DISABLE.getKey().equals(biEtlModel.getEffect())) {
            throw new RuntimeException("EtlServiceImpl.trigger.error : 失效状态下无法执行");
        }
        if (YesOrNoEnum.NO.getKey().equals(biEtlModel.getValidate())) {
            throw new RuntimeException("EtlServiceImpl.trigger.error : 校验失败下无法执行");
        }
        if (RunStatusEnum.STOP.getKey().equals(biEtlModel.getStatus())) {
            throw new RuntimeException("EtlServiceImpl.trigger.error : 未运行状态下无法执行");
        }
        //未执行任务才执行
        if (YesOrNoEnum.NO.getKey().equals(biEtlModel.getSyncStatus())) {
            jobService.trigger(modelCode);
        }

    }

    private BiEtlModel doFile(String modelCode, CreateModelDto dto) {
        BiEtlModel inf = new BiEtlModel();
        //文件夹
        if (YesOrNoEnum.YES.getKey().equals(dto.getIsFile())) {
            inf.setCode(modelCode);
            if ("0".equals(dto.getParentCode())) {
                inf.setRootCode(modelCode);
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
            biEtlModelMapper.insert(inf);
        }
        return inf;
    }

    private BiEtlModel doModel(String modelCode, CreateModelDto dto) throws Exception {
        BiEtlModel model = biEtlModelMapper.selectOne(new LambdaQueryWrapper<BiEtlModel>()
                .eq(BiEtlModel::getName, dto.getName())
        );
        if (null != model) {
            throw new RuntimeException("模板编码名字重复!");
        }
        BiEtlModel inf = new BiEtlModel();
        BeanUtils.copyProperties(dto, inf);
        inf.setCode(modelCode);
        if (StringUtil.isEmpty(inf.getPosition())) {
            inf.setPosition(NifiProcessUtil.randPosition());
        }
        if (!"0".equals(dto.getParentCode())) {
            throw new RuntimeException("非文件夹模式下只能创建ETL模板");
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
        reqNifi.put("position", JsonUtil.string2Obj(NifiProcessUtil.randPosition(), Map.class));
        Map<String, Object> sourceMap = nifiProcessService.createProcessGroup(reqNifi, null);

        if (!StringUtil.isEmpty(dto.getCronExpression())) {
            Map<String, String> params = Maps.newHashMap();
            params.put("modelCode", inf.getCode());
            params.put("tenantCode", ThreadLocalHolder.getTenantCode());
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
}
