package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.data.dao.bi.BiEtlConnectionMapper;
import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.model.request.CreateConnectionDto;
import com.deloitte.bdh.data.service.*;
import com.deloitte.bdh.common.base.AbstractService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-09-29
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlConnectionServiceImpl extends AbstractService<BiEtlConnectionMapper, BiEtlConnection> implements BiEtlConnectionService {

    @Autowired
    private NifiProcessService nifiProcessService;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private BiEtlModelService etlModelService;
    @Autowired
    private BiEtlProcessorService etlProcessorService;
    @Resource
    private BiEtlConnectionMapper etlConnectionMapper;

    @Override
    public BiEtlConnection createConnection(CreateConnectionDto dto) throws Exception {
        BiEtlProcessor fromProcessor = etlProcessorService
                .getOne(new LambdaQueryWrapper<BiEtlProcessor>().eq(BiEtlProcessor::getCode, dto.getFromProcessorCode()));

        BiEtlProcessor toProcessors = etlProcessorService
                .getOne(new LambdaQueryWrapper<BiEtlProcessor>().eq(BiEtlProcessor::getCode, dto.getToProcessorCode()));

        BiProcessors processors = processorsService
                .getOne(new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getCode, fromProcessor.getRelProcessorsCode()));

        BiEtlModel model = etlModelService
                .getOne(new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, processors.getRelModelCode()));

        BiEtlConnection connection = new BiEtlConnection();
        BeanUtils.copyProperties(dto, connection);
        connection.setCode(GenerateCodeUtil.genConnect());
        connection.setRelProcessorsCode(processors.getCode());
        connection.setRelModelCode(processors.getRelModelCode());
        connection.setCreateDate(LocalDateTime.now());
        connection.setModifiedDate(LocalDateTime.now());

        //nifi 创建 processor
        Map<String, Object> source = Maps.newHashMap();
        source.put("id", fromProcessor.getProcessId());
        source.put("groupId", fromProcessor.getProcessGroupId());
        source.put("type", "PROCESSOR");

        Map<String, Object> destination = Maps.newHashMap();
        destination.put("id", toProcessors.getProcessId());
        destination.put("groupId", fromProcessor.getProcessGroupId());
        destination.put("type", "PROCESSOR");

        List<String> selectedRelationships = JsonUtil.string2Obj(fromProcessor.getRelationships(), List.class);

        Map<String, Object> component = Maps.newHashMap();
        component.put("source", source);
        component.put("destination", destination);
        component.put("selectedRelationships", selectedRelationships);

        //此处去nifi value
        Map<String, Object> connectionMap = nifiProcessService.createConnections(component, model.getProcessGroupId());

        connection.setConnectionId(MapUtils.getString(connectionMap, "id"));
        connection.setVersion(NifiProcessUtil.getVersion(connectionMap));
        etlConnectionMapper.insert(connection);
        return connection;
    }

    @Override
    public void dropConnection(String connectCode) throws Exception {
        BiEtlConnection connection = etlConnectionMapper.selectOne(new LambdaQueryWrapper<BiEtlConnection>().eq(BiEtlConnection::getCode, connectCode));
        nifiProcessService.dropConnections(connection.getConnectionId());
    }

    @Override
    public void delConnection(String connectCode) throws Exception {
        BiEtlConnection connection = etlConnectionMapper.selectOne(new LambdaQueryWrapper<BiEtlConnection>().eq(BiEtlConnection::getCode, connectCode));
        nifiProcessService.delConnections(connection.getConnectionId());
        etlConnectionMapper.delete(new LambdaQueryWrapper<BiEtlConnection>().eq(BiEtlConnection::getCode, connectCode));


    }

}
