package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.dao.bi.BiComponentMapper;
import com.deloitte.bdh.data.collation.model.resp.BiComponentTree;
import com.deloitte.bdh.data.collation.service.*;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public BiComponentTree selectTree(String modelCode, String componentCode) {
        return biComponentMapper.selectTree(modelCode, componentCode);
    }

    @Override
    public void stopComponents(String modelCode) {
        List<BiComponent> components = biComponentMapper.selectList(new LambdaQueryWrapper<BiComponent>()
                .eq(BiComponent::getRefModelCode, modelCode)
                .and(wrapper -> wrapper.eq(BiComponent::getType, ComponentTypeEnum.DATASOURCE.getKey())
                        .or()
                        .eq(BiComponent::getType, ComponentTypeEnum.OUT.getKey())
                )
        );

        //调用nifi 停止与清空
        components.forEach(s -> {
            String processorsCode = getProcessorsCode(s.getCode());
            async(() -> {
                processorsService.runState(processorsCode, RunStatusEnum.STOP, true);
            });
        });
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
}
