package com.deloitte.bdh.common.exception;

import com.deloitte.bdh.common.base.RetCode;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.constant.ResultConstant;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolationException;

import com.deloitte.bdh.common.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author dahpeng
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 注解校验异常的处理
     *
     * @author hshu
     * @date 2019/01/14
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validExceptionHandler(MethodArgumentNotValidException e) {
        RetResult<Object> result = this.initResult();
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<String> errorMessages = new ArrayList<>();

        allErrors.forEach(objectError -> {
            FieldError fieldError = (FieldError) objectError;
            errorMessages.add(fieldError.getDefaultMessage());
        });
        //todo
        result.fail(ResultConstant.INVALID_PARAMS.getCode(), JsonUtil.obj2String(errorMessages));
//		result.fail(ResultConstant.INVALID_PARAMS.getCode(), ResultConstant.INVALID_PARAMS.getMessage());
        return this.buildResponseEntity(result);
    }

    /**
     * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
     *
     * @param ex the ConstraintViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex) {
        logger.error(ThrowableUtil.getStackTrace(ex));
        RetResult<Object> result = this.initResult();
        logger.info(StringUtils.join(ex.getConstraintViolations()));
        result.fail(ResultConstant.INVALID_PARAMS.getCode(), ResultConstant.INVALID_PARAMS.getMessage());
        return buildResponseEntity(result);
    }

    /**
     * 业务异常的处理
     */
    @ExceptionHandler(value = BizException.class)
    public ResponseEntity<Object> serviceExceptionHandler(BizException e) {
        RetResult<Object> result = this.initResult();
        if (e.getErrorCode() == 0) {
            result.fail(RetCode.FAIL.code, e.getMessage());
        } else {
            result.fail(e.getErrorCode(), e.getMessage());
        }
        logger.error(e.getMessage(), e);
        return this.buildResponseEntity(result);
    }

    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public ResponseEntity<Object> sqlSyntaxErrorExceptionHandler(SQLSyntaxErrorException e) {
        RetResult<Object> result = this.initResult();
        result.fail(RetCode.INTERNAL_SERVER_ERROR.code, "未查询到表或字段");
        logger.error(e.getMessage(), e);
        return this.buildResponseEntity(result);
    }

    /**
     * 其他异常统一处理
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> exceptionHandler(Exception e) {
        RetResult<Object> result = this.initResult();
//		result.fail(RetCode.INTERNAL_SERVER_ERROR.code, "服务器错误，请稍后再试~");
        result.fail(RetCode.INTERNAL_SERVER_ERROR.code, e.getMessage());
        logger.error(e.getMessage(), e);
        return this.buildResponseEntity(result);
    }

    /**
     * 统一返回
     *
     * @return ResponseEntity
     */
    private ResponseEntity<Object> buildResponseEntity(
            RetResult retResult) {
        return new ResponseEntity(retResult, HttpStatus.OK);
    }

    /**
     * 初始化返回结果
     *
     * @return Object
     */
    private RetResult<Object> initResult() {
        RetResult<Object> retResult = new RetResult<>();
        String traceId = MDC.get("traceId");
        retResult.setTraceId(traceId);
        return retResult;
    }

}
