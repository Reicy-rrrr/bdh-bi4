package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseCategoryMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.request.AnalyseCategoryReq;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;
import com.deloitte.bdh.data.analyse.service.BiUiAnalyseCategoryService;
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
public class BiUiAnalyseCategoryServiceImpl extends AbstractService<BiUiAnalyseCategoryMapper, BiUiAnalyseCategory> implements BiUiAnalyseCategoryService {

    @Resource
    BiUiAnalyseCategoryMapper biuiAnalyseCategoryMapper;

    @Override
    public PageResult<List<BiUiAnalyseCategory>> getAnalyseCategorys(AnalyseCategoryReq dto) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            query.eq(BiUiAnalyseCategory::getTenantId, dto.getTenantId());
        }
        // 根据数据源名称模糊查询
        if (StringUtils.isNotBlank(dto.getName())) {
            query.like(BiUiAnalyseCategory::getName, dto.getName());
        }
        query.orderByDesc(BiUiAnalyseCategory::getCreateDate);
        PageInfo<BiUiAnalyseCategory> pageInfo = new PageInfo(this.list(query));
        PageResult pageResult = new PageResult(pageInfo);
        return pageResult;
    }

    @Override
    public BiUiAnalyseCategory getAnalyseCategory(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个resource 失败:id 不能为空");
        }
        return biuiAnalyseCategoryMapper.selectById(id);
    }

    @Override
    public BiUiAnalyseCategory createAnalyseCategory(CreateAnalyseCategoryDto dto) throws Exception {
        BiUiAnalyseCategory entity = new BiUiAnalyseCategory();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateDate(LocalDateTime.now());
        biuiAnalyseCategoryMapper.insert(entity);
        return entity;
    }

    @Override
    public void delAnalyseCategory(String id) throws Exception {
//        BiUiAnalyseCategory inf = biuiAnalyseCategoryMapper.selectById(id);
        biuiAnalyseCategoryMapper.deleteById(id);
    }

    @Override
    public BiUiAnalyseCategory updateAnalyseCategory(UpdateAnalyseCategoryDto dto) throws Exception {
        BiUiAnalyseCategory entity = biuiAnalyseCategoryMapper.selectById(dto.getId());
        entity.setName(dto.getName());
        entity.setModifiedDate(LocalDateTime.now());
        biuiAnalyseCategoryMapper.updateById(entity);
        return entity;
    }

    @Override
    public List<AnalyseCategoryTree> getTree(AnalyseCategoryReq dto) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            query.eq(BiUiAnalyseCategory::getTenantId, dto.getTenantId());
        }
        if (dto.getFolderOnly() != null && dto.getFolderOnly()) {
            query.eq(BiUiAnalyseCategory::getType, AnalyseConstants.FOLDER);
        }
        // 根据数据源名称模糊查询
        if (StringUtils.isNotBlank(dto.getName())) {
            query.like(BiUiAnalyseCategory::getName, dto.getName());
        }
        List<BiUiAnalyseCategory> contents = list(query);
        List<AnalyseCategoryTree> results = new ArrayList<>();
        Map<String, String> parentChildMap = new HashMap<>();
        Map<String, AnalyseCategoryTree> treeMap = new HashMap<>();
        for (BiUiAnalyseCategory page : contents) {
            AnalyseCategoryTree tree = new AnalyseCategoryTree();
            convertTree(tree, page);
            parentChildMap.put(page.getId(), page.getParentId());
            treeMap.put(page.getId(), tree);
            if (page.getParentId() == null) {
                results.add(tree);
            }
        }
        for (BiUiAnalyseCategory page : contents) {
            AnalyseCategoryTree tree = treeMap.get(page.getId());
            if (page.getParentId() != null) {
                AnalyseCategoryTree parent = treeMap.get(page.getParentId());
                parent.getChildren().add(tree);
            }
        }
        return results;
    }

    private void convertTree(AnalyseCategoryTree tree, BiUiAnalyseCategory page) {
        BeanUtils.copyProperties(page, tree);
    }
}
