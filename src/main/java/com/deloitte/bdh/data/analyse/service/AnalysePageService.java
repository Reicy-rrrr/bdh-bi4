package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
public interface AnalysePageService extends Service<BiUiAnalysePage> {

    PageResult<AnalysePageDto> getChildAnalysePageList(PageRequest<GetAnalysePageDto> request);

    /**
     * 查看单个resource
     * @param id
     * @return
     */
    AnalysePageDto getAnalysePage(String id);

    /**
     * 创建页面
     * @param request
     * @return
     */
    AnalysePageDto createAnalysePage(RetRequest<CreateAnalysePageDto> request);

    /**
     * 复制页面
     * @param request
     * @return
     */
    AnalysePageDto copyAnalysePage(CopyAnalysePageDto request);

    /**
     * 批量删除页面
     *
     * @param request
     * @return
     */
    void batchDelAnalysePage(BatchDeleteAnalyseDto request);

    /**
     * 修改页面
     * @param request
     * @return
     */
    AnalysePageDto updateAnalysePage(RetRequest<UpdateAnalysePageDto> request);

    /**
     * 发布一个页面
     * @param request
     * @return
     */
    AnalysePageConfigDto publishAnalysePage(RetRequest<PublishAnalysePageDto> request);

    /**
     * 获取草稿数据
     * @param request
     * @return
     */
    PageResult<AnalysePageDto> getAnalysePageDrafts(PageRequest<AnalyseNameDto> request);

    /**
     * 删除草稿
     * @param request
     */
    void delAnalysePageDrafts(RetRequest<BatchDeleteAnalyseDto> request);

}
