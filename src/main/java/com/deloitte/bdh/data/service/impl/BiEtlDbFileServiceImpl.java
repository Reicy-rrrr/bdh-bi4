package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.model.BiEtlDbFile;
import com.deloitte.bdh.data.dao.bi.BiEtlDbFileMapper;
import com.deloitte.bdh.data.service.BiEtlDbFileService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chenghzhang
 * @since 2020-10-12
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlDbFileServiceImpl extends AbstractService<BiEtlDbFileMapper, BiEtlDbFile> implements BiEtlDbFileService {

}
