package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseSubscribe;
import com.deloitte.bdh.data.analyse.model.request.SubscribeDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseSubscribeDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseSubscribeLogDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Author:LIJUN
 * Date:15/12/2020
 * Description:
 */
public interface AnalysePageSubscribeService extends Service<BiUiAnalyseSubscribe> {

    /**
     * 订阅
     * @param request
     */
    void subscribe(SubscribeDto request);

    /**
     * 获取订阅配置
     * @param pageId
     */
    AnalyseSubscribeDto getSubscribe(String pageId);

    /**
     * 获取执行记录
     * @param pageId
     */
    AnalyseSubscribeLogDto getExecuteLog(String pageId);

    /**
     * 执行计划任务回调
     * @param pageId
     */
    void execute(String pageId);
}
