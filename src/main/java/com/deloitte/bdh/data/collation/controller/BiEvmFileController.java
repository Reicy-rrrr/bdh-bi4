package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.annotation.NoInterceptor;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.properties.BiProperties;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import com.deloitte.bdh.data.collation.model.request.BiEtlDbFileUploadDto;
import com.deloitte.bdh.data.collation.service.BiEvmFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
@Slf4j
public class BiEvmFileController {
    @Autowired
    private BiEvmFileService evmFileService;
    @Autowired
    private BiProperties properties;
    @Resource
    protected LocaleMessageService localeMessageService;


    @ApiOperation(value = "上传文件", notes = "EVM上传文件")
    @PostMapping("/upload")
    @NoInterceptor
    public RetResult<String> upload(@ModelAttribute BiEtlDbFileUploadDto fileUploadDto) {
        log.info("biEvmFile.upload:" + JsonUtil.obj2String(fileUploadDto.getTables()));
        // 租户id
        String tenantId = fileUploadDto.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            throw new BizException(ResourceMessageEnum.EXPRESS_29.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_29.getMessage(), ThreadLocalHolder.getLang()));
        }
        // 设置全局租户id
        ThreadLocalHolder.set("tenantId", tenantId);
        // 租户code
        String tenantCode = fileUploadDto.getTenantCode();
        if (StringUtils.isBlank(tenantCode)) {
            throw new BizException(ResourceMessageEnum.EXPRESS_30.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_30.getMessage(), ThreadLocalHolder.getLang()));
        }
        // 设置全局租户code
        ThreadLocalHolder.set("tenantCode", tenantCode);
        String operator = fileUploadDto.getOperator();
        if (StringUtils.isBlank(operator)) {
            throw new BizException(ResourceMessageEnum.EXPRESS_31.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_31.getMessage(), ThreadLocalHolder.getLang()));
        }
        // 设置全局用户
        ThreadLocalHolder.set("operator", operator);
        evmFileService.uploadEvm(fileUploadDto);
        return RetResponse.makeOKRsp(localeMessageService.getMessage(ResourceMessageEnum.SUCCESS.getMessage(), ThreadLocalHolder.getLang()));
    }


    @ApiOperation(value = "获取下载地址", notes = "获取下载地址")
    @GetMapping("/getDownLoadAddress")
    @NoInterceptor
    public RetResult<String> getDownLoadAddress() {
        return RetResponse.makeOKRsp(properties.getEvmDownLoadAddress());
    }
}
