package com.deloitte.bdh.data.analyse.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.annotation.NoInterceptor;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.util.AesUtil;
import com.deloitte.bdh.common.util.Md5Util;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ShareTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePublicShare;
import com.deloitte.bdh.data.analyse.model.request.AnalysePublicShareDto;
import com.deloitte.bdh.data.analyse.model.request.AnalysePublicShareValidateDto;
import com.deloitte.bdh.data.analyse.model.request.DecryptDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePublicShareService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-11-23
 */
@Api(tags = "分析管理-公开报表")
@RestController
@RequestMapping("/biUiAnalysePublicShare")
@Slf4j
public class BiUiAnalysePublicShareController {
    @Value("${bi.analyse.encryptPass}")
    private String encryptPass;

    @Resource
    private BiUiAnalysePublicShareService shareService;


    @ApiOperation(value = "获取当前page的公开分享状态", notes = "获取当前page的公开分享状态")
    @PostMapping("/get")
    public RetResult<BiUiAnalysePublicShare> get(@RequestBody @Validated RetRequest<String> request) {
//        return RetResponse.makeOKRsp(shareService.get(request.getData()));
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "改变公开状态", notes = "改变公开状态")
    @PostMapping("/update")
    public RetResult<String> update(@RequestBody @Validated RetRequest<AnalysePublicShareDto> request) {
        return RetResponse.makeOKRsp();
//        return RetResponse.makeOKRsp(shareService.update(request.getData()));
    }

    @ApiOperation(value = "解密", notes = "解密")
    @PostMapping("/decrypt")
    @NoInterceptor
    public RetResult<Map<String, Object>> decrypt(@RequestBody @Validated RetRequest<DecryptDto> request) {
        if (null == request.getData()) {
            throw new RuntimeException("参数不能为空");
        }
        String str = AesUtil.decryptNoSymbol(request.getData().getCiphertext(), encryptPass);
        log.info("请求参数:{},解密后:{},密文{}", JsonUtil.readObjToJson(request), str, encryptPass);

        //设置租户编码
        Map<String, Object> result = JsonUtil.JsonStrToMap(str);
        ThreadLocalHolder.set("tenantCode", MapUtils.getString(result, "tenantCode"));

        //检查状态
        LambdaQueryWrapper<BiUiAnalysePublicShare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalysePublicShare::getRefPageId, MapUtils.getString(result, "refPageId"));
        if (StringUtils.equals(request.getData().getDecryptType(), "0")) {
            List<String> typeList = Lists.newArrayList(ShareTypeEnum.ONE.getKey(), ShareTypeEnum.TWO.getKey());
            queryWrapper.in(BiUiAnalysePublicShare::getType, typeList);
        } else if (StringUtils.equals(request.getData().getDecryptType(), "1")) {
            queryWrapper.eq(BiUiAnalysePublicShare::getType, ShareTypeEnum.FOUR.getKey());
        } else if (StringUtils.equals(request.getData().getDecryptType(), "2")) {
            List<String> typeList = Lists.newArrayList(ShareTypeEnum.ZERO.getKey(), ShareTypeEnum.FIVE.getKey());
            queryWrapper.in(BiUiAnalysePublicShare::getType, typeList);
        }
        //正常只有一条数据，为防止脏数据影响，用list取
        List<BiUiAnalysePublicShare> share = shareService.list(queryWrapper);
        if (CollectionUtils.isEmpty(share)) {
            result.put("refPageId", null);
        } else {
            ThreadLocalHolder.set("tenantId", share.get(0).getTenantId());
            result.put("isEncrypt", ShareTypeEnum.ZERO.getKey());
            if (ShareTypeEnum.TWO.getKey().equals(share.get(0).getType())) {
                result.put("isEncrypt", ShareTypeEnum.ONE.getKey());
            }
        }
        return RetResponse.makeOKRsp(result);
    }

    @ApiOperation(value = "校验密码", notes = "校验密码")
    @PostMapping("/password/validate")
    public RetResult<Boolean> validate(@RequestBody @Validated RetRequest<AnalysePublicShareValidateDto> request) {
        LambdaQueryWrapper<BiUiAnalysePublicShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BiUiAnalysePublicShare::getRefPageId, request.getData().getPageId());
        //排除订阅数据
        List<String> typeList = Lists.newArrayList(ShareTypeEnum.ZERO.getKey(), ShareTypeEnum.ONE.getKey(), ShareTypeEnum.TWO.getKey());
        lambdaQueryWrapper.in(BiUiAnalysePublicShare::getType, typeList);
        List<BiUiAnalysePublicShare> share = shareService.list(lambdaQueryWrapper);
        if (ShareTypeEnum.TWO.getKey().equals(share.get(0).getType())) {
            String md5 = Md5Util.getMD5(request.getData().getPassword(), encryptPass + ThreadLocalHolder.getTenantCode());
            if (!md5.equals(share.get(0).getPassword())) {
                return RetResponse.makeOKRsp(false);
            }
        }
        return RetResponse.makeOKRsp(true);
    }
}
