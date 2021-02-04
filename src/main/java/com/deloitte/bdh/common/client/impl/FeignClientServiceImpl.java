package com.deloitte.bdh.common.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.client.CacheUser;
import com.deloitte.bdh.common.client.FeignClientService;
import com.deloitte.bdh.common.client.dto.IntactUserInfoVoCache;
import com.deloitte.bdh.common.util.ThreadLocalHolder;


@Service
public class FeignClientServiceImpl implements FeignClientService{

	@Autowired
	private CacheUser CacheUser;
	
	
	@Override
//	@Cacheable(value = RedisKeyProperties.FEIGN_DLA_PREFIX
//    + "getIntactUserInfo", keyGenerator = "feignKeyGenerator")
	public IntactUserInfoVoCache getIntactUserInfo(String userId,String lang) {
		RetRequest<String> retRequest = createRetRequest(userId,lang);
		RetResult<IntactUserInfoVoCache> retResult = CacheUser.getIntactUserInfo(retRequest);
		return retResult.getData();
	}
	
	
	public <T> RetRequest<T> createRetRequest(T data,String lang) {
	    RetRequest<T> retRequest = new RetRequest<>();
	    retRequest.setLang(lang);
	    retRequest.setData(data);
	    return retRequest;
	  }

}
