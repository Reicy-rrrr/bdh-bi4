package com.deloitte.bdh.data.analyse.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.cron.CronUtil;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.util.*;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseSubscribeMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseSubscribe;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePublicShare;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseSubscribeLog;
import com.deloitte.bdh.data.analyse.model.request.EmailDto;
import com.deloitte.bdh.data.analyse.model.request.SubscribeDto;
import com.deloitte.bdh.data.analyse.model.request.UserIdMailDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseSubscribeDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseSubscribeLogDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageSubscribeLogService;
import com.deloitte.bdh.data.analyse.service.AnalysePageSubscribeService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePublicShareService;
import com.deloitte.bdh.data.analyse.service.EmailService;
import com.deloitte.bdh.data.analyse.utils.ScreenshotUtil;
import com.deloitte.bdh.data.collation.integration.XxJobService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:15/12/2020
 * Description:
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalysePageSubscribeServiceImpl extends AbstractService<BiUiAnalyseSubscribeMapper, BiUiAnalyseSubscribe> implements AnalysePageSubscribeService {

    @Value("${bi.analyse.public.address}")
    private String publicAddress;

    @Value("${bi.analyse.encryptPass}")
    private String encryptPass;

    @Resource
    private BiUiAnalysePublicShareService shareService;

    @Resource
    private AnalysePageSubscribeService subscribeService;

    @Resource
    private AnalysePageSubscribeLogService subscribeLogService;

    @Resource
    private XxJobService jobService;

    @Resource
    private ScreenshotUtil screenshotUtil;

    @Resource
    private EmailService emailService;

    @Transactional
    @Override
    public void subscribe(SubscribeDto request) {
        CronUtil.validate(CronUtil.createCronExpression(request.getCronData()));
        //保存计划任务配置
        LambdaQueryWrapper<BiUiAnalyseSubscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseSubscribe::getPageId, request.getPageId());
        BiUiAnalyseSubscribe subscribe = this.getOne(queryWrapper);
        if (null == subscribe) {
            subscribe = new BiUiAnalyseSubscribe();
            subscribe.setTaskId(GenerateCodeUtil.genPage());
        }
        BeanUtils.copyProperties(request, subscribe);
        subscribe.setReceiver(JSON.toJSONString(request.getReceiver()));
        subscribe.setAccessUrl(getAccessUrl(request));
        subscribe.setTenantId(ThreadLocalHolder.getTenantId());
        subscribeService.saveOrUpdate(subscribe);

        //添加执行计划
        Map<String, String> params = Maps.newHashMap();
        params.put("pageId", subscribe.getPageId());
        params.put("modelCode", subscribe.getTaskId());
        params.put("tenantId", ThreadLocalHolder.getTenantId());
        params.put("operator", ThreadLocalHolder.getOperator());
        try {
//            jobService.addOrUpdate(subscribe.getTaskId(), GetIpAndPortUtil.getIpAndPort() + "/bi/biEtlSyncPlan/model",
//                    CronUtil.createCronExpression(request.getCronData()), params);
            jobService.addOrUpdate(subscribe.getTaskId(), GetIpAndPortUtil.getIpAndPort() + "/bi/subscribe/execute",
                    "0 0/1 * * * ? ", params);
            if (StringUtils.equals(subscribe.getStatus(), "1")) {
                jobService.start(subscribe.getTaskId());
            } else {
                jobService.stop(subscribe.getTaskId());
            }
        } catch (Exception e) {
            throw new BizException("添加计划任务失败");
        }

    }

    @Override
    public AnalyseSubscribeDto getSubscribe(String pageId) {
        LambdaQueryWrapper<BiUiAnalyseSubscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseSubscribe::getPageId, pageId);
        BiUiAnalyseSubscribe subscribe = this.getOne(queryWrapper);
        AnalyseSubscribeDto dto = new AnalyseSubscribeDto();
        BeanUtils.copyProperties(subscribe, dto);
        dto.setReceiver(JSONArray.parseArray(subscribe.getReceiver(), UserIdMailDto.class));
        return dto;
    }

    @Override
    public AnalyseSubscribeLogDto getExecuteLog(String pageId) {
        LambdaQueryWrapper<BiUiAnalyseSubscribeLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseSubscribeLog::getPageId, pageId);
        BiUiAnalyseSubscribeLog subscribeLog = subscribeLogService.getOne(queryWrapper);
        AnalyseSubscribeLogDto dto = new AnalyseSubscribeLogDto();
        BeanUtils.copyProperties(subscribeLog, dto);
        dto.setReceiver(JSONObject.parseObject(subscribeLog.getReceiver(), UserIdMailDto.class));
        return dto;
    }

    @Override
    public void execute(String pageId) {
        //查询要执行的数据
        LambdaQueryWrapper<BiUiAnalyseSubscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseSubscribe::getPageId, pageId);
        BiUiAnalyseSubscribe subscribe = this.getOne(queryWrapper);
        if (null == subscribe) {
            throw new BizException("目标数据不存在");
        }
        List<UserIdMailDto> userIdMailDtoList = JSONArray.parseArray(subscribe.getReceiver(), UserIdMailDto.class);
        if (CollectionUtils.isNotEmpty(userIdMailDtoList)) {
            List<UserIdMailDto> receiveList = JSONArray.parseArray(subscribe.getReceiver(), UserIdMailDto.class);
            if (CollectionUtils.isNotEmpty(receiveList)) {
                String imgUrl = null;
                try {
                    imgUrl = screenshotUtil.fullScreen(subscribe.getAccessUrl());
                } catch (Exception e) {
                    for (UserIdMailDto userIdMailDto : receiveList) {
                        //执行记录
                        BiUiAnalyseSubscribeLog subscribeLog = new BiUiAnalyseSubscribeLog();
                        subscribeLog.setCron(CronUtil.createCronExpression(subscribe.getCronData()));
                        subscribeLog.setCronDesc(CronUtil.createDescription(subscribe.getCronData()));
                        subscribeLog.setPageId(subscribe.getPageId());
                        subscribeLog.setReceiver(JSON.toJSONString(userIdMailDto));
                        subscribeLog.setExecuteStatus("0");
                        subscribeLog.setFailMessage(e.getMessage());
                        subscribeLogService.save(subscribeLog);
                    }
                    throw new BizException("截图失败：" + e.getMessage());
                }
                for (UserIdMailDto userIdMailDto : receiveList) {
                    //发邮件
                    HashMap<String, Object> params = Maps.newHashMap();
                    params.put("userName", userIdMailDto.getUserName());
                    params.put("imgUrl", imgUrl);
                    params.put("accessUrl", subscribe.getAccessUrl());
                    EmailDto emailDto = new EmailDto();
                    emailDto.setEmail(userIdMailDto.getEmail());
                    emailDto.setSubject(subscribe.getMailSubject());
                    emailDto.setTemplate(AnalyseConstants.EMAIL_TEMPLATE_SUBSCRIBE);
                    emailDto.setParamMap(params);

                    //执行记录
                    BiUiAnalyseSubscribeLog subscribeLog = new BiUiAnalyseSubscribeLog();
                    subscribeLog.setCron(CronUtil.createCronExpression(subscribe.getCronData()));
                    subscribeLog.setCronDesc(CronUtil.createDescription(subscribe.getCronData()));
                    subscribeLog.setPageId(subscribe.getPageId());
                    subscribeLog.setReceiver(JSON.toJSONString(userIdMailDto));
                    try {
                        emailService.sendEmail(emailDto, AnalyseConstants.EMAIL_TEMPLATE_SUBSCRIBE);
                        subscribeLog.setExecuteStatus("1");
                    } catch (Exception e) {
                        subscribeLog.setExecuteStatus("0");
                        subscribeLog.setFailMessage(e.getMessage());
                        throw new BizException("执行失败，失败原因：" + e.getMessage());
                    } finally {
                        subscribeLogService.save(subscribeLog);
                    }
                }
            }

        }
    }

    private String getAccessUrl(SubscribeDto request) {
        //获取访问地址
        LambdaQueryWrapper<BiUiAnalysePublicShare> shareLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shareLambdaQueryWrapper.eq(BiUiAnalysePublicShare::getRefPageId, request.getPageId());
        shareLambdaQueryWrapper.eq(BiUiAnalysePublicShare::getType, "4");
        BiUiAnalysePublicShare share = shareService.getOne(shareLambdaQueryWrapper);
        if (null == share) {
            share = new BiUiAnalysePublicShare();
            share.setRefPageId(request.getPageId());
            share.setType("4");
            share.setTenantId(ThreadLocalHolder.getTenantId());
            Map<String, Object> params = Maps.newHashMap();
            params.put("tenantCode", ThreadLocalHolder.getTenantCode());
            params.put("refPageId", request.getPageId());
            share.setCode(AesUtil.encryptNoSymbol(JsonUtil.readObjToJson(params), encryptPass));
            share.setAddress(publicAddress);
            shareService.save(share);
        }
        return share.getAddress() + "/" + share.getCode();
    }
}
