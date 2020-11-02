package com.deloitte.bdh.data.analyse.controller;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.analyse.model.request.DBTableColumnReq;
import com.deloitte.bdh.data.analyse.model.request.DBTableReq;
import com.deloitte.bdh.data.analyse.service.BiUiDBService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFieldService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFolderService;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ui/analyse/db")
public class BiUiDBController {
    @Resource
    BiUiDBService biUiDBService;
    @Resource
    BiUiModelFolderService biUiModelFolderService;
    @Resource
    BiUiModelFieldService biUiModelFieldService;

    @ApiOperation(value = "获取所有表", notes = "获取所有表")
    @PostMapping("/getAllDataSource")
    public RetResult<List<String>> getAllDataSource() {
        return RetResponse.makeOKRsp(biUiDBService.getAllDataSource());
    }

    @ApiOperation(value = "获取所有表", notes = "获取所有表")
    @PostMapping("/getAllTable")
    public RetResult<List<String>> getAllTable(@RequestBody @Validated RetRequest<DBTableReq> request) {
        return RetResponse.makeOKRsp(biUiDBService.getAllTable());
    }

    @ApiOperation(value = "获取所有表列", notes = "获取所有表列")
    @PostMapping("/getAllColumns")
    public RetResult<List<TableColumn>> getAllColumns(@RequestBody @Validated RetRequest<DBTableColumnReq> request) {
        DBTableColumnReq req = request.getData();
        return RetResponse.makeOKRsp(biUiDBService.getAllColumns(req.getTableName(), req.getTenantId()));
    }

}
