package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.GetAnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
@Api(value = "分析管理-报表配置")
@RestController
@RequestMapping("/ui/analyse/pageConfig")
public class AnalysePageConfigController {
    @Resource
    AnalysePageConfigService biUiReportPageConfigService;

    @ApiOperation(value = "查看单个页面配置详情", notes = "查看单个页面配置详情")
    @PostMapping("/getAnalysePageConfig")
    public RetResult<AnalysePageConfigDto> getAnalysePageConfig(@RequestBody @Validated RetRequest<GetAnalysePageConfigDto> request) {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.getAnalysePageConfig(request));
    }

    @ApiOperation(value = "查看某个页面配置历史版本详情列表", notes = "查看某个页面配置历史版本详情列表")
    @PostMapping("/getAnalysePageConfigList")
    public RetResult<List<AnalysePageConfigDto>> getAnalysePageConfigList(@RequestBody @Validated RetRequest<GetAnalysePageConfigDto> request) {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.getAnalysePageConfigList(request.getData()));
    }

    @ApiOperation(value = "新增页面配置", notes = "新增页面配置")
    @PostMapping("/createAnalysePageConfig")
    public RetResult<AnalysePageConfigDto> createAnalysePageConfig(@RequestBody @Validated RetRequest<CreateAnalysePageConfigsDto> request) {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.createAnalysePageConfig(request));
    }

    @ApiOperation(value = "删除页面配置", notes = "删除页面配置")
    @PostMapping("/delAnalysePageConfig")
    public RetResult<Void> delAnalysePageConfig(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biUiReportPageConfigService.delAnalysePageConfig(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改页面配置", notes = "修改页面配置")
    @PostMapping("/updateAnalysePageConfig")
    public RetResult<AnalysePageConfigDto> updateAnalysePageConfig(@RequestBody @Validated RetRequest<UpdateAnalysePageConfigsDto> request) {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.updateAnalysePageConfig(request));
    }
}
