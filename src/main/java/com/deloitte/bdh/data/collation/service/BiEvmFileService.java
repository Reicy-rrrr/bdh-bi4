package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiEvmFile;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.BiEtlDbFileUploadDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lw
 * @since 2021-02-01
 */
public interface BiEvmFileService extends Service<BiEvmFile> {

    /**
     * EVM文件上传
     *
     * @param fileUploadDto
     * @return
     */
    void uploadEvm(BiEtlDbFileUploadDto fileUploadDto);

}
