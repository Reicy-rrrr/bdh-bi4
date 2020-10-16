package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiEtlProcessor;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.nifi.dto.CreateProcessorDto;
import com.deloitte.bdh.data.collation.model.request.EffectModelDto;
import com.deloitte.bdh.data.collation.model.request.UpdateModelDto;
import com.deloitte.bdh.data.collation.nifi.dto.Processor;

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
