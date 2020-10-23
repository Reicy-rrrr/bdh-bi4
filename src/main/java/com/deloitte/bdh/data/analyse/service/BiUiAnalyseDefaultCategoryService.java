package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseDefaultCategory;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-22
 */
public interface BiUiAnalyseDefaultCategoryService extends Service<BiUiAnalyseDefaultCategory> {
    List<BiUiAnalyseDefaultCategory> getAllDefaultCategories();
}
