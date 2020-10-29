package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.util.ServletUtil;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
    @GetMapping("/process")
    public RetResult<Void> process(String tenantId) throws Exception {
        ServletUtil.rSetHeader(tenantId);
        planService.process();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "调度处理", notes = "数据整理")
    @GetMapping("/etl")
    public RetResult<Void> etl(String tenantId) throws Exception {
        ServletUtil.rSetHeader(tenantId);
        planService.etl();
        return RetResponse.makeOKRsp();
    }
}
