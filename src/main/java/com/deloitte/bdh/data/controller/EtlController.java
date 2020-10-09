package com.deloitte.bdh.data.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.integration.EtlService;
import com.deloitte.bdh.data.model.BiConnections;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.model.request.CreateConnectionsDto;
import com.deloitte.bdh.data.model.request.CreateOutProcessorsDto;
import com.deloitte.bdh.data.model.request.JoinResourceDto;
import com.deloitte.bdh.data.model.resp.EtlProcessorsResp;
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


    @ApiOperation(value = "引入数据源", notes = "引入数据源")
    @PostMapping("/joinResource")
    public RetResult<BiProcessors> joinResource(@RequestBody @Validated RetRequest<JoinResourceDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.joinResource(request.getData()));
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


    @ApiOperation(value = "输出组件", notes = "输出组件")
    @PostMapping("/outProcessors")
    public RetResult<BiProcessors> outProcessors(@RequestBody @Validated RetRequest<CreateOutProcessorsDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.outProcessors(request.getData()));
    }

//
//    @ApiOperation(value = "启动", notes = "操作")
//    @PostMapping("/getProcessor")
//    public RetResult<BiEtlProcessor> getProcessor(@RequestBody @Validated RetRequest<String> request) {
//        return RetResponse.makeOKRsp(biEtlProcessorService.getProcessor(request.getData()));
//    }

}
