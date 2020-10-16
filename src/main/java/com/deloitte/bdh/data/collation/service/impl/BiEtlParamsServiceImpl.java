package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.model.BiEtlParams;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlParamsMapper;
import com.deloitte.bdh.data.collation.service.BiEtlParamsService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-09-25
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlParamsServiceImpl extends AbstractService<BiEtlParamsMapper, BiEtlParams> implements BiEtlParamsService {

}
