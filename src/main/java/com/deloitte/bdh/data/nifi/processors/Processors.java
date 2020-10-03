package com.deloitte.bdh.data.nifi.processors;


import com.deloitte.bdh.data.nifi.Nifi;

public interface Processors<T extends Nifi> {

    T etl(T context) throws Exception;
}
