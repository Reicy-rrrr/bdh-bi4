package com.deloitte.bdh.common.base;

/**
 * 将结果转换为封装后的对象
 *
 * @author dahpeng
 * @date 2018/6/11
 */
public class RetResponse {

	private final static String SUCCESS = "success";
	private final static String FAIL = "fail";
	private final static boolean TRUE = true;
	private final static boolean FALSE = false;

	public static <T> RetResult<T> makeOKRsp() {
		return new RetResult<T>().setCommon().setSuccess(TRUE).setCode(RetCode.SUCCESS).setMessage(SUCCESS);
	}

	public static <T> RetResult<T> makeOKRsp(T data) {
		return new RetResult<T>().setCommon().setSuccess(TRUE).setCode(RetCode.SUCCESS).setMessage(SUCCESS).setData(data);
	}

	public static <T> RetResult<T> makeZeroRsp(T data) {
		return new RetResult<T>().setCommon().setSuccess(TRUE).setCode(RetCode.ZERO).setMessage(SUCCESS).setData(data);
	}

	public static <T> RetResult<T> makeErrRsp() {
		return new RetResult<T>().setCommon().setSuccess(FALSE).setCode(RetCode.FAIL).setMessage(FAIL);
	}

	public static <T> RetResult<T> makeErrRsp(String message) {
		return new RetResult<T>().setCommon().setSuccess(FALSE).setCode(RetCode.FAIL).setMessage(message);
	}

	public static <T> RetResult<T> makeRsp(boolean success, int code, String msg) {
		return new RetResult<T>().setCommon().setSuccess(success).setCode(code).setMessage(msg);
	}

	public static <T> RetResult<T> makeRsp(boolean success, int code, String msg, T data) {
		return new RetResult<T>().setCommon().setSuccess(success).setCode(code).setMessage(msg).setData(data);
	}

	public static <T> RetResult<T> makeSuccessRsp(T data) {
		return new RetResult<T>().success(data);
	}

	public static <T> RetResult<T> makeSuccessRsp(String msg, T data) {
		return new RetResult<T>().setMessage(msg).success();
	}

	public static <T> RetResult<T> makeFailRsp(int code, String msg) {
		return new RetResult<T>().fail(code, msg);
	}
}
