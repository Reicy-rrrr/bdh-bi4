package com.deloitte.bdh.data.collation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.BiComponentConnection;
import com.deloitte.bdh.data.collation.model.request.ComponentLinkDto;
import com.deloitte.bdh.data.collation.service.BiComponentConnectionService;
import io.swagger.annotations.Api;
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
 * @since 2020-09-29
 */
@Api(tags = "数据整理-组件关联")
@RestController
@RequestMapping("/biComponentConnection")
public class BiComponentConnectionController {
    @Autowired
    private BiComponentConnectionService componentConnectionService;

    @ApiOperation(value = "组件关联", notes = "组件关联")
    @PostMapping("/link")
    public RetResult<BiComponentConnection> link(@RequestBody @Validated RetRequest<ComponentLinkDto> request) {
        return RetResponse.makeOKRsp(componentConnectionService.link(request.getData()));
    }

    @ApiOperation(value = "取消组件关联", notes = "取消组件关联")
    @PostMapping("/cancel")
    public RetResult<Void> cancel(@RequestBody @Validated RetRequest<String> request) {
        componentConnectionService.remove(new LambdaQueryWrapper<BiComponentConnection>()
                .eq(BiComponentConnection::getCode, request.getData())
        );
        return RetResponse.makeOKRsp();
    }
}
