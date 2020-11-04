package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.enums.BiProcessorsTypeEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.dao.bi.BiProcessorsMapper;
import com.deloitte.bdh.data.collation.nifi.EtlProcess;
import com.deloitte.bdh.data.collation.nifi.dto.Processor;
import com.deloitte.bdh.data.collation.nifi.dto.ProcessorContext;
import com.deloitte.bdh.data.collation.nifi.enums.MethodEnum;
import com.deloitte.bdh.data.collation.service.*;
import com.deloitte.bdh.common.base.AbstractService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
    @Autowired
    private NifiProcessService nifiProcessService;
    @Autowired
    private BiEtlDatabaseInfService databaseInfService;
    @Autowired
    private BiEtlModelService biEtlModelService;
    @Resource
    private BiProcessorsMapper processorsMapper;
    @Resource
    private BiEtlProcessorService processorService;
    @Autowired
    private BiConnectionsService connectionsService;
    @Autowired
    private BiComponentParamsService componentParamsService;
    @Autowired
    private BiEtlConnectionService biEtlConnectionService;
    @Autowired
    private BiEtlMappingConfigService configService;
    @Resource
    private EtlProcess etlProcess;


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
    public void runState(String code, RunStatusEnum state, boolean isGroup) throws Exception {
        BiProcessors processors = processorsMapper.selectOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getCode, code)
        );
        nifiProcessService.runState(processors.getProcessGroupId(), state.getKey(), isGroup);
        if (RunStatusEnum.STOP == state) {
            //清空所有 这里该走异步
            List<BiEtlConnection> connectionList = biEtlConnectionService.list(
                    new LambdaQueryWrapper<BiEtlConnection>().eq(BiEtlConnection::getRelProcessorsCode, code)
            );
            //清空所有
            for (BiEtlConnection var : connectionList) {
                nifiProcessService.dropConnections(var.getConnectionId());
            }
        }
    }

    @Override
    public void removeProcessors(String processorsCode, String dbId) throws Exception {
        BiProcessors processors = processorsMapper.selectOne(new LambdaQueryWrapper<BiProcessors>()
                .eq(BiProcessors::getCode, processorsCode)
        );
        BiEtlModel biEtlModel = biEtlModelService.getOne(
                new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, processors.getRelModelCode()));

        List<Processor> processorList = processorService.invokeProcessorList(processors.getCode());

        List<BiEtlConnection> connectionList = biEtlConnectionService.list(new LambdaQueryWrapper<BiEtlConnection>()
                .eq(BiEtlConnection::getRelProcessorsCode, processors.getCode()));


        //获取数据源类型，用于回滚，当为数据源组件 肯定有值
        String dbType = null;
        if (null != dbId) {
            dbType = databaseInfService.getById(dbId).getType();
        }

        Map<String, Object> req = Maps.newHashMap();
        ProcessorContext context = new ProcessorContext();
        context.setEnumList(BiProcessorsTypeEnum.getEnum(processors.getType()).includeProcessor(dbType));
        context.setMethod(MethodEnum.DELETE);
        context.setModel(biEtlModel);
        context.setProcessors(processors);
        context.addProcessorList(processorList);
        context.addConnectionList(connectionList);
        context.setReq(req);
        etlProcess.operateProcessorGroup(context);
        processorsMapper.deleteById(processors.getId());
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
