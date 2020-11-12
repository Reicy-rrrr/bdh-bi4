package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.annotation.NoLocal;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.integration.SyncService;
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
    private SyncService sync;

    @ApiOperation(value = "同步调度处理", notes = "数据同步")
    @GetMapping("/sync")
    public RetResult<Void> sync() throws Exception {
        sync.sync();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "整理调度处理", notes = "数据整理")
    @GetMapping("/etl")
    public RetResult<Void> etl() throws Exception {
        sync.etl();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "模板调度处理", notes = "模板")
    @GetMapping("/model")
    public RetResult<Void> model(String modelCode, String tenantId, String operator) throws Exception {
        ThreadLocalHolder.set("tenantId", tenantId);
        ThreadLocalHolder.set("operator", operator);
        sync.model(modelCode);
        return RetResponse.makeOKRsp();
    }
}
