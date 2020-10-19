package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.integration.EtlService;
import com.deloitte.bdh.data.collation.model.BiConnections;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.EtlProcessorsResp;
import com.deloitte.bdh.data.collation.model.resp.EtlRunModelResp;
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
 * @since 2020-09-25
 */
@RestController
@RequestMapping("/bi/etl")
public class EtlController {

    @Autowired
    private EtlService etlService;


    @ApiOperation(value = "引入数据源组件", notes = "引入数据源组件")
    @PostMapping("/joinResource")
    public RetResult<BiProcessors> joinResource(@RequestBody @Validated RetRequest<JoinResourceDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.joinResource(request.getData()));
    }

    @ApiOperation(value = "输出组件", notes = "输出组件")
    @PostMapping("/outProcessors")
    public RetResult<BiProcessors> outProcessors(@RequestBody @Validated RetRequest<CreateOutProcessorsDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.outProcessors(request.getData()));
    }

    @ApiOperation(value = "移除组件", notes = "移除组件")
    @PostMapping("/removeProcessors")
    public RetResult<Void> removeProcessors(@RequestBody @Validated RetRequest<String> request) throws Exception {
        etlService.removeProcessors(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "关联组件", notes = "关联组件")
    @PostMapping("/connect")
    public RetResult<List<BiConnections>> connect(@RequestBody @Validated RetRequest<CreateConnectionsDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.connectProcessors(request.getData()));
    }

    @ApiOperation(value = "取消关联组件", notes = "取消关联组件")
    @PostMapping("/cancelConnect")
    public RetResult<List<BiConnections>> cancelConnect(@RequestBody @Validated RetRequest<String> request) throws Exception {
        etlService.cancelConnectProcessors(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "查看组件详情", notes = "查看组件详情(包含参数集合)")
    @PostMapping("/getProcessors")
    public RetResult<EtlProcessorsResp> getProcessors(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(etlService.getProcessors(request.getData()));
    }

    @ApiOperation(value = "基于模板编码查看组件列表", notes = "基于模板编码查看组件列表")
    @PostMapping("/getProcessorsList")
    public RetResult<List<EtlProcessorsResp>> getProcessorsList(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(etlService.getProcessorsList(request.getData()));
    }

    @ApiOperation(value = "运行/停止 model", notes = "运行/停止 model")
    @PostMapping("/runModel")
    public RetResult<EtlRunModelResp> runModel(@RequestBody @Validated RetRequest<RunModelDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.runModel(request.getData()));
    }

    @ApiOperation(value = "预览 ", notes = "预览 ")
    @PostMapping("/preview")
    public RetResult<String> preview(@RequestBody @Validated RetRequest<PreviewDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.preview(request.getData()));
    }

}