package com.deloitte.bdh.data.analyse.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseSubscribeLogMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseSubscribeLog;
import com.deloitte.bdh.data.analyse.service.AnalysePageSubscribeLogService;
import org.springframework.stereotype.Service;

/**
 * Author:LIJUN
 * Date:15/12/2020
 * Description:
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalysePageSubscribeLogServiceImpl extends AbstractService<BiUiAnalyseSubscribeLogMapper, BiUiAnalyseSubscribeLog> implements AnalysePageSubscribeLogService {

}
