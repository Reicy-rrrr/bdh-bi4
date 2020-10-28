package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.integration.AsyncService;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.model.BiConnections;
import com.deloitte.bdh.data.collation.model.BiEtlConnection;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.deloitte.bdh.data.collation.dao.bi.BiProcessorsMapper;
import com.deloitte.bdh.data.collation.service.BiConnectionsService;
import com.deloitte.bdh.data.collation.service.BiEtlConnectionService;
import com.deloitte.bdh.data.collation.service.BiEtlProcessorService;
import com.deloitte.bdh.data.collation.service.BiProcessorsService;
import com.deloitte.bdh.common.base.AbstractService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
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
    @Autowired
    private BiEtlConnectionService biEtlConnectionService;
    @Autowired
    private NifiProcessService nifiProcessService;
    @Autowired
    private AsyncService asyncService;

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

    @Override
    public void stopAndClearSync(String processGroupId, String modelCode) throws Exception {
        //清空所有
        List<BiEtlConnection> connectionList = biEtlConnectionService.list(
                new LambdaQueryWrapper<BiEtlConnection>().eq(BiEtlConnection::getRelModelCode, modelCode)
        );
        if (CollectionUtils.isNotEmpty(connectionList)) {
            asyncService.stopAndClearSync(processGroupId, modelCode, connectionList);
        }
    }

    @Override
    public void stopAndClearAsync(String processGroupId, String modelCode) throws Exception {
        //清空所有
        List<BiEtlConnection> connectionList = biEtlConnectionService.list(
                new LambdaQueryWrapper<BiEtlConnection>().eq(BiEtlConnection::getRelModelCode, modelCode)
        );
        if (CollectionUtils.isNotEmpty(connectionList)) {
            asyncService.stopAndClearAsync(processGroupId, modelCode, connectionList);
        }
    }

    @Override
    public void runState(String id, String state, boolean isGroup) throws Exception {
        nifiProcessService.runState(id, state, isGroup);
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


}
