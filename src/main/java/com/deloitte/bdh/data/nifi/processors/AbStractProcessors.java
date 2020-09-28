package com.deloitte.bdh.data.nifi.processors;

import java.time.LocalDateTime;

import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.data.enums.BiProcessorsTypeEnum;
import com.deloitte.bdh.data.enums.EffectEnum;
import com.deloitte.bdh.data.enums.YesOrNoEnum;
import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.service.BiProcessorsService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


public abstract class AbStractProcessors implements Processors {
    private static final Logger logger = LoggerFactory.getLogger(AbStractProcessors.class);
    @Autowired
    private BiProcessorsService processorsService;


    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessorContext etl(ProcessorContext context) throws Exception {
        try {
            validateContext(context);
            positive(context);
        } catch (Exception e) {
            e.printStackTrace();
            reverse(context);
            throw new RuntimeException(e);
        }
        return context;
    }

    protected abstract ProcessorContext positive(ProcessorContext context) throws Exception;

    protected abstract void db(ProcessorContext context) throws Exception;

    protected abstract void reverse(ProcessorContext context) throws Exception;

    protected void validateContext(ProcessorContext context) throws Exception {
        if (null == context.getMethod() || CollectionUtils.isEmpty(context.getEnumList())) {
            throw new RuntimeException("参数缺失");
        }
    }

}
