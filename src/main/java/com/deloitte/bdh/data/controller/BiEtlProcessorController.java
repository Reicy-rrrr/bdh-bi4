package com.deloitte.bdh.data.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.request.CreateProcessorDto;
import com.deloitte.bdh.data.model.request.UpdateModelDto;
import com.deloitte.bdh.data.model.resp.ProcessorResp;
import com.deloitte.bdh.data.service.BiEtlProcessorService;
import io.swagger.annotations.ApiOperation;
import javafx.util.Pair;
import org.springframework.beans.BeanUtils;
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
@RequestMapping("/biEtlProcessor")
public class BiEtlProcessorController {
    @Autowired
    private BiEtlProcessorService biEtlProcessorService;

    @ApiOperation(value = "查看单个 PROCESSOR", notes = "查看单个 PROCESSOR 详情")
    @PostMapping("/getProcessor")
    public RetResult<ProcessorResp> getProcessor(@RequestBody @Validated RetRequest<String> request) {
        ProcessorResp processors = new ProcessorResp();
        Pair<BiEtlProcessor, List<BiEtlParams>> result = biEtlProcessorService.getProcessor(request.getData());
        BeanUtils.copyProperties(result.getKey(), processors);
        processors.setList(result.getValue());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "新增 Processor", notes = "新增 Processor")
    @PostMapping("/createProcessor")
    public RetResult<BiEtlProcessor> createProcessor(@RequestBody @Validated RetRequest<CreateProcessorDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlProcessorService.createProcessor(request.getData()));
    }
//
//    @ApiOperation(value = "启用/停用 单个Processor", notes = "启用/停用 Processor")
//    @PostMapping("/runProcessor")
//    public RetResult<BiEtlProcessor> runProcessor(@RequestBody @Validated RetRequest<RunModelDto> request) throws Exception {
//        return RetResponse.makeOKRsp(biEtlProcessorService.runProcessor(request.getData()));
//    }
//
//    @ApiOperation(value = "删除 Processor", notes = "删除 Processor")
//    @PostMapping("/delProcessor")
//    public RetResult<Void> delProcessor(@RequestBody @Validated RetRequest<String> request) throws Exception {
//        biEtlProcessorService.delProcessor(request.getData());
//        return RetResponse.makeOKRsp();
//    }

    @ApiOperation(value = "修改 Processor", notes = "修改 Processor")
    @PostMapping("/updateProcessor")
    public RetResult<BiEtlProcessor> updateProcessor(@RequestBody @Validated RetRequest<UpdateModelDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlProcessorService.updateProcessor(request.getData()));
    }
}
