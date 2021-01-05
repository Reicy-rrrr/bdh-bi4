package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageHomepage;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;

import java.util.List;

/**
 * Author:LIJUN
 * Date:05/01/2021
 * Description:
 */
public interface AnalysePageHomepageService extends Service<BiUiAnalysePageHomepage> {

    void setHomepage(List<AnalysePageDto> pageDtoList);

}
