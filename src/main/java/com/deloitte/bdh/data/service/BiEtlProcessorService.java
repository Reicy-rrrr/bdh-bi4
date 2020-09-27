package com.deloitte.bdh.data.service;

import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.model.request.CreateProcessorDto;
import com.deloitte.bdh.data.model.request.EffectModelDto;
import com.deloitte.bdh.data.model.request.UpdateModelDto;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-09-25
 */
public interface BiEtlProcessorService extends Service<BiEtlProcessor> {
    /**
     * 查看单个 Processor
     *
     * @param id
     * @return
     */
    Pair<BiEtlProcessor, List<BiEtlParams>> getProcessor(String id);

    /**
     * 根据 relProcessorCode 获取所有 processor
     *
     * @param relProcessorCode
     * @return
     */
    List<Pair<BiEtlProcessor, List<BiEtlParams>>> getProcessorList(String relProcessorCode);


    /**
     * 创建 Processor
     *
     * @param dto
     * @return
     */
    BiEtlProcessor createProcessor(CreateProcessorDto dto) throws Exception;

    /**
     * 修改 Processor
     *
     * @param dto
     * @return
     */
    BiEtlProcessor updateProcessor(UpdateModelDto dto) throws Exception;


    /**
     * 启用/禁用 Processor
     *
     * @param dto
     * @return
     */
    BiEtlProcessor runProcessor(EffectModelDto dto) throws Exception;

    /**
     * delProcessor
     *
     * @param
     * @return
     */
    void delProcessor(String id) throws Exception;

    /**
     * 关联数据源
     *
     * @param
     * @return
     */
    Map<String, Object> joinResource(String processorId, String controllerServiceId, String userId, String tableName) throws Exception;


}
