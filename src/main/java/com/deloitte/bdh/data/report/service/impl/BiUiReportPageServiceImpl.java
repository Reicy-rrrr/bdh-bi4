package com.deloitte.bdh.data.report.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.report.constants.ReportPageConstants;
import com.deloitte.bdh.data.report.dao.bi.BiUiReportPageMapper;
import com.deloitte.bdh.data.report.model.BiUiReportPage;
import com.deloitte.bdh.data.report.model.request.CreateReportDto;
import com.deloitte.bdh.data.report.model.request.ReportPageReq;
import com.deloitte.bdh.data.report.model.request.UpdateReportDto;
import com.deloitte.bdh.data.report.model.resp.ReportPageTree;
import com.deloitte.bdh.data.report.service.BiUiReportPageService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public PageResult<List<BiUiReportPage>> getReportPages(ReportPageReq dto) {
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
    public BiUiReportPage getReportPage(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个resource 失败:id 不能为空");
        }
        return biuiReportPageMapper.selectById(id);
    }

    @Override
    public BiUiReportPage createReportPage(CreateReportDto dto) throws Exception {
        BiUiReportPage entity = new BiUiReportPage();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateDate(LocalDateTime.now());
        biuiReportPageMapper.insert(entity);
        return entity;
    }

    @Override
    public void delReportPage(String id) throws Exception {
//        BiUiReportPage inf = biuiReportPageMapper.selectById(id);
        biuiReportPageMapper.deleteById(id);
    }

    @Override
    public BiUiReportPage updateReportPage(UpdateReportDto dto) throws Exception {
        BiUiReportPage entity = biuiReportPageMapper.selectById(dto.getId());
        entity.setName(dto.getName());
        entity.setModifiedDate(LocalDateTime.now());
        biuiReportPageMapper.updateById(entity);
        return entity;
    }

    @Override
    public List<ReportPageTree> getTree(ReportPageReq dto) {
        LambdaQueryWrapper<BiUiReportPage> query = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            query.eq(BiUiReportPage::getTenantId, dto.getTenantId());
        }
        if (dto.getFolderOnly() != null && dto.getFolderOnly()) {
            query.eq(BiUiReportPage::getType, ReportPageConstants.FOLDER);
        }
        // 根据数据源名称模糊查询
        if (StringUtils.isNotBlank(dto.getName())) {
            query.like(BiUiReportPage::getName, dto.getName());
        }
        List<BiUiReportPage> contents = list(query);
        List<ReportPageTree> results = new ArrayList<>();
        Map<String, String> parentChildMap = new HashMap<>();
        Map<String, ReportPageTree> treeMap = new HashMap<>();
        for (BiUiReportPage page : contents) {
            ReportPageTree tree = new ReportPageTree();
            convertTree(tree, page);
            parentChildMap.put(page.getId(), page.getParentId());
            treeMap.put(page.getId(), tree);
            if (page.getParentId() == null) {
                results.add(tree);
            }
        }
        for (BiUiReportPage page : contents) {
            ReportPageTree tree = treeMap.get(page.getId());
            if (page.getParentId() != null) {
                ReportPageTree parent = treeMap.get(page.getParentId());
                parent.getChildren().add(tree);
            }
        }
        return results;
    }

    private void convertTree(ReportPageTree tree, BiUiReportPage page) {
        BeanUtils.copyProperties(page, tree);
    }
}
