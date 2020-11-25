package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.common.base.Service;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
public interface BiEtlMappingConfigService extends Service<BiEtlMappingConfig> {

    /**
     * 校验数据源相关信息
     *
     * @param
     * @return
     */
    void validateSource(String modelCode) throws Exception;

    /**
     * 校验数据源相关信息
     *
     * @param
     * @return
     */
    String validateSource(BiEtlMappingConfig config) ;

}
