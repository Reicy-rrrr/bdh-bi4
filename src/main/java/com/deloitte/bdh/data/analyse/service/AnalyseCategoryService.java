package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;

import java.util.List;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
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
     * @param request
     * @return
     */
    void delAnalyseCategory(RetRequest<String> request);

    /**
     * 修改文件夹
     * @param request
     * @return
     */
    AnalyseCategoryDto updateAnalyseCategory(RetRequest<UpdateAnalyseCategoryDto> request);

    List<AnalyseCategoryTree> getTree(RetRequest<GetAnalyseCategoryDto> request);

    void batchDelAnalyseCategories(RetRequest<BatchDeleteAnalyseDto> request);

    /**
     * 创建文件夹
     * @param request
     * @return
     */
    AnalyseCategoryDto createAnalyseCategory(CreateAnalyseCategoryDto categoryDto);

}
