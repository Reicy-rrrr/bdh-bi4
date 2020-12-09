package com.deloitte.bdh.data.analyse.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseUserDataMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserData;
import com.deloitte.bdh.data.analyse.model.request.PermissionItemDto;
import com.deloitte.bdh.data.analyse.service.AnalyseUserDataService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalyseUserDataServiceImpl extends AbstractService<BiUiAnalyseUserDataMapper, BiUiAnalyseUserData> implements AnalyseUserDataService {

    @Override
    public void saveDataPermission(List<PermissionItemDto> itemDtoList, String pageId) {
        if (CollectionUtils.isNotEmpty(itemDtoList)) {
            //删除之前的配置
            LambdaQueryWrapper<BiUiAnalyseUserData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BiUiAnalyseUserData::getPageId, pageId);
            this.remove(queryWrapper);

            List<BiUiAnalyseUserData> userDataList = Lists.newArrayList();
            for (PermissionItemDto itemDto : itemDtoList) {
                for (String userId : itemDto.getUserList()) {
                    BiUiAnalyseUserData userData = new BiUiAnalyseUserData();
                    BeanUtils.copyProperties(itemDto, userData);
                    userData.setPageId(pageId);
                    userData.setTenantId(ThreadLocalHolder.getTenantId());
                    userData.setUserId(userId);
                    userDataList.add(userData);
                }
            }
            if (CollectionUtils.isNotEmpty(userDataList)) {
                this.saveBatch(userDataList);
            }
        }
    }
}
