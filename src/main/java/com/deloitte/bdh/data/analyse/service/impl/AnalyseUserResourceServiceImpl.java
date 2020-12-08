package com.deloitte.bdh.data.analyse.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseUserResourceMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserResource;
import com.deloitte.bdh.data.analyse.service.AnalyseUserResourceService;
import org.springframework.stereotype.Service;

/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalyseUserResourceServiceImpl extends AbstractService<BiUiAnalyseUserResourceMapper, BiUiAnalyseUserResource> implements AnalyseUserResourceService {

}
