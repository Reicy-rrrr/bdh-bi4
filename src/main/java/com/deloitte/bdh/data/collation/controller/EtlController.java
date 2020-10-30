package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.integration.EtlService;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.request.*;
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


    @ApiOperation(value = "引入数据源组件", notes = "引入数据源组件")
    @PostMapping("/joinResource")
    public RetResult<BiComponent> joinResource(@RequestBody @Validated RetRequest<JoinComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.joinResource(request.getData()));
    }

    @ApiOperation(value = "输出组件", notes = "输出组件")
    @PostMapping("/out")
    public RetResult<BiComponent> out(@RequestBody @Validated RetRequest<OutComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.out(request.getData()));
    }

}
