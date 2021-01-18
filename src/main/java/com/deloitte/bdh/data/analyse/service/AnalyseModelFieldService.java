package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-21
 */
public interface AnalyseModelFieldService extends Service<BiUiModelField> {
    /**
     * 查看单个resource
     *
     * @param id
     * @return
     */
    BiUiModelField getResource(String id);

    public List<BiUiModelField> getTenantBiUiModelFields(String tenantId);

    /**
     * 创建页面
     *
     * @param dto
     * @return
     */
    BiUiModelField createResource(CreateResourcesDto dto) throws Exception;

    /**
     * del页面
     *
     * @param id
     * @return
     */
    void delResource(String id) throws Exception;

    /**
     * 修改页面
     *
     * @param dto
     * @return
     */
    BiUiModelField updateResource(UpdateResourcesDto dto) throws Exception;

    /**
     * getTables
     *
     * @param tableName
     * @return
     */
    Map<String, List<String>> getTables(String tableName);
}
