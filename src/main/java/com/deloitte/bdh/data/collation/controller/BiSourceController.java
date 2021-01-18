package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-09-23
 */
@Api(tags = "数据整理-数据源相关")
@RestController
@RequestMapping("/bi/etl")
public class BiSourceController {
    @Autowired
    private BiEtlDatabaseInfService biEtlDatabaseInfService;

    @ApiOperation(value = "基于租户获取数据源列表", notes = "基于租户获取数据源列表")
    @PostMapping("/getResources")
    public RetResult<PageResult<BiEtlDatabaseInf>> getResources(@RequestBody @Validated RetRequest<GetResourcesDto> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.getResources(request.getData()));
    }

    @ApiOperation(value = "查看单个数据源详情", notes = "查看单个数据源详情")
    @PostMapping("/getResource")
    public RetResult<BiEtlDatabaseInf> getResource(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.getById(request.getData()));
    }

    @ApiOperation(value = "新增数据源", notes = "新增数据源")
    @PostMapping("/createResource")
    public RetResult<BiEtlDatabaseInf> createResource(@RequestBody @Validated RetRequest<CreateResourcesDto> request) {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.createResource(request.getData()));
    }

    @ApiOperation(value = "创建文件型数据源", notes = "创建文件型数据源")
    @PostMapping("/createFileResource")
    public RetResult<BiEtlDatabaseInf> createFileResource(@RequestBody @Validated RetRequest<CreateFileResourcesDto> request) {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.createFileResource(request.getData()));
    }

    @ApiOperation(value = "追加文件型数据源", notes = "追加文件型数据源")
    @PostMapping("/appendFileResource")
    public RetResult<BiEtlDatabaseInf> appendFileResource(@RequestBody @Validated RetRequest<AppendFileResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.appendFileResource(request.getData()));
    }

    @ApiOperation(value = "重置文件型数据源", notes = "重置文件型数据源")
    @PostMapping("/resetFileResource")
    public RetResult<BiEtlDatabaseInf> resetFileResource(@RequestBody @Validated RetRequest<ResetFileResourcesDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.resetFileResource(request.getData()));
    }

    @ApiOperation(value = "启用/禁用数据源", notes = "启用/禁用数据源")
    @PostMapping("/runResource")
    public RetResult<BiEtlDatabaseInf> runResource(@RequestBody @Validated RetRequest<RunResourcesDto> request) throws Exception {
        RunResourcesDto dto = request.getData();
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.runResource(dto.getId(), dto.getEffect()));
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

    @ApiOperation(value = "测试连接", notes = "测试连接")
    @PostMapping("/testConnection")
    public RetResult<String> testConnection(@RequestBody @Validated RetRequest<TestConnectionDto> request) {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.testConnection(request.getData()));
    }

    @ApiOperation(value = "获取数据源下所有表集合", notes = "获取数据源下所有表集合")
    @PostMapping("/getTables")
    public RetResult<List> getTables(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.getTables(request.getData()));
    }

    //    @ApiOperation(value = "获取表所有字段集合", notes = "获取表所有字段集合")
//    @PostMapping("/getFields")
    public RetResult<List> getFields(@RequestBody @Validated RetRequest<GetFieldsDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.getFields(request.getData().getId(), request.getData().getTableName()));
    }

    @ApiOperation(value = "查询表字段字段信息", notes = "查询表字段字段信息")
    @PostMapping("/getTableSchema")
    public RetResult<TableSchema> getTableSchema(@RequestBody @Validated RetRequest<GetTableSchemaDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.getTableSchema(request.getData()));
    }

    @ApiOperation(value = "查询表字段数据列表（分页）", notes = "查询表字段数据列表（分页）")
    @PostMapping("/getTableData")
    public RetResult<TableData> getTableData(@RequestBody @Validated RetRequest<GetTableDataDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlDatabaseInfService.getTableData(request.getData()));
    }
}
