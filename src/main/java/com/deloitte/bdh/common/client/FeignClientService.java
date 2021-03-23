package com.deloitte.bdh.common.client;

import com.deloitte.bdh.common.client.dto.IntactUserInfoVoCache;
import com.deloitte.bdh.common.client.dto.TenantBasicVo;

import java.util.List;

/**
 * @author jianpeng
 */
public interface FeignClientService {


    IntactUserInfoVoCache getIntactUserInfo(String userId, String lang);

    List<String> selectTenantUserList();

    List<TenantBasicVo> queryTenantList(String name);

}
