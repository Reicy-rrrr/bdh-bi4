package com.deloitte.bdh.data.analyse.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFolderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-21
 */
@RestController
@RequestMapping("/ui/analyse/folder")
public class AnalyseModelFolderController {
    @Resource
    AnalyseModelFolderService analyseModelFolderService;

    @ApiOperation(value = "查看单个文件夹详情", notes = "查看单个文件夹详情")
    @PostMapping("/getResource")
    public RetResult<BiUiModelFolder> getResource(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(analyseModelFolderService.getResource(request.getData()));
    }

    @ApiOperation(value = "新增文件夹", notes = "新增文件夹")
    @PostMapping("/createResource")
    public RetResult<BiUiModelFolder> createResource(@RequestBody @Validated RetRequest<CreateResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(analyseModelFolderService.createResource(request.getData()));
    }

    @ApiOperation(value = "删除文件夹", notes = "删除文件夹")
    @PostMapping("/delResource")
    public RetResult<Void> delResource(@RequestBody @Validated RetRequest<String> request) throws Exception {
        analyseModelFolderService.delResource(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改文件夹", notes = "修改文件夹")
    @PostMapping("/updateResource")
    public RetResult<BiUiModelFolder> updateResource(@RequestBody @Validated RetRequest<UpdateResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(analyseModelFolderService.updateResource(request.getData()));
    }
}
