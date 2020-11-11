package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseCategoryMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseDefaultCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalyseCategoryService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalyseDefaultCategoryService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
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
    BiUiAnalyseDefaultCategoryService biUiAnalyseDefaultCategoryService;

    @Resource
    BiUiAnalysePageService pageService;

    @Override
    public PageResult<List<BiUiAnalyseCategory>> getAnalyseCategoryList(RetRequest<AnalyseCategoryReq> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(request.getTenantId())) {
            query.eq(BiUiAnalyseCategory::getTenantId, request.getTenantId());
        }
        // 根据数据源名称模糊查询
        if (StringUtils.isNotBlank(request.getData().getName())) {
            query.like(BiUiAnalyseCategory::getName, request.getData().getName());
        }
        query.orderByDesc(BiUiAnalyseCategory::getCreateDate);
        PageInfo<BiUiAnalyseCategory> page = new PageInfo<>(this.list(query));
        return new PageResult<>(page);
    }

    @Override
    public BiUiAnalyseCategory getAnalyseCategory(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new BizException("查看单个resource 失败:id 不能为空");
        }
        return this.getById(id);
    }

    @Override
    public BiUiAnalyseCategory createAnalyseCategory(CreateAnalyseCategoryDto dto) {
        checkBiUiAnalyseCategoryByName(dto.getName(), dto.getTenantId(), null);
        BiUiAnalyseCategory parent = null;
        if (dto.getParentId() == null) {
            parent = getCustomerTop(dto.getTenantId());
            if (parent == null) {
                throw new BizException("清先初始化默认文件夹");
            }
        } else {
            parent = getAnalyseCategory(dto.getParentId());
            if (parent == null) {
                throw new BizException("错误的上级文件夹id");
            }
        }
        BiUiAnalyseCategory entity = new BiUiAnalyseCategory();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getParentId() == null) {
            entity.setParentId("0");
        }
        entity.setCreateDate(LocalDateTime.now());
        entity.setInitType(AnalyseConstants.CATEGORY_INIT_TYPE_CUSTOMER);
        entity.setType(AnalyseConstants.CATEGORY_TYPE_CUSTOMER);
        /**
         * 创建的自定义文件夹都在我的分析下面
         */
        entity.setParentId(parent.getId());
        this.save(entity);
        return entity;
    }

    @Override
    public void delAnalyseCategory(String id) {
        BiUiAnalyseCategory category = this.getById(id);
        if (category == null) {
            throw new BizException("错误的id");
        }
        if (AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT.equals(category.getInitType())) {
            throw new BizException("默认文件夹不能删除");
        }
        //有下级的不能删除
        List<BiUiAnalyseCategory> childs = getChildCategory(category.getId(), category.getTenantId());
        if (childs.size() > 0) {
            throw new BizException("清先删除下级文件夹");
        }
        AnalysePageReq req = new AnalysePageReq();
        req.setTenantId(category.getTenantId());
        req.setCategoryId(category.getId());
        List<BiUiAnalysePage> childPages = getChildAnalysePageReq(req);
        if (childPages.size() > 0) {
            throw new BizException("清先删除下级页面");
        }
        this.removeById(id);
    }

    @Override
    public BiUiAnalyseCategory updateAnalyseCategory(UpdateAnalyseCategoryDto dto) {
        BiUiAnalyseCategory entity = this.getById(dto.getId());
        if (AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT.equals(entity.getInitType())) {
            throw new BizException("默认文件夹不能修改");
        }
        checkBiUiAnalyseCategoryByName(dto.getName(), entity.getTenantId(), entity.getId());
        entity.setName(dto.getName());
        entity.setDes(dto.getDes());
        entity.setModifiedDate(LocalDateTime.now());
        this.updateById(entity);
        return entity;
    }

    @Override
    public List<AnalyseCategoryTree> getTree(RetRequest<AnalyseCategoryReq> request) {
        //查询文件夹
        LambdaQueryWrapper<BiUiAnalyseCategory> categoryQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(request.getTenantId())) {
            categoryQueryWrapper.eq(BiUiAnalyseCategory::getTenantId, request.getTenantId());
        }
        if (StringUtils.isNotBlank(request.getData().getInitType())) {
            categoryQueryWrapper.eq(BiUiAnalyseCategory::getInitType, request.getData().getInitType());
        }
        if (StringUtils.isNotBlank(request.getData().getType())) {
            categoryQueryWrapper.eq(BiUiAnalyseCategory::getType, request.getData().getType());
        }
        // 根据数据源名称模糊查询
        if (StringUtils.isNotBlank(request.getData().getName())) {
            categoryQueryWrapper.like(BiUiAnalyseCategory::getName, request.getData().getName());
        }
        List<BiUiAnalyseCategory> categoryList = this.list(categoryQueryWrapper);

        //查询page
        List<String> categoryIds = Lists.newArrayList();
        categoryList.forEach(category -> categoryIds.add(category.getId()));
        LambdaQueryWrapper<BiUiAnalysePage> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pageLambdaQueryWrapper.in(BiUiAnalysePage::getParentId, categoryIds);
        List<BiUiAnalysePage> pageList = pageService.list(pageLambdaQueryWrapper);

        Map<String, List<AnalysePageDto>> pageDtoMap = Maps.newHashMap();
        for (BiUiAnalysePage page : pageList) {
            List<AnalysePageDto> pageDtoList;
            if (pageDtoMap.containsKey(page.getParentId())) {
                pageDtoList = pageDtoMap.get(page.getParentId());
            } else {
                pageDtoList = Lists.newArrayList();
            }
            AnalysePageDto dto = new AnalysePageDto();
            BeanUtils.copyProperties(page, dto);
            pageDtoList.add(dto);
            pageDtoMap.put(page.getParentId(), pageDtoList);
        }

        return buildCategoryTree(categoryList, pageDtoMap, "0");
    }

    @Override
    public void initTenantAnalyse(InitTenantReq data) {
        if (data.getTenantId() == null) {
            throw new BizException("租户id不能为空");
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
                this.save(category);
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
            if (!StringUtils.equals(parentId, "0")) {
                //默认的parent
                BiUiAnalyseDefaultCategory defaultParent = defaultCategoryParentIdMap.get(parentId);
                String parentName = defaultParent.getName();
                //当前的上级
                BiUiAnalyseCategory parent = tenantCategoryMap.get(parentName);
                //更新
                category.setParentId(parent.getId());
                this.updateById(category);
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
        List<BiUiAnalysePage> pages = pageService.list(query);
        return pages;
    }

    @Override
    public void batchDelAnalyseCategories(BatchAnalyseCategoryDelReq data) {
        for (String id : data.getIds()) {
            delAnalyseCategory(id);
        }
    }

    public List<BiUiAnalyseCategory> getChildCategory(String parentId, String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getParentId, parentId);
        List<BiUiAnalyseCategory> pages = list(query);
        return pages;
    }

    private BiUiAnalyseCategory getCustomerTop(String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getInitType, AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT);
        query.eq(BiUiAnalyseCategory::getName, AnalyseConstants.CATEGORY_MY_ANALYSE);
        return getOne(query);
    }

    private void checkBiUiAnalyseCategoryByName(String name, String tenantId, String currentId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getName, name);
        if (StringUtils.isNotBlank(currentId)) {
            query.ne(BiUiAnalyseCategory::getId, currentId);
        }
        List<BiUiAnalyseCategory> categoryList = this.list(query);
        if (CollectionUtils.isNotEmpty(categoryList)) {
            throw new BizException("存在相同名称文件夹");
        }
    }

    private List<BiUiAnalyseCategory> getTenantAnalyseCategories(String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        return this.list(query);
    }

    /**
     * 递归转换成树
     * @param categoryList
     * @param parentId
     * @return
     */
    private List<AnalyseCategoryTree> buildCategoryTree(List<BiUiAnalyseCategory> categoryList, Map<String, List<AnalysePageDto>> pageDtoMap, String parentId) {
        List<AnalyseCategoryTree> treeDataModels = Lists.newArrayList();
        for (BiUiAnalyseCategory category : categoryList) {
            AnalyseCategoryTree categoryTree = new AnalyseCategoryTree();
            BeanUtils.copyProperties(category, categoryTree);
            categoryTree.setPageList(pageDtoMap.get(category.getId()));

            if (parentId.equals(categoryTree.getParentId())) {
                categoryTree.setChildren(buildCategoryTree(categoryList, pageDtoMap, categoryTree.getId()));
                treeDataModels.add(categoryTree);
            }
        }
        return treeDataModels;
    }
}
