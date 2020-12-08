package com.deloitte.bdh.data.analyse.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseUserDataMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserData;
import com.deloitte.bdh.data.analyse.service.AnalyseUserDataService;
import org.springframework.stereotype.Service;

/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalyseUserDataServiceImpl extends AbstractService<BiUiAnalyseUserDataMapper, BiUiAnalyseUserData> implements AnalyseUserDataService {

}
