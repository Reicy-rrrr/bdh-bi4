package com.deloitte.bdh.common.util;

import org.springframework.core.task.AsyncTaskExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ThreadLocalHolder {
    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(() -> new ConcurrentHashMap<String, Object>(4) {
    });

    public static Map<String, Object> getThreadLocal() {
        return threadLocal.get();
    }

    public static String getTenantId() {
        Map<String, Object> map = threadLocal.get();
        return (String) map.get("tenantId");
    }

    public static String getTenantCode() {
        Map<String, Object> map = threadLocal.get();
        return (String) map.get("tenantCode");
    }

    public static String getOperator() {
        Map<String, Object> map = threadLocal.get();
        return (String) map.get("operator");
    }

    public static String getIp() {
        Map<String, Object> map = threadLocal.get();
        return (String) map.get("ip");
    }

    public static <T> T get(String key) {
        Map<String, Object> map = threadLocal.get();
        return (T) map.get(key);
    }

    public static <T> T get(String key, T defaultValue) {
        Map<String, Object> map = threadLocal.get();
        return map.get(key) == null ? defaultValue : (T) map.get(key);
    }

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        map.put(key, value);
    }

    public static void set(Map<String, Object> keyValueMap) {
        Map<String, Object> map = threadLocal.get();
        map.putAll(keyValueMap);
    }

    public static void remove() {
        threadLocal.remove();
    }

    public static <T> T remove(String key) {
        Map<String, Object> map = threadLocal.get();
        return (T) map.remove(key);
    }

    public static void clear(String prefix) {
        if (prefix == null) {
            return;
        }
        Map<String, Object> map = threadLocal.get();
        Set<Map.Entry<String, Object>> set = map.entrySet();
        List<String> removeKeys = new ArrayList<>();

        for (Map.Entry<String, Object> entry : set) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                removeKeys.add(key);
            }
        }
        for (String key : removeKeys) {
            map.remove(key);
        }
    }

    public static void clear() {
        threadLocal.remove();
    }

    public static boolean isEmpty() {
        return null == threadLocal.get();
    }


    public static void async(Async async) {
        AsyncTaskExecutor executor = SpringUtil.getBean("taskExecutor", AsyncTaskExecutor.class);
        Map<String, Object> local = getThreadLocal();
        executor.execute(() -> {
            ThreadLocalHolder.set(local);
            try {
                async.invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ThreadLocalHolder.clear();
        });
    }

    public interface Async {
        void invoke() throws Exception;
    }
}
