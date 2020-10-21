package com.deloitte.bdh.data.report.service;

import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.report.model.BiUiModelFolder;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.report.model.BiUiModelFolder;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-21
 */
public interface BiUiModelFolderService extends Service<BiUiModelFolder> {
    /**
     * 查看单个resource
     *
     * @param id
     * @return
     */
    BiUiModelFolder getResource(String id);

    /**
     * 创建页面
     *
     * @param dto
     * @return
     */
    BiUiModelFolder createResource(CreateResourcesDto dto) throws Exception;

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
    BiUiModelFolder updateResource(UpdateResourcesDto dto) throws Exception;
}
