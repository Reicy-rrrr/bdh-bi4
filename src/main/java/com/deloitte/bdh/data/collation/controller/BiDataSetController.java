package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.model.request.CreateDataSetDto;
import com.deloitte.bdh.data.collation.model.request.CreateDataSetFileDto;
import com.deloitte.bdh.data.collation.model.request.DataSetReNameDto;
import com.deloitte.bdh.data.collation.model.request.GetDataSetPageDto;
import com.deloitte.bdh.data.collation.model.resp.DataSetResp;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.github.pagehelper.PageHelper;
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
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-12-10
 */
@Api(tags = "数据整理-数据集")
@RestController
@RequestMapping("/biDataSet")
public class BiDataSetController {

    @Resource
    private BiDataSetService dataSetService;

    @ApiOperation(value = "获取数据集文件夹", notes = "获取数据集文件夹")
    @PostMapping("/getFiles")
    public RetResult<List<BiDataSet>> getFiles(@RequestBody @Validated RetRequest request) {
        return RetResponse.makeOKRsp(dataSetService.getFiles());
    }

    @ApiOperation(value = "基于文件夹户获取数据集列表", notes = "基于文件夹户获取数据集列表")
    @PostMapping("/getDataSetPage")
    public RetResult<PageResult<List<DataSetResp>>> getDataSetPage(@RequestBody @Validated RetRequest<GetDataSetPageDto> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        return RetResponse.makeOKRsp(dataSetService.getDataSetPage(request.getData()));
    }

    @ApiOperation(value = "数据集的表重命名", notes = "数据集的表重命名")
    @PostMapping("/reName")
    public RetResult<Void> reName(@RequestBody @Validated RetRequest<DataSetReNameDto> request) {
        dataSetService.reName(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "创建数据集文件夹", notes = "创建数据集文件夹")
    @PostMapping("/file/create")
    public RetResult<Void> fileCreate(@RequestBody @Validated RetRequest<CreateDataSetFileDto> request) {
        dataSetService.fileCreate(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "创建数据集", notes = "创建数据集")
    @PostMapping("/create")
    public RetResult<Void> create(@RequestBody @Validated RetRequest<CreateDataSetDto> request) {
        dataSetService.create(request.getData());
        return RetResponse.makeOKRsp();
    }
}