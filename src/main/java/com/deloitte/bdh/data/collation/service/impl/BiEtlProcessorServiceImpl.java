package com.deloitte.bdh.data.collation.service.impl;

import java.time.LocalDateTime;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlProcessorMapper;
import com.deloitte.bdh.data.collation.nifi.dto.CreateProcessorDto;
import com.deloitte.bdh.data.collation.model.request.EffectModelDto;
import com.deloitte.bdh.data.collation.model.request.UpdateModelDto;
import com.deloitte.bdh.data.collation.nifi.dto.Processor;
import com.deloitte.bdh.data.collation.service.BiEtlDbRefService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.deloitte.bdh.data.collation.service.BiEtlParamsService;
import com.deloitte.bdh.data.collation.service.BiEtlProcessorService;
import com.deloitte.bdh.common.base.AbstractService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
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
    private BiEtlModelService etlModelService;
    @Autowired
    private BiEtlDbRefService etlDbRefService;


    @Override
    public List<Processor> invokeProcessorList(String relProcessorsCode) {
        if (StringUtil.isEmpty(relProcessorsCode)) {
            throw new RuntimeException("BiEtlProcessorServiceImpl.getProcessorList error : relProcessorsCode 不能为空");
        }

        List<BiEtlProcessor> etlProcessorList = etlProcessorMapper.selectList(
                new LambdaQueryWrapper<BiEtlProcessor>().eq(BiEtlProcessor::getRelProcessorsCode, relProcessorsCode)
                        .orderByAsc(BiEtlProcessor::getCode));

        List<BiEtlParams> paramsList = etlParamsService.list(
                new LambdaQueryWrapper<BiEtlParams>()
                        .eq(BiEtlParams::getRelProcessorsCode, relProcessorsCode)
        );

        List<BiEtlDbRef> dbRefList = etlDbRefService.list(
                new LambdaQueryWrapper<BiEtlDbRef>()
                        .eq(BiEtlDbRef::getProcessorsCode, relProcessorsCode)
        );

        List<Processor> processorList = Lists.newLinkedList();
        if (CollectionUtils.isNotEmpty(etlProcessorList)) {
            etlProcessorList.stream().sorted(Comparator.comparing(BiEtlProcessor::getCode)).forEach(varEtlProcessorList -> {
                Processor processor = new Processor();
                BeanUtils.copyProperties(varEtlProcessorList, processor);
                List<BiEtlParams> biEtlParamsList = Lists.newArrayList();
                paramsList.forEach(varParamsList -> {
                    if (varParamsList.getRelCode().equals(varEtlProcessorList.getCode())) {
                        biEtlParamsList.add(varParamsList);
                    }
                });
                processor.setList(biEtlParamsList);

                dbRefList.forEach(varDbRefList -> {
                    if (varDbRefList.getProcessorCode().equals(varEtlProcessorList.getCode())) {
                        processor.setDbRef(varDbRefList);
                    }
                });
                processorList.add(processor);
            });
        }
        return processorList;
    }

    @Override
    public BiEtlProcessor createProcessor(CreateProcessorDto dto) throws Exception {
        BiProcessors processors = dto.getProcessors();

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
        processor.setRelProcessorsCode(processors.getCode());
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
//        processor.setVersion(NifiProcessUtil.getVersion(source));
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
