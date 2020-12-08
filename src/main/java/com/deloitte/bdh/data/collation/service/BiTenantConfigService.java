package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiTenantConfig;
import com.deloitte.bdh.common.base.Service;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-12-08
 */
public interface BiTenantConfigService extends Service<BiTenantConfig> {

    void init() throws Exception;

    String getGroupId();

    String getControllerServiceId();

}
