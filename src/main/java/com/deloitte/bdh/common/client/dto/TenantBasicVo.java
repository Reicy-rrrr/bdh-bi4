package com.deloitte.bdh.common.client.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 租户表
 * </p>
 *
 * @author Ashen
 * @since 2020-10-15
 */
@Data
@ApiModel(value = "租户基本信息", description = "租户基本信息")
public class TenantBasicVo {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "租户ID")
  private String tenantId;

  @TableField("NAME")
  private String name;

  @ApiModelProperty(value = "租户编码（系统生成）")
  private String tenantCode;

  @ApiModelProperty(value = "租户编码（客户设置）")
  private String tenantEncoding;
}
