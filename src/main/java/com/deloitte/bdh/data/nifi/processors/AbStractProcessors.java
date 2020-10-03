package com.deloitte.bdh.data.nifi.processors;


import com.deloitte.bdh.data.nifi.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public abstract class AbStractProcessors implements Processors {
    protected static final Logger logger = LoggerFactory.getLogger(AbStractProcessors.class);


    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ProcessorContext etl(ProcessorContext context) throws Exception {
        try {
            validateContext(context);
            positive(context);
//            int i = 1 / 0;
        } catch (Exception e) {
            e.printStackTrace();
            context.removeProcessorTemp();
            reverse(context);
            throw new Exception(e);
        }
        return context;
    }

    protected abstract ProcessorContext positive(ProcessorContext context) throws Exception;

    protected abstract void reverse(ProcessorContext context) throws Exception;

    protected void validateContext(ProcessorContext context) throws Exception {

    }

}
