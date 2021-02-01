package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.BiEtlDbFile;
import com.deloitte.bdh.data.collation.model.request.BiEtlDbFileUploadDto;
import com.deloitte.bdh.data.collation.model.resp.BiEtlDbFileUploadResp;

/**
 * <p>
 * 数据源文件服务类
 * </p>
 *
 * @author chenghzhang
 * @since 2020-10-12
 */
public interface BiEtlDbFileService extends Service<BiEtlDbFile> {

    /**
     * 数据源文件上传
     *
     * @param fileUploadDto
     * @return
     */
    BiEtlDbFileUploadResp upload(BiEtlDbFileUploadDto fileUploadDto);

    /**
     * 数据源文件删除（只删除文件信息、ftp上的文件）
     *
     * @param deleteRequest
     * @return
     */
    Boolean delete(RetRequest<String> deleteRequest);

    /**
     * 根据数据源删除文件文件（只删除文件信息、ftp上的文件）
     *
     * @param dbId 数据源id
     * @return
     */
    Boolean deleteByDbId(String dbId);

    /**
     * EVM文件上传
     *
     * @param fileUploadDto
     * @return
     */
    void uploadEvm(BiEtlDbFileUploadDto fileUploadDto);

}
