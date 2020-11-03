package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.component.model.ComponentModel;

/**
 * etl模板处理Service
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
public interface BiEtlModelHandleService {

    /**
     * 处理组件
     *
     * @param modelCode     模板code
     * @param componentCode 组件code
     * @return ComponentModel
     */
    ComponentModel handleComponent(String modelCode, String componentCode);

    /**
     * 处理模板
     *
     * @param modelCode 模板code
     * @return ComponentModel
     */
    ComponentModel handleModel(String modelCode);

    /**
     * 处理组件预览sql（只有在处理完以后的组件才能处理预览sql）
     *
     * @param componentModel 组件模型
     * @return ComponentModel
     */
    void handlePreviewSql(ComponentModel componentModel);
}
