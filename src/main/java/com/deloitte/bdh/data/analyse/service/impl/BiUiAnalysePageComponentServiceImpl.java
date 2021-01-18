package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageComponentMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageComponent;
import com.deloitte.bdh.data.analyse.model.request.pageComponentDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageComponentService;
import org.apache.commons.collections4.CollectionUtils;
import org.datanucleus.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Ashen
 * @since 2020-12-15
 */
@Service
@DS(DSConstant.BI_DB)
public class BiUiAnalysePageComponentServiceImpl extends AbstractService<BiUiAnalysePageComponentMapper, BiUiAnalysePageComponent> implements BiUiAnalysePageComponentService {

    @Override
    public Boolean saveChartComponent(pageComponentDto dto) {

        List<BiUiAnalysePageComponent> exist = list(new LambdaQueryWrapper<BiUiAnalysePageComponent>()
                .eq(BiUiAnalysePageComponent::getComponentId, dto.getComponentId())
                .eq(BiUiAnalysePageComponent::getParentId, dto.getParentId()));
        String id = "";
        if (CollectionUtils.isNotEmpty(exist)) {
            id = exist.get(0).getId();
        }
        BiUiAnalysePageComponent component = new BiUiAnalysePageComponent();
        if (StringUtils.notEmpty(id)) {
            component.setId(id);
        }
        BeanUtils.copyProperties(dto, component);
        component.setTenantId(ThreadLocalHolder.getTenantId());
        return this.saveOrUpdate(component);
    }

    @Override
    public Boolean delChartComponent(pageComponentDto dto) {

        return remove(new LambdaQueryWrapper<BiUiAnalysePageComponent>()
                .in(BiUiAnalysePageComponent::getComponentId, dto.getComponentIds())
                .eq(BiUiAnalysePageComponent::getParentId, dto.getParentId()));
    }

    @Override
    public List<BiUiAnalysePageComponent> getChartComponent(pageComponentDto dto) {
        LambdaQueryWrapper<BiUiAnalysePageComponent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalysePageComponent::getParentId, dto.getParentId());
        queryWrapper.eq(BiUiAnalysePageComponent::getCreateUser, ThreadLocalHolder.getOperator());
        return list(queryWrapper);
    }
}
