package com.deloitte.bdh.data.report.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.GetResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.report.dao.bi.BiUiReportPageMapper;
import com.deloitte.bdh.data.report.model.BiUiReportPage;
import com.deloitte.bdh.data.report.service.BiUiReportPageService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@Service
@DS(DSConstant.BI_DB)
public class BiUiReportPageServiceImpl extends AbstractService<BiUiReportPageMapper, BiUiReportPage> implements BiUiReportPageService {

    @Resource
    BiUiReportPageMapper biuiReportPageMapper;

    @Override
    public PageResult<List<BiUiReportPage>> getResources(GetResourcesDto dto) {
        LambdaQueryWrapper<BiUiReportPage> query = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            query.eq(BiUiReportPage::getTenantId, dto.getTenantId());
        }
        // 根据数据源名称模糊查询
        if (StringUtils.isNotBlank(dto.getName())) {
            query.like(BiUiReportPage::getName, dto.getName());
        }
        query.orderByDesc(BiUiReportPage::getCreateDate);
        PageInfo<BiUiReportPage> pageInfo = new PageInfo(this.list(query));
        PageResult pageResult = new PageResult(pageInfo);
        return pageResult;
    }

    @Override
    public BiUiReportPage getResource(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个resource 失败:id 不能为空");
        }
        return biuiReportPageMapper.selectById(id);
    }

    @Override
    public BiUiReportPage createResource(CreateResourcesDto dto) throws Exception {
        BiUiReportPage entity = new BiUiReportPage();
        BeanUtils.copyProperties(dto, entity);
        biuiReportPageMapper.insert(entity);
        return entity;
    }

    @Override
    public void delResource(String id) throws Exception {
//        BiUiReportPage inf = biuiReportPageMapper.selectById(id);
        biuiReportPageMapper.deleteById(id);
    }

    @Override
    public BiUiReportPage updateResource(UpdateResourcesDto dto) throws Exception {
//        BiUiReportPage inf = biuiReportPageMapper.selectById(dto.getId());
        BiUiReportPage entity = new BiUiReportPage();
        BeanUtils.copyProperties(dto, entity);
        entity.setModifiedDate(LocalDateTime.now());
        biuiReportPageMapper.updateById(entity);
        return entity;
    }
}
