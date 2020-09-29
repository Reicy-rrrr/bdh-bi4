package com.deloitte.bdh.data.service;

import com.deloitte.bdh.data.model.resp.FtpUploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface FtpService {

    /**
     * 上传文件到ftp服务器
     *
     * @param file     文件（csv类型文件）
     * @param tenantId 租户id
     * @return Pair<String, String>: left-文件地址 right-文件名称
     */
    FtpUploadResult uploadExcelFile(MultipartFile file, String tenantId);
}
