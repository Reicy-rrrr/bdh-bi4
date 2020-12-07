package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.model.FilePreReadResult;
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
     * 读取文件数据，并存储到关系型数据库中
     *
     * @param bytes       文件输字节数组
     * @param fileType    文件类型
     * @param columnTypes 字段类型
     * @param tableName   集合类型（建议：租户id + "_" + yyyyMMdd + "_" + dbId）
     */
    void readIntoDB(byte[] bytes, String fileType, Map<String, TableField> columnTypes, String tableName);
}
