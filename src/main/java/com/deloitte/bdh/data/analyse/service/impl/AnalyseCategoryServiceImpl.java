package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseCategoryMapper;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageMapper;
import com.deloitte.bdh.data.analyse.enums.*;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserResource;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalyseCategoryService;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
import com.deloitte.bdh.data.analyse.service.AnalyseUserResourceService;
import com.deloitte.bdh.data.collation.controller.BiTenantConfigController;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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
    private AnalysePageService pageService;

    @Resource
    private AnalyseUserResourceService userResourceService;

    @Resource
    private BiUiAnalyseCategoryMapper categoryMapper;

    @Resource
    private BiUiAnalysePageMapper analysePageMapper;

    @Override
    public AnalyseCategoryDto createAnalyseCategory(RetRequest<CreateAnalyseCategoryDto> request) {
        return createAnalyseCategory(request.getData());
    }

    @Override
    public AnalyseCategoryDto updateAnalyseCategory(RetRequest<UpdateAnalyseCategoryDto> request) {
        BiUiAnalyseCategory entity = this.getById(request.getData().getId());
        checkBiUiAnalyseCategoryByName(request.getData().getName(), entity.getTenantId(), entity.getId());
        entity.setName(request.getData().getName());
        entity.setDes(request.getData().getDes());
        this.updateById(entity);
        AnalyseCategoryDto dto = new AnalyseCategoryDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<AnalyseCategoryTree> getTree(RetRequest<GetAnalyseCategoryDto> request) {
        List<AnalyseCategoryDto> categoryDtoList = Lists.newArrayList();
        List<String> categoryIds = Lists.newArrayList();
        //查询文件夹
        if (StringUtils.equals(YesOrNoEnum.YES.getKey(), request.getData().getSuperUserFlag())) {
            LambdaQueryWrapper<BiUiAnalyseCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BiUiAnalyseCategory::getType, request.getData().getType());
            if (StringUtils.isNotBlank(request.getData().getName())) {
                queryWrapper.like(BiUiAnalyseCategory::getName, request.getData().getName());
            }
            List<BiUiAnalyseCategory> categoryList = list(queryWrapper);
            for (BiUiAnalyseCategory category : categoryList) {
                AnalyseCategoryDto dto = new AnalyseCategoryDto();
                BeanUtils.copyProperties(category, dto);
                categoryDtoList.add(dto);
            }
        } else {
            SelectCategoryDto selectCategoryDto = new SelectCategoryDto();
            selectCategoryDto.setUserId(ThreadLocalHolder.getOperator());
            selectCategoryDto.setResourceType(ResourcesTypeEnum.CATEGORY.getCode());
            selectCategoryDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
            selectCategoryDto.setTenantId(ThreadLocalHolder.getTenantId());
            selectCategoryDto.setName(request.getData().getName());
            selectCategoryDto.setType(request.getData().getType());
            if (StringUtils.equals(request.getData().getType(), CategoryTypeEnum.COMPONENT.getCode())) {
                List<String> createUserList = Lists.newArrayList(BiTenantConfigController.OPERATOR, ThreadLocalHolder.getOperator());
                selectCategoryDto.setCreateUserList(createUserList);
            }
            categoryDtoList = categoryMapper.selectCategory(selectCategoryDto);
            categoryDtoList.forEach(category -> categoryIds.add(category.getId()));
        }
        //设置文件夹权限
        userResourceService.setCategoryPermission(categoryDtoList, request.getData().getSuperUserFlag());

        //查询page
        SelectPublishedPageDto selectPublishedPageDto = new SelectPublishedPageDto();
        selectPublishedPageDto.setUserId(ThreadLocalHolder.getOperator());
        selectPublishedPageDto.setResourceType(ResourcesTypeEnum.PAGE.getCode());
        selectPublishedPageDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
        selectPublishedPageDto.setTenantId(ThreadLocalHolder.getTenantId());
        selectPublishedPageDto.setName(request.getData().getName());
        selectPublishedPageDto.setResourcesIds(categoryIds);
        List<AnalysePageDto> pageList = analysePageMapper.selectPublishedPage(selectPublishedPageDto);

        //设置报表权限
        userResourceService.setPagePermission(pageList, request.getData().getSuperUserFlag());

        //组装category id和page的map结构，方便递归取数据
        Map<String, List<AnalysePageDto>> pageDtoMapTmp = new LinkedHashMap<>();
        for (AnalysePageDto page : pageList) {
            List<AnalysePageDto> pageDtoList;
            if (pageDtoMapTmp.containsKey(page.getParentId())) {
                pageDtoList = pageDtoMapTmp.get(page.getParentId());
            } else {
                pageDtoList = Lists.newArrayList();
            }
            AnalysePageDto pageDto = new AnalysePageDto();
            BeanUtils.copyProperties(page, pageDto);
            pageDtoList.add(pageDto);
            pageDtoMapTmp.put(page.getParentId(), pageDtoList);
        }

        //整理数据
        List<AnalyseCategoryTree> treeDataModels = Lists.newArrayList();
        for (AnalyseCategoryDto categoryDto : categoryDtoList) {
            AnalyseCategoryTree categoryTree = new AnalyseCategoryTree();
            BeanUtils.copyProperties(categoryDto, categoryTree);
            categoryTree.setChildrenType(TreeChildrenTypeEnum.CATEGORY.getCode());
            //将页面和文件夹放到同一个children，通过children type区分
            List<AnalysePageDto> pageDtoList = pageDtoMapTmp.get(categoryDto.getId());
            List<AnalyseCategoryTree> childrenPageList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(pageDtoList)) {
                for (AnalysePageDto dto : pageDtoList) {
                    AnalyseCategoryTree tree = new AnalyseCategoryTree();
                    BeanUtils.copyProperties(dto, tree);
                    tree.setChildrenType(TreeChildrenTypeEnum.PAGE.getCode());
                    childrenPageList.add(tree);
                }
                categoryTree.setChildren(childrenPageList);
            }
            treeDataModels.add(categoryTree);
        }

