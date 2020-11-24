package com.deloitte.bdh.data.analyse.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.annotation.NoInterceptor;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.util.AesUtil;
import com.deloitte.bdh.common.util.Md5Util;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePublicShare;
import com.deloitte.bdh.data.analyse.model.request.AnalysePublicShareDto;
import com.deloitte.bdh.data.analyse.model.request.AnalysePublicShareValidateDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePublicShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-11-23
 */
@Api(value = "分析管理-公开报表")
@RestController
@RequestMapping("/biUiAnalysePublicShare")
public class BiUiAnalysePublicShareController {
    @Value("${bi.analyse.encryptPass}")
    private String encryptPass;

    @Resource
    private BiUiAnalysePublicShareService shareService;


    @ApiOperation(value = "获取当前page的公开分享状态", notes = "获取当前page的公开分享状态")
    @PostMapping("/get")
    public RetResult<BiUiAnalysePublicShare> get(@RequestBody @Validated RetRequest<String> request) {
        BiUiAnalysePublicShare share = shareService.getOne(new LambdaQueryWrapper<BiUiAnalysePublicShare>()
                .eq(BiUiAnalysePublicShare::getRefPageId, request.getData())
        );

        if (null == share) {
            share = new BiUiAnalysePublicShare();
            share.setRefPageId(request.getData());
            share.setType("0");
            share.setTenantId(ThreadLocalHolder.getTenantId());
            shareService.save(share);
        }
        return RetResponse.makeOKRsp(share);
    }

    @ApiOperation(value = "改变公开状态", notes = "改变公开状态")
    @PostMapping("/update")
    public RetResult<String> update(@RequestBody @Validated RetRequest<AnalysePublicShareDto> request) {
        return RetResponse.makeOKRsp(shareService.update(request.getData()));
    }

    @ApiOperation(value = "解密", notes = "解密")
    @PostMapping("/decrypt")
    @NoInterceptor
    public RetResult<Map<String, Object>> decrypt(@RequestBody @Validated RetRequest<String> request) {
        if (StringUtil.isEmpty(request.getData())) {
            throw new RuntimeException("参数不能为空");
        }
        Map<String, Object> result = JsonUtil.JsonStrToMap(AesUtil.decryptNoSymbol(request.getData(), encryptPass));
        return RetResponse.makeOKRsp(result);
    }

    @ApiOperation(value = "校验密码", notes = "校验密码")
    @PostMapping("/password/validate")
    public RetResult<Boolean> validate(@RequestBody @Validated RetRequest<AnalysePublicShareValidateDto> request) {
        BiUiAnalysePublicShare share = shareService.getOne(new LambdaQueryWrapper<BiUiAnalysePublicShare>()
                .eq(BiUiAnalysePublicShare::getRefPageId, request.getData().getPageId())
        );
        if ("2".equals(share.getType())) {
            String md5 = Md5Util.getMD5(request.getData().getPassword(), ThreadLocalHolder.getTenantCode());
            if (!md5.equals(share.getPassword())) {
                return RetResponse.makeOKRsp(false);
            }
        }
        return RetResponse.makeOKRsp(true);
    }
}
