package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.request.PreviewSqlDto;

/**
 * etl模板处理Service
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
public interface BiEtlModelHandlerService {

    /**
     * 预览sql
     *
     * @param dto
     * @return
     */
    String previewSql(PreviewSqlDto dto);

    /**
     * 组装sql
     *
     * @param modelId 模板id
     * @return
     */
    String createSql(String modelId);
}
