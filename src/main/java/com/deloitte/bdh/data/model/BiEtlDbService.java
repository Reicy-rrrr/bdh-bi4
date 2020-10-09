package com.deloitte.bdh.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author chenghzhang
 * @since 2020-09-30
 */
@Data
@TableName("BI_ETL_DB_SERVICE")
public class BiEtlDbService implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 数据源id
     */
    @TableField("DB_ID")
    private String dbId;

    /**
     * NIFI ControllerService的id
     */
    @TableField("SERVICE_ID")
    private String serviceId;

    /**
     * NIFI Process的属性名称
     */
    @TableField("PROPERTY_NAME")
    private String propertyName;

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
