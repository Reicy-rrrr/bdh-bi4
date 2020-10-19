package com.deloitte.bdh.data.report.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.GetResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.report.model.BiUiReportPage;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface BiUiReportPageService extends Service<BiUiReportPage> {

    /**
     * 基于租户获取页面列表
     *
     * @param dto
     * @return
     */
    PageResult<List<BiUiReportPage>> getResources(GetResourcesDto dto);

    /**
     * 查看单个resource
     *
     * @param id
     * @return
     */
    BiUiReportPage getResource(String id);

    /**
     * 创建页面
     *
     * @param dto
     * @return
     */
    BiUiReportPage createResource(CreateResourcesDto dto) throws Exception;

    /**
     * del页面
     *
     * @param id
     * @return
     */
    void delResource(String id) throws Exception;

    /**
     * 修改页面
     *
     * @param dto
     * @return
     */
    BiUiReportPage updateResource(UpdateResourcesDto dto) throws Exception;
}
