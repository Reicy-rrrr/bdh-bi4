package com.deloitte.bdh.data.service.impl;

import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.model.resp.Processor;
import com.google.common.collect.Lists;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.dao.bi.BiProcessorsMapper;
import com.deloitte.bdh.data.model.resp.Processors;
import com.deloitte.bdh.data.service.BiEtlProcessorService;
import com.deloitte.bdh.data.service.BiProcessorsService;
import com.deloitte.bdh.common.base.AbstractService;
import javafx.util.Pair;
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
    private BiEtlProcessorService etlProcessorService;
    @Resource
    private BiProcessorsMapper processorsMapper;

    @Override
    public Processors getProcessors(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("BiProcessorsServiceImpl.getProcessors error:id 不嫩为空");
        }
        BiProcessors processors = processorsMapper.selectById(id);
        Processors result = new Processors();
        BeanUtils.copyProperties(processors, result);

        List<Pair<BiEtlProcessor, List<BiEtlParams>>> pairs = etlProcessorService.getProcessorList(processors.getCode());
        if (CollectionUtils.isEmpty(pairs)) {
            throw new RuntimeException("BiProcessorsServiceImpl.getProcessors error : 未找到目标对象 processor");
        }

        List<Processor> processorList = Lists.newArrayList();
        for (Pair<BiEtlProcessor, List<BiEtlParams>> pair : pairs) {
            Processor processor = new Processor();
            BeanUtils.copyProperties(pair.getKey(), processor);
            processor.setList(pair.getValue());
            processorList.add(processor);
        }
        result.setList(processorList);
        return result;
    }
}
