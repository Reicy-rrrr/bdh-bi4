package com.deloitte.bdh.data.nifi;

public interface EtlProcess<T> {

    T process(T var) throws Exception;

}
