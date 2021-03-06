package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.enums.ResourcesTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserResource;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.collation.model.resp.DataSetResp;

import java.util.List;


/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
public interface AnalyseUserResourceService extends Service<BiUiAnalyseUserResource> {

    void saveResourcePermission(SaveResourcePermissionDto dto);

    void delResourcePermission(SaveResourcePermissionDto dto);

    ResourcePermissionDto getResourcePermission(GetResourcePermissionDto dto);

    OrganizationPermissionDto getCategoryOrganization(String categoryId);

    ResourcePermissionDto getPagePermissionByCode(GetPermissionByCodeDto dto);

    List<PermissionItemDto> getPageDataPermissionByCode(GetPermissionByCodeDto dto);

    List<PermissionItemDto> getDataPermission(String pageId);

    void setCategoryPermission(List<AnalyseCategoryDto> categoryList, String superUserFlag);

    void setPagePermission(List<AnalysePageDto> pageDtoList, String superUserFlag);

    void setDataSetPermission(List<DataSetResp> dataSetList, ResourcesTypeEnum resourcesTypeEnum, String superUserFlag);
}
