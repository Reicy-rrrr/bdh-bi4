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
