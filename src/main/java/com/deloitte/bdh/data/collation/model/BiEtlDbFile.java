package com.deloitte.bdh.data.collation.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 上传文件信息实体类
 * </p>
 *
 * @author chenghzhang
 * @since 2020-10-12
 */
@TableName("BI_ETL_DB_FILE")
@Data
public class BiEtlDbFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 数据源id
     */
    @TableField("DB_ID")
    private String dbId;

    /**
     * 原始文件名
     */
    @TableField("ORIGINAL_FILE_NAME")
    private String originalFileName;

    /**
     * 存储文件名
     */
    @TableField("STORED_FILE_NAME")
    private String storedFileName;

    /**
     * 存储文件的key（存储OSS时返回的key）
     */
    @TableField("STORED_FILE_KEY")
    private String storedFileKey;

    /**
     * 文件类型
     */
    @TableField("FILE_TYPE")
    private String fileType;

    /**
     * 文件存储目录
     */
    @TableField("FILE_PATH")
    private String filePath;

    /**
     * 文件大小：默认以B为单位
     */
    @TableField("FILE_SIZE")
    private Long fileSize;

    /**
     * 文件失效时间
     */
    @TableField("EXPIRE_DATE")
    private LocalDateTime expireDate;

    /**
     * 有效标识：0-失效，1-有效
     */
    @TableField("ACTIVE_FLAG")
    private Integer activeFlag;

    /**
     * 读标识：0-已读，1-未读（已读状态不允许在读取入库）
     */
    @TableField("READ_FLAG")
    private Integer readFlag;

    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    @TableField("CREATE_USER")
    private String createUser;

    @TableField("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @TableField("MODIFIED_USER")
    private String modifiedUser;

    @TableField("IP")
    private String ip;

    @TableField("TENANT_ID")
    private String tenantId;
}
