package com.deloitte.bdh.data.collation.nifi;

public interface EtlProcess<T> {

    T process(T var) throws Exception;

}
