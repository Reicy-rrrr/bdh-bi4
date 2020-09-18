package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.model.TUser;
import com.deloitte.bdh.data.dao.bi.TUserMapper;
import com.deloitte.bdh.data.service.TUserService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ashen
 * @since 2020-09-17
 */
@Service
@DS(DSConstant.BI_DB)
public class TUserServiceImpl extends AbstractService<TUserMapper, TUser> implements TUserService {

}
