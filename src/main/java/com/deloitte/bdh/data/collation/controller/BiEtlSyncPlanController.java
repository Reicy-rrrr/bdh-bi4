package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.integration.SyncService;
import com.deloitte.bdh.data.collation.model.request.BiEtlSyncPlanListDto;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
@Api(tags = "数据整理-调度")
@RestController
@RequestMapping("/biEtlSyncPlan")
public class BiEtlSyncPlanController {
    @Autowired
    private SyncService sync;

    @Autowired
    private BiEtlSyncPlanService syncPlanService;

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
        sync.model(MapUtils.getString(map, "modelCode"), MapUtils.getString(map, "isTrigger", YesOrNoEnum.NO.getKey()));
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "查询任务列表", notes = "查询任务列表")
    @PostMapping("/list")
    public RetResult<PageResult> list(@RequestBody @Validated RetRequest<BiEtlSyncPlanListDto> request) {
        return RetResponse.makeOKRsp(new PageResult(syncPlanService.selectPlanList(request.getData())));
    }
}
