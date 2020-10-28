package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.AnalysePageConfigReq;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.PublishAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@RestController
@RequestMapping("/ui/analyse/pageConfig")
public class BiUiAnalysePageConfigController {
    @Resource
    BiUiAnalysePageConfigService biUiReportPageConfigService;

    @ApiOperation(value = "查看单个页面配置详情", notes = "查看单个页面配置详情")
    @PostMapping("/getAnalysePageConfig")
    public RetResult<BiUiAnalysePageConfig> getAnalysePageConfig(@RequestBody @Validated RetRequest<AnalysePageConfigReq> request) throws Exception {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.getAnalysePageConfig(request.getData()));
    }

    @ApiOperation(value = "新增页面配置", notes = "新增页面配置")
    @PostMapping("/createAnalysePageConfig")
    public RetResult<BiUiAnalysePageConfig> createAnalysePageConfig(@RequestBody @Validated RetRequest<CreateAnalysePageConfigsDto> request) throws Exception {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.createAnalysePageConfig(request.getData()));
    }

    @ApiOperation(value = "发布页面", notes = "发布页面")
    @PostMapping("/publishAnalysePageConfig")
    public RetResult<BiUiAnalysePageConfig> publishAnalysePageConfig(@RequestBody @Validated RetRequest<PublishAnalysePageConfigsDto> request) throws Exception {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.publishAnalysePageConfig(request.getData()));
    }

    @ApiOperation(value = "删除页面配置", notes = "删除页面配置")
    @PostMapping("/delAnalysePageConfig")
    public RetResult<Void> delAnalysePageConfig(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biUiReportPageConfigService.delAnalysePageConfig(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改页面配置", notes = "修改页面配置")
    @PostMapping("/updateAnalysePageConfig")
    public RetResult<BiUiAnalysePageConfig> updateAnalysePageConfig(@RequestBody @Validated RetRequest<UpdateAnalysePageConfigsDto> request) throws Exception {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.updateAnalysePageConfig(request.getData()));
    }
}
