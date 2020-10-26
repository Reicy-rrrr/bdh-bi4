package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.model.BiEtlMappingField;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlMappingFieldMapper;
import com.deloitte.bdh.data.collation.service.BiEtlMappingFieldService;
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
public class BiEtlMappingFieldServiceImpl extends AbstractService<BiEtlMappingFieldMapper, BiEtlMappingField> implements BiEtlMappingFieldService {

}
