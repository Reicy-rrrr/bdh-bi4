package com.deloitte.bdh.data.service;

import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.model.resp.Processors;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-09-27
 */
public interface BiProcessorsService extends Service<BiProcessors> {
    /**
     * 查看单个 Processor
     *
     * @param processorsId
     * @return
     */
    Processors getProcessors(String processorsId);

    /**
     * 查看模板下面已引用的 PROCESSORS 集合
     *
     * @param modelId
     * @return
     */
    List<Processors> getProcessorsList(String modelId);

}
