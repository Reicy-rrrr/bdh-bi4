package com.deloitte.bdh.data.analyse.controller;

import com.deloitte.bdh.common.annotation.NoInterceptor;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.request.GetAnalyseDataTreeDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFolderTree;
import com.deloitte.bdh.data.analyse.service.AnalyseModelService;
import com.deloitte.bdh.data.collation.database.po.TableInfo;
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
@Api(tags = "分析管理-数据模型")
@RestController
@RequestMapping("/ui/analyse/model")
public class AnalyseModelController {
    @Resource
    AnalyseModelService analyseModelService;

    @ApiOperation(value = "获取所有表", notes = "获取所有表")
    @PostMapping("/getAllTable")
    public RetResult<List<TableInfo>> getAllTable(@RequestBody @Validated RetRequest<Void> request) {
        return RetResponse.makeOKRsp(analyseModelService.getAllTable());
    }

    @ApiOperation(value = "获取数据树状结构", notes = "获取数据树状结构")
    @PostMapping("/getDataTree")
    public RetResult<List<AnalyseFolderTree>> getDataTree(@RequestBody @Validated RetRequest<GetAnalyseDataTreeDto> request) throws Exception {
        return RetResponse.makeOKRsp(analyseModelService.getDataTree(request));
    }

    @ApiOperation(value = "保存数据树状结构", notes = "保存数据树状结构")
    @PostMapping("/saveDataTree")
    public RetResult<Void> saveDataTree(@RequestBody @Validated RetRequest<List<AnalyseFolderTree>> request) {
        analyseModelService.saveDataTree(request);
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "获取组件数据", notes = "获取组件数据")
    @PostMapping("/getComponentData")
    public RetResult<BaseComponentDataResponse> getComponentData(@RequestBody @Validated RetRequest<ComponentDataRequest> request) throws Exception {
        return RetResponse.makeOKRsp(analyseModelService.getComponentData(request.getData()));
    }

}
