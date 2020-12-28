package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.*;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryTree;
import com.deloitte.bdh.data.analyse.service.AnalyseCategoryService;
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
@Api(tags = "分析管理-文件夹")
@RestController
@RequestMapping("/ui/analyse/category")
public class AnalyseCategoryController {
    @Resource
    AnalyseCategoryService analyseCategoryService;

    @ApiOperation(value = "基于租户查询报表的树状结构", notes = "基于租户查询报表的树状结构")
    @PostMapping("/getCategoryTree")
    public RetResult<List<AnalyseCategoryTree>> getCategoryTree(@RequestBody @Validated RetRequest<GetAnalyseCategoryDto> request) {
        return RetResponse.makeOKRsp(analyseCategoryService.getTree(request));
    }

//    @ApiOperation(value = "我的分析", notes = "我的分析")
//    @PostMapping("/getCustomerCategoryTree")
//    public RetResult<List<AnalyseCategoryTree>> getCustomerCategoryTree(@RequestBody @Validated RetRequest<GetAnalyseCategoryDto> request) {
//        request.getData().setType(CategoryTypeEnum.CUSTOMER.getCode());
//        List<AnalyseCategoryTree> tree = analyseCategoryService.getTree(request);
//        return RetResponse.makeOKRsp();
//    }

    @ApiOperation(value = "新增文件夹", notes = "新增文件夹")
    @PostMapping("/createAnalyseCategory")
    public RetResult<AnalyseCategoryDto> createAnalyseCategory(@RequestBody @Validated RetRequest<CreateAnalyseCategoryDto> request) {
        return RetResponse.makeOKRsp(analyseCategoryService.createAnalyseCategory(request));
    }

    @ApiOperation(value = "删除文件夹", notes = "删除文件夹")
    @PostMapping("/delAnalyseCategory")
    public RetResult<Void> delAnalyseCategory(@RequestBody @Validated RetRequest<String> request) {
        analyseCategoryService.delAnalyseCategory(request);
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "批量删除文件夹", notes = "批量删除文件夹")
    @PostMapping("/batchDelAnalyseCategories")
    public RetResult<Void> batchDelAnalyseCategories(@RequestBody @Validated RetRequest<BatchDeleteAnalyseDto> request) {
        analyseCategoryService.batchDelAnalyseCategories(request);
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改文件夹", notes = "修改文件夹")
    @PostMapping("/updateAnalyseCategory")
    public RetResult<AnalyseCategoryDto> updateAnalyseCategory(@RequestBody @Validated RetRequest<UpdateAnalyseCategoryDto> request) {
        return RetResponse.makeOKRsp(analyseCategoryService.updateAnalyseCategory(request));
    }

}
