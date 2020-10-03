package com.deloitte.bdh.data.nifi.processors;


import com.deloitte.bdh.data.nifi.Nifi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public abstract class AbStractProcessors<T extends Nifi> implements Processors<T> {
    protected static final Logger logger = LoggerFactory.getLogger(AbStractProcessors.class);


    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public T etl(T context) throws Exception {
        try {
            validateContext(context);
            positive(context);
//            int i = 1 / 0;
        } catch (Exception e) {
            e.printStackTrace();
            reverse(context);
            throw new Exception(e);
        }
        return context;
    }

    protected abstract T positive(T context) throws Exception;

    protected abstract void reverse(T context) throws Exception;

    protected void validateContext(T context) throws Exception {
        //todo 公共的
    }

}
