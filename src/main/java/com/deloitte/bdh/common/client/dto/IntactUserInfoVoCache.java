package com.deloitte.bdh.common.client.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ApiModel(value = "用户基础信息视图")
@Data
@Setter
@Getter
@ToString
public class IntactUserInfoVoCache implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7452095126041467697L;
	@ApiModelProperty(value = "用户ID")
	  private BigDecimal userId;
	  @ApiModelProperty(value = "用户名称")
	  private String fullName;
	  @ApiModelProperty(value = "用户移动电话")
	  private String mobilePhone;
	  @ApiModelProperty(value = "用户电话")
	  private String phoneCode;
	  @ApiModelProperty(value = "用户邮箱")
	  private String email;
	  @ApiModelProperty(value = "用户头像")
	  private String userImage;
	  @ApiModelProperty(value = "状态(0:锁定,1:待审核,2:正常)")
	  private String userStatus;
	  @ApiModelProperty(value = "用户生效时间")
	  private Date userStartDateActive;
	  @ApiModelProperty(value = "用户失效时间")
	  private Date userEndDateActive;
	  @ApiModelProperty(value = "租户ID")
	  private BigDecimal tenantId;
	  @ApiModelProperty(value = "租户名称")
	  private String tenantName;
	  @ApiModelProperty(value = "租户编码")
	  private String tenantCode;
	  @ApiModelProperty(value = "内部租户标识,0:否,1:是")
	  private Integer internalTenantFlag;
	  @ApiModelProperty(value = "生效标识,0:否,1:是")
	  private Integer activeFlag;
	  @ApiModelProperty(value = "租户生效时间")
	  private Date startDateActive;
	  @ApiModelProperty(value = "租户失效时间")
	  private Date endDateActive;
	  @ApiModelProperty(value = "用户租户头像")
	  private String tenantImage;
	  @ApiModelProperty(value = "租户生效时间")
	  private Date tenantStartDateActive;
	  @ApiModelProperty(value = "租户失效时间")
	  private Date tenantEndDateActive;
	  @ApiModelProperty(value = "用户组织ID")
	  private BigDecimal organizationId;
	  @ApiModelProperty(value = "组织CODE")
	  private String organizationCode;
	  @ApiModelProperty(value = "组织名称")
	  private String organizationName;
	  @ApiModelProperty(value = "组织币种")
	  private String currency;
	  @ApiModelProperty(value = "用户银行ID")
	  private BigDecimal userBankId;
	  @ApiModelProperty(value = "用户银行名称")
	  private String userBankName;
	  @ApiModelProperty(value = "用户银行支行名称")
	  private String userBankBranchName;
	  @ApiModelProperty(value = "用户银行账户名")
	  private String userBankAccountName;
	  @ApiModelProperty(value = "用户银行账号")
	  private String userBankAccountNumber;
	  @ApiModelProperty(value = "员工ID")
	  private BigDecimal employeeId;
	  @ApiModelProperty(value = "员工编号")
	  private String employeeNumber;
	  @ApiModelProperty(value = "性别(0:女,1:男)")
	  private String gender;
	  @ApiModelProperty(value = "员工部门ID")
	  private BigDecimal deptId;
	  @ApiModelProperty(value = "员工部门编号")
	  private String deptCode;
	  @ApiModelProperty(value = "员工部门名称")
	  private String deptName;
	  @ApiModelProperty(value = "员工部门领导")
	  private BigDecimal empDeptLeader;
	  @ApiModelProperty(value = "员工入职时间")
	  private Date entryTime;
	  @ApiModelProperty(value = "员工借款限额")
	  private BigDecimal empLoanLimitAmount;
	  @ApiModelProperty(value = "员工名称")
	  private String employeeName;
	  @ApiModelProperty(value = "员工血型")
	  private String empBloodType;
	  @ApiModelProperty(value = "员工离职时间")
	  private Date empDepartureTime;
	  @ApiModelProperty(value = "员工生日")
	  private Date empBirthday;
	  @ApiModelProperty(value = "员工籍贯")
	  private String empPlaceOfBirth;
	  @ApiModelProperty(value = "员工国籍")
	  private String empNationality;
	  @ApiModelProperty(value = "员工国籍唯一标识")
	  private String empNationalIdentifier;
	  @ApiModelProperty(value = "是否已婚：0未婚，1已婚")
	  private String empMaritalStatus;
	  @ApiModelProperty(value = "员工类型(1:正式员工，0:临时工)")
	  private String empType;
	  @ApiModelProperty(value = "员工主管ID")
	  private BigDecimal empSupervisorEmpId;
	  @ApiModelProperty(value = "员工身份证")
	  private String empIdCard;
	  @ApiModelProperty(value = "员工护照")
	  private String empPassport;
	  @ApiModelProperty(value = "员工职位ID")
	  private BigDecimal empPositionId;
	  @ApiModelProperty(value = "员工职级")
	  private String empPositionNo;
	  @ApiModelProperty(value = "员工职位编码")
	  private String empPositionCode;
	  @ApiModelProperty(value = "员工职位名称")
	  private String empPositionName;
	  @ApiModelProperty(value = "员工职位描述")
	  private String empPositionDescription;
	  @ApiModelProperty(value = "员工职位列表")
	  private String empPositionList;
	  @ApiModelProperty(value = "密码更新标识")
	  private String passwordFlag;

}
