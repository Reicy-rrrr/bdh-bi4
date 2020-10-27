package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.dao.bi.BiComponentMapper;
import com.deloitte.bdh.data.collation.model.resp.BiComponentTree;
import com.deloitte.bdh.data.collation.service.BiComponentService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BiComponentServiceImpl extends AbstractService<BiComponentMapper, BiComponent> implements BiComponentService {

    @Autowired
    private BiComponentMapper biComponentMapper;

    @Override
    public BiComponentTree selectTree(String modelCode, String componentCode) {
        return biComponentMapper.selectTree(modelCode, componentCode);
    }
}
