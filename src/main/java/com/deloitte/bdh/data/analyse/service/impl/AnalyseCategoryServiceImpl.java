package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseCategoryMapper;
import com.deloitte.bdh.data.analyse.enums.CategoryTreeChildrenTypeEnum;
import com.deloitte.bdh.data.analyse.enums.CategoryTypeEnum;
import com.deloitte.bdh.data.analyse.enums.YnTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseDefaultCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.BatchDeleteAnalyseDto;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.request.GetAnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalyseCategoryService;
import com.deloitte.bdh.data.analyse.service.AnalyseDefaultCategoryService;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        category.setType(CategoryTypeEnum.CUSTOMER.getCode());
        //创建的自定义文件夹都在我的分析下面
        BiUiAnalyseCategory customerTop = getCustomerTop(request.getTenantId());
        category.setParentId(customerTop.getId());
        this.save(category);
        AnalyseCategoryDto dto = new AnalyseCategoryDto();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }

    @Override
    public AnalyseCategoryDto updateAnalyseCategory(RetRequest<UpdateAnalyseCategoryDto> request) {
        BiUiAnalyseCategory entity = this.getById(request.getData().getId());
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
        if (StringUtils.isBlank(request.getTenantId())) {
            throw new BizException("租户id不能为空");
        }
        List<BiUiAnalyseCategory> initCategoryList = Lists.newArrayList();
        //初始化默认文件夹
        List<BiUiAnalyseDefaultCategory> defaultCategoryList = analyseDefaultCategoryService.list();
        //查询已存在文件夹
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalyseCategory::getTenantId, request.getTenantId());
        List<BiUiAnalyseCategory> existAnalyseCategoryList = this.list(query);

        Map<String, BiUiAnalyseCategory> existCategoryNameMap = Maps.newHashMap();
        Map<String, BiUiAnalyseDefaultCategory> defaultCategoryIdMap = Maps.newHashMap();
        for (BiUiAnalyseCategory category : existAnalyseCategoryList) {
            existCategoryNameMap.put(category.getName(), category);
        }
        for (BiUiAnalyseDefaultCategory defaultCategory : defaultCategoryList) {
            defaultCategoryIdMap.put(defaultCategory.getId(), defaultCategory);
        }
        //初始化数据
        for (BiUiAnalyseDefaultCategory defaultCategory : defaultCategoryList) {
            String name = defaultCategory.getName();
            BiUiAnalyseCategory category = existCategoryNameMap.get(name);
            if (category == null) {
                category = new BiUiAnalyseCategory();
                BeanUtils.copyProperties(defaultCategory, category);
                category.setCreateDate(LocalDateTime.now());
                category.setCreateUser(request.getOperator());
                category.setModifiedUser(null);
                category.setModifiedDate(null);
                category.setTenantId(request.getTenantId());
                this.save(category);
                initCategoryList.add(category);
            }
        }
        //处理文件夹的上下级关系
        for (BiUiAnalyseCategory category : initCategoryList) {
            if (!StringUtils.equals(category.getParentId(), AnalyseConstants.PARENT_ID_ZERO)) {
                //默认的parent
                BiUiAnalyseDefaultCategory defaultParent = defaultCategoryIdMap.get(category.getParentId());
                //当前的上级
                BiUiAnalyseCategory parent = existCategoryNameMap.get(defaultParent.getName());
                //更新
                category.setParentId(parent.getId());
                this.updateById(category);
            }
        }
        //todo 默认文件夹的权限问题
        //todo 初始化默认报表
    }

    @Override
    public void delAnalyseCategory(RetRequest<String> request) {

        List<String> ids = Arrays.asList(request.getData().split(" "));
        String tenatId = request.getTenantId();
        delAnalyseCategories(ids, tenatId);
    }

    @Override
    @Transactional
    public void batchDelAnalyseCategories(RetRequest<BatchDeleteAnalyseDto> request) {

        List<String> ids = request.getData().getIds();
        String tenatId = request.getTenantId();
        delAnalyseCategories(ids, tenatId);
    }

    //删除文件夹和批量删除文件夹公用方法
    private void delAnalyseCategories(List<String> ids, String tenantId) {

        if (null == tenantId || tenantId.isEmpty()) {
            throw new BizException("请选择租户");
        }
        if (CollectionUtils.isEmpty(ids)) {
            throw new BizException("请选择要删除的文件夹");
        }
        List<BiUiAnalyseCategory> categoryList = this.listByIds(ids);
        if (CollectionUtils.isNotEmpty(categoryList)) {
            List<String> parentIdList = Lists.newArrayList();
            categoryList.forEach(category -> {
                if (StringUtils.equals(category.getParentId(), AnalyseConstants.PARENT_ID_ZERO)) {
                    throw new BizException("顶级文件夹不能删除");
                }
                parentIdList.add(category.getId());
            });
            //删除前检查是否有下级文件
            List<BiUiAnalyseCategory> childList = getChildCategory(parentIdList, tenantId);
            if (CollectionUtils.isNotEmpty(childList)) {
                throw new BizException("请先删除下级文件夹");
            }
            List<BiUiAnalysePage> childPageList = getChildPage(parentIdList, tenantId);
            if (CollectionUtils.isNotEmpty(childPageList)) {
                List<String> existEdit = childPageList.stream()
                        .filter(BiUiAnalysePage -> null != BiUiAnalysePage.getIsEdit() &&
                                BiUiAnalysePage.getIsEdit().equals(YnTypeEnum.YES.getCode()))
                        .map(BiUiAnalysePage::getName).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(existEdit)) {
                    String message = StringUtils.join(existEdit);
                    throw new BizException("请先删除该文件夹中的草稿页面，草稿页面名称如下：" + message);
                } else {
                    throw new BizException("请先删除下级页面");
                }
            }
            //删除
            this.removeByIds(parentIdList);
        }
    }

    private List<BiUiAnalysePage> getChildPage(List<String> parentIdList, String tenantId) {
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalysePage::getTenantId, tenantId);
        query.in(BiUiAnalysePage::getParentId, parentIdList);
        return pageService.list(query);
    }

    private List<BiUiAnalyseCategory> getChildCategory(List<String> parentIdList, String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.in(BiUiAnalyseCategory::getParentId, parentIdList);
        return list(query);
    }

    private BiUiAnalyseCategory getCustomerTop(String tenantId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getType, CategoryTypeEnum.CUSTOMER.getCode());
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

    /**
     * 递归转换成树
     *
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
                categoryTree.setChildrenType(CategoryTreeChildrenTypeEnum.CATEGORY.getCode());
                treeDataModels.add(categoryTree);

                //将页面和文件夹放到同一个children，通过children type区分
                List<AnalysePageDto> pageDtoList = pageDtoMap.get(category.getId());
                List<AnalyseCategoryTree> pageList = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(pageDtoList)) {
                    for (AnalysePageDto dto : pageDtoList) {
                        AnalyseCategoryTree tree = new AnalyseCategoryTree();
                        BeanUtils.copyProperties(dto, tree);
                        tree.setChildrenType(CategoryTreeChildrenTypeEnum.PAGE.getCode());
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
