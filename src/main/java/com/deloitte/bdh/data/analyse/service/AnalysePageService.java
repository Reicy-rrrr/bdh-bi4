package com.deloitte.bdh.data.analyse.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;

import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
public interface AnalysePageService extends Service<BiUiAnalysePage> {

    PageResult<AnalysePageDto> getChildAnalysePageList(PageRequest<GetAnalysePageListDto> request);

    /**
     * 查看单个resource
     *
     * @param pageId
     * @return
     */
    AnalysePageDto getAnalysePage(String pageId);

    /**
     * 创建页面
     *
     * @param request
     * @return
     */
    AnalysePageDto createAnalysePage(RetRequest<CreateAnalysePageDto> request);

    /**
     * @return
     */
    AnalysePageDto saveNewPage(String name, String categoryId, String fromPageId, List<String> linkPageId,
                               JSONObject content, JSONArray childrenArr, Map<String, String> codeMap);

    CopySourceDto getCopySourceData(String pageId);

    Map<String, Object> buildNewDataSet(String dataSetName, String dataSetCategoryId, String code);

    void saveNewTable(Map<String, Object> map);

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
     *
     * @param dto
     * @return
     */
    void replaceDataSet(ReplaceDataSetDto dto) throws Exception;

    /**
     * 获取报表使用的表
     *
     * @param pageId
     * @return
     */
    List<String> getUsedTableName(String pageId);

    /**
     * 获取所有分层page(不好含草稿箱的)
     *
     * @param pageId
     * @return
     */
    List<AnalysePageDto> getPageWithChildren(String pageId);
}
