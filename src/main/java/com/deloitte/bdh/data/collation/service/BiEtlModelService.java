package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.CreateModelDto;
import com.deloitte.bdh.data.collation.model.request.EffectModelDto;
import com.deloitte.bdh.data.collation.model.request.GetModelPageDto;
import com.deloitte.bdh.data.collation.model.request.UpdateModelDto;
import com.deloitte.bdh.data.collation.model.resp.ModelResp;

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

    void initModelTree();

    /**
     * 基于租户获取Model列表
     *
     * @param
     * @return
     */
    List<BiEtlModel> getModelTree(String userFlag);


    /**
     * 基于租户获取Model列表
     *
     * @param dto
     * @return
     */
    PageResult<List<ModelResp>> getModelPage(GetModelPageDto dto);

    /**
     * 创建数据源
     *
     * @param dto
     * @return
     */
    BiEtlModel createModel(CreateModelDto dto) throws Exception;

    /**
     * 启用/禁用Model
     *
     * @param dto
     * @return
     */
    BiEtlModel effectModel(EffectModelDto dto);

    /**
     * delModel
     *
     * @param id
     * @return
     */
    void delModel(String id) throws Exception;

    /**
     * 修改Model
     *
     * @param dto
     * @return
     */
    BiEtlModel updateModel(UpdateModelDto dto) throws Exception;

    /**
     * 运行/停止 Model
     *
     * @param modelCode
     * @return
     */
    BiEtlModel runModel(String modelCode) throws Exception;

    /**
     * 启动时校验
     *
     * @param
     * @return
     */
    void runValidate(String modelCode) throws Exception;


    /**
     * 运行/停止 Model
     *
     * @param modelCode
     * @return
     */
    void trigger(String modelCode) throws Exception;

}
