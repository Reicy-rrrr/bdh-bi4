package com.deloitte.bdh.data.report.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.report.model.BiUiReportPage;
import com.deloitte.bdh.data.report.model.request.CreateReportDto;
import com.deloitte.bdh.data.report.model.request.ReportPageReq;
import com.deloitte.bdh.data.report.model.request.UpdateReportDto;
import com.deloitte.bdh.data.report.model.resp.ReportPageTree;

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
    PageResult<List<BiUiReportPage>> getReportPages(ReportPageReq dto);

    /**
     * 查看单个resource
     *
     * @param id
     * @return
     */
    BiUiReportPage getReportPage(String id);

    /**
     * 创建页面
     *
     * @param dto
     * @return
     */
    BiUiReportPage createReportPage(CreateReportDto dto) throws Exception;

    /**
     * del页面
     *
     * @param id
     * @return
     */
    void delReportPage(String id) throws Exception;

    /**
     * 修改页面
     *
     * @param dto
     * @return
     */
    BiUiReportPage updateReportPage(UpdateReportDto dto) throws Exception;

    List<ReportPageTree> getTree(ReportPageReq dto);
}
