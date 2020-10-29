package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
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
 * @since 2020-10-26
 */
@RestController
@RequestMapping("/biEtlSyncPlan")
public class BiEtlSyncPlanController {
    @Autowired
    private BiEtlSyncPlanService planService;

    @ApiOperation(value = "调度处理", notes = "数据同步")
    @PostMapping("/process")
    public RetResult<Void> process(@RequestBody @Validated RetRequest<Void> request) throws Exception {
        planService.process();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "调度处理", notes = "数据整理")
    @PostMapping("/etl")
    public RetResult<Void> etl(@RequestBody @Validated RetRequest<Void> request) throws Exception {
        planService.etl();
        return RetResponse.makeOKRsp();
    }
}
