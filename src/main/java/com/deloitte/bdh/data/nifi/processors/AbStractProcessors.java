package com.deloitte.bdh.data.nifi.processors;


import com.deloitte.bdh.data.nifi.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


public abstract class AbStractProcessors implements Processors {
    protected static final Logger logger = LoggerFactory.getLogger(AbStractProcessors.class);


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ProcessorContext etl(ProcessorContext context) throws Exception {
        try {
            validateContext(context);
            positive(context);
//            int i=1/0;
        } catch (Exception e) {
            e.printStackTrace();
            reverse(context);
            throw new RuntimeException(e);
        }
        return context;
    }

    protected abstract ProcessorContext positive(ProcessorContext context) throws Exception;

    protected abstract void reverse(ProcessorContext context) throws Exception;

    protected void validateContext(ProcessorContext context) throws Exception {
        if (null == context.getMethod() || CollectionUtils.isEmpty(context.getEnumList())) {
            throw new RuntimeException("参数缺失");
        }
    }

}
