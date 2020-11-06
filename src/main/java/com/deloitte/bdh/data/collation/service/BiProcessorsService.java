package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.deloitte.bdh.common.base.Service;

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

    void runState(String code, RunStatusEnum state, boolean isGroup) throws Exception;

    void removeProcessors(String processorsCode, String dbId) throws Exception;

    void clearRequest(String processorsCode) throws Exception;


}
