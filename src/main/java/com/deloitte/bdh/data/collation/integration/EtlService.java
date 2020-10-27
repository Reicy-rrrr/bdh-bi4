package com.deloitte.bdh.data.collation.integration;

import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.request.*;


public interface EtlService {

    /**
     * 引入数据源
     *
     * @param
     * @return
     */
    BiComponent joinResource(JoinResourceDto dto) throws Exception;





}
