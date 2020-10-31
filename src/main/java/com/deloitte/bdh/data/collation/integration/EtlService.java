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
    BiComponent joinResource(JoinComponentDto dto) throws Exception;

    /**
     * 移除数据源
     *
     * @param
     * @return
     */
    void removeResource(String code) throws Exception;

    /**
     * 输出组件
     *
     * @param
     * @return
     */
    BiComponent out(OutComponentDto dto) throws Exception;


}
