package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseCategoryOrgMapper;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageLinkMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategoryOrg;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageLink;
import com.deloitte.bdh.data.analyse.service.AnalyseCategoryOrgService;
import com.deloitte.bdh.data.analyse.service.AnalysePageLinkService;
import org.springframework.stereotype.Service;


@Service
@DS(DSConstant.BI_DB)
public class AnalyseCategoryOrgImpl extends AbstractService<BiUiAnalyseCategoryOrgMapper, BiUiAnalyseCategoryOrg> implements AnalyseCategoryOrgService {

}
