package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.*;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.dao.bi.BiComponentMapper;
import com.deloitte.bdh.data.collation.model.BiComponentTree;
import com.deloitte.bdh.data.collation.nifi.template.servie.Transfer;
import com.deloitte.bdh.data.collation.nifi.template.config.OutSql;
import com.deloitte.bdh.data.collation.service.*;
import com.deloitte.bdh.common.base.AbstractService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
@Service
@DS(DSConstant.BI_DB)
public class BiComponentServiceImpl extends AbstractService<BiComponentMapper, BiComponent> implements BiComponentService {

    @Resource
    private BiComponentMapper biComponentMapper;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private BiComponentParamsService componentParamsService;
    @Autowired
    private BiComponentConnectionService connectionService;
    @Autowired
    private BiEtlMappingConfigService configService;
    @Autowired
    private BiEtlSyncPlanService planService;
    @Autowired
    private DbHandler dbHandler;
    @Autowired
    private BiEtlMappingFieldService fieldService;
    @Autowired
    private Transfer transfer;

    @Override
    public BiComponentTree selectTree(String modelCode, String componentCode) {
        return biComponentMapper.selectTree(modelCode, componentCode);
    }

    @Override
    public void stopAndDelComponents(String modelCode) throws Exception {
        List<BiComponent> components = biComponentMapper.selectList(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefModelCode, modelCode)
                .eq(BiComponent::getType, ComponentTypeEnum.DATASOURCE.getKey())
        );

        BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getRelModelCode, modelCode)
                .eq(BiProcessors::getType, BiProcessorsTypeEnum.ETL_SOURCE.getType())
        );

        //数据源组件只停止
        for (BiComponent s : components) {
            String processorsGroupId = getProcessorsGroupId(s.getCode());
            transfer.stop(processorsGroupId);
        }
        //输出组件要删除
        transfer.del(processors.getProcessGroupId());
        processorsService.removeById(processors.getId());
    }

    @Override
    public void validate(String modelCode) {
        //获取模板下所有的组件
        List<BiComponent> components = biComponentMapper.selectList(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefModelCode, modelCode)
        );
        if (CollectionUtils.isEmpty(components)) {
            throw new RuntimeException("EtlServiceImpl.runModel.validate : 请先配置组件信息");
        }

        List<BiComponentConnection> connections = connectionService.list(new LambdaQueryWrapper<BiComponentConnection>()
                .eq(BiComponentConnection::getRefModelCode, modelCode)
        );

        if (CollectionUtils.isEmpty(connections)) {
            throw new RuntimeException("EtlServiceImpl.runModel.validate : 请先配置组件之间的关联关系");
        }
        //基于输出组件往上推
        Optional<BiComponent> componentOptional = components.stream().filter(param -> param.getType()
                .equals(ComponentTypeEnum.OUT.getKey())).findAny();
        if (!componentOptional.isPresent()) {
            throw new RuntimeException("EtlServiceImpl.runModel.validate : 请先配置输出组件信息");
        }
        BiComponent out = componentOptional.get();
        validate(out, components, connections);
        //todo 待完善
    }

    @Override
    public void removeResourceComponent(BiComponent component) throws Exception {
        //获取组件参数
        List<BiComponentParams> paramsList = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
        );

        //是否独立的数据源组件
        String dulicate = paramsList.stream()
                .filter(p -> p.getParamKey().equals(ComponentCons.DULICATE)).findAny().get().getParamValue();

        biComponentMapper.deleteById(component.getId());
        componentParamsService.remove(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
        );

        if (YesOrNoEnum.NO.getKey().equals(dulicate)) {
            //非独立副本可以直接删除返回
            return;
        }

        //独立副本时，该组件是否被其他模板的组件引用
        String mappingCode = component.getRefMappingCode();
        List<BiComponent> sameRefList = biComponentMapper.selectList(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefMappingCode, mappingCode)
                .ne(BiComponent::getCode, component.getCode())
        );
        if (CollectionUtils.isNotEmpty(sameRefList)) {
            throw new RuntimeException("EtlServiceImpl.removeResource.error : 该组件不能移除，已经被其他模板引用，请先取消其他被引用的组件。");
        }

        //判断当前组件同步类型，"直连或本地" 则直接删除
        BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getCode, mappingCode)
        );
        configService.removeById(config.getId());
        fieldService.remove(new LambdaQueryWrapper<BiEtlMappingField>()
                .eq(BiEtlMappingField::getRefCode, config.getCode())
        );
        if (SyncTypeEnum.DIRECT.getKey().toString().equals(config.getType())
                || SyncTypeEnum.LOCAL.getKey().toString().equals(config.getType())) {
            //直接或本地连接需要删除 mappingConfig和 fields
            return;
        }

        //当前是 "非直连、非本地"
        //不管当前是 第一次同步还是定时调度，是待同步还是同步中还是同步完成，都一致操作
        //1：若当前调度计划未完成则取消，2： 停止清空NIFI，修改状态为取消，3：删除本地表，4：删除本地组件配置，5： 删除NIFI配置

        String processorsCode = paramsList.stream()
                .filter(p -> p.getParamKey().equals(ComponentCons.REF_PROCESSORS_CDOE)).findAny().get().getParamValue();
        BiEtlSyncPlan syncPlan = planService.getOne(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getRefMappingCode, mappingCode)
                .eq(BiEtlSyncPlan::getPlanType, "0")
                .orderByDesc(BiEtlSyncPlan::getCreateDate)
                .last("limit 1")
        );
        if (StringUtils.isBlank(syncPlan.getPlanResult())) {
            syncPlan.setPlanResult(PlanResultEnum.CANCEL.getKey());
            syncPlan.setResultDesc(PlanResultEnum.CANCEL.getValue());
            planService.updateById(syncPlan);
        }

        BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getCode, processorsCode)
        );

        transfer.del(processors.getProcessGroupId());
        processorsService.removeById(processors.getId());
        dbHandler.drop(config.getToTableName());
    }

    @Override
    public void removeOut(BiComponent component) {
        // 删除组件之前先删除连接
        LambdaQueryWrapper<BiComponentConnection> wrapper = new LambdaQueryWrapper();
        wrapper.eq(BiComponentConnection::getToComponentCode, component.getCode());
        connectionService.remove(wrapper);

        String finalTableName = null;
        BiComponentParams param = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
                .eq(BiComponentParams::getParamKey, ComponentCons.TO_TABLE_NAME)
        );
        if (param != null && StringUtils.isNotBlank(param.getParamValue())) {
            finalTableName = param.getParamValue();
        }
        //删除最终表
        if (StringUtils.isNotBlank(finalTableName)) {
            dbHandler.drop(finalTableName);
        }
        biComponentMapper.deleteById(component.getId());
        componentParamsService.remove(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
        );
        fieldService.remove(new LambdaQueryWrapper<BiEtlMappingField>()
                .eq(BiEtlMappingField::getRefCode, component.getCode())
        );
    }

    @Override
    public void remove(BiComponent component) {
        // 删除组件之前先删除连接
        LambdaQueryWrapper<BiComponentConnection> wrapper = new LambdaQueryWrapper();
        wrapper.eq(BiComponentConnection::getToComponentCode, component.getCode());
        connectionService.remove(wrapper);

        biComponentMapper.deleteById(component.getId());
        componentParamsService.remove(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
        );
    }

    @Override
    public String addOutComponent(String querySql, String tableName, BiEtlModel biEtlModel) throws Exception {
        String processGroupId = transfer.add(biEtlModel.getProcessGroupId(), BiProcessorsTypeEnum.ETL_SOURCE.includeProcessor(null).getKey(), () -> {
            OutSql sql = new OutSql();
            sql.setDttDatabaseServieId("a5b9fc8e-0174-1000-0000-000039bf90cc");
            sql.setDttSqlQuery(querySql);
            sql.setDttPutReader("a5994ef0-0174-1000-0000-00006d114be3");
            sql.setDttPutServiceId("a5b9fc8e-0174-1000-0000-000039bf90cc");
            sql.setDttPutTableName(tableName);
            sql.setDttComponentName("输出组件");
            return sql;
        });
        BiProcessors processors = new BiProcessors();
        processors.setCode(GenerateCodeUtil.genProcessors());
        processors.setType(BiProcessorsTypeEnum.ETL_SOURCE.getType());
        processors.setName(BiProcessorsTypeEnum.getTypeDesc(processors.getType()) + System.currentTimeMillis());
        processors.setTypeDesc(BiProcessorsTypeEnum.getTypeDesc(processors.getType()));
        processors.setStatus(YesOrNoEnum.NO.getKey());
        processors.setEffect(EffectEnum.ENABLE.getKey());
        processors.setValidate(YesOrNoEnum.NO.getKey());
        processors.setRelModelCode(biEtlModel.getCode());
        processors.setVersion("1");
        processors.setTenantId(ThreadLocalHolder.getTenantId());
        processors.setProcessGroupId(processGroupId);
        processorsService.save(processors);
        return processGroupId;
    }

    @Override
    public String getProcessorsGroupId(String componentCode) {
        BiComponentParams componentParams = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, componentCode)
                .eq(BiComponentParams::getParamKey, ComponentCons.REF_PROCESSORS_CDOE)
        );
        if (null == componentParams) {
            return null;
        }
        BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getCode, componentParams.getParamValue())
        );
        return processors.getProcessGroupId();
    }

    @Override
    public boolean isSync(String componentCode) {
        BiComponentParams componentParams = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, componentCode)
                .eq(BiComponentParams::getParamKey, ComponentCons.DULICATE)
        );
        if (YesOrNoEnum.NO.getKey().equals(componentParams.getParamValue())) {
            return false;
        }

        BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getRefComponentCode, componentCode));

        List<BiEtlSyncPlan> syncPlans = planService.list(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getRefMappingCode, config.getCode())
                .isNull(BiEtlSyncPlan::getPlanResult));

        return CollectionUtils.isNotEmpty(syncPlans);
    }

    private void validate(BiComponent component, List<BiComponent> components, List<BiComponentConnection> connections) {
        if (EffectEnum.DISABLE.getKey().equals(component.getEffect())) {
            throw new RuntimeException("EtlServiceImpl.runModel.validate : 未生效的组件," + component.getName());
        }
        List<BiComponentConnection> collects = connections.stream().filter(s -> s.getToComponentCode().equals(component.getCode()))
                .collect(Collectors.toList());

        if (ComponentTypeEnum.DATASOURCE.getKey().equals(component.getType())) {
            if (CollectionUtils.isNotEmpty(collects)) {
                throw new RuntimeException("EtlServiceImpl.runModel.validate : 数据源组件不能被关联");
            }
            return;
        } else {
            if (CollectionUtils.isEmpty(collects)) {
                throw new RuntimeException("EtlServiceImpl.runModel.validate : 未与组件进行关联");
            }
        }

        collects.forEach(connection -> {
            String fromCode = connection.getFromComponentCode();
            if (fromCode.equals(component.getCode())) {
                throw new RuntimeException("EtlServiceImpl.runModel.validate : 组件关联不能指向自己");
            }

            Optional<BiComponent> fromComponents = components.stream().filter(s -> s.getCode().equals(fromCode)).findAny();
            if (!fromComponents.isPresent()) {
                throw new RuntimeException("EtlServiceImpl.runModel.validate : 未找到该组件的来源组件,组件名称:" + component.getName());
            }
            validate(fromComponents.get(), components, connections);
        });

    }
}
