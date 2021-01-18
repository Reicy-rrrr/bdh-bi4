package com.deloitte.bdh.common.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.deloitte.bdh.common.util.StringUtil;
import java.time.LocalDateTime;

import com.deloitte.bdh.common.util.ThreadLocalHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jinqwang
 */
@Component
public class BaseModelMetaObjectHandler implements MetaObjectHandler {

	private static final Logger logger = LoggerFactory.getLogger(BaseModelMetaObjectHandler.class);

	@Override
	public void insertFill(MetaObject metaObject) {
		String operator = ThreadLocalHolder.getOperator();
		setFieldValByName("createDate", LocalDateTime.now(), metaObject);
		setFieldValByName("createUser", operator, metaObject);
		setFieldValByName("ip", getIp(), metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		String operator = ThreadLocalHolder.getOperator();
		setFieldValByName("modifiedDate", LocalDateTime.now(), metaObject);
		if(!StringUtil.isEmpty(operator)){
			setFieldValByName("modifiedUser", operator, metaObject);
		}
		setFieldValByName("ip", getIp(), metaObject);

	}

	private String getIp() {
		String ip = ThreadLocalHolder.getIp();
		return StringUtil.isEmpty(ip) ? "" : ip;
	}

}
