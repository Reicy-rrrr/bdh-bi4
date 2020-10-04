package com.deloitte.bdh.data.service;

import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.nifi.dto.CreateProcessorDto;
import com.deloitte.bdh.data.model.request.EffectModelDto;
import com.deloitte.bdh.data.model.request.UpdateModelDto;
import com.deloitte.bdh.data.nifi.dto.Processor;
import javafx.util.Pair;

import java.util.List;

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
     * @param relProcessorsCode
     * @return
     */
    List<Processor>  invokeProcessorList(String relProcessorsCode);


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


}
