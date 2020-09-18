package com.deloitte.bdh.common.exception;


import com.deloitte.bdh.common.constant.ResultConstant;
import com.deloitte.bdh.common.util.StringUtil;
import java.util.Arrays;

/**
 * 自定义异常
 *
 * @author dahpeng
 * @create: 2017-08-25 14:50
 */
public class BizException extends RuntimeException {

	private static final long serialVersionUID = -2466703721851641645L;

	private final int errorCode;

	private final Object[] params;

	public BizException() {
		this.errorCode = 0;
		this.params = null;
	}

	public BizException(String message) {
		super(message);
		this.errorCode = 0;
		this.params = null;
	}
	public BizException(ResultConstant resultConstant){
		super(resultConstant.getMessage());
		this.errorCode=resultConstant.getCode();
		this.params=null;
	}

	public BizException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = 0;
		this.params = null;
	}

	public BizException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.params = null;
	}

	public BizException(int errorCode, String message, Object... params) {
		super(message);
		this.errorCode = errorCode;
		this.params = params;
	}

	public BizException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.params = null;
	}

	/**
	 * Returns the detail message string of this throwable.
	 *
	 * @return the detail message string of this {@code Throwable} instance (which may be {@code
	 * null}).
	 */
	@Override
	public String getMessage() {
		return StringUtil.replaceParams(super.getMessage(), params);
	}

	public int getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		return "BizException{" +
				"errorCode=" + errorCode +
				", params=" + Arrays.toString(params) +
				'}';
	}
}
