package com.deloitte.bdh.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

/**
 * @author dahpeng
 * @date 2020/02/18
 */
@ApiModel(description = "分页请求基类")
public class PageRequest<T> extends RetRequest<T> implements Serializable {
	@ApiModelProperty(value = "分页参数，页码,默认 0", example = "0")
	private Integer page = 0;
	@ApiModelProperty(value = "分页参数，每页数量,默认 10", example = "10")
	private Integer size =10;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "PageRequest{" +
				"page=" + page +
				", size=" + size +
				'}';
	}
}
