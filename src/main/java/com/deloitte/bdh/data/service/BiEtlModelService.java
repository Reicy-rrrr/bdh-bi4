package com.deloitte.bdh.data.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.model.request.*;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-09-24
 */
public interface BiEtlModelService extends Service<BiEtlModel> {

    /**
     * 基于租户获取Model列表
     *
     * @param dto
     * @return
     */
    PageResult<List<BiEtlModel>> getModelPage(GetModelPageDto dto);

    /**
     * 查看单个 Model
     *
     * @param id
     * @return
     */
    BiEtlModel getModel(String id);

    /**
     * 创建数据源
     *
     * @param dto
     * @return
     */
    BiEtlModel createModel(CreateModelDto dto) throws Exception;

    /**
     * 启用/禁用数据源
     *
     * @param dto
     * @return
     */
    BiEtlModel runProcesGroup(RunModelDto dto) throws Exception;

    /**
     * del数据源
     *
     * @param dto
     * @return
     */
    void delModel(String id) throws Exception;

    /**
     * 修改数据源
     *
     * @param dto
     * @return
     */
    BiEtlModel updateModel(UpdateModelDto dto) throws Exception;
}
