package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
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
     * del页面
     *
     * @param id
     * @return
     */
    void delAnalysePage(String id);

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
    AnalysePageConfigDto publishAnalysePage(RetRequest<AnalysePageIdDto> request);

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
    void delAnalysePageDrafts(@RequestBody @Validated RetRequest<BatchDeleteAnalyseDto> request);

    /**
     * 获取组件数据
     *
     * @param data
     * @return
     */
    BaseComponentDataResponse getComponentData(BaseComponentDataRequest data);

}
