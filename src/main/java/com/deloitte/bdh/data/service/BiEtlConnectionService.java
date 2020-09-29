package com.deloitte.bdh.data.service;

import com.deloitte.bdh.data.model.BiEtlConnection;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.model.request.CreateConnectionDto;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-09-29
 */
public interface BiEtlConnectionService extends Service<BiEtlConnection> {

    /**
     * 创建 Connection
     *
     * @param dto
     * @return
     */
    BiEtlConnection createConnection(CreateConnectionDto dto) throws Exception;

    /**
     * 创建 Connection
     *
     * @param dto
     * @return
     */
    void dropConnection(String connectCode) throws Exception;


    /**
     * 创建 Connection
     *
     * @param dto
     * @return
     */
    void delConnection(String connectCode) throws Exception;

}
