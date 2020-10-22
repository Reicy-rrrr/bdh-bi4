package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.request.GetResourcesDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalyseDemoSaleDetailService;
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
@RequestMapping("/ui/analyse/demoSaleDetail")
public class BiUiAnalyseDemoSaleDetailController {
    @Resource
    BiUiAnalyseDemoSaleDetailService biUiReportDemoSaleDetailService;

    @ApiOperation(value = "获取demo数据的分页数据", notes = "获取demo数据的分页数据,不和租户信息挂钩")
    @PostMapping("/getResources")
    public RetResult<PageResult> getResources(@RequestBody @Validated RetRequest<GetResourcesDto> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        return RetResponse.makeOKRsp(biUiReportDemoSaleDetailService.getResources(request.getData()));
    }
}
