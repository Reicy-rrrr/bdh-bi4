package com.deloitte.bdh.data.report.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.GetResourcesDto;
import com.deloitte.bdh.data.report.model.BiUiReportDemoSaleDetail;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface BiUiReportDemoSaleDetailService extends Service<BiUiReportDemoSaleDetail> {
    /**
     * 基于租户获取页面配置列表
     *
     * @param dto
     * @return
     */
    PageResult<List<BiUiReportDemoSaleDetail>> getResources(GetResourcesDto dto);
}
