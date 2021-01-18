package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.service.BiTenantConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-12-08
 */
@Api(tags = "数据整理-初始接口")
@RestController
@RequestMapping("/biTenantConfig")
public class BiTenantConfigController {
    public final static String OPERATOR = "admin";
    @Resource
    private BiTenantConfigService configService;


    @ApiOperation(value = "基于租户初始化BI相关配置", notes = "基于租户初始化BI相关配置")
    @PostMapping("/init")
    public RetResult<Void> init(@RequestBody @Validated RetRequest<Void> request) throws Exception {
        ThreadLocalHolder.set("operator", OPERATOR);
        configService.init();
        return RetResponse.makeOKRsp();
    }
}
