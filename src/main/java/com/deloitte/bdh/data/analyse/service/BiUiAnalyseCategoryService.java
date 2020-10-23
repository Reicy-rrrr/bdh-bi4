package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.request.AnalyseCategoryReq;
import com.deloitte.bdh.data.analyse.model.request.InitTenantReq;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalyseCategoryDto;
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
     *
     * @param dto
     * @return
     */
    PageResult<List<BiUiAnalyseCategory>> getAnalyseCategories(AnalyseCategoryReq dto);

    /**
     * 查看单个resource
     *
     * @param id
     * @return
     */
    BiUiAnalyseCategory getAnalyseCategory(String id);

    /**
     * 创建页面
     *
     * @param dto
     * @return
     */
    BiUiAnalyseCategory createAnalyseCategory(CreateAnalyseCategoryDto dto) throws Exception;

    /**
     * del页面
     *
     * @param id
     * @return
     */
    void delAnalyseCategory(String id) throws Exception;

    /**
     * 修改页面
     *
     * @param dto
     * @return
     */
    BiUiAnalyseCategory updateAnalyseCategory(UpdateAnalyseCategoryDto dto) throws Exception;

    List<AnalyseCategoryTree> getTree(AnalyseCategoryReq dto);

    void initTenantAnalyse(InitTenantReq data) throws Exception;
}
