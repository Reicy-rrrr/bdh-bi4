package com.deloitte.bdh.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.client.dto.IntactUserInfoVoCache;
import com.deloitte.bdh.common.config.FeignConfiguration;

@FeignClient(value = "bdh-platform", contextId = "cache-user-client",configuration = FeignConfiguration.class)
@Component
public interface CacheUser {
	
	@PostMapping("/platform/commonDatas/intactUserInfo")
	public RetResult<IntactUserInfoVoCache> getIntactUserInfo(
			@RequestBody RetRequest<String> retRequest);

}
