package com.deloitte.bdh.data.collation.nifi.processors;


import com.deloitte.bdh.data.collation.nifi.dto.Nifi;

public interface Processors<T extends Nifi> {

    T etl(T context) throws Exception;
}
