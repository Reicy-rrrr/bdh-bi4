package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageService;
import com.github.pagehelper.PageHelper;
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
@RequestMapping("/ui/analyse/page")
public class BiUiAnalysePageController {
    @Resource
    BiUiAnalysePageService biUiAnalysePageService;

    @ApiOperation(value = "基于租户获取页面列表", notes = "基于租户获取页面列表")
    @PostMapping("/getAnalysePages")
    public RetResult<PageResult<BiUiAnalysePage>> getAnalysePages(@RequestBody @Validated RetRequest<GetAnalysePageDto> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        return RetResponse.makeOKRsp(biUiAnalysePageService.getAnalysePages(request));
    }

    @ApiOperation(value = "查看单个页面详情", notes = "查看单个页面详情")
    @PostMapping("/getAnalysePage")
    public RetResult<BiUiAnalysePage> getAnalysePage(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(biUiAnalysePageService.getAnalysePage(request.getData()));
    }

    @ApiOperation(value = "新增页面", notes = "新增页面")
    @PostMapping("/createAnalysePage")
    public RetResult<BiUiAnalysePage> createAnalysePage(@RequestBody @Validated RetRequest<CreateAnalysePageDto> request) {
        return RetResponse.makeOKRsp(biUiAnalysePageService.createAnalysePage(request));
    }

    @ApiOperation(value = "复制页面", notes = "复制页面")
    @PostMapping("/copyAnalysePage")
    public RetResult<BiUiAnalysePage> copyAnalysePage(@RequestBody @Validated RetRequest<CopyAnalysePageDto> request) {
        return RetResponse.makeOKRsp(biUiAnalysePageService.copyAnalysePage(request.getData()));
    }

    @ApiOperation(value = "删除页面", notes = "删除页面")
    @PostMapping("/delAnalysePage")
    public RetResult<Void> delAnalysePage(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biUiAnalysePageService.delAnalysePage(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "批量删除页面", notes = "批量删除页面")
    @PostMapping("/batchDelAnalysePage")
    public RetResult<Void> batchDelAnalysePage(@RequestBody @Validated RetRequest<BatchDelAnalysePageDto> request) {
        biUiAnalysePageService.batchDelAnalysePage(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改页面", notes = "修改页面")
    @PostMapping("/updateAnalysePage")
    public RetResult<BiUiAnalysePage> updateAnalysePage(@RequestBody @Validated RetRequest<UpdateAnalysePageDto> request) {
        return RetResponse.makeOKRsp(biUiAnalysePageService.updateAnalysePage(request.getData()));
    }

    @ApiOperation(value = "获取组件数据", notes = "获取组件数据")
    @PostMapping("/getComponentData")
    public RetResult<BaseComponentDataResponse> getComponentData(@RequestBody @Validated RetRequest<BaseComponentDataRequest> request) {
        return RetResponse.makeOKRsp(biUiAnalysePageService.getComponentData(request.getData()));
    }

}
