package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface AnalyseCategoryService extends Service<BiUiAnalyseCategory> {

    /**
     * 创建文件夹
     * @param request
     * @return
     */
    AnalyseCategoryDto createAnalyseCategory(RetRequest<CreateAnalyseCategoryDto> request);

    /**
     * 删除文件夹
     * @param id
     * @return
     */
    void delAnalyseCategory(String id);

    /**
     * 修改文件夹
     * @param request
     * @return
     */
    AnalyseCategoryDto updateAnalyseCategory(RetRequest<UpdateAnalyseCategoryDto> request);

    List<AnalyseCategoryTree> getTree(RetRequest<GetAnalyseCategoryDto> request);

    void initTenantAnalyse(RetRequest<Void> request);

    PageResult<AnalysePageDto> getChildAnalysePageList(PageRequest<GetAnalysePageDto> request);

    void batchDelAnalyseCategories(RetRequest<BatchDeleteAnalyseDto> request);
}
