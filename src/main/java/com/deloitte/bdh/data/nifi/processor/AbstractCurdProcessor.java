package com.deloitte.bdh.data.nifi.processor;

import com.deloitte.bdh.data.nifi.ProcessorContext;

import java.util.Map;

public abstract class AbstractCurdProcessor {

    protected abstract Map<String, Object> save(ProcessorContext context) throws Exception;

    protected abstract Map<String, Object> rSave(ProcessorContext context) throws Exception;

    protected abstract Map<String, Object> delete(ProcessorContext context) throws Exception;

    protected abstract Map<String, Object> rDelete(ProcessorContext context) throws Exception;

    protected abstract Map<String, Object> update(ProcessorContext context) throws Exception;

    protected abstract Map<String, Object> rUpdate(ProcessorContext context) throws Exception;

    protected abstract Map<String, Object> validate(ProcessorContext context) throws Exception;
}
