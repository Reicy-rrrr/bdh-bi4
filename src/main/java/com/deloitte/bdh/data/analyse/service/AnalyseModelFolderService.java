package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-21
 */
public interface AnalyseModelFolderService extends Service<BiUiModelFolder> {
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
    BiUiModelFolder createResource(CreateResourcesDto dto);

    /**
     * del页面
     *
     * @param id
     * @return
     */
    void delResource(String id);

    /**
     * 修改页面
     *
     * @param dto
     * @return
     */
    BiUiModelFolder updateResource(UpdateResourcesDto dto);
}
