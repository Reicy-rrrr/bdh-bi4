package com.deloitte.bdh.data.collation.nifi.exception;


import com.deloitte.bdh.common.constant.ResultConstant;
import com.deloitte.bdh.common.util.StringUtil;

import java.util.Arrays;

/**
 * 自定义异常
 *
 */
public class NifiException extends RuntimeException {

	private static final long serialVersionUID = -2466703721851641645L;

	private final int errorCode;

	private final Object[] params;

	public NifiException() {
		this.errorCode = 0;
		this.params = null;
	}

	public NifiException(String message) {
		super(message);
		this.errorCode = 0;
		this.params = null;
	}
	public NifiException(ResultConstant resultConstant){
		super(resultConstant.getMessage());
		this.errorCode=resultConstant.getCode();
		this.params=null;
	}

	public NifiException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = 0;
		this.params = null;
	}

	public NifiException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.params = null;
	}

	public NifiException(int errorCode, String message, Object... params) {
		super(message);
		this.errorCode = errorCode;
		this.params = params;
	}

	public NifiException(int errorCode, String message, Throwable cause) {
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
		return "NifiException{" +
				"errorCode=" + errorCode +
				", params=" + Arrays.toString(params) +
				'}';
	}
}
