package com.deloitte.bdh.data.collation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.deloitte.bdh.data.collation.service.BiProcessorsService;
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
 * @since 2020-09-27
 */
@RestController
@RequestMapping("/biProcessors")
public class BiProcessorsController {

    @Autowired
    private BiProcessorsService processorsService;


    @ApiOperation(value = "查看单个 PROCESSORS", notes = "根据编码 查看单个PROCESSORS")
    @PostMapping("/getProcessors")
    public RetResult<BiProcessors> getProcessors(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getCode, request.getData())));
    }

    @ApiOperation(value = "查看模板下面已引用的 PROCESSORS 集合", notes = "查看模板下面已引用的 PROCESSORS 集合")
    @PostMapping("/getProcessorsList")
    public RetResult<List<BiProcessors>> getProcessorsList(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(processorsService.list(
                new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getRelModelCode, request.getData()))
        );
    }

}
