package com.deloitte.bdh.data.service.impl;

import java.time.LocalDateTime;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.model.BiConnections;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.dao.bi.BiProcessorsMapper;
import com.deloitte.bdh.data.nifi.dto.Processor;
import com.deloitte.bdh.data.service.BiConnectionsService;
import com.deloitte.bdh.data.service.BiProcessorsService;
import com.deloitte.bdh.common.base.AbstractService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-09-27
 */
@Service
@DS(DSConstant.BI_DB)
public class BiProcessorsServiceImpl extends AbstractService<BiProcessorsMapper, BiProcessors> implements BiProcessorsService {
    private static final Logger logger = LoggerFactory.getLogger(BiProcessorsServiceImpl.class);
    @Resource
    private BiProcessorsMapper processorsMapper;
    @Autowired
    private BiConnectionsService connectionsService;

    @Override
    public List<BiProcessors> getPreChain(String processorsCode) {
        if (StringUtil.isEmpty(processorsCode)) {
            throw new BizException("BiProcessorsServiceImpl.getPreChain error : processorsCode 不能为空");
        }
        BiProcessors biProcessors = processorsMapper.selectOne(
                new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getCode, processorsCode)
                        .orderByAsc(BiProcessors::getCode));
        //所有的连接关系
        List<BiConnections> connectionsList = connectionsService.list(
                new LambdaQueryWrapper<BiConnections>().eq(BiConnections::getRelModelCode, biProcessors.getRelModelCode())
        );

        //找出当前processors 的所有上级 processors
        List<String> processorsCodeList = Lists.newArrayList(preProcessorChain(connectionsList, null, processorsCode));
        List<BiProcessors> processorsList = processorsMapper.selectList(
                new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getRelModelCode, biProcessors.getRelModelCode())
        );

        List<BiProcessors> preChain = Lists.newLinkedList();
        processorsCodeList.forEach(outer -> processorsList.forEach(inner -> {
                    if (outer.equals(inner.getCode())) {
                        preChain.add(inner);
                    }
                })
        );
        preChain.stream().sorted(Comparator.comparing(BiProcessors::getCode));
        return preChain;
    }

    private Set<String> preProcessorChain(List<BiConnections> list, Set<String> set, String processorsCode) {
        if (null == set) {
            set = Sets.newHashSet();
            set.add(processorsCode);
        }
        for (BiConnections var : list) {
            if (var.getToProcessorsCode().equals(processorsCode)) {
                set.add(var.getFromProcessorsCode());
                preProcessorChain(list, set, var.getFromProcessorsCode());
            }
        }
        return set;
    }


//    public static void main(String[] args) {
//        BiConnections connections12 = new BiConnections();
//        connections12.setCode("1");
//        connections12.setFromProcessorsCode("1");
//        connections12.setToProcessorsCode("2");
//        connections12.setRelModelCode("3");
//
//        BiConnections connections23 = new BiConnections();
//        connections23.setCode("2");
//        connections23.setFromProcessorsCode("2");
//        connections23.setToProcessorsCode("3");
//        connections23.setRelModelCode("3");
//
//        BiConnections connections24 = new BiConnections();
//        connections24.setCode("3");
//        connections24.setFromProcessorsCode("2");
//        connections24.setToProcessorsCode("4");
//        connections24.setRelModelCode("3");
//
//        BiConnections connections35 = new BiConnections();
//        connections35.setCode("4");
//        connections35.setFromProcessorsCode("3");
//        connections35.setToProcessorsCode("5");
//        connections35.setRelModelCode("3");
//
//        BiConnections connections56 = new BiConnections();
//        connections56.setCode("5");
//        connections56.setFromProcessorsCode("5");
//        connections56.setToProcessorsCode("6");
//        connections56.setRelModelCode("3");
//
//        BiConnections connections47 = new BiConnections();
//        connections47.setCode("6");
//        connections47.setFromProcessorsCode("4");
//        connections47.setToProcessorsCode("7");
//        connections47.setRelModelCode("3");
//
//        List<BiConnections> list = new ArrayList<>();
//        list.add(connections12);
//        list.add(connections23);
//        list.add(connections24);
//        list.add(connections35);
//        list.add(connections56);
//        list.add(connections47);
//
//        Set<String> set = preProcessorChain(list, null, "3");
//    }
}
