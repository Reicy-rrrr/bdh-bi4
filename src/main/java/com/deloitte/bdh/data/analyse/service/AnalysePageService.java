package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;

import java.util.List;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
public interface AnalysePageService extends Service<BiUiAnalysePage> {

    PageResult<AnalysePageDto> getChildAnalysePageList(PageRequest<GetAnalysePageDto> request);

    /**
     * 查看单个resource
     *
     * @param id
     * @return
     */
    AnalysePageDto getAnalysePage(String id);

    /**
     * 创建页面
     *
     * @param request
     * @return
     */
    AnalysePageDto createAnalysePage(RetRequest<CreateAnalysePageDto> request);

    /**
     * 复制页面
     *
     * @param request
     * @return
     */
    AnalysePageDto copyDeloittePage(CopyDeloittePageDto request);

    /**
     * 批量删除页面
     *
     * @param request
     * @return
     */
    void batchDelAnalysePage(BatchDeleteAnalyseDto request);

    /**
     * 修改页面
     *
     * @param request
     * @return
     */
    AnalysePageDto updateAnalysePage(RetRequest<UpdateAnalysePageDto> request);

    /**
     * 发布一个页面
     *
     * @param request
     * @return
     */
    AnalysePageConfigDto publishAnalysePage(PublishAnalysePageDto request);

    /**
     * 获取草稿数据
     *
     * @param request
     * @return
     */
    PageResult<AnalysePageDto> getAnalysePageDrafts(PageRequest<AnalyseNameDto> request);

    /**
     * 替换数据集
     * @param dto
     * @return
     */
    void replaceDataSet (ReplaceDataSetDto dto) throws Exception;

    /**
     * 获取报表使用的表
     *
     * @param pageId
     * @return
     */
    List<String> getUsedTableName(String pageId);
}
