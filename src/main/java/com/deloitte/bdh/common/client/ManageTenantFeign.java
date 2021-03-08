package com.deloitte.bdh.common.client;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.client.dto.FndPortalOrgPosWithEmpDto;
import com.deloitte.bdh.common.client.dto.IntactUserInfoVoCache;
import com.deloitte.bdh.common.client.dto.SelectOrgPosWithEmpDto;
import com.deloitte.bdh.common.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "bdh-manage-tenant", contextId = "manager-tenant-client", configuration = FeignConfiguration.class)
@Component
public interface ManageTenantFeign {
	
	@PostMapping("/manage/fndPortalOrganization/selectOrgPosWithEmpTree")
	RetResult<List<FndPortalOrgPosWithEmpDto>> selectOrgPosWithEmpTree(
			@RequestBody @Validated RetRequest<SelectOrgPosWithEmpDto> retRequest);

}
