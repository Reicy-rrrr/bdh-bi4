package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.AnalysePageConfigReq;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.PublishAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface BiUiAnalysePageConfigService extends Service<BiUiAnalysePageConfig> {

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
    BiUiAnalysePageConfig getAnalysePageConfig(AnalysePageConfigReq req) throws Exception;

    /**
     * 创建页面配置
     *
     * @param dto
     * @return
     */
    BiUiAnalysePageConfig createAnalysePageConfig(CreateAnalysePageConfigsDto dto) throws Exception;

    /**
     * 发布一个页面
     *
     * @param dto
     * @return
     * @throws Exception
     */
    public BiUiAnalysePageConfig publishAnalysePageConfig(PublishAnalysePageConfigsDto dto) throws Exception;

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
}
