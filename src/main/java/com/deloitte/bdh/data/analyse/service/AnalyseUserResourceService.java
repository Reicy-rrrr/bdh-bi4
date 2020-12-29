package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserResource;
import com.deloitte.bdh.data.analyse.model.request.GetResourcePermissionDto;
import com.deloitte.bdh.data.analyse.model.request.ResourcePermissionDto;
import com.deloitte.bdh.data.analyse.model.request.SaveResourcePermissionDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;

import java.util.List;


/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
public interface AnalyseUserResourceService extends Service<BiUiAnalyseUserResource> {

    void saveResourcePermission(SaveResourcePermissionDto dto);

    ResourcePermissionDto getResourcePermission(GetResourcePermissionDto dto);

    void setCategoryPermission(List<AnalyseCategoryDto> categoryList);

    void setPagePermission(List<AnalysePageDto> pageDtoList);
}
