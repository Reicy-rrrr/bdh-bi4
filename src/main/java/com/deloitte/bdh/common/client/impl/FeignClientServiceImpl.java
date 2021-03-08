package com.deloitte.bdh.common.client.impl;

import com.deloitte.bdh.common.client.ManageTenantFeign;
import com.deloitte.bdh.common.client.dto.FndPortalOrgPosWithEmpDto;
import com.deloitte.bdh.common.client.dto.SelectOrgPosWithEmpDto;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.client.CacheUser;
import com.deloitte.bdh.common.client.FeignClientService;
import com.deloitte.bdh.common.client.dto.IntactUserInfoVoCache;
import com.deloitte.bdh.common.util.ThreadLocalHolder;

import javax.annotation.Resource;
import java.util.List;


@Service
public class FeignClientServiceImpl implements FeignClientService{

	@Autowired
	private CacheUser CacheUser;

	@Resource
	private ManageTenantFeign manageTenantFeign;
	
	
	@Override
//	@Cacheable(value = RedisKeyProperties.FEIGN_DLA_PREFIX
//    + "getIntactUserInfo", keyGenerator = "feignKeyGenerator")
	public IntactUserInfoVoCache getIntactUserInfo(String userId,String lang) {
		RetRequest<String> retRequest = createRetRequest(userId,lang);
		RetResult<IntactUserInfoVoCache> retResult = CacheUser.getIntactUserInfo(retRequest);
		return retResult.getData();
	}

	@Override
	public List<String> selectTenantUserList() {
		List<String> userList = Lists.newArrayList();
		RetRequest<SelectOrgPosWithEmpDto> retRequest = new RetRequest<>();
		SelectOrgPosWithEmpDto dto = new SelectOrgPosWithEmpDto();
		dto.setType("USER");
		retRequest.setLang(ThreadLocalHolder.getLang());
		retRequest.setData(dto);
		RetResult<List<FndPortalOrgPosWithEmpDto>> result = manageTenantFeign.selectOrgPosWithEmpTree(retRequest);
		List<FndPortalOrgPosWithEmpDto> resultList = result.getData();
		if (CollectionUtils.isNotEmpty(resultList)) {
			resultList.forEach(resultDto -> userList.add(resultDto.getId()));
		}
		return userList;
	}

	public <T> RetRequest<T> createRetRequest(T data, String lang) {
	    RetRequest<T> retRequest = new RetRequest<>();
	    retRequest.setLang(lang);
	    retRequest.setData(data);
	    return retRequest;
	  }

}
