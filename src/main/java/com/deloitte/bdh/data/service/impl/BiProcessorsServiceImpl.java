package com.deloitte.bdh.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.model.resp.ProcessorsResp;
import com.deloitte.bdh.data.service.BiEtlModelService;
import com.google.common.collect.Lists;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.dao.bi.BiProcessorsMapper;
import com.deloitte.bdh.data.service.BiProcessorsService;
import com.deloitte.bdh.common.base.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

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
    private BiEtlModelService modelService;
    @Resource
    private BiProcessorsMapper processorsMapper;


    @Override
    public List<ProcessorsResp> getProcessorsList(String modelId) {
        if (StringUtil.isEmpty(modelId)) {
            throw new RuntimeException("BiProcessorsServiceImpl.getProcessors error:modelId 不嫩为空");
        }
        BiEtlModel model = modelService.getModel(modelId);
        List<BiProcessors> processorList = processorsMapper.selectList(
                new LambdaQueryWrapper<BiProcessors>().eq(BiProcessors::getRelModelCode, model.getCode()));

        List<ProcessorsResp> result = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(processorList)) {
            for (BiProcessors biProcessors : processorList) {
                ProcessorsResp processors = new ProcessorsResp();
                BeanUtils.copyProperties(biProcessors, processors);
                result.add(processors);
            }
        }
        return result;
    }
}
