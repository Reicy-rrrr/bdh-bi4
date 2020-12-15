package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageComponent;
import com.deloitte.bdh.data.analyse.model.request.SavePageComponentDto;

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

    Boolean saveChartComponent(SavePageComponentDto dto);

    Boolean delChartComponent(String id);

    List<BiUiAnalysePageComponent> getChartComponent();
}
