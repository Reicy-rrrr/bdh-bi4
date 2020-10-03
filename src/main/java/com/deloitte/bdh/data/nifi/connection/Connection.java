package com.deloitte.bdh.data.nifi.connection;


import com.deloitte.bdh.data.nifi.Nifi;

import java.util.Map;

public interface Connection<T extends Nifi> {

    Map<String, Object> pConnect(T context) throws Exception;

    Map<String, Object> rConnect(T context) throws Exception;

}
