package com.deloitte.bdh.data.nifi.processors;


import com.deloitte.bdh.data.nifi.dto.Nifi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public abstract class AbStractProcessors<T extends Nifi> implements Processors<T> {
    protected static final Logger logger = LoggerFactory.getLogger(AbStractProcessors.class);


    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public T etl(T context) throws Exception {
        validateContext(context);
        try {
            positive(context);
        } catch (Exception e) {
            e.printStackTrace();
            reverse(context);
            throw new Exception(e);
        } finally {
            end(context);
        }
        return context;
    }

    protected abstract T positive(T context) throws Exception;

    protected abstract void reverse(T context) throws Exception;

    protected void end(T context) throws Exception {
    }

    protected void validateContext(T context) throws Exception {
        if (null == context.getMethod() || null == context.getModel()) {
            throw new Exception("校验失败:参数不合法");
        }
    }

}
