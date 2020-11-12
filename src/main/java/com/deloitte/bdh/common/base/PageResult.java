package com.deloitte.bdh.common.base;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Ashen
 * @date 07/05/2018
 */
@Data
public class PageResult<T> {
	@ApiModelProperty(value = "当前页")
	private int pageNum;
	@ApiModelProperty(value = "每页大小")
	private int pageSize;
	@ApiModelProperty(value = "当前页大小")
	private int size;
	@ApiModelProperty(value = "总页数")
	private int pages;
	@ApiModelProperty(value = "总条数")
	private long total;
	@ApiModelProperty(value = "是否有下一页")
	private boolean more;
	@ApiModelProperty(value = "结果集")
	List<T> rows;

	public PageResult(PageInfo page, List<T>... rowList) {
		this.pageNum = page.getPageNum();
		this.pageSize = page.getPageSize();
		this.size = page.getSize();
		this.pages = page.getPages();
		this.rows = page.getList();
		this.total = page.getTotal();
		this.more = page.isHasNextPage();
		if(rowList.length > 0){
			this.rows = rowList[0];
		}
	}

}