//        //获取所有的文件夹
//        List<BiUiAnalyseCategory> biCate = list();
//        List<AnalyseCategoryDto> biCateDtoList = Lists.newArrayList();
//        for (BiUiAnalyseCategory category : biCate) {
//            AnalyseCategoryDto dto = new AnalyseCategoryDto();
//            BeanUtils.copyProperties(category, dto);
//            biCateDtoList.add(dto);
//        }
//        Map<String, AnalyseCategoryDto> allCategoryMap = biCateDtoList.stream().collect(Collectors.toMap(AnalyseCategoryDto::getId, b -> b, (v1, v2) -> v1));
//        //递归的时候判断当前数据有无遍历过
//        List<String> record = new ArrayList<>();
//        Map<String, List<AnalysePageDto>> pageDtoMap = pageDtoMapFun(categoryList, pageDtoMapTmp, record, allCategoryMap);
//
//        //递归整理数据
//        List<AnalyseCategoryTree> trees = buildCategoryTree(categoryList, pageDtoMap, "0");
        return treeDataModels;
    }

    private Map<String, List<AnalysePageDto>> pageDtoMapFun(
            List<AnalyseCategoryDto> categoryList, Map<String, List<AnalysePageDto>> pageDtoMapTmp,
            List<String> record, Map<String, AnalyseCategoryDto> allCategoryMap) {
        //记录当前文件夹是否已经被记录防重复，也防止其他文件的父级被记录 本文件夹不再被记录
        List<String> cateMap = categoryList.stream().map(AnalyseCategoryDto::getId).collect(Collectors.toList());
        //遍历所有page
        for (String key : pageDtoMapTmp.keySet()) {
            //如果当前page没被遍历
            if (!record.contains(key)) {
                //添加记录，作为已遍历
                record.add(key);
                //获取当前id的文件夹
                AnalyseCategoryDto biUiAnalyseCategory = allCategoryMap.get(key);
                //得到文件夹父id
                String parentId = biUiAnalyseCategory.getParentId();
                //判断父文件夹是否在当前文件夹中并且是否是0级文件夹
                if (!pageDtoMapTmp.containsKey(parentId) && !parentId.equals("0")) {
                    //如果不存在就放进当前文件夹
                    pageDtoMapTmp.put(parentId, new ArrayList<>());
                }
                //记录当前文件夹是否已经被记录防重复，也防止其他文件的父级被记录 本文件夹不再被记录
                if (!cateMap.contains(key)) {
                    cateMap.add(key);
                    categoryList.add(biUiAnalyseCategory);
                }
                if (!cateMap.contains(parentId) && !parentId.equals("0")) {
                    cateMap.add(parentId);
                    categoryList.add(allCategoryMap.get(parentId));
                }
                //递归处理
                pageDtoMapFun(categoryList, pageDtoMapTmp, record, allCategoryMap);
                //递归结束，说明已经遍历所有的文件夹，返回即可
                return pageDtoMapTmp;
            }
        }
        return pageDtoMapTmp;
    }


    /**
     * 递归转换成树
     *
     * @param categoryList
     * @param parentId
     * @return
     */
    private List<AnalyseCategoryTree> buildCategoryTree(List<AnalyseCategoryDto> categoryList, Map<String, List<AnalysePageDto>> pageDtoMap, String parentId) {
        List<AnalyseCategoryTree> treeDataModels = Lists.newArrayList();
        for (AnalyseCategoryDto category : categoryList) {
            AnalyseCategoryTree categoryTree = new AnalyseCategoryTree();
            BeanUtils.copyProperties(category, categoryTree);

            if (parentId.equals(categoryTree.getParentId())) {
                categoryTree.setChildren(buildCategoryTree(categoryList, pageDtoMap, categoryTree.getId()));
                categoryTree.setChildrenType(TreeChildrenTypeEnum.CATEGORY.getCode());
                treeDataModels.add(categoryTree);

                //将页面和文件夹放到同一个children，通过children type区分
                List<AnalysePageDto> pageDtoList = pageDtoMap.get(category.getId());
                List<AnalyseCategoryTree> pageList = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(pageDtoList)) {
                    for (AnalysePageDto dto : pageDtoList) {
                        AnalyseCategoryTree tree = new AnalyseCategoryTree();
                        BeanUtils.copyProperties(dto, tree);
                        tree.setChildrenType(TreeChildrenTypeEnum.PAGE.getCode());
                        pageList.add(tree);
                    }
                    if (CollectionUtils.isEmpty(categoryTree.getChildren())) {
                        List<AnalyseCategoryTree> children = Lists.newArrayList();
                        categoryTree.setChildren(children);
                    }
                    categoryTree.getChildren().addAll(pageList);
                }
            }
        }
        return treeDataModels;
    }


    @Override
    public void delAnalyseCategory(RetRequest<String> request) {
        List<String> ids = Arrays.asList(request.getData().split(" "));
        delAnalyseCategories(ids, ThreadLocalHolder.getTenantId());
    }

    @Override
    @Transactional
    public void batchDelAnalyseCategories(RetRequest<BatchDeleteAnalyseDto> request) {

        List<String> ids = request.getData().getIds();
        delAnalyseCategories(ids, ThreadLocalHolder.getTenantId());
    }

    @Override
    public AnalyseCategoryDto createAnalyseCategory(CreateAnalyseCategoryDto categoryDto) {
        checkBiUiAnalyseCategoryByName(categoryDto.getName(), ThreadLocalHolder.getTenantId(), null);
//        BiUiAnalyseCategory parent = this.getById(request.getData().getParentId());
//        if (parent == null) {
//            throw new BizException("上级文件夹不存在");
//        }
//        //只可创建二级文件夹
//        if (!StringUtils.equals(parent.getParentId(), AnalyseConstants.PARENT_ID_ZERO)) {
//            throw new BizException("当前层级不允许创建文件夹");
//        }

        BiUiAnalyseCategory category = new BiUiAnalyseCategory();
        BeanUtils.copyProperties(categoryDto, category);
//        创建的自定义文件夹都在我的分析下面
//        BiUiAnalyseCategory customerTop = getCustomerTop(ThreadLocalHolder.getTenantId());
        category.setTenantId(ThreadLocalHolder.getTenantId());
        this.save(category);
        AnalyseCategoryDto dto = new AnalyseCategoryDto();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }

    //删除文件夹和批量删除文件夹公用方法
    private void delAnalyseCategories(List<String> ids, String tenantId) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BizException(ResourceMessageEnum.NO_CATEGORY_SELECT.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.NO_CATEGORY_SELECT.getMessage(), ThreadLocalHolder.getLang()));
        }
        List<BiUiAnalyseCategory> categoryList = this.listByIds(ids);
        if (CollectionUtils.isNotEmpty(categoryList)) {
            List<String> parentIdList = Lists.newArrayList();
            categoryList.forEach(category -> {
//                if (StringUtils.equals(category.getParentId(), AnalyseConstants.PARENT_ID_ZERO)) {
//                    throw new BizException("顶级文件夹不能删除");
//                }
                parentIdList.add(category.getId());
            });
            //删除前检查是否有下级文件
            List<BiUiAnalyseCategory> childList = getChildCategory(parentIdList, tenantId);
            if (CollectionUtils.isNotEmpty(childList)) {
                throw new BizException(ResourceMessageEnum.DELETE_SUB_FIRST.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.DELETE_SUB_FIRST.getMessage(), ThreadLocalHolder.getLang()));
            }
            List<BiUiAnalysePage> childPageList = getChildPage(parentIdList, tenantId);
            if (CollectionUtils.isNotEmpty(childPageList)) {
//                List<String> existEdit = childPageList.stream()
//                        .filter(BiUiAnalysePage -> null != BiUiAnalysePage.getIsEdit() &&
//                                BiUiAnalysePage.getIsEdit().equals(YnTypeEnum.YES.getCode()))
//                        .map(BiUiAnalysePage::getName).collect(Collectors.toList());
//                if (CollectionUtils.isNotEmpty(existEdit)) {
//                    String message = StringUtils.join(existEdit);
//                    throw new BizException("请删除在草稿箱中的" + message + "报表");
//                } else {
//                    throw new BizException("请先删除下级页面");
//                }
                List<String> pageNameList = childPageList.stream().map(BiUiAnalysePage::getName).collect(Collectors.toList());
                String message = StringUtils.join(pageNameList);
                throw new BizException(ResourceMessageEnum.PAGE_EXIST_IN_CATEGORY.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.PAGE_EXIST_IN_CATEGORY.getMessage(), ThreadLocalHolder.getLang()), message);
            }
            //删除
            this.removeByIds(parentIdList);

            //删除可见编辑权限
            LambdaQueryWrapper<BiUiAnalyseUserResource> resourceQueryWrapper = new LambdaQueryWrapper<>();
            resourceQueryWrapper.in(BiUiAnalyseUserResource::getResourceId, parentIdList);
            userResourceService.remove(resourceQueryWrapper);
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

//    private BiUiAnalyseCategory getCustomerTop(String tenantId) {
//        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
//        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
//        query.eq(BiUiAnalyseCategory::getType, CategoryTypeEnum.CUSTOMER.getCode());
//        query.eq(BiUiAnalyseCategory::getParentId, AnalyseConstants.PARENT_ID_ZERO);
//        return getOne(query);
//    }

    private void checkBiUiAnalyseCategoryByName(String name, String tenantId, String currentId) {
        LambdaQueryWrapper<BiUiAnalyseCategory> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalyseCategory::getTenantId, tenantId);
        query.eq(BiUiAnalyseCategory::getName, name);
        if (StringUtils.isNotBlank(currentId)) {
            query.ne(BiUiAnalyseCategory::getId, currentId);
        }
        List<BiUiAnalyseCategory> categoryList = this.list(query);
        if (CollectionUtils.isNotEmpty(categoryList)) {
            String a = localeMessageService.getMessage(ResourceMessageEnum.EXIST_SAME_CATEGORY.getMessage(), ThreadLocalHolder.getLang());
            throw new BizException(ResourceMessageEnum.EXIST_SAME_CATEGORY.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXIST_SAME_CATEGORY.getMessage(), ThreadLocalHolder.getLang()));
        }
    }
}
