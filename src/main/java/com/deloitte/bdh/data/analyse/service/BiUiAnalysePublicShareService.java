package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePublicShare;
import com.deloitte.bdh.data.analyse.model.request.AnalysePublicShareDto;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-11-23
 */
public interface BiUiAnalysePublicShareService extends Service<BiUiAnalysePublicShare> {


    String update(AnalysePublicShareDto dto);

    BiUiAnalysePublicShare get(String Id);

}
