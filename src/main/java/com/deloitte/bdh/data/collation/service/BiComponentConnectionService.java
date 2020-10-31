package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiComponentConnection;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.ComponentLinkDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
public interface BiComponentConnectionService extends Service<BiComponentConnection> {

    /**
     * 组件关联
     *
     * @param
     * @return
     */
    BiComponentConnection link(ComponentLinkDto dto);


}
