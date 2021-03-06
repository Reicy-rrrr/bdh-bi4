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
import com.deloitte.bdh.common.mq.MessageProducer;
import com.deloitte.bdh.common.properties.OssProperties;
import com.deloitte.bdh.common.util.*;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalyseSubscribeMapper;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.enums.ShareTypeEnum;
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
import com.deloitte.bdh.data.collation.service.XxJobService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:15/12/2020
 * Description:
 */
@Slf4j
@Service
@DS(DSConstant.BI_DB)
public class AnalysePageSubscribeServiceImpl extends AbstractService<BiUiAnalyseSubscribeMapper, BiUiAnalyseSubscribe> implements AnalysePageSubscribeService {

    @Value("${bi.analyse.subscribe.address}")
    private String subscribeAddress;

    @Value("${bi.analyse.subscribe.call}")
    private String callBackAddress;

    @Value("${bi.analyse.view.address}")
    private String viewAddress;

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
    private AliyunOssUtil aliyunOssUtil;

    @Resource
    private OssProperties ossProperties;

    @Resource
    private MessageProducer messageProducer;

    private static SnowFlakeUtil idWorker = new SnowFlakeUtil(0, 0);

    @Transactional
    @Override
    public void subscribe(SubscribeDto request) {
        CronUtil.validate(CronUtil.createCronExpression(request.getCronData()));
        if (StringUtils.equals("1", request.getStatus()) && CollectionUtils.isEmpty(request.getReceiver())) {
            throw new BizException(ResourceMessageEnum.RECEIVER_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.RECEIVER_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }
        //????????????????????????
        LambdaQueryWrapper<BiUiAnalyseSubscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseSubscribe::getPageId, request.getPageId());
        BiUiAnalyseSubscribe subscribe = this.getOne(queryWrapper);
        if (null == subscribe) {
            subscribe = new BiUiAnalyseSubscribe();
            subscribe.setTaskId(GenerateCodeUtil.genPage());
        }
        BeanUtils.copyProperties(request, subscribe);
        if (CollectionUtils.isNotEmpty(request.getReceiver())) {
            subscribe.setReceiver(JSON.toJSONString(request.getReceiver()));
        } else {
            subscribe.setReceiver(null);
        }
        String imgUrl = getImgUrl(request);
        subscribe.setImgUrl(imgUrl);
        subscribe.setAccessUrl(getAccessUrl(request));
        subscribe.setTenantId(ThreadLocalHolder.getTenantId());
        subscribeService.saveOrUpdate(subscribe);

        //??????????????????
        Map<String, String> params = Maps.newHashMap();
        params.put("pageId", subscribe.getPageId());
        params.put("tenantId", ThreadLocalHolder.getTenantId());
        params.put("operator", ThreadLocalHolder.getOperator());
        params.put("url", imgUrl);
        params.put("url_id", idWorker.nextId() + "");
        try {
            jobService.addOrUpdate(subscribe.getTaskId(), callBackAddress, CronUtil.createCronExpression(request.getCronData()), params);
            if (StringUtils.equals(subscribe.getStatus(), "1")) {
                jobService.start(subscribe.getTaskId());
            } else {
                jobService.stop(subscribe.getTaskId());
            }
        } catch (Exception e) {
            throw new BizException(ResourceMessageEnum.ADD_TASK_ERROR.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.ADD_TASK_ERROR.getMessage(), ThreadLocalHolder.getLang()));
        }

    }

    @Override
    public AnalyseSubscribeDto getSubscribe(String pageId) {
        LambdaQueryWrapper<BiUiAnalyseSubscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseSubscribe::getPageId, pageId);
        BiUiAnalyseSubscribe subscribe = this.getOne(queryWrapper);
        if (null != subscribe) {
            AnalyseSubscribeDto dto = new AnalyseSubscribeDto();
            BeanUtils.copyProperties(subscribe, dto);
            dto.setReceiver(JSONArray.parseArray(subscribe.getReceiver(), UserIdMailDto.class));
            return dto;
        }
        return null;
    }

    @Override
    public List<AnalyseSubscribeLogDto> getExecuteLog(String pageId) {
        LambdaQueryWrapper<BiUiAnalyseSubscribeLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseSubscribeLog::getPageId, pageId);
        List<BiUiAnalyseSubscribeLog> subscribeLog = subscribeLogService.list(queryWrapper);
        List<AnalyseSubscribeLogDto> dtoList = Lists.newArrayList();
        subscribeLog.forEach(log -> {
            AnalyseSubscribeLogDto dto = new AnalyseSubscribeLogDto();
            BeanUtils.copyProperties(log, dto);
            dto.setReceiver(JSONObject.parseObject(log.getReceiver(), UserIdMailDto.class));
            dtoList.add(dto);
        });
        return dtoList;
    }

    @Override
    public void execute(String pageId, MultipartFile file) {
        //????????????????????????
        LambdaQueryWrapper<BiUiAnalyseSubscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalyseSubscribe::getPageId, pageId);
        BiUiAnalyseSubscribe subscribe = this.getOne(queryWrapper);
        if (null == subscribe) {
            throw new BizException(ResourceMessageEnum.SUBSCRIBE_DATA_NOT_EXIST.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.SUBSCRIBE_DATA_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));
        }
        if("1".equals(subscribe.getStatus())) {
        	if (StringUtils.isNotBlank(subscribe.getReceiver())) {
                List<UserIdMailDto> receiveList = JSONArray.parseArray(subscribe.getReceiver(), UserIdMailDto.class);
                if (CollectionUtils.isNotEmpty(receiveList)) {
                    String imgUrl;
                    try {
                        String filePath = AnalyseConstants.DOCUMENT_DIR + ThreadLocalHolder.getTenantCode() + "/bi/subscribe/";
                        String name = aliyunOssUtil.uploadFile2Oss(filePath, file);
                        imgUrl = aliyunOssUtil.getImgUrl(filePath, name);
                        // ?????????????????????????????????????????????????????????????????????
                        if (imgUrl.contains(ossProperties.getTargetEndpoint())) {
                            imgUrl = imgUrl.replace(ossProperties.getTargetEndpoint(), ossProperties.getReplacementEndpoint());
                        }
//                        imgUrl = screenshotUtil.fullScreen(subscribe.getImgUrl());
                    } catch (Exception e) {
                        for (UserIdMailDto userIdMailDto : receiveList) {
                            //????????????
                            BiUiAnalyseSubscribeLog subscribeLog = new BiUiAnalyseSubscribeLog();
                            subscribeLog.setCron(CronUtil.createCronExpression(subscribe.getCronData()));
                            subscribeLog.setCronDesc(CronUtil.createDescription(subscribe.getCronData()));
                            subscribeLog.setPageId(subscribe.getPageId());
                            subscribeLog.setReceiver(JSON.toJSONString(userIdMailDto));
                            subscribeLog.setExecuteStatus("0");
                            subscribeLog.setFailMessage(e.getMessage());
                            subscribeLogService.save(subscribeLog);
                        }
                        throw new BizException(ResourceMessageEnum.SHOT_ERROR.getCode(),
                                localeMessageService.getMessage(ResourceMessageEnum.SHOT_ERROR.getMessage(), ThreadLocalHolder.getLang()));
                    }
                    for (UserIdMailDto userIdMailDto : receiveList) {
                        //?????????
                        HashMap<String, Object> params = Maps.newHashMap();
                        params.put("userName", userIdMailDto.getUserName());
                        params.put("imgUrl", imgUrl);
                        params.put("accessUrl", subscribe.getAccessUrl());
                        EmailDto emailDto = new EmailDto();
                        emailDto.setEmail(userIdMailDto.getEmail());
                        emailDto.setSubject(subscribe.getMailSubject());
                        emailDto.setTemplate(AnalyseConstants.EMAIL_TEMPLATE_SUBSCRIBE);
                        emailDto.setParamMap(params);
                        emailDto.setPageId(pageId);

//                        KafkaEmailDto KafkaEmailDto = new KafkaEmailDto();
//                        KafkaEmailDto.setCcList(emailDto.getCcList());
//                        KafkaEmailDto.setEmail(userIdMailDto.getEmail());
//                        KafkaEmailDto.setPageId(pageId);
//                        KafkaEmailDto.setParamMap(params);
//                        KafkaEmailDto.setSubject(subscribe.getMailSubject());
//                        KafkaEmailDto.setTemplate(AnalyseConstants.EMAIL_TEMPLATE_SUBSCRIBE);
//                        KafkaMessage message = new KafkaMessage(UUID.randomUUID().toString().replaceAll("-",""),KafkaEmailDto,KafkaTypeEnum.Email.getType());

                        //????????????
                        BiUiAnalyseSubscribeLog subscribeLog = new BiUiAnalyseSubscribeLog();
                        subscribeLog.setCron(CronUtil.createCronExpression(subscribe.getCronData()));
                        subscribeLog.setCronDesc(CronUtil.createDescription(subscribe.getCronData()));
                        subscribeLog.setPageId(subscribe.getPageId());
                        subscribeLog.setReceiver(JSON.toJSONString(userIdMailDto));
                        try {
//                            emailService.sendEmail(emailDto, AnalyseConstants.EMAIL_TEMPLATE_SUBSCRIBE);
//                        	producter.sendEmail(message);
                            messageProducer.sendEmailMessage(JSON.toJSONString(emailDto));
                            subscribeLog.setExecuteStatus("1");
                        } catch (Exception e) {
                            subscribeLog.setExecuteStatus("0");
                            subscribeLog.setFailMessage(e.getMessage());
                        } finally {
                            subscribeLogService.save(subscribeLog);
                        }
                    }
                }
            }
        }
        
    }

    private String getAccessUrl(SubscribeDto request) {
        //??????????????????
        LambdaQueryWrapper<BiUiAnalysePublicShare> shareLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shareLambdaQueryWrapper.eq(BiUiAnalysePublicShare::getRefPageId, request.getPageId());
        shareLambdaQueryWrapper.eq(BiUiAnalysePublicShare::getType, ShareTypeEnum.FIVE.getKey());
        BiUiAnalysePublicShare share = shareService.getOne(shareLambdaQueryWrapper);
        if (null == share) {
            share = new BiUiAnalysePublicShare();
            share.setRefPageId(request.getPageId());
            share.setType(ShareTypeEnum.FIVE.getKey());
            share.setTenantId(ThreadLocalHolder.getTenantId());
            Map<String, Object> params = Maps.newHashMap();
            params.put("tenantCode", ThreadLocalHolder.getTenantCode());
            params.put("refPageId", request.getPageId());
            share.setCode(AesUtil.encryptNoSymbol(JsonUtil.readObjToJson(params), encryptPass));
            share.setAddress(viewAddress);
            shareService.save(share);
        }
        return share.getAddress() + "/" + share.getCode();
    }

    private String getImgUrl(SubscribeDto request) {
        //??????????????????
        LambdaQueryWrapper<BiUiAnalysePublicShare> shareLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shareLambdaQueryWrapper.eq(BiUiAnalysePublicShare::getRefPageId, request.getPageId());
        shareLambdaQueryWrapper.eq(BiUiAnalysePublicShare::getType, "4");
        BiUiAnalysePublicShare share = shareService.getOne(shareLambdaQueryWrapper);
        if (null == share) {
            share = new BiUiAnalysePublicShare();
            share.setRefPageId(request.getPageId());
            share.setType(ShareTypeEnum.FOUR.getKey());
            share.setTenantId(ThreadLocalHolder.getTenantId());
            Map<String, Object> params = Maps.newHashMap();
            params.put("tenantCode", ThreadLocalHolder.getTenantCode());
            params.put("refPageId", request.getPageId());
            share.setCode(AesUtil.encryptNoSymbol(JsonUtil.readObjToJson(params), encryptPass));
            share.setAddress(subscribeAddress);
            shareService.save(share);
        }
        return share.getAddress() + "/" + share.getCode();
    }
}
