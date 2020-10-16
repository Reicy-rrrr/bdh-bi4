package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiEtlDbService;
import com.deloitte.bdh.common.base.Service;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chenghzhang
 * @since 2020-09-30
 */
public interface BiEtlDbCSService extends Service<BiEtlDbService> {

    String insert(BiEtlDbService biEtlDbService);

    int update(BiEtlDbService biEtlDbService);

    int deleteById(String id);

    List<BiEtlDbService> list(BiEtlDbService biEtlDbService);
}
