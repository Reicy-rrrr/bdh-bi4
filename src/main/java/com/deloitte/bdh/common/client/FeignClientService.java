package com.deloitte.bdh.common.client;

import com.deloitte.bdh.common.client.dto.IntactUserInfoVoCache;

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

}
