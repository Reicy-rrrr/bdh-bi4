package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserData;
import com.deloitte.bdh.data.analyse.model.request.PermissionItemDto;

import java.util.List;

/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
public interface AnalyseUserDataService extends Service<BiUiAnalyseUserData> {

    void saveDataPermission(List<PermissionItemDto> itemDtoList, String pageId);

}
