package com.deloitte.bdh.data.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.model.resp.Processors;
import com.deloitte.bdh.data.service.BiProcessorsService;
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
 * @since 2020-09-27
 */
@RestController
@RequestMapping("/biProcessors")
public class BiProcessorsController {

    @Autowired
    private BiProcessorsService processorsService;


    @ApiOperation(value = "查看单个 PROCESSORS", notes = "查看单个 PROCESSORS 详情")
    @PostMapping("/getProcessors")
    public RetResult<Processors> getProcessors(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(processorsService.getProcessors(request.getData()));
    }

}
