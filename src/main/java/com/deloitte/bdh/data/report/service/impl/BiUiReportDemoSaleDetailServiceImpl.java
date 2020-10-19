package com.deloitte.bdh.data.report.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.report.model.BiUiReportDemoSaleDetail;
import com.deloitte.bdh.data.report.dao.bi.BiUiReportDemoSaleDetailMapper;
import com.deloitte.bdh.data.report.service.BiUiReportDemoSaleDetailService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@Service
@DS(DSConstant.BI_DB)
public class BiUiReportDemoSaleDetailServiceImpl extends AbstractService<BiUiReportDemoSaleDetailMapper, BiUiReportDemoSaleDetail> implements BiUiReportDemoSaleDetailService {

}
