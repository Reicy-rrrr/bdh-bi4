package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.AnalysePageReq;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageDto;
import com.deloitte.bdh.data.analyse.model.datamodel.BaseComponentDataResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface BiUiAnalysePageService extends Service<BiUiAnalysePage> {

    /**
     * 基于租户获取页面列表
     *
     * @param dto
     * @return
     */
    PageResult<List<BiUiAnalysePage>> getAnalysePages(AnalysePageReq dto);

    /**
     * 查看单个resource
     *
     * @param id
     * @return
     */
    BiUiAnalysePage getAnalysePage(String id);

    /**
     * 创建页面
     *
     * @param dto
     * @return
     */
    BiUiAnalysePage createAnalysePage(CreateAnalysePageDto dto) throws Exception;

    /**
     * del页面
     *
     * @param id
     * @return
     */
    void delAnalysePage(String id) throws Exception;

    /**
     * 修改页面
     *
     * @param dto
     * @return
     */
    BiUiAnalysePage updateAnalysePage(UpdateAnalysePageDto dto) throws Exception;

    /**
     * 获取组件数据
     * @param data
     * @return
     */
    BaseComponentDataResponse getComponentDta(Map data);
}
