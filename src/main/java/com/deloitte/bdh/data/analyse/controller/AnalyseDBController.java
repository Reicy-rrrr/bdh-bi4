package com.deloitte.bdh.data.analyse.controller;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.analyse.model.request.GetAnalyseDataTreeDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFolderTree;
import com.deloitte.bdh.data.analyse.service.AnalyseDBService;
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
public class AnalyseDBController {
    @Resource
    AnalyseDBService analyseDBService;

    @ApiOperation(value = "获取所有表", notes = "获取所有表")
    @PostMapping("/getAllTable")
    public RetResult<List<String>> getAllTable(@RequestBody @Validated RetRequest<Void> request) {
        return RetResponse.makeOKRsp(analyseDBService.getAllTable());
    }

    @ApiOperation(value = "获取数据树状结构", notes = "获取数据树状结构")
    @PostMapping("/getDataTree")
    public RetResult<List<AnalyseFolderTree>> getDataTree(@RequestBody @Validated RetRequest<GetAnalyseDataTreeDto> request) {
        return RetResponse.makeOKRsp(analyseDBService.getDataTree(request));
    }

    @ApiOperation(value = "保存数据树状结构", notes = "保存数据树状结构")
    @PostMapping("/saveDataTree")
    public RetResult<Void> saveDataTree(@RequestBody @Validated RetRequest<List<AnalyseFolderTree>> request) {
        analyseDBService.saveDataTree(request);
        return RetResponse.makeOKRsp();
    }

}
