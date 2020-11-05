package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.enums.BiProcessorsTypeEnum;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.dao.bi.BiComponentMapper;
import com.deloitte.bdh.data.collation.model.resp.BiComponentTree;
import com.deloitte.bdh.data.collation.service.*;
import com.deloitte.bdh.common.base.AbstractService;
import org.apache.commons.collections4.CollectionUtils;
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

    @Override
    public BiComponentTree selectTree(String modelCode, String componentCode) {
        return biComponentMapper.selectTree(modelCode, componentCode);
    }

    @Override
    public void stopComponents(String modelCode) throws Exception {
        List<BiComponent> components = biComponentMapper.selectList(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefModelCode, modelCode)
                .eq(BiComponent::getType, ComponentTypeEnum.DATASOURCE.getKey())
        );

        BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getRelModelCode, modelCode)
                .eq(BiProcessors::getType, BiProcessorsTypeEnum.ETL_SOURCE.getType())
        );

        //调用nifi 停止与清空
        for (BiComponent s : components) {
            String processorsCode = getProcessorsCode(s.getCode());
            processorsService.runState(processorsCode, RunStatusEnum.STOP, true);
        }
        processorsService.runState(processors.getCode(), RunStatusEnum.STOP, true);
        processorsService.removeProcessors(processors.getCode(), null);
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

        if (CollectionUtils.isEmpty(components)) {
            throw new RuntimeException("EtlServiceImpl.runModel.validate : 请先配置组件直接的关联关系");
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


    private String getProcessorsCode(String code) {
        BiComponentParams componentParams = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, code)
                .eq(BiComponentParams::getParamKey, ComponentCons.REF_PROCESSORS_CDOE)
        );
        if (null == componentParams) {
            return null;
        }
        return componentParams.getParamValue();
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
                throw new RuntimeException("EtlServiceImpl.runModel.validate : 未找到该组件的来源组件，" + component.getName());
            }
            validate(fromComponents.get(), components, connections);
        });

    }
}
