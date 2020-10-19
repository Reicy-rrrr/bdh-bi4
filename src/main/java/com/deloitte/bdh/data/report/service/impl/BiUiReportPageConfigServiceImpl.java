package com.deloitte.bdh.data.report.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.report.model.BiUiReportPageConfig;
import com.deloitte.bdh.data.report.dao.bi.BiUiReportPageConfigMapper;
import com.deloitte.bdh.data.report.service.BiUiReportPageConfigService;
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
public class BiUiReportPageConfigServiceImpl extends AbstractService<BiUiReportPageConfigMapper, BiUiReportPageConfig> implements BiUiReportPageConfigService {

}
