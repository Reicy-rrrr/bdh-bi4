package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.annotation.NoInterceptor;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.model.request.BiEtlDbFileUploadDto;
import com.deloitte.bdh.data.collation.service.BiEvmFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2021-02-01
 */
@Api(tags = "EVM-处理文件")
@RestController
@RequestMapping("/biEvmFile")
public class BiEvmFileController {
    @Autowired
    private BiEvmFileService evmFileService;


    @ApiOperation(value = "上传文件", notes = "EVM上传文件")
    @PostMapping("/upload")
    @NoInterceptor
    public RetResult<Void> upload(@ModelAttribute BiEtlDbFileUploadDto fileUploadDto) {
        // 租户id
        String tenantId = fileUploadDto.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            throw new BizException("租户id不能为空");
        }
        // 设置全局租户id
        ThreadLocalHolder.set("tenantId", tenantId);
        // 租户code
        String tenantCode = fileUploadDto.getTenantCode();
        if (StringUtils.isBlank(tenantCode)) {
            throw new BizException("租户code不能为空");
        }
        // 设置全局租户code
        ThreadLocalHolder.set("tenantCode", tenantCode);
        String operator = fileUploadDto.getOperator();
        if (StringUtils.isBlank(operator)) {
            throw new BizException("操作人id不能为空");
        }
        // 设置全局用户
        ThreadLocalHolder.set("operator", operator);
        evmFileService.uploadEvm(fileUploadDto);
        return RetResponse.makeOKRsp();
    }

}
