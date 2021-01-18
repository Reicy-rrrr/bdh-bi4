package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.GetAnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;

import java.util.List;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
public interface AnalysePageConfigService extends Service<BiUiAnalysePageConfig> {

    /**
     * 查询报表配置
     * @param request
     * @return
     */
    AnalysePageConfigDto getAnalysePageConfig(RetRequest<GetAnalysePageConfigDto> request);

    /**
     * 创建报表配置
     * @param request
     * @return
     */
    AnalysePageConfigDto createAnalysePageConfig(RetRequest<CreateAnalysePageConfigsDto> request);

    /**
     * 删除报表配置
     * @param id
     * @return
     */
    void delAnalysePageConfig(String id);

    /**
     * 修改报表配置
     * @param request
     * @return
     */
    AnalysePageConfigDto updateAnalysePageConfig(RetRequest<UpdateAnalysePageConfigsDto> request);

    /**
     * 查询报表配置历史
     * @param data
     * @return
     */
    List<AnalysePageConfigDto> getAnalysePageConfigList(GetAnalysePageConfigDto data);
}
