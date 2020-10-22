package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.GetResourcesDto;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseDemoSaleDetail;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface BiUiAnalyseDemoSaleDetailService extends Service<BiUiAnalyseDemoSaleDetail> {
    /**
     * 基于租户获取页面配置列表
     *
     * @param dto
     * @return
     */
    PageResult<List<BiUiAnalyseDemoSaleDetail>> getResources(GetResourcesDto dto);
}
