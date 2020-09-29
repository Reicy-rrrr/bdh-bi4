package com.deloitte.bdh.data.service.impl;

import java.time.LocalDateTime;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.enums.*;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.dao.bi.BiEtlProcessorMapper;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.model.request.CreateProcessorDto;
import com.deloitte.bdh.data.model.request.EffectModelDto;
import com.deloitte.bdh.data.model.request.UpdateModelDto;
import com.deloitte.bdh.data.service.BiEtlModelService;
import com.deloitte.bdh.data.service.BiEtlParamsService;
import com.deloitte.bdh.data.service.BiEtlProcessorService;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.data.service.BiProcessorsService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-09-25
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlProcessorServiceImpl extends AbstractService<BiEtlProcessorMapper, BiEtlProcessor> implements BiEtlProcessorService {
    @Resource
    private BiEtlProcessorMapper etlProcessorMapper;
    @Autowired
    private NifiProcessService nifiProcessService;
    @Autowired
    private BiEtlParamsService etlParamsService;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private BiEtlModelService etlModelService;

    @Override
    public Pair<BiEtlProcessor, List<BiEtlParams>> getProcessor(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("BiEtlProcessorServiceImpl.getProcessor error:processorId 不嫩为空");
        }
        BiEtlProcessor processor = etlProcessorMapper.selectById(id);
        if (null == processor) {
            throw new RuntimeException("BiEtlProcessorServiceImpl.getProcessor error:未找到对应的对象");
        }
        //获取对应processor 参数集合
        List<BiEtlParams> paramsList = etlParamsService.list(
                new LambdaQueryWrapper<BiEtlParams>()
                        .eq(BiEtlParams::getRelCode, processor.getCode())
        );
        return new Pair(processor, paramsList);
    }

    @Override
    public List<Pair<BiEtlProcessor, List<BiEtlParams>>> getProcessorList(String relProcessorCode) {
        if (StringUtil.isEmpty(relProcessorCode)) {
            throw new RuntimeException("BiEtlProcessorServiceImpl.getProcessorList error : relProcessorCode 不嫩为空");
        }
        List<Pair<BiEtlProcessor, List<BiEtlParams>>> pairs = Lists.newArrayList();
        List<BiEtlProcessor> etlProcessorList = etlProcessorMapper.selectList(
                new LambdaQueryWrapper<BiEtlProcessor>().eq(BiEtlProcessor::getRelProcessorsCode, relProcessorCode));

        List<BiEtlParams> paramsList = etlParamsService.list(
                new LambdaQueryWrapper<BiEtlParams>()
                        .eq(BiEtlParams::getRelProcessorsCode, relProcessorCode)
        );
        if (CollectionUtils.isNotEmpty(etlProcessorList)) {
            for (BiEtlProcessor processor : etlProcessorList) {
                if (CollectionUtils.isEmpty(paramsList)) {
                    Pair<BiEtlProcessor, List<BiEtlParams>> pair = new Pair<>(processor, null);
                    pairs.add(pair);
                    continue;
                }

                List<BiEtlParams> etlParamsList = Lists.newArrayList();
                for (BiEtlParams params : paramsList) {
                    if (processor.getCode().equals(params.getRelCode())) {
                        etlParamsList.add(params);
                    }
                }
                Pair<BiEtlProcessor, List<BiEtlParams>> pair = new Pair<>(processor, etlParamsList);
                pairs.add(pair);
            }
        }
        return pairs;
    }

    @Override
    public BiEtlProcessor createProcessor(CreateProcessorDto dto) throws Exception {
        BiProcessors processors = processorsService
                .getOne(new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getCode, dto.getProcessorsCode()));

        BiEtlModel model = etlModelService
                .getOne(new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, processors.getRelModelCode()));

        BiEtlProcessor processor = new BiEtlProcessor();
        BeanUtils.copyProperties(dto, processor);
        processor.setCode(GenerateCodeUtil.genProcessor());
        processor.setTypeDesc(ProcessorTypeEnum.getTypeDesc(dto.getType()));
        processor.setStatus(RunStatusEnum.STOP.getKey());
        processor.setEffect(EffectEnum.DISABLE.getKey());
        processor.setValidate(YesOrNoEnum.NO.getKey());
        processor.setValidateMessage(YesOrNoEnum.NO.getvalue());
        processor.setCreateDate(LocalDateTime.now());
        processor.setRelProcessorsCode(dto.getProcessorsCode());
        processor.setProcessGroupId(model.getProcessGroupId());
        //nifi 创建 processor
        Map<String, Object> reqNifi = dto.getParams();
        reqNifi.put("name", dto.getName());
        reqNifi.put("id", model.getProcessGroupId());

        //此处去nifi value
        reqNifi.put("type", ProcessorTypeEnum.getNifiValue(dto.getType()));
        reqNifi.put("position", JsonUtil.string2Obj(dto.getPosition(), Map.class));
        Map<String, Object> source = nifiProcessService.createProcessor(reqNifi);

        processor.setProcessId(MapUtils.getString(source, "id"));
        processor.setVersion(NifiProcessUtil.getVersion(source));
        processor.setRelationships(JsonUtil.obj2String(NifiProcessUtil.getRelationShip(source)));
        etlProcessorMapper.insert(processor);
        return processor;
    }

    @Override
    public BiEtlProcessor updateProcessor(UpdateModelDto dto) throws Exception {
//        Map<String, Object> reqNifi = Maps.newHashMap();
//        Map<String, Object> source = nifiProcessService.updateProcessor(reqNifi);
        return null;
    }

    @Override
    public BiEtlProcessor runProcessor(EffectModelDto dto) throws Exception {
        return null;
    }

    @Override
    public void delProcessor(String id) throws Exception {
        BiEtlProcessor processor = etlProcessorMapper.selectById(id);
        nifiProcessService.delProcessor(processor.getProcessId());
        etlProcessorMapper.deleteById(processor.getId());
    }

}
