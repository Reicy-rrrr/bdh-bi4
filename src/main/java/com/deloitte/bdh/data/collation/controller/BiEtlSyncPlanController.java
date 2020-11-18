package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.integration.SyncService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


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
    @PostMapping("/sync")
    public RetResult<Void> sync() throws Exception {
        sync.sync();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "整理调度处理", notes = "数据整理")
    @PostMapping("/etl")
    public RetResult<Void> etl() throws Exception {
        sync.etl();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "模板调度处理", notes = "模板")
    @PostMapping("/model")
    public RetResult<Void> model(@RequestBody Map<String, Object> map) throws Exception {
        ThreadLocalHolder.set("tenantId", MapUtils.getString(map, "tenantId"));
        ThreadLocalHolder.set("operator", MapUtils.getString(map, "operator"));
        sync.model(MapUtils.getString(map, "modelCode"));
        return RetResponse.makeOKRsp();
    }
}
