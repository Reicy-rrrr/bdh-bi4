package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.model.BiConnections;
import com.deloitte.bdh.data.dao.bi.BiConnectionsMapper;
import com.deloitte.bdh.data.service.BiConnectionsService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-10-03
 */
@Service
@DS(DSConstant.BI_DB)
public class BiConnectionsServiceImpl extends AbstractService<BiConnectionsMapper, BiConnections> implements BiConnectionsService {

}
