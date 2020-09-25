package com.deloitte.bdh.data.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.integration.EtlService;
import com.deloitte.bdh.data.model.request.JoinResourceDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
    public RetResult<Void> joinResource(@RequestBody @Validated RetRequest<JoinResourceDto> request) throws Exception {
        etlService.joinResource(request.getData());
        return RetResponse.makeOKRsp();
    }

//    @ApiOperation(value = "配置、修改已引入的数据源", notes = "配置、修改已引入的数据源")
//    @PostMapping("/Resource")
//    public RetResult<Void> updJoinedResource(@RequestBody @Validated RetRequest<JoinResourceDto> request) throws Exception {
//        etlService.joinResource(request.getData());
//        return RetResponse.makeOKRsp();
//    }

//    @ApiOperation(value = "操作", notes = "操作")
//    @PostMapping("/getProcessor")
//    public RetResult<BiEtlProcessor> getProcessor(@RequestBody @Validated RetRequest<String> request) {
//        return RetResponse.makeOKRsp(biEtlProcessorService.getProcessor(request.getData()));
//    }
//
//    @ApiOperation(value = "启动", notes = "操作")
//    @PostMapping("/getProcessor")
//    public RetResult<BiEtlProcessor> getProcessor(@RequestBody @Validated RetRequest<String> request) {
//        return RetResponse.makeOKRsp(biEtlProcessorService.getProcessor(request.getData()));
//    }


}
