package com.deloitte.bdh.data.analyse.controller;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.service.AnalyseUserResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
@Api(tags = "分析管理-资源权限管理")
@RestController
@RequestMapping("/ui/analyse/resource")
public class AnalyseUserResourceController {

    @Resource
    AnalyseUserResourceService userResourceService;

    @ApiOperation(value = "保存资源权限", notes = "保存资源权限")
    @PostMapping("/saveResourcePermission")
    public RetResult<Void> saveResourcePermission(@RequestBody @Valid RetRequest<SaveResourcePermissionDto> request) {
        userResourceService.saveResourcePermission(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "查询资源权限", notes = "查询资源权限")
    @PostMapping("/getResourcePermission")
    public RetResult<ResourcePermissionDto> getResourcePermission(@RequestBody @Valid RetRequest<GetResourcePermissionDto> request) {
        return RetResponse.makeOKRsp(userResourceService.getResourcePermission(request.getData()));
    }

    @ApiOperation(value = "查询数据权限", notes = "查询数据权限")
    @PostMapping("/getDataPermission")
    public RetResult<List<PermissionItemDto>> getDataPermission(@RequestBody @Valid RetRequest<String> request) {
        return RetResponse.makeOKRsp(userResourceService.getDataPermission(request.getData()));
    }

    @ApiOperation(value = "根据报表code、文件夹id查询报表权限", notes = "根据报表code、文件夹id查询报表权限")
    @PostMapping("/getPagePermissionByCode")
    public RetResult<ResourcePermissionDto> getPagePermissionByCode(@RequestBody @Valid RetRequest<GetPermissionByCodeDto> request) {
        return RetResponse.makeOKRsp(userResourceService.getPagePermissionByCode(request.getData()));
    }

    @ApiOperation(value = "根据报表code、文件夹id查询数据权限", notes = "根据报表code、文件夹id查询数据权限")
    @PostMapping("/getPageDataPermissionByCode")
    public RetResult<List<PermissionItemDto>> getPageDataPermissionByCode(@RequestBody @Valid RetRequest<GetPermissionByCodeDto> request) {
        return RetResponse.makeOKRsp(userResourceService.getPageDataPermissionByCode(request.getData()));
    }

}
