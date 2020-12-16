package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.model.request.SubscribeDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseSubscribeDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseSubscribeLogDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageSubscribeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:15/12/2020
 * Description:
 */
@Api(tags = "分析管理-订阅")
@RestController
@RequestMapping("/subscribe")
@Slf4j
public class AnalyseSubscribeController {

    @Resource
    private AnalysePageSubscribeService subscribeService;

    @ApiOperation(value = "订阅", notes = "订阅")
    @PostMapping("/save")
    public RetResult<Void> subscribe(@RequestBody @Valid RetRequest<SubscribeDto> request) {
        subscribeService.subscribe(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "查询订阅配置", notes = "查询订阅配置")
    @PostMapping("/get")
    public RetResult<AnalyseSubscribeDto> getSubscribe(@RequestBody @Valid RetRequest<String> request) {
        return RetResponse.makeOKRsp(subscribeService.getSubscribe(request.getData()));
    }

    @ApiOperation(value = "查询执行记录", notes = "查询执行记录")
    @PostMapping("/getExecuteLog")
    public RetResult<AnalyseSubscribeLogDto> getExecuteLog(@RequestBody @Valid RetRequest<String> request) {
        return RetResponse.makeOKRsp(subscribeService.getExecuteLog(request.getData()));
    }

    @ApiOperation(value = "计划任务回调", notes = "计划任务回调")
    @PostMapping("/execute")
    public RetResult<Void> execute(@RequestBody Map<String, Object> map) {
        ThreadLocalHolder.set("tenantId", MapUtils.getString(map, "tenantId"));
        ThreadLocalHolder.set("operator", MapUtils.getString(map, "operator"));
        subscribeService.execute(MapUtils.getString(map, "pageId"));
        return RetResponse.makeOKRsp();
    }

}
