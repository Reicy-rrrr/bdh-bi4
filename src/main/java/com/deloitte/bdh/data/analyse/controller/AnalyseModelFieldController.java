package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFieldService;
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
 * @since 2020-10-21
 */
@RestController
@RequestMapping("/ui/analyse/field")
public class AnalyseModelFieldController {
    @Resource
    AnalyseModelFieldService analyseModelFieldService;

    @ApiOperation(value = "查看单个字段详情", notes = "查看单个字段详情")
    @PostMapping("/getResource")
    public RetResult<BiUiModelField> getResource(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(analyseModelFieldService.getResource(request.getData()));
    }

    @ApiOperation(value = "新增字段", notes = "新增字段")
    @PostMapping("/createResource")
    public RetResult<BiUiModelField> createResource(@RequestBody @Validated RetRequest<CreateResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(analyseModelFieldService.createResource(request.getData()));
    }

    @ApiOperation(value = "删除字段", notes = "删除字段")
    @PostMapping("/delResource")
    public RetResult<Void> delResource(@RequestBody @Validated RetRequest<String> request) throws Exception {
        analyseModelFieldService.delResource(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改字段", notes = "修改字段")
    @PostMapping("/updateResource")
    public RetResult<BiUiModelField> updateResource(@RequestBody @Validated RetRequest<UpdateResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(analyseModelFieldService.updateResource(request.getData()));
    }
}