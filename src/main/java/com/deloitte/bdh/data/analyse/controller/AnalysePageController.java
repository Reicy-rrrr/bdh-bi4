package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.*;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
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
public class AnalysePageController {

    @Resource
    AnalysePageService analysePageService;

    @ApiOperation(value = "基于租户获取页面列表", notes = "基于租户获取页面列表")
    @PostMapping("/getAnalysePages")
    public RetResult<PageResult<BiUiAnalysePage>> getAnalysePages(@RequestBody @Validated PageRequest<GetAnalysePageDto> request) {
        PageHelper.startPage(request.getPage(), request.getSize());
        return RetResponse.makeOKRsp(analysePageService.getAnalysePages(request));
    }

    @ApiOperation(value = "查看单个页面详情", notes = "查看单个页面详情")
    @PostMapping("/getAnalysePage")
    public RetResult<BiUiAnalysePage> getAnalysePage(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(analysePageService.getAnalysePage(request.getData()));
    }

    @ApiOperation(value = "新增页面", notes = "新增页面")
    @PostMapping("/createAnalysePage")
    public RetResult<BiUiAnalysePage> createAnalysePage(@RequestBody @Validated RetRequest<CreateAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.createAnalysePage(request));
    }

    @ApiOperation(value = "复制页面", notes = "复制页面")
    @PostMapping("/copyAnalysePage")
    public RetResult<BiUiAnalysePage> copyAnalysePage(@RequestBody @Validated RetRequest<CopyAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.copyAnalysePage(request.getData()));
    }

    @ApiOperation(value = "删除页面", notes = "删除页面")
    @PostMapping("/delAnalysePage")
    public RetResult<Void> delAnalysePage(@RequestBody @Validated RetRequest<String> request) throws Exception {
        analysePageService.delAnalysePage(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "批量删除页面", notes = "批量删除页面")
    @PostMapping("/batchDelAnalysePage")
    public RetResult<Void> batchDelAnalysePage(@RequestBody @Validated RetRequest<BatchDeleteAnalyseDto> request) {
        analysePageService.batchDelAnalysePage(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改页面", notes = "修改页面")
    @PostMapping("/updateAnalysePage")
    public RetResult<BiUiAnalysePage> updateAnalysePage(@RequestBody @Validated RetRequest<UpdateAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.updateAnalysePage(request.getData()));
    }

    @ApiOperation(value = "发布页面", notes = "发布页面")
    @PostMapping("/publishAnalysePage")
    public RetResult<BiUiAnalysePageConfig> publishAnalysePageConfig(@RequestBody @Validated RetRequest<AnalysePageIdDto> request) {
        return RetResponse.makeOKRsp(analysePageService.publishAnalysePage(request));
    }

    @ApiOperation(value = "查询草稿", notes = "查询草稿")
    @PostMapping("/getAnalysePageDrafts")
    public RetResult<PageResult<AnalysePageDto>> getAnalysePageDrafts(@RequestBody @Validated PageRequest<AnalyseNameDto> request) {
        return RetResponse.makeOKRsp(analysePageService.getAnalysePageDrafts(request));
    }

    @ApiOperation(value = "删除草稿", notes = "删除草稿")
    @PostMapping("/delAnalysePageDrafts")
    public RetResult<Void> delAnalysePageDrafts(@RequestBody @Validated RetRequest<BatchDeleteAnalyseDto> request) {
        analysePageService.delAnalysePageDrafts(request);
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "获取组件数据", notes = "获取组件数据")
    @PostMapping("/getComponentData")
    public RetResult<BaseComponentDataResponse> getComponentData(@RequestBody @Validated RetRequest<BaseComponentDataRequest> request) {
        return RetResponse.makeOKRsp(analysePageService.getComponentData(request.getData()));
    }

}
