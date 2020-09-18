package com.deloitte.bdh.common.constant;

/**
 * 系统接口结果常量枚举类
 *
 * @author dahpeng
 */
public enum ResultConstant {

	/**
	 * 成功
	 */
	SUCCESS(0, "success","成功","成功"),

	/**
	 * 操作失败
	 */
	FAILED(400, "failed","操作失败","操作失敗"),

	/**
	 * 程序错误
	 */
	INTERNAL_SERVER_ERROR(500, "internal server error","程序错误","程式錯誤"),

	/**
	 * 错误
	 */
	ERROR(888, "error","错误","錯誤"),

	/**
	 * 鉴权不通过
	 */
	NOTAUTHC(777, "notauthc","鉴权不通过","鑒權不通過"),

	INVALID_PARAMS(999, "Validation error", "入参校验不通过","入參校驗不通過"),

	/**
	 * 无效长度
	 */
	INVALID_LENGTH(10001, "Invalid length","无效长度","無效長度"),

	/**
	 * 用户名不能为空
	 */
	EMPTY_USERNAME(10101, "Username cannot be empty","用户名不能为空","用戶名不能為空"),

	/**
	 * 密码不能为空
	 */
	EMPTY_PASSWORD(10102, "Password cannot be empty","密码不能为空","密碼不能為空"),

	/**
	 * 帐号不存在
	 */
	INVALID_USERNAME(10103, "Account does not exist","帐号不存在","帳號不存在"),

	/**
	 * 密码错误
	 */
	INVALID_PASSWORD(10104, "Invalid Password","密码错误","密碼錯誤"),

	/**
	 * 无效帐号
	 */
	INVALID_ACCOUNT(10105, "Invalid account","无效帐号","無效帳號"),

	/**
	 * 手机号无效，无该手机号的用户
	 */
	INVALID_MOBILE_PHONE(10106, "Invalid mobilePhone","手机号无效，无该手机号的用户","手機號無效，無該手機號的用戶"),

	/**
	 * 个人信息的手机号或邮箱不正确
	 */
	INVALID_MOBILE_EMAIL(10107, "Invalid Email or Phone Number","无效的邮箱或手机号","無效的郵箱或手機號"),

	/**
	 * 原密码错误
	 */
	INVALID_OLD_PASSWORD(10108, "Original Password error","原密码错误","原密碼錯誤"),

	/**
	 * 超出短信限额阈值
	 */
	MAX_MESSAGE_LIMIT(10109,"Exceed Message Limit control","超出短信限额阈值","超出短信限額閾值"),

	/**
	 * 无效验证码
	 */
	INVALID_VERIFY_CODE(10110,"Invalid Code","无效验证码","無效驗證碼"),

	/**
	 * 密码一天之内只能修改一次
	 */
	INVALID_PASSWORD_PROTECTED(10111, "Passwords can only be changed once a day","密码一天之内只能修改一次","密碼一天之內只能修改一次"),

	/**
	 * 新密码已经存在
	 */
	INVALID_PASSWORD_EXISTS(10112, "The new password can't be the password you used","新密码不能是您曾经用过的密码","新密碼不能是您曾經用過的密碼"),

	/**
	 * 密码已过期
	 */
	INVALID_PASSWORD_EXPIRY(10113, "Your password has expired. Please change your password immediately and login again","您的密码已过期，请立即修改密码，重新登录",
			"您的密碼已過期，請立即修改密碼，重新登錄"),

	/**
	 * 账号已过期
	 */
	INVALID_ACCOUNT_EXPIRY(10114, "The account has expired.","该账户已失效","該帳戶已失效"),

	/*
	 *邮箱或密码错误统一枚举值
	 */
	INVALID_EMAILORPASSWORD_EXPIRY(10115,"Email or password is wrong","邮箱或密码错误","郵箱或密碼錯誤"),

	/*
	 * 重复发送频率
	 */
	LOGIN_CODE_REPEAT_SEND(10116,"Send too often please try again later","发送过于频繁 请稍后再试","發送過於頻繁 請稍後再試"),


	/**
	 * 密码重试计数
	 */
	PASSWORD_RETRY_COUNT_1(10117, "Login retry count", "登录重试次数","登錄重試次數"),

	/**
	 * 密码重试计数
	 */
	PASSWORD_RETRY_COUNT_2(10117, "Login retry count", "登录重试次数","登錄重試次數"),

	/**
	 * 登录重试限制
	 */
	PASSWORD_RETRY_LIMIT_1(10118, "Login retry restrictions", "登录重试限制","登錄重試限制"),

	/**
	 * 登录重试限制
	 */
	PASSWORD_RETRY_LIMIT_2(10118, "Login retry restrictions", "登录重试限制","登錄重試限制"),

	/**
	 * 登录重试限制
	 */
	PASSWORD_RETRY_LIMIT_3(10118, "Login retry restrictions", "登录重试限制","登錄重試限制"),

	;

	private int code;

	private String message;

	private String comment;

	private String hkComment;
	ResultConstant(int code, String message,String comment,String hkComment) {
		this.code = code;
		this.message = message;
		this.comment = comment;
		this.hkComment = hkComment;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getComment() {
		return comment;
	}

	public String getHkComment() {
		return hkComment;
	}
}
