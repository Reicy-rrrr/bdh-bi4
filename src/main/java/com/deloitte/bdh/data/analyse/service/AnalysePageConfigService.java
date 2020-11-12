package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface AnalysePageConfigService extends Service<BiUiAnalysePageConfig> {

//    /**
//     * 基于租户获取页面配置列表
//     *
//     * @param dto
//     * @return
//     */
//    PageResult<List<BiUiReportPageConfig>> getAnalysePageConfigs(GetAnalysePageConfigsDto dto);

    /**
     * 查看单个resource
     *
     * @param req
     * @return
     */
    BiUiAnalysePageConfig getAnalysePageConfig(AnalysePageConfigDto req) throws Exception;

    /**
     * 创建页面配置
     * @param request
     * @return
     */
    BiUiAnalysePageConfig createAnalysePageConfig(RetRequest<CreateAnalysePageConfigsDto> request);

    /**
     * del页面配置
     *
     * @param id
     * @return
     */
    void delAnalysePageConfig(String id) throws Exception;

    /**
     * 修改页面配置
     *
     * @param dto
     * @return
     */
    BiUiAnalysePageConfig updateAnalysePageConfig(UpdateAnalysePageConfigsDto dto) throws Exception;

    /**
     * 获取list
     * @param data
     * @return
     */
    List<AnalysePageConfigDto> getAnalysePageConfigList(AnalysePageConfigDto data);
}
