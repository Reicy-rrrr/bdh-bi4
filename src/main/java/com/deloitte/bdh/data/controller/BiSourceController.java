package com.deloitte.bdh.data.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.model.request.*;
import com.deloitte.bdh.data.model.resp.FilePreReadResult;
import com.deloitte.bdh.data.service.BiEtlDatabaseInfService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-09-23
 */
@RestController
@RequestMapping("/bi/etl")
public class BiSourceController {
    @Autowired
    private BiEtlDatabaseInfService biEtlDatabaseInfService;

    @ApiOperation(value = "基于租户获取数据源列表", notes = "基于租户获取数据源列表")
    @PostMapping("/getResources")
    public RetResult<PageResult> getResources(@RequestBody @Validated RetRequest<GetResourcesDto> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.getResources(request.getData()));
    }

    @ApiOperation(value = "查看单个数据源详情", notes = "查看单个数据源详情")
    @PostMapping("/getResource")
    public RetResult<BiEtlDatabaseInf> getResource(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.getResource(request.getData()));
    }

    @ApiOperation(value = "新增数据源", notes = "新增数据源")
    @PostMapping("/createResource")
    public RetResult<BiEtlDatabaseInf> createResource(@RequestBody @Validated RetRequest<CreateResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.createResource(request.getData()));
    }

    @ApiOperation(value = "上传文件数据源", notes = "上传文件数据源")
    @PostMapping("/uploadFileResource")
    public RetResult<FilePreReadResult> uploadFileResource(@ModelAttribute FileResourcesUploadDto fileResourcesUploadDto) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.uploadFileResource(fileResourcesUploadDto));
    }

    @ApiOperation(value = "追加上传文件数据源", notes = "追加上传文件数据源")
    @PostMapping("/addUploadFileResource")
    public RetResult<FilePreReadResult> addUploadFileResource(@ModelAttribute FileResourcesAddUploadDto addUploadDto) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.addUploadFileResource(addUploadDto));
    }

    @ApiOperation(value = "保存文件数据源", notes = "保存文件数据源")
    @PostMapping("/saveFileResource")
    public RetResult<BiEtlDatabaseInf> saveFileResource(@RequestBody @Validated RetRequest<FileResourcesSaveDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.saveFileResource(request.getData()));
    }

    @ApiOperation(value = "启用/禁用数据源", notes = "启用/禁用数据源")
    @PostMapping("/runResource")
    public RetResult<BiEtlDatabaseInf> runResource(@RequestBody @Validated RetRequest<RunResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.runResource(request.getData()));
    }

    @ApiOperation(value = "删除数据源", notes = "删除数据源")
    @PostMapping("/delResource")
    public RetResult<Void> delResource(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biEtlDatabaseInfService.delResource(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改数据源", notes = "修改数据源")
    @PostMapping("/updateResource")
    public RetResult<BiEtlDatabaseInf> updateResource(@RequestBody @Validated RetRequest<UpdateResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.updateResource(request.getData()));
    }

}
