package com.deloitte.bdh.common.base;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * Service层基础接口，其他Service接口请继承该接口
 *
 * @author dahpeng
 * @date 2018/6/11
 */
public interface Service<T> extends IService<T> {

	String getSequence();
}
