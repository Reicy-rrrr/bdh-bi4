package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;

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
//    PageResult<List<BiUiReportPageConfig>> getResources(GetResourcesDto dto);

    /**
     * 查看单个resource
     *
     * @param id
     * @return
     */
    BiUiAnalysePageConfig getResource(String id);

    /**
     * 创建页面配置
     *
     * @param dto
     * @return
     */
    BiUiAnalysePageConfig createResource(CreateResourcesDto dto) throws Exception;

    /**
     * del页面配置
     *
     * @param id
     * @return
     */
    void delResource(String id) throws Exception;

    /**
     * 修改页面配置
     *
     * @param dto
     * @return
     */
    BiUiAnalysePageConfig updateResource(UpdateResourcesDto dto) throws Exception;
}
