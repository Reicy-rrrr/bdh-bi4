package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.properties.BiProperties;
import com.deloitte.bdh.common.util.GetIpAndPortUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.model.request.KafkaEmailDto;
import com.deloitte.bdh.data.collation.evm.service.EvmServiceImpl;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.nifi.template.servie.Transfer;
import com.deloitte.bdh.data.collation.service.BiProcessorsService;
import com.deloitte.bdh.data.collation.service.Producter;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/nifi")
public class NifiController {

    @Autowired
    private NifiProcessService nifiProcessService;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private Transfer transfer;

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
        return RetResponse.makeOKRsp(nifiProcessService.updateProcessor(null, request.getData()));
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

    @ApiOperation(value = "run", notes = "run")
    @PostMapping("/run")
    public RetResult<Void> run(@RequestBody @Validated RetRequest<String> request) throws Exception {
        transfer.run(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "stop", notes = "stop")
    @PostMapping("/stop")
    public RetResult<Void> stop(@RequestBody @Validated RetRequest<String> request) throws Exception {
        transfer.stop(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "del", notes = "del")
    @PostMapping("/del")
    public RetResult<Void> del(@RequestBody @Validated RetRequest<String> request) throws Exception {
        transfer.del(request.getData());
        processorsService.removeById("999");
        return RetResponse.makeOKRsp();
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
    public RetResult<String> getTime(@RequestBody @Validated RetRequest<String> request) throws Exception {
        evmService.choose("ZCXLZTSPB");
        return RetResponse.makeOKRsp("ok");
    }
    
    
    @ApiOperation(value = "email", notes = "email")
    @PostMapping("/email")
    public RetResult<String> email(@RequestParam String email,
			@RequestParam(required = false) List<String> ccList,
			@RequestParam String subject,
			@RequestParam String templateCode,
			@RequestParam String userNameCn,
			@RequestParam String userNameEn,
			@RequestParam String contentCn,
			@RequestParam String contentEn,
			@RequestParam String tenantId,
			@RequestParam String operator,
			@RequestParam(required = false) MultipartFile attachmentFile) throws Exception {
    	KafkaEmailDto dto = new KafkaEmailDto();
    	dto.setEmail(email);
    	dto.setTemplate(templateCode);
    	dto.setSubject(subject);
    	
    	HashMap<String , Object> paramMap = new HashMap<>();
    	paramMap.put("userName", "peng");
    	paramMap.put("imgUrl", "https://bidev.tax.deloitte.com.cn/analyseManage/public/subscribe/pZH7s96GFjRoFTljWYUX9fAoo00oljkfOQZqlAeva7unoiUIu3O7o000obIztxuzktRl9IkzLGvu3i601XBIjloo00onr2EpJQO0O0OO0O0O");
    	paramMap.put("accessUrl", "https://bidev.tax.deloitte.com.cn/analyseManage/share/publicReport/9q9tZBWtPYDjM8d7s77V1VjtVBQpO6xo000o9Hu8l0CgOtbV45Q419HfQ52ePkWLeHDqWGbVNBo15EjeDSBECguOhwO0O0OO0O0O");
    	paramMap.put("userNameCn", userNameCn);
    	paramMap.put("userNameEn", userNameEn);
    	paramMap.put("contentCn", contentCn);
    	paramMap.put("contentEn", contentEn);
    	dto.setParamMap(paramMap);
        KafkaMessage message = new KafkaMessage(UUID.randomUUID().toString().replaceAll("-",""), dto, "email");
        message.setTenantId(tenantId);
        message.setOperator(operator);
        producter.sendEmail(message);
        return RetResponse.makeOKRsp("ok");
    }

    @Autowired
    private BiProperties biProperties;
    @Autowired
    private Producter producter;
    @Autowired
    private EvmServiceImpl evmService;
}
