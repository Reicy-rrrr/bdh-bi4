package com.deloitte.bdh.common.base;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface Mapper<T> extends BaseMapper<T> {

	/**
	 * 查询sequence
	 *
	 * @return
	 */
	String selectSequence();
}
