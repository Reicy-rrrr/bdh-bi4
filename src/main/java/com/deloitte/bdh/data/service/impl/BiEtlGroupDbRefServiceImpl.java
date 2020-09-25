package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.model.BiEtlGroupDbRef;
import com.deloitte.bdh.data.dao.bi.BiEtlGroupDbRefMapper;
import com.deloitte.bdh.data.service.BiEtlGroupDbRefService;
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
public class BiEtlGroupDbRefServiceImpl extends AbstractService<BiEtlGroupDbRefMapper, BiEtlGroupDbRef> implements BiEtlGroupDbRefService {

}
