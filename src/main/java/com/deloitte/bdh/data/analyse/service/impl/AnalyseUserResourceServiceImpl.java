package com.deloitte.bdh.data.analyse.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseUserResourceMapper;
import com.deloitte.bdh.data.analyse.enums.PermittedActionEnum;
import com.deloitte.bdh.data.analyse.enums.ResourcesTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserData;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserResource;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
import com.deloitte.bdh.data.analyse.service.AnalyseUserDataService;
import com.deloitte.bdh.data.analyse.service.AnalyseUserResourceService;
import com.deloitte.bdh.data.collation.model.resp.DataSetResp;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalyseUserResourceServiceImpl extends AbstractService<BiUiAnalyseUserResourceMapper, BiUiAnalyseUserResource> implements AnalyseUserResourceService {

    @Resource
    private AnalyseUserDataService userDataService;

    @Resource
    private AnalysePageService pageService;

    @Override
    public void saveResourcePermission(SaveResourcePermissionDto dto) {
        if (null != dto) {
            //删除之前的配置
            LambdaQueryWrapper<BiUiAnalyseUserResource> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BiUiAnalyseUserResource::getResourceType, dto.getResourceType());
            queryWrapper.eq(BiUiAnalyseUserResource::getResourceId, dto.getId());
            this.remove(queryWrapper);

            //交集（查看和编辑）
            List<String> bothPermission = dto.getViewUserList().stream().filter(num -> dto.getEditUserList().contains(num))
                    .collect(Collectors.toList());

            //差集（查看）
            List<String> viewPermission = dto.getViewUserList().stream().filter(num -> !dto.getEditUserList().contains(num))
                    .collect(Collectors.toList());

            //差集（编辑）
            List<String> editPermission = dto.getEditUserList().stream().filter(num -> !dto.getViewUserList().contains(num))
                    .collect(Collectors.toList());
            List<BiUiAnalyseUserResource> resourceList = Lists.newArrayList();
            for (String userId : bothPermission) {
                BiUiAnalyseUserResource resource = new BiUiAnalyseUserResource();
                resource.setResourceId(dto.getId());
                resource.setResourceType(dto.getResourceType());
                resource.setPermittedAction(PermittedActionEnum.VIEW.getCode() + "," + PermittedActionEnum.EDIT.getCode());
                resource.setTenantId(ThreadLocalHolder.getTenantId());
                resource.setUserId(userId);
                resourceList.add(resource);
            }
            for (String userId : viewPermission) {
                BiUiAnalyseUserResource resource = new BiUiAnalyseUserResource();
                resource.setResourceId(dto.getId());
                resource.setResourceType(dto.getResourceType());
                resource.setPermittedAction(PermittedActionEnum.VIEW.getCode());
                resource.setTenantId(ThreadLocalHolder.getTenantId());
                resource.setUserId(userId);
                resourceList.add(resource);
            }
            for (String userId : editPermission) {
                BiUiAnalyseUserResource resource = new BiUiAnalyseUserResource();
                resource.setResourceId(dto.getId());
                resource.setResourceType(dto.getResourceType());
                resource.setPermittedAction(PermittedActionEnum.EDIT.getCode());
                resource.setTenantId(ThreadLocalHolder.getTenantId());
                resource.setUserId(userId);
                resourceList.add(resource);
            }
            //添加当前用户
            BiUiAnalyseUserResource resource = new BiUiAnalyseUserResource();
            resource.setResourceId(dto.getId());
            resource.setResourceType(dto.getResourceType());
            resource.setPermittedAction(PermittedActionEnum.VIEW.getCode() + "," + PermittedActionEnum.EDIT.getCode());
            resource.setTenantId(ThreadLocalHolder.getTenantId());
            resource.setUserId(ThreadLocalHolder.getOperator());
            resourceList.add(resource);
            if (CollectionUtils.isNotEmpty(resourceList)) {
                this.saveBatch(resourceList);
            }
        }
    }

    @Override
    public ResourcePermissionDto getResourcePermission(GetResourcePermissionDto dto) {
        LambdaQueryWrapper<BiUiAnalyseUserResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseUserResource::getResourceId, dto.getId());
        queryWrapper.eq(BiUiAnalyseUserResource::getResourceType, dto.getResourceType());
        queryWrapper.eq(BiUiAnalyseUserResource::getTenantId, ThreadLocalHolder.getTenantId());
        List<BiUiAnalyseUserResource> list = list(queryWrapper);
        List<String> viewUserList = Lists.newArrayList();
        List<String> editUserList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (BiUiAnalyseUserResource userResource : list) {
                if (StringUtils.contains(userResource.getPermittedAction(), PermittedActionEnum.VIEW.getCode())) {
                    viewUserList.add(userResource.getUserId());
                }
                if (StringUtils.contains(userResource.getPermittedAction(), PermittedActionEnum.EDIT.getCode())) {
                    editUserList.add(userResource.getUserId());
                }
            }
        }
        ResourcePermissionDto result = new ResourcePermissionDto();
        result.setViewUserList(viewUserList);
        result.setEditUserList(editUserList);
        return result;
    }

    @Override
    public ResourcePermissionDto getPagePermissionByCode(GetPermissionByCodeDto dto) {
        LambdaQueryWrapper<BiUiAnalysePage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalysePage::getCode, dto.getCode());
        queryWrapper.eq(BiUiAnalysePage::getParentId, dto.getCategoryId());
        BiUiAnalysePage page = pageService.getOne(queryWrapper);
        if (null != page) {
            GetResourcePermissionDto permissionDto = new GetResourcePermissionDto();
            permissionDto.setId(page.getId());
            permissionDto.setResourceType(ResourcesTypeEnum.PAGE.getCode());
            return getResourcePermission(permissionDto);
        }
        return new ResourcePermissionDto();
    }

    @Override
    public List<PermissionItemDto> getPageDataPermissionByCode(GetPermissionByCodeDto dto) {
        LambdaQueryWrapper<BiUiAnalysePage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalysePage::getCode, dto.getCode());
        queryWrapper.eq(BiUiAnalysePage::getParentId, dto.getCategoryId());
        BiUiAnalysePage page = pageService.getOne(queryWrapper);
        if (null != page) {
            return getDataPermission(page.getId());
        }
        return null;
    }

    @Override
    public List<PermissionItemDto> getDataPermission(String pageId) {
        LambdaQueryWrapper<BiUiAnalyseUserData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseUserData::getPageId, pageId);
        List<BiUiAnalyseUserData> userDataList = userDataService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(userDataList)) {
            //已componentId和field分组
            Map<String, List<BiUiAnalyseUserData>> componentMap = userDataList.stream().collect(Collectors.groupingBy(
                    userData -> userData.getComponentId() + ',' + userData.getTableName() + ',' + userData.getTableField()));
            List<PermissionItemDto> result = Lists.newArrayList();
            for (Map.Entry<String, List<BiUiAnalyseUserData>> componentEntry : componentMap.entrySet()) {
                PermissionItemDto permissionItemDto = new PermissionItemDto();
                permissionItemDto.setComponentId(StringUtils.split(componentEntry.getKey(), ",")[0]);
                permissionItemDto.setTableName(StringUtils.split(componentEntry.getKey(), ",")[1]);
                permissionItemDto.setTableField(StringUtils.split(componentEntry.getKey(), ",")[2]);
                //已userId分组
                Map<String, List<BiUiAnalyseUserData>> userIdMap = componentEntry.getValue().stream().collect(Collectors.groupingBy(BiUiAnalyseUserData::getUserId));
                List<PermissionUserDto> permissionUserList = Lists.newArrayList();
                for (Map.Entry<String, List<BiUiAnalyseUserData>> valueEntry : userIdMap.entrySet()) {
                    PermissionUserDto permissionUserDto = new PermissionUserDto();
                    permissionUserDto.setUserId(valueEntry.getKey());
                    List<String> valueList = Lists.newArrayList();
                    valueEntry.getValue().forEach(value -> valueList.add(value.getFieldValue()));
                    permissionUserDto.setFieldValueList(valueList);
                    permissionUserList.add(permissionUserDto);
                }
                permissionItemDto.setPermissionUserList(permissionUserList);
                result.add(permissionItemDto);
            }
            return result;
        }
        return null;
    }

    @Override
    public void setCategoryPermission(List<AnalyseCategoryDto> categoryList) {
        List<String> categoryIds = new ArrayList<>();
        categoryList.forEach(category -> categoryIds.add(category.getId()));

        LambdaQueryWrapper<BiUiAnalyseUserResource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(categoryIds)) {
            resourceLambdaQueryWrapper.in(BiUiAnalyseUserResource::getResourceId, categoryIds);
        }
        resourceLambdaQueryWrapper.eq(BiUiAnalyseUserResource::getResourceType, ResourcesTypeEnum.CATEGORY.getCode());
        resourceLambdaQueryWrapper.eq(BiUiAnalyseUserResource::getUserId, ThreadLocalHolder.getOperator());
        List<BiUiAnalyseUserResource> categoryResources = this.list(resourceLambdaQueryWrapper);
        Map<String, BiUiAnalyseUserResource> categoryIdResourcesMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(categoryResources)) {
            categoryIdResourcesMap = categoryResources.stream().collect(Collectors.toMap(BiUiAnalyseUserResource::getResourceId, b -> b, (v1, v2) -> v1));
        }
        for (AnalyseCategoryDto dto : categoryList) {
            BiUiAnalyseUserResource resource = MapUtils.getObject(categoryIdResourcesMap, dto.getId());
            if (resource != null) {
                if (StringUtils.equals(PermittedActionEnum.VIEW.getCode(), resource.getPermittedAction())) {
                    dto.setPermittedAction(Lists.newArrayList(PermittedActionEnum.VIEW.getCode()));
                } else {
                    dto.setPermittedAction(Lists.newArrayList(PermittedActionEnum.VIEW.getCode(), PermittedActionEnum.EDIT.getCode()));
                }
            } else {
                dto.setPermittedAction(Lists.newArrayList(PermittedActionEnum.VIEW.getCode(), PermittedActionEnum.EDIT.getCode()));
            }
        }
    }

    @Override
    public void setPagePermission(List<AnalysePageDto> pageDtoList) {
        List<String> pageIdList = new ArrayList<>();
        pageDtoList.forEach(page -> pageIdList.add(page.getId()));

        LambdaQueryWrapper<BiUiAnalyseUserResource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(pageIdList)) {
            resourceLambdaQueryWrapper.in(BiUiAnalyseUserResource::getResourceId, pageIdList);
        }
        resourceLambdaQueryWrapper.eq(BiUiAnalyseUserResource::getResourceType, ResourcesTypeEnum.PAGE.getCode());
        resourceLambdaQueryWrapper.eq(BiUiAnalyseUserResource::getUserId, ThreadLocalHolder.getOperator());
        List<BiUiAnalyseUserResource> categoryResources = this.list(resourceLambdaQueryWrapper);
        Map<String, BiUiAnalyseUserResource> pageIdResourcesMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(categoryResources)) {
            pageIdResourcesMap = categoryResources.stream().collect(Collectors.toMap(BiUiAnalyseUserResource::getResourceId, b -> b, (v1, v2) -> v1));
        }
        for (AnalysePageDto dto : pageDtoList) {
            BiUiAnalyseUserResource resource = MapUtils.getObject(pageIdResourcesMap, dto.getId());
            if (resource != null) {
                if (StringUtils.equals(PermittedActionEnum.VIEW.getCode(), resource.getPermittedAction())) {
                    dto.setPermittedAction(Lists.newArrayList(PermittedActionEnum.VIEW.getCode()));
                } else {
                    dto.setPermittedAction(Lists.newArrayList(PermittedActionEnum.VIEW.getCode(), PermittedActionEnum.EDIT.getCode()));
                }
            } else {
                dto.setPermittedAction(Lists.newArrayList(PermittedActionEnum.VIEW.getCode(), PermittedActionEnum.EDIT.getCode()));
            }
        }
    }

    @Override
    public void setDataSetPermission(List<DataSetResp> dataSetList, ResourcesTypeEnum resourcesTypeEnum) {
        List<String> idList = new ArrayList<>();
        dataSetList.forEach(dataSet -> idList.add(dataSet.getId()));

        LambdaQueryWrapper<BiUiAnalyseUserResource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(idList)) {
            resourceLambdaQueryWrapper.in(BiUiAnalyseUserResource::getResourceId, idList);
        }
        resourceLambdaQueryWrapper.eq(BiUiAnalyseUserResource::getResourceType, resourcesTypeEnum.getCode());
        resourceLambdaQueryWrapper.eq(BiUiAnalyseUserResource::getUserId, ThreadLocalHolder.getOperator());
        List<BiUiAnalyseUserResource> resourceList = this.list(resourceLambdaQueryWrapper);
        Map<String, BiUiAnalyseUserResource> dataSetIdResourcesMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(resourceList)) {
            dataSetIdResourcesMap = resourceList.stream().collect(Collectors.toMap(BiUiAnalyseUserResource::getResourceId, b -> b, (v1, v2) -> v1));
        }
        for (DataSetResp dto : dataSetList) {
            BiUiAnalyseUserResource resource = MapUtils.getObject(dataSetIdResourcesMap, dto.getId());
            if (resource != null) {
                if (StringUtils.equals(PermittedActionEnum.VIEW.getCode(), resource.getPermittedAction())) {
                    dto.setPermittedAction(Lists.newArrayList(PermittedActionEnum.VIEW.getCode()));
                } else {
                    dto.setPermittedAction(Lists.newArrayList(PermittedActionEnum.VIEW.getCode(), PermittedActionEnum.EDIT.getCode()));
                }
            } else {
                dto.setPermittedAction(Lists.newArrayList(PermittedActionEnum.VIEW.getCode(), PermittedActionEnum.EDIT.getCode()));
            }
        }
    }
}
