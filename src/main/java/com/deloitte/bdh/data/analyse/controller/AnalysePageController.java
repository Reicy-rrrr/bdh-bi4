package com.deloitte.bdh.data.analyse.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.*;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageComponent;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageComponentService;
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
@Api(tags = "分析管理-报表")
@RestController
@RequestMapping("/ui/analyse/page")
public class AnalysePageController {

    @Resource
    AnalysePageService analysePageService;

    @Resource
    BiUiAnalysePageComponentService biUiAnalysePageComponentService;

    @ApiOperation(value = "查询文件夹下的页面", notes = "查询文件夹下的页面")
    @PostMapping("/getChildAnalysePageList")
    public RetResult<PageResult<AnalysePageDto>> getChildAnalysePageList(@RequestBody @Validated PageRequest<GetAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.getChildAnalysePageList(request));
    }

    @ApiOperation(value = "查看单个页面详情", notes = "查看单个页面详情")
    @PostMapping("/getAnalysePage")
    public RetResult<AnalysePageDto> getAnalysePage(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(analysePageService.getAnalysePage(request.getData()));
    }

    @ApiOperation(value = "新增页面", notes = "新增页面")
    @PostMapping("/createAnalysePage")
    public RetResult<AnalysePageDto> createAnalysePage(@RequestBody @Validated RetRequest<CreateAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.createAnalysePage(request));
    }

    @ApiOperation(value = "复制页面", notes = "复制页面")
    @PostMapping("/copyAnalysePage")
    public RetResult<AnalysePageDto> copyAnalysePage(@RequestBody @Validated RetRequest<CopyAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.copyAnalysePage(request.getData()));
    }

    @ApiOperation(value = "批量删除页面", notes = "批量删除页面")
    @PostMapping("/batchDelAnalysePage")
    public RetResult<Void> batchDelAnalysePage(@RequestBody @Validated RetRequest<BatchDeleteAnalyseDto> request) {
        analysePageService.batchDelAnalysePage(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改页面", notes = "修改页面")
    @PostMapping("/updateAnalysePage")
    public RetResult<AnalysePageDto> updateAnalysePage(@RequestBody @Validated RetRequest<UpdateAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.updateAnalysePage(request));
    }

    @ApiOperation(value = "发布页面", notes = "发布页面")
    @PostMapping("/publishAnalysePage")
    public RetResult<AnalysePageConfigDto> publishAnalysePageConfig(@RequestBody @Validated RetRequest<PublishAnalysePageDto> request) {
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

    @ApiOperation(value = "设置主页", notes = "设置主页")
    @PostMapping("/setHomePage")
    public RetResult<Void> setHomePage(@RequestBody @Validated RetRequest<String> request) {
        BiUiAnalysePage homePage = analysePageService.getOne(new LambdaQueryWrapper<BiUiAnalysePage>().eq(BiUiAnalysePage::getHomePage, "1"));
        if (null != homePage) {
            homePage.setHomePage("0");
            analysePageService.updateById(homePage);
        }
        BiUiAnalysePage newHomePage = analysePageService.getById(request.getData());
        newHomePage.setHomePage("1");
        analysePageService.updateById(newHomePage);
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "获取主页的ID", notes = "获取主页的ID")
    @PostMapping("/getHomePageId")
    public RetResult<String> getHomePageId(@RequestBody @Validated RetRequest<Void> request) {
        BiUiAnalysePage homePage = analysePageService.getOne(new LambdaQueryWrapper<BiUiAnalysePage>().eq(BiUiAnalysePage::getHomePage, "1"));
        if (null != homePage) {
            return RetResponse.makeOKRsp(homePage.getId());
        }
        return RetResponse.makeOKRsp(null);
    }

    @ApiOperation(value = "保存图形指标", notes = "保存图形指标")
    @PostMapping("/saveChartComponent")
    public RetResult<Boolean> saveChartComponent(@RequestBody @Validated RetRequest<pageComponentDto> request) {

        return RetResponse.makeOKRsp(biUiAnalysePageComponentService.saveChartComponent(request.getData()));
    }

    @ApiOperation(value = "删除图形指标", notes = "删除图形指标")
    @PostMapping("/delChartComponent")
    public RetResult<Boolean> delChartComponent(@RequestBody @Validated RetRequest<pageComponentDto> request) {

        return RetResponse.makeOKRsp(biUiAnalysePageComponentService.delChartComponent(request.getData()));
    }

    @ApiOperation(value = "获取图形指标列表", notes = "获取图形指标列表")
    @PostMapping("/getChartComponent")
    public RetResult<List<BiUiAnalysePageComponent>> getChartComponent(@RequestBody @Validated RetRequest<pageComponentDto> request) {

        return RetResponse.makeOKRsp(biUiAnalysePageComponentService.getChartComponent(request.getData()));
    }

}
