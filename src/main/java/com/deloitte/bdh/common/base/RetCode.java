package com.deloitte.bdh.common.base;

/**
 * @Description: 响应码枚举，参考HTTP状态码的语义
 * @author dahpeng
 * @date 2018/6/11
 */
public enum RetCode {

	//layui 部分功能0为成功
	ZERO(0),

	// 成功
	SUCCESS(0),

	// 失败
	FAIL(400),

	// 未认证（签名错误）
	UNAUTHORIZED(401),

	// 接口不存在
	NOT_FOUND(404),

	// 服务器内部错误
	INTERNAL_SERVER_ERROR(500);

	public int code;

	RetCode(int code) {
		this.code = code;
	}
}
