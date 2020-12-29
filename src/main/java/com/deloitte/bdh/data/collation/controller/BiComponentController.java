package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.request.ComponentRenameDto;
import com.deloitte.bdh.data.collation.service.BiComponentService;
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
 * @since 2020-09-24
 */
@Api(tags = "数据整理-组件相关")
@RestController
@RequestMapping("/biComponent")
public class BiComponentController {
    @Autowired
    private BiComponentService biComponentService;

    @ApiOperation(value = "查看单个Component详情", notes = "查看单个Component详情")
    @PostMapping("/getComponent")
    public RetResult<BiComponent> getComponent(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(biComponentService.getById(request.getData()));
    }

    @ApiOperation(value = "重命名组件", notes = "重命名组件")
    @PostMapping("/rename")
    public RetResult<BiComponent> rename(@RequestBody @Validated RetRequest<ComponentRenameDto> request) {
        return RetResponse.makeOKRsp(biComponentService.rename(request.getData()));
    }
}
