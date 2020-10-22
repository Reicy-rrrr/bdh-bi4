package com.deloitte.bdh.data.report.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.report.model.BiUiReportPage;
import com.deloitte.bdh.data.report.model.request.CreateReportDto;
import com.deloitte.bdh.data.report.model.request.ReportPageReq;
import com.deloitte.bdh.data.report.model.request.UpdateReportDto;
import com.deloitte.bdh.data.report.model.resp.ReportPageTree;
import com.deloitte.bdh.data.report.service.BiUiReportPageService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@RestController
@RequestMapping("/ui/report/page")
public class BiUiReportPageController {
    @Resource
    BiUiReportPageService biUiReportPageService;

    @ApiOperation(value = "基于租户查询报表的树状结构", notes = "基于租户查询报表的树状结构")
    @PostMapping("/getReportTree")
    public RetResult<List<ReportPageTree>> getReportTree(@RequestBody @Validated RetRequest<ReportPageReq> request) {
        return RetResponse.makeOKRsp(biUiReportPageService.getTree(request.getData()));
    }

    @ApiOperation(value = "基于租户获取页面列表", notes = "基于租户获取页面列表")
    @PostMapping("/getReportPages")
    public RetResult<PageResult> getReportPages(@RequestBody @Validated RetRequest<ReportPageReq> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        return RetResponse.makeOKRsp(biUiReportPageService.getReportPages(request.getData()));
    }

    @ApiOperation(value = "查看单个页面详情", notes = "查看单个页面详情")
    @PostMapping("/getReportPage")
    public RetResult<BiUiReportPage> getReportPage(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(biUiReportPageService.getReportPage(request.getData()));
    }

    @ApiOperation(value = "新增页面", notes = "新增页面")
    @PostMapping("/createReportPage")
    public RetResult<BiUiReportPage> createReportPage(@RequestBody @Validated RetRequest<CreateReportDto> request) throws Exception {
        return RetResponse.makeOKRsp(biUiReportPageService.createReportPage(request.getData()));
    }

    @ApiOperation(value = "删除页面", notes = "删除页面")
    @PostMapping("/delReportPage")
    public RetResult<Void> delReportPage(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biUiReportPageService.delReportPage(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改页面", notes = "修改页面")
    @PostMapping("/updateReportPage")
    public RetResult<BiUiReportPage> updateReportPage(@RequestBody @Validated RetRequest<UpdateReportDto> request) throws Exception {
        return RetResponse.makeOKRsp(biUiReportPageService.updateReportPage(request.getData()));
    }
}
