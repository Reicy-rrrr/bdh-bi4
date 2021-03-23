package com.deloitte.bdh.common.client;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.client.dto.TenantBasicVo;
import com.deloitte.bdh.common.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "bdh-platform", contextId = "bdh-platform", configuration = FeignConfiguration.class)
@Component
public interface PlatformTenantFeign {
	
	@PostMapping("/platform/baseTenant/queryTenantList")
	RetResult<List<TenantBasicVo>> queryTenantList(@RequestBody @Validated RetRequest<String> retRequest);

}
