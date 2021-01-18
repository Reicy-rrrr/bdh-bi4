package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageComponent;
import com.deloitte.bdh.data.analyse.model.request.pageComponentDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Ashen
 * @since 2020-12-15
 */
public interface BiUiAnalysePageComponentService extends Service<BiUiAnalysePageComponent> {

    Boolean saveChartComponent(pageComponentDto dto);

    Boolean delChartComponent(pageComponentDto dto);

    List<BiUiAnalysePageComponent> getChartComponent(pageComponentDto dto);
}
