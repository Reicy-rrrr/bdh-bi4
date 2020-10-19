package com.deloitte.bdh.data.report.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.report.model.BiUiReportPageConfig;
import com.deloitte.bdh.data.report.service.BiUiReportPageConfigService;
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
@RequestMapping("/bi/ui/report/pageConfig")
public class BiUiReportPageConfigController {
    @Resource
    BiUiReportPageConfigService biUiReportPageConfigService;

    @ApiOperation(value = "查看单个页面配置详情", notes = "查看单个页面配置详情")
    @PostMapping("/getResource")
    public RetResult<BiUiReportPageConfig> getResource(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.getResource(request.getData()));
    }

    @ApiOperation(value = "新增页面配置", notes = "新增页面配置")
    @PostMapping("/createResource")
    public RetResult<BiUiReportPageConfig> createResource(@RequestBody @Validated RetRequest<CreateResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.createResource(request.getData()));
    }

    @ApiOperation(value = "删除页面配置", notes = "删除页面配置")
    @PostMapping("/delResource")
    public RetResult<Void> delResource(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biUiReportPageConfigService.delResource(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改页面配置", notes = "修改页面配置")
    @PostMapping("/updateResource")
    public RetResult<BiUiReportPageConfig> updateResource(@RequestBody @Validated RetRequest<UpdateResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(biUiReportPageConfigService.updateResource(request.getData()));
    }
}
