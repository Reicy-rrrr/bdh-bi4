package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.model.BiComponentConnection;
import com.deloitte.bdh.data.collation.dao.bi.BiComponentConnectionMapper;
import com.deloitte.bdh.data.collation.service.BiComponentConnectionService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
@Service
@DS(DSConstant.BI_DB)
public class BiComponentConnectionServiceImpl extends AbstractService<BiComponentConnectionMapper, BiComponentConnection> implements BiComponentConnectionService {

}
