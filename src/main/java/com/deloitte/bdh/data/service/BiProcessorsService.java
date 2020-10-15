package com.deloitte.bdh.data.service;

import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.nifi.dto.RunContext;

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

    List<BiProcessors> getPreChain(String processorsCode);

    void preview(RunContext context) throws Exception;

    void stopAndClear(String processGroupId, String modelCode) throws Exception;

    void runState(String id, String state, boolean isGroup) throws Exception;

}
