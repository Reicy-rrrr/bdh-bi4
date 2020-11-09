package com.deloitte.bdh.data.analyse.controller;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelFieldTree;
import com.deloitte.bdh.data.analyse.model.request.DBTableColumnReq;
import com.deloitte.bdh.data.analyse.model.request.DBTableReq;
import com.deloitte.bdh.data.analyse.model.request.GetDataTreeRequest;
import com.deloitte.bdh.data.analyse.model.request.SaveDataTreeRequest;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFolderTree;
import com.deloitte.bdh.data.analyse.service.BiUiDBService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFieldService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFolderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
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
    public RetResult<Collection<DataModelFieldTree>> getAllColumns(@RequestBody @Validated RetRequest<DBTableColumnReq> request) {
        DBTableColumnReq req = request.getData();
        return RetResponse.makeOKRsp(biUiDBService.getAllColumns(req.getTableName(), req.getTenantId()));
    }

    @ApiOperation(value = "获取数据树状结构", notes = "获取数据树状结构")
    @PostMapping("/getDataTree")
    public RetResult<List<AnalyseFolderTree>> getDataTree(@RequestBody @Validated RetRequest<GetDataTreeRequest> request) {
        return RetResponse.makeOKRsp(biUiDBService.getDataTree(request));
    }

    @ApiOperation(value = "保存数据树状结构", notes = "保存数据树状结构")
    @PostMapping("/saveDataTree")
    public RetResult<Void> saveDataTree(@RequestBody @Validated RetRequest<List<AnalyseFolderTree>> request) {
        biUiDBService.saveDataTree(request);
        return RetResponse.makeOKRsp();
    }

}
