package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.model.BiReport;
import com.deloitte.bdh.data.collation.dao.bi.BiReportMapper;
import com.deloitte.bdh.data.collation.service.BiReportService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lw
 * @since 2021-01-27
 */
@Service
@DS(DSConstant.BI_DB)
public class BiReportServiceImpl extends AbstractService<BiReportMapper, BiReport> implements BiReportService {

}
