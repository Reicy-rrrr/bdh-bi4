package com.deloitte.bdh.data.collation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 组件树对象
 *
 * @author chenghzhang
 * @since 2020-10-26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiComponentTree implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID", example = "10")
    private String id;

    @ApiModelProperty(value = "编码", example = "10")
    private String code;

    @ApiModelProperty(value = "处理器名称", example = "10")
    private String name;

    @ApiModelProperty(value = "组件类型", example = "10")
    private String type;

    @ApiModelProperty(value = "是否有效", example = "10")
    private String effect;

    @ApiModelProperty(value = "所属模板code", example = "10")
    private String refModelCode;

    @ApiModelProperty(value = "关联映射code（数据源组件）", example = "10")
    private String refMappingCode;

    @ApiModelProperty(value = "版本号", example = "10")
    private String version;

    @ApiModelProperty(value = "坐标", example = "10")
    private String position;

    @ApiModelProperty(value = "CREATE_DATE", example = "10")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "CREATE_USER", example = "10")
    private String createUser;

    @ApiModelProperty(value = "MODIFIED_DATE", example = "10")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @ApiModelProperty(value = "MODIFIED_USER", example = "10")
    private String modifiedUser;

    @ApiModelProperty(value = "IP", example = "127.0.0.1")
    private String ip;

    @ApiModelProperty(value = "TENANT_ID", example = "10")
    private String tenantId;

    @ApiModelProperty(value = "从组件（前面的组件）")
    private List<BiComponentTree> from;
}
