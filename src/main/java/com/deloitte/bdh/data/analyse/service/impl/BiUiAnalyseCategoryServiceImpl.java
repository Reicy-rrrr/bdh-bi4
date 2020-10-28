package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseCategoryMapper;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseDefaultCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;
import com.deloitte.bdh.data.analyse.service.BiUiAnalyseCategoryService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalyseDefaultCategoryService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtils;
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
    @Resource
    BiUiAnalyseDefaultCategoryService biUiAnalyseDefaultCategoryService;
    @Resource
    BiUiAnalysePageMapper biUiAnalysePageMapper;

    @Override
    public PageResult<List<BiUiAnalyseCategory>> getAnalyseCategories(AnalyseCategoryReq dto) {
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
        if (checkBiUiAnalyseCategoryByName(dto.getName(), dto.getTenantId(), null)) {
            BiUiAnalyseCategory customerTop = getCustomerTop(dto.getTenantId());
            if (customerTop == null) {
                throw new Exception("清先初始化默认文件夹");
            }
            BiUiAnalyseCategory entity = new BiUiAnalyseCategory();
            BeanUtils.copyProperties(dto, entity);
            entity.setCreateDate(LocalDateTime.now());
            entity.setInitType(AnalyseConstants.CATEGORY_INIT_TYPE_CUSTOMER);
            entity.setType(AnalyseConstants.CATEGORY_TYPE_CUSTOMER);
            /**
             * 创建的自定义文件夹都在我的分析下面
             */
            entity.setParentId(customerTop.getId());
            biuiAnalyseCategoryMapper.insert(entity);
            return entity;
        } else {
            throw new Exception("已存在相同名称的文件夹");
        }
    }

    private BiUiAnalyseCategory getCustomerTop(String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getInitType, AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT);
        query.eq(BiUiAnalyseCategory::getType, AnalyseConstants.CATEGORY_TYPE_PRE_DEFINED);
        List<BiUiAnalyseCategory> customerTops = list(query);
        return customerTops.size() > 0 ? customerTops.get(0) : null;
    }

    @Override
    public void delAnalyseCategory(String id) throws Exception {
        BiUiAnalyseCategory category = biuiAnalyseCategoryMapper.selectById(id);
        if (category == null) {
            throw new Exception("错误的id");
        }
        if (AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT.equals(category.getInitType())) {
            throw new Exception("默认文件夹不能删除");
        }
        //有下级的不能删除
        List<BiUiAnalyseCategory> childs = getChildBiUiAnalyseCategoryReq(category.getId(), category.getTenantId());
        if (childs.size() > 0) {
            throw new Exception("清先删除下级文件夹");
        }
        AnalysePageReq req = new AnalysePageReq();
        req.setTenantId(category.getTenantId());
        req.setCategoryId(category.getId());
        List<BiUiAnalysePage> childPages = getChildAnalysePageReq(req);
        if (childPages.size() > 0) {
            throw new Exception("清先删除下级页面");
        }
        biuiAnalyseCategoryMapper.deleteById(id);
    }

    @Override
    public BiUiAnalyseCategory updateAnalyseCategory(UpdateAnalyseCategoryDto dto) throws Exception {
        BiUiAnalyseCategory entity = biuiAnalyseCategoryMapper.selectById(dto.getId());
        if (AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT.equals(entity.getInitType())) {
            throw new Exception("默认文件夹不能修改");
        }
        if (checkBiUiAnalyseCategoryByName(dto.getName(), entity.getTenantId(), entity.getId())) {
            entity.setName(dto.getName());
            entity.setDes(dto.getDes());
            entity.setModifiedDate(LocalDateTime.now());
            biuiAnalyseCategoryMapper.updateById(entity);
            return entity;
        } else {
            throw new Exception("已存在相同名称的文件夹");
        }
    }

    @Override
    public List<AnalyseCategoryTree> getTree(AnalyseCategoryReq dto) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            query.eq(BiUiAnalyseCategory::getTenantId, dto.getTenantId());
        }
        if (dto.getInitType() != null) {
            query.eq(BiUiAnalyseCategory::getInitType, dto.getInitType());
        }
        if (dto.getType() != null) {
            query.eq(BiUiAnalyseCategory::getType, dto.getType());
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
                if (parent != null) {
                    parent.getChildren().add(tree);
                }
            }
        }
        return results;
    }

    @Override
    public void initTenantAnalyse(InitTenantReq data) throws Exception {
        if (data.getTenantId() == null) {
            throw new Exception("租户id不能为空");
        }
        List<BiUiAnalyseCategory> newCategories = new ArrayList<>();
        /**
         * 初始化默认文件夹
         */
        List<BiUiAnalyseDefaultCategory> defaultCategories = biUiAnalyseDefaultCategoryService.getAllDefaultCategories();
        List<BiUiAnalyseCategory> tenantAnalyseCategories = getTenantAnalyseCategories(data.getTenantId());
        Map<String, BiUiAnalyseCategory> tenantCategoryMap = new HashMap<>();
        Map<String, BiUiAnalyseDefaultCategory> defaultCategoryParentIdMap = new HashMap<>();
        Map<String, BiUiAnalyseDefaultCategory> defaultCategoryParentNameMap = new HashMap<>();
        for (BiUiAnalyseCategory category : tenantAnalyseCategories) {
            tenantCategoryMap.put(category.getName(), category);
        }
        for (BiUiAnalyseDefaultCategory defaultCategory : defaultCategories) {
            defaultCategoryParentIdMap.put(defaultCategory.getId(), defaultCategory);
            defaultCategoryParentNameMap.put(defaultCategory.getName(), defaultCategory);
        }
        for (BiUiAnalyseDefaultCategory defaultCategory : defaultCategories) {
            String name = defaultCategory.getName();
            BiUiAnalyseCategory category = tenantCategoryMap.get(name);
            if (category == null) {
                category = new BiUiAnalyseCategory();
                BeanUtils.copyProperties(defaultCategory, category);
                category.setCreateDate(LocalDateTime.now());
                category.setCreateUser(AnalyseUtils.getCurrentUser());
                category.setModifiedUser(null);
                category.setModifiedDate(null);
                category.setTenantId(data.getTenantId());
                category.setInitType(AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT);
                biuiAnalyseCategoryMapper.insert(category);
                tenantCategoryMap.put(name, category);
                newCategories.add(category);
            }
        }
        //处理文件夹的上下级关系
        for (BiUiAnalyseCategory category : newCategories) {
            //默认配置
            BiUiAnalyseDefaultCategory defaultCategory = defaultCategoryParentNameMap.get(category.getName());
            //parentId
            String parentId = defaultCategory.getParentId();
            if (parentId != null) {
                //默认的parent
                BiUiAnalyseDefaultCategory defaultParent = defaultCategoryParentIdMap.get(parentId);
                String parentName = defaultParent.getName();
                //当前的上级
                BiUiAnalyseCategory parent = tenantCategoryMap.get(parentName);
                //更新
                category.setParentId(parent.getId());
                biuiAnalyseCategoryMapper.updateById(category);
            }
        }
        //todo 默认文件夹的权限问题
        //todo 初始化默认报表
    }

    @Override
    public List<BiUiAnalysePage> getChildAnalysePageReq(AnalysePageReq data) {
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalysePage::getTenantId, data.getTenantId());
        if (data.getName() != null) {
            query.like(BiUiAnalysePage::getName, data.getName());
        }
        if (data.getCategoryId() != null) {
            query.eq(BiUiAnalysePage::getParentId, data.getCategoryId());
        }
        List<BiUiAnalysePage> pages = biUiAnalysePageMapper.selectList(query);
        return pages;
    }

    @Override
    public void batchDelAnalyseCategories(BatchAnalyseCategoryDelReq data) throws Exception {
        for (String id : data.getIds()) {
            delAnalyseCategory(id);
        }
    }

    public List<BiUiAnalyseCategory> getChildBiUiAnalyseCategoryReq(String parentId, String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getParentId, parentId);
        List<BiUiAnalyseCategory> pages = list(query);
        return pages;
    }

    private void convertTree(AnalyseCategoryTree tree, BiUiAnalyseCategory page) {
        BeanUtils.copyProperties(page, tree);
    }

    public boolean checkBiUiAnalyseCategoryByName(String name, String tenantId, String currentId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getName, name);
        if (currentId != null) {
            query.ne(BiUiAnalyseCategory::getId, currentId);
        }
        List<BiUiAnalyseCategory> contents = list(query);
        if (contents.size() > 0) {
            return false;
        }
        return true;
    }

    public List<BiUiAnalyseCategory> getTenantAnalyseCategories(String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        return this.list(query);
    }
}
