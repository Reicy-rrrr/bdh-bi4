package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;
import com.deloitte.bdh.data.analyse.service.BiUiAnalyseCategoryService;
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
@RequestMapping("/ui/analyse/category")
public class BiUiAnalyseCategoryController {
    @Resource
    BiUiAnalyseCategoryService biUiAnalyseCategoryService;

    @ApiOperation(value = "基于租户查询报表的树状结构", notes = "基于租户查询报表的树状结构")
    @PostMapping("/getCategoryTree")
    public RetResult<List<AnalyseCategoryTree>> getCategoryTree(@RequestBody @Validated RetRequest<AnalyseCategoryReq> request) {
        return RetResponse.makeOKRsp(biUiAnalyseCategoryService.getTree(request));
    }

    @ApiOperation(value = "预定义报表", notes = "预定义报表")
    @PostMapping("/getDefaultCategoryTree")
    public RetResult<List<AnalyseCategoryTree>> getDefaultCategoryTree(@RequestBody @Validated RetRequest<AnalyseCategoryReq> request) {
        request.getData().setType(AnalyseConstants.CATEGORY_TYPE_PRE_DEFINED);
        List<AnalyseCategoryTree> tree = biUiAnalyseCategoryService.getTree(request);
        if (!tree.isEmpty()) {
            return RetResponse.makeOKRsp(tree.get(0).getChildren());
        }
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "我的分析", notes = "我的分析")
    @PostMapping("/getCustomerCategoryTree")
    public RetResult<List<AnalyseCategoryTree>> getCustomerCategoryTree(@RequestBody @Validated RetRequest<AnalyseCategoryReq> request) {
        request.getData().setType(AnalyseConstants.CATEGORY_TYPE_CUSTOMER);
        List<AnalyseCategoryTree> tree = biUiAnalyseCategoryService.getTree(request);
        if (!tree.isEmpty()) {
            return RetResponse.makeOKRsp(tree.get(0).getChildren());
        }
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "基于租户获取页面列表", notes = "基于租户获取页面列表")
    @PostMapping("/getAnalyseCategorys")
    public RetResult<PageResult<List<BiUiAnalyseCategory>>> getAnalyseCategoryList(@RequestBody @Validated RetRequest<AnalyseCategoryReq> request) {
        return RetResponse.makeOKRsp(biUiAnalyseCategoryService.getAnalyseCategoryList(request));
    }

    @ApiOperation(value = "查看单个页面详情", notes = "查看单个页面详情")
    @PostMapping("/getAnalyseCategory")
    public RetResult<BiUiAnalyseCategory> getAnalyseCategory(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(biUiAnalyseCategoryService.getAnalyseCategory(request.getData()));
    }

    @ApiOperation(value = "新增文件夹", notes = "新增文件夹")
    @PostMapping("/createAnalyseCategory")
    public RetResult<BiUiAnalyseCategory> createAnalyseCategory(@RequestBody @Validated RetRequest<CreateAnalyseCategoryDto> request) {
        return RetResponse.makeOKRsp(biUiAnalyseCategoryService.createAnalyseCategory(request.getData()));
    }

    @ApiOperation(value = "删除文件夹", notes = "删除文件夹")
    @PostMapping("/delAnalyseCategory")
    public RetResult<Void> delAnalyseCategory(@RequestBody @Validated RetRequest<String> request) {
        biUiAnalyseCategoryService.delAnalyseCategory(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "批量删除文件夹", notes = "批量删除文件夹")
    @PostMapping("/batchDelAnalyseCategories")
    public RetResult<Void> batchDelAnalyseCategories(@RequestBody @Validated RetRequest<BatchAnalyseCategoryDelReq> request) throws Exception {
        biUiAnalyseCategoryService.batchDelAnalyseCategories(request.getData());
        return RetResponse.makeOKRsp();
    }


    @ApiOperation(value = "修改文件夹", notes = "修改文件夹")
    @PostMapping("/updateAnalyseCategory")
    public RetResult<BiUiAnalyseCategory> updateAnalyseCategory(@RequestBody @Validated RetRequest<UpdateAnalyseCategoryDto> request) throws Exception {
        return RetResponse.makeOKRsp(biUiAnalyseCategoryService.updateAnalyseCategory(request.getData()));
    }

    @ApiOperation(value = "初始化租户目录", notes = "初始化租户目录")
    @PostMapping("/initTenantAnalyse")
    public RetResult<Void> initTenantAnalyse(@RequestBody @Validated RetRequest<InitTenantReq> request) throws Exception {
        biUiAnalyseCategoryService.initTenantAnalyse(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "报表和dashboard查询", notes = "报表和dashboard查询")
    @PostMapping("/getChildAnalysePageList")
    public RetResult<List<BiUiAnalysePage>> getChildAnalysePageList(@RequestBody @Validated RetRequest<AnalysePageReq> request) throws Exception {
        return RetResponse.makeOKRsp(biUiAnalyseCategoryService.getChildAnalysePageReq(request.getData()));
    }
}
