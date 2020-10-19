package com.deloitte.bdh.data.report.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.report.model.BiUiReportPageConfig;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface BiUiReportPageConfigService extends Service<BiUiReportPageConfig> {

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
    BiUiReportPageConfig getResource(String id);

    /**
     * 创建页面配置
     *
     * @param dto
     * @return
     */
    BiUiReportPageConfig createResource(CreateResourcesDto dto) throws Exception;

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
    BiUiReportPageConfig updateResource(UpdateResourcesDto dto) throws Exception;
}
