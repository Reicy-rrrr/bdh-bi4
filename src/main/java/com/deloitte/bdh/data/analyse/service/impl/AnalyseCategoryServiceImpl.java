package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseCategoryMapper;
import com.deloitte.bdh.data.analyse.enums.CategoryPageTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseDefaultCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalyseCategoryService;
import com.deloitte.bdh.data.analyse.service.AnalyseDefaultCategoryService;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
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
public class AnalyseCategoryServiceImpl extends AbstractService<BiUiAnalyseCategoryMapper, BiUiAnalyseCategory> implements AnalyseCategoryService {

    @Resource
    AnalyseDefaultCategoryService analyseDefaultCategoryService;

    @Resource
    AnalysePageService pageService;

    @Override
    public AnalyseCategoryDto createAnalyseCategory(RetRequest<CreateAnalyseCategoryDto> request) {
        checkBiUiAnalyseCategoryByName(request.getData().getName(), request.getTenantId(), null);
        BiUiAnalyseCategory parent = this.getById(request.getData().getParentId());
        if (parent == null) {
            throw new BizException("上级文件夹不存在");
        }
        //只可创建二级文件夹
        if (!StringUtils.equals(parent.getParentId(), AnalyseConstants.PARENT_ID_ZERO)) {
            throw new BizException("当前层级不允许创建文件夹");
        }

        BiUiAnalyseCategory category = new BiUiAnalyseCategory();
        BeanUtils.copyProperties(request.getData(), category);
        category.setCreateDate(LocalDateTime.now());
        category.setInitType(AnalyseConstants.CATEGORY_INIT_TYPE_CUSTOMER);
        category.setType(AnalyseConstants.CATEGORY_TYPE_CUSTOMER);
        //创建的自定义文件夹都在我的分析下面
        BiUiAnalyseCategory customerTop = getCustomerTop(request.getTenantId());
        category.setParentId(customerTop.getId());
        this.save(category);
        AnalyseCategoryDto dto = new AnalyseCategoryDto();
        BeanUtils.copyProperties(category, dto);
        return dto;
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
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalysePage::getTenantId, category.getTenantId());
        query.eq(BiUiAnalysePage::getParentId, category.getId());
        List<BiUiAnalysePage> childPages = pageService.list(query);
        if (childPages.size() > 0) {
            throw new BizException("清先删除下级页面");
        }
        this.removeById(id);
    }

    @Override
    public AnalyseCategoryDto updateAnalyseCategory(RetRequest<UpdateAnalyseCategoryDto> request) {
        BiUiAnalyseCategory entity = this.getById(request.getData().getId());
        if (AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT.equals(entity.getInitType())) {
            throw new BizException("默认文件夹不能修改");
        }
        checkBiUiAnalyseCategoryByName(request.getData().getName(), entity.getTenantId(), entity.getId());
        entity.setName(request.getData().getName());
        entity.setDes(request.getData().getDes());
        entity.setModifiedDate(LocalDateTime.now());
        this.updateById(entity);
        AnalyseCategoryDto dto = new AnalyseCategoryDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<AnalyseCategoryTree> getTree(RetRequest<GetAnalyseCategoryDto> request) {
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
        if (StringUtils.isNotBlank(request.getData().getName())) {
            categoryQueryWrapper.like(BiUiAnalyseCategory::getName, request.getData().getName());
        }
        List<BiUiAnalyseCategory> categoryList = this.list(categoryQueryWrapper);

        //查询page
        List<String> categoryIds = Lists.newArrayList();
        categoryList.forEach(category -> categoryIds.add(category.getId()));
        LambdaQueryWrapper<BiUiAnalysePage> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pageLambdaQueryWrapper.in(BiUiAnalysePage::getParentId, categoryIds);
        pageLambdaQueryWrapper.isNotNull(BiUiAnalysePage::getPublishId);
        List<BiUiAnalysePage> pageList = pageService.list(pageLambdaQueryWrapper);

        //组装category id和page的map结构，方便递归取数据
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

        //递归整理数据
        return buildCategoryTree(categoryList, pageDtoMap, "0");
    }

    @Override
    public void initTenantAnalyse(RetRequest<Void> request) {
        if (request.getTenantId() == null) {
            throw new BizException("租户id不能为空");
        }
        List<BiUiAnalyseCategory> newCategories = Lists.newArrayList();
        /**
         * 初始化默认文件夹
         */
        List<BiUiAnalyseDefaultCategory> defaultCategories = analyseDefaultCategoryService.getAllDefaultCategories();
        List<BiUiAnalyseCategory> tenantAnalyseCategories = getTenantAnalyseCategories(request.getTenantId());
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
                category.setCreateUser(request.getOperator());
                category.setModifiedUser(null);
                category.setModifiedDate(null);
                category.setTenantId(request.getTenantId());
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
    public PageResult<AnalysePageDto> getChildAnalysePageList(PageRequest<GetAnalysePageDto> request) {
        PageHelper.startPage(request.getPage(), request.getSize());
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalysePage::getTenantId, request.getTenantId());
        if (StringUtils.isNotBlank(request.getData().getName())) {
            query.like(BiUiAnalysePage::getName, request.getData().getName());
        }
        if (StringUtils.isNotBlank(request.getData().getCategoryId())) {
            query.eq(BiUiAnalysePage::getParentId, request.getData().getCategoryId());
        }
        query.isNotNull(BiUiAnalysePage::getPublishId);
        query.orderByDesc(BiUiAnalysePage::getCreateDate);
        List<BiUiAnalysePage> pageList = pageService.list(query);
        PageInfo pageInfo = PageInfo.of(pageList);
        List<AnalysePageDto> pageDtoList = Lists.newArrayList();
        pageList.forEach(page -> {
            AnalysePageDto dto = new AnalysePageDto();
            BeanUtils.copyProperties(page, dto);
            pageDtoList.add(dto);
        });
        pageInfo.setList(pageDtoList);
        return new PageResult<>(pageInfo);
    }

    @Override
    public void batchDelAnalyseCategories(RetRequest<BatchDeleteAnalyseDto> request) {
        for (String id : request.getData().getIds()) {
            delAnalyseCategory(id);
        }
    }

    public List<BiUiAnalyseCategory> getChildCategory(String parentId, String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getParentId, parentId);
        List<BiUiAnalyseCategory> pages = list(query);
        return pages;
    }

    private BiUiAnalyseCategory getCustomerTop(String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getType, AnalyseConstants.CATEGORY_TYPE_CUSTOMER);
        query.eq(BiUiAnalyseCategory::getParentId, AnalyseConstants.PARENT_ID_ZERO);
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
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
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

            if (parentId.equals(categoryTree.getParentId())) {
                categoryTree.setChildren(buildCategoryTree(categoryList, pageDtoMap, categoryTree.getId()));
                categoryTree.setChildrenType(CategoryPageTypeEnum.CATEGORY.getName());
                treeDataModels.add(categoryTree);

                //将页面和文件夹放到同一个children，通过children type区分
                List<AnalysePageDto> pageDtoList = pageDtoMap.get(category.getId());
                List<AnalyseCategoryTree> pageList = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(pageDtoList)) {
                    for (AnalysePageDto dto : pageDtoList) {
                        AnalyseCategoryTree tree = new AnalyseCategoryTree();
                        BeanUtils.copyProperties(dto, tree);
                        tree.setChildrenType(CategoryPageTypeEnum.PAGE.getName());
                        pageList.add(tree);
                    }
                    if (CollectionUtils.isNotEmpty(categoryTree.getChildren())) {
                        categoryTree.getChildren().addAll(pageList);
                    }
                }
            }
        }
        return treeDataModels;
    }
}
