package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.model.FndPortalUser;
import com.deloitte.bdh.data.dao.platform.FndPortalUserMapper;
import com.deloitte.bdh.data.service.FndPortalUserService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Ashen
 * @since 2020-08-20
 */
@Service
@DS(DSConstant.PLATFORM_DB)
public class FndPortalUserServiceImpl extends AbstractService<FndPortalUserMapper, FndPortalUser> implements FndPortalUserService {

}
