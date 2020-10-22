package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseDefaultCategory;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseDefaultCategoryMapper;
import com.deloitte.bdh.data.analyse.service.BiUiAnalyseDefaultCategoryService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-22
 */
@Service
@DS(DSConstant.BI_DB)
public class BiUiAnalyseDefaultCategoryServiceImpl extends AbstractService<BiUiAnalyseDefaultCategoryMapper, BiUiAnalyseDefaultCategory> implements BiUiAnalyseDefaultCategoryService {

    @Override
    public List<BiUiAnalyseDefaultCategory> getAllDefaultCategories() {
        return list();
    }
}
