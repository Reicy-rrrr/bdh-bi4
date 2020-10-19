package com.deloitte.bdh.data.report.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.model.request.GetResourcesDto;
import com.deloitte.bdh.data.report.dao.bi.BiUiReportDemoSaleDetailMapper;
import com.deloitte.bdh.data.report.model.BiUiReportDemoSaleDetail;
import com.deloitte.bdh.data.report.service.BiUiReportDemoSaleDetailService;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@Service
@DS(DSConstant.BI_DB)
public class BiUiReportDemoSaleDetailServiceImpl extends AbstractService<BiUiReportDemoSaleDetailMapper, BiUiReportDemoSaleDetail> implements BiUiReportDemoSaleDetailService {
    @Override
    public PageResult<List<BiUiReportDemoSaleDetail>> getResources(GetResourcesDto dto) {
        LambdaQueryWrapper<BiUiReportDemoSaleDetail> query = new LambdaQueryWrapper();
//        if (!StringUtil.isEmpty(dto.getTenantId())) {
//            query.eq(BiUiReportDemoSaleDetail::getTenantId, dto.getTenantId());
//        }
        query.orderByDesc(BiUiReportDemoSaleDetail::getCreateDate);
        PageInfo<BiUiReportDemoSaleDetail> pageInfo = new PageInfo(this.list(query));
        PageResult pageResult = new PageResult(pageInfo);
        return pageResult;
    }
}
