package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.util.GetIpAndPortUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
        return RetResponse.makeOKRsp(nifiProcessService.createProcessGroup(request.getData(), (String) request.getData().get("id")));
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
        return RetResponse.makeOKRsp(nifiProcessService.updateProcessor(null,request.getData()));
    }

    @ApiOperation(value = "createConnections", notes = "createConnections")
    @PostMapping("/createConnections")
    public RetResult<Object> createConnections(@RequestBody @Validated RetRequest<Map<String, Object>> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.createConnections(request.getData(), (String) request.getData().get("id")));
    }

    @ApiOperation(value = "dropConnections", notes = "dropConnections")
    @PostMapping("/dropConnections")
    public RetResult<Object> dropConnections(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.dropConnections(request.getData()));
    }

    @ApiOperation(value = "delConnections", notes = "delConnections")
    @PostMapping("/delConnections")
    public RetResult<Object> delConnectionsa(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.delConnections(request.getData()));
    }

    @ApiOperation(value = "runState", notes = "runState")
    @PostMapping("/runState")
    public RetResult<Object> runState(@RequestBody @Validated RetRequest<Map<String, Object>> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.runState((String) request.getData().get("id"),
                (String) request.getData().get("state"), (Boolean) request.getData().get("group")));
    }

    @ApiOperation(value = "previewConnction", notes = "previewConnction")
    @PostMapping("/previewConnction")
    public RetResult<Object> previewConnction(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.preview(request.getData()));
    }


    @ApiOperation(value = "terminate", notes = "terminate")
    @PostMapping("/terminate")
    public RetResult<Object> terminate(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.terminate(request.getData()));
    }

    @ApiOperation(value = "clearRequest", notes = "clearRequest")
    @PostMapping("/clearRequest")
    public RetResult<Object> clearRequest(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.clearRequest(request.getData()));
    }

    @ApiOperation(value = "getMax", notes = "getMax")
    @PostMapping("/getMax")
    public RetResult<Object> getMax(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.getMax(request.getData()));
    }

    @ApiOperation(value = "getProcessGroupFull", notes = "getProcessGroupFull")
    @PostMapping("/getProcessGroupFull")
    public RetResult<Object> getProcessGroupFull(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(nifiProcessService.getProcessGroupFull(request.getData()));
    }

    @ApiOperation(value = "getIp", notes = "getIp")
    @PostMapping("/getIp")
    public RetResult<String> getIp() throws Exception {
        return RetResponse.makeOKRsp(GetIpAndPortUtil.getIpAndPort());
    }

    @ApiOperation(value = "getTime", notes = "getTime")
    @PostMapping("/getTime")
    public RetResult<String> getTime() throws Exception {
        return RetResponse.makeOKRsp(JsonUtil.obj2String(LocalDateTime.now()));
    }
}
