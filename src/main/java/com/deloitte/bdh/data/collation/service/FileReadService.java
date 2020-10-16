package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.resp.FilePreReadResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author chenghzhang
 */
public interface FileReadService {
    /**
     * 预读取文件内容
     *
     * @param file 文件（上传文件）
     * @return com.deloitte.bdh.data.model.resp.FilePreReadResult
     */
    FilePreReadResult preRead(MultipartFile file);

    /**
     * 读取文件数据，并存储到mongodb中
     *
     * @param file           文件（上传文件）
     * @param columnTypes    字段类型
     * @param collectionName 集合类型（建议：租户id + "_" + yyyyMMdd + "_" + dbId）
     */
    void read(MultipartFile file, Map<String, String> columnTypes, String collectionName);

    /**
     * 读取文件数据，并存储到mongodb中
     *
     * @param bytes          文件输字节数组
     * @param fileType       文件类型
     * @param columnTypes    字段类型
     * @param collectionName 集合类型（建议：租户id + "_" + yyyyMMdd + "_" + dbId）
     */
    void read(byte[] bytes, String fileType, Map<String, String> columnTypes, String collectionName);
}
