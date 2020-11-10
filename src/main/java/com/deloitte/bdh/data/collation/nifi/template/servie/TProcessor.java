package com.deloitte.bdh.data.collation.nifi.template.servie;


import java.util.Map;

public interface TProcessor {

    boolean update(String processorId,Map<String, Object> var) throws Exception;

}
