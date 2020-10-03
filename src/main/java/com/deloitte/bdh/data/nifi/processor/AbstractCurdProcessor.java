package com.deloitte.bdh.data.nifi.processor;


import java.util.Map;

public abstract class AbstractCurdProcessor<T> {

    protected abstract Map<String, Object> save(T context) throws Exception;

    protected abstract Map<String, Object> rSave(T context) throws Exception;

    protected abstract Map<String, Object> delete(T context) throws Exception;

    protected abstract Map<String, Object> rDelete(T context) throws Exception;

    protected abstract Map<String, Object> update(T context) throws Exception;

    protected abstract Map<String, Object> rUpdate(T context) throws Exception;

    protected abstract Map<String, Object> validate(T context) throws Exception;
}
