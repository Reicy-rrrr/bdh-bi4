package com.deloitte.bdh.data.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.integration.NifiProcessService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/nifi")
public class NifiController {

    @Autowired
    private NifiProcessService nifiProcessService;


    @ApiOperation(value = "getToken", notes = "getToken")
    @GetMapping("/getToken")
    public RetResult<String> getToken() throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.getToken());
    }

    @ApiOperation(value = "cluster", notes = " cluster")
    @GetMapping("/cluster")
    public RetResult<Object> cluster() throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.cluster());
    }

    @ApiOperation(value = "getRootGroupInfo", notes = "getRootGroupInfo")
    @GetMapping("/getRootGroupInfo")
    public RetResult<Object> getRootGroupInfo() throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.getRootGroupInfo());
    }

    @ApiOperation(value = "createProcessGroup", notes = "createProcessGroup")
    @PostMapping("/createProcessGroup")
    public RetResult<Object> createProcessGroup(@RequestBody @Validated RetRequest<Map<String, Object>> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.createProcessGroup(request.getData()));
    }

    @ApiOperation(value = "getProcessGroup", notes = "getProcessGroup")
    @PostMapping("/getProcessGroup")
    public RetResult<Object> getProcessGroup(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.getProcessGroup(request.getData()));
    }

    @ApiOperation(value = "createControllerService", notes = "createControllerService")
    @PostMapping("/createControllerService")
    public RetResult<Object> createControllerService(@RequestBody @Validated RetRequest<Map<String, Object>> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.createControllerService(request.getData()));
    }

    @ApiOperation(value = "getControllerService", notes = "getControllerService")
    @PostMapping("/getControllerService")
    public RetResult<Object> getControllerService(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.getControllerService(request.getData()));
    }

    @ApiOperation(value = "createProcessor", notes = "createProcessor")
    @PostMapping("/createProcessor")
    public RetResult<Object> createProcessor(@RequestBody @Validated RetRequest<Map<String, Object>> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.createProcessor(request.getData()));
    }

    @ApiOperation(value = "getProcessor", notes = "getProcessor")
    @PostMapping("/getProcessor")
    public RetResult<Object> getProcesstor(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.getProcessor(request.getData()));
    }

    @ApiOperation(value = "updateProcessor", notes = "updateProcessor")
    @PostMapping("/updateProcessor")
    public RetResult<Object> updateProcessor(@RequestBody @Validated RetRequest<Map<String, Object>> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.updateProcessor(request.getData()));
    }
}
