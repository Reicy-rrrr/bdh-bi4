package com.deloitte.bdh.common.client;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.client.dto.FndPortalOrgPosWithEmpDto;
import com.deloitte.bdh.common.client.dto.IntactUserInfoVoCache;
import com.deloitte.bdh.common.client.dto.SelectOrgPosWithEmpDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 
 * @author jianpeng
 *
 */
public interface FeignClientService {
	
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	IntactUserInfoVoCache getIntactUserInfo(String userId,String lang);

	List<String> selectTenantUserList();

}
