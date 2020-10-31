package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiEtlConnection;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.nifi.dto.CreateConnectionDto;

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
     * @param connection
     * @return
     */
    void dropConnection(BiEtlConnection connection) throws Exception;


    /**
     * 创建 Connection
     *
     * @param connection
     * @return
     */
    void delConnection(BiEtlConnection connection) throws Exception;

}
