package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.model.request.GetResourcesDto;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseDemoSaleDetailMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseDemoSaleDetail;
import com.deloitte.bdh.data.analyse.service.BiUiAnalyseDemoSaleDetailService;
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
public class BiUiAnalyseDemoSaleDetailServiceImpl extends AbstractService<BiUiAnalyseDemoSaleDetailMapper, BiUiAnalyseDemoSaleDetail> implements BiUiAnalyseDemoSaleDetailService {
    @Override
    public PageResult<List<BiUiAnalyseDemoSaleDetail>> getResources(GetResourcesDto dto) {
        LambdaQueryWrapper<BiUiAnalyseDemoSaleDetail> query = new LambdaQueryWrapper();
//        if (!StringUtil.isEmpty(dto.getTenantId())) {
//            query.eq(BiUiReportDemoSaleDetail::getTenantId, dto.getTenantId());
//        }
        query.orderByDesc(BiUiAnalyseDemoSaleDetail::getCreateDate);
        PageInfo<BiUiAnalyseDemoSaleDetail> pageInfo = new PageInfo(this.list(query));
        PageResult pageResult = new PageResult(pageInfo);
        return pageResult;
    }
}
