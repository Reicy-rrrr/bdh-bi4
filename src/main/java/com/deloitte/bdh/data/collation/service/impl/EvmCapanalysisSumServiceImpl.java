package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.model.EvmCapanalysisSum;
import com.deloitte.bdh.data.collation.dao.bi.EvmCapanalysisSumMapper;
import com.deloitte.bdh.data.collation.service.EvmCapanalysisSumService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lw
 * @since 2021-02-02
 */
@Service
@DS(DSConstant.BI_DB)
public class EvmCapanalysisSumServiceImpl extends AbstractService<EvmCapanalysisSumMapper, EvmCapanalysisSum> implements EvmCapanalysisSumService {

}
