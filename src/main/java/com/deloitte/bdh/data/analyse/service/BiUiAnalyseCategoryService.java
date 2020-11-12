package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface BiUiAnalyseCategoryService extends Service<BiUiAnalyseCategory> {

    /**
     * 基于租户获取页面列表
     * @param request
     * @return
     */
    PageResult<List<BiUiAnalyseCategory>> getAnalyseCategoryList(PageRequest<GetCategoryDto> request);

    /**
     * 查看单个resource
     * @param id
     * @return
     */
    BiUiAnalyseCategory getAnalyseCategory(String id);

    /**
     * 创建页面
     * @param request
     * @return
     */
    BiUiAnalyseCategory createAnalyseCategory(RetRequest<CreateAnalyseCategoryDto> request);

    /**
     * del页面
     * @param id
     * @return
     */
    void delAnalyseCategory(String id);

    /**
     * 修改页面
     * @param request
     * @return
     */
    BiUiAnalyseCategory updateAnalyseCategory(RetRequest<UpdateCategoryDto> request);

    List<AnalyseCategoryTree> getTree(RetRequest<GetCategoryDto> request);

    void initTenantAnalyse(RetRequest<Void> request);

    List<BiUiAnalysePage> getChildAnalysePageReq(RetRequest<GetAnalysePageDto> request);

    void batchDelAnalyseCategories(RetRequest<BatchDeleteCategoryDto> request);
}
