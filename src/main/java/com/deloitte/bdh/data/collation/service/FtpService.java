package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.FtpUploadResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chenghzhang
 */
public interface FtpService {

    /**
     * 上传文件到ftp服务器
     *
     * @param file     文件（csv类型文件）
     * @param tenantId 租户id
     * @return Pair<String, String>: left-文件地址 right-文件名称
     */
    FtpUploadResult uploadExcelFile(MultipartFile file, String tenantId);

    /**
     * 获取ftp文件字节数组
     *
     * @param filePath 文件目录
     * @param fileName 文件名称
     * @return
     */
    byte[] getFileBytes(String filePath, String fileName);

    /**
     * 删除ftp文件
     *
     * @param filePath 文件目录
     * @param fileName 文件名称
     * @return
     */
    boolean deleteFile(String filePath, String fileName);
}
