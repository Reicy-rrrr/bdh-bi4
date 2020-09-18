package com.deloitte.bdh.common.base;

import com.github.pagehelper.PageInfo;
import java.util.List;

/**
 * @author Ashen
 * @date 07/05/2018
 */
public class PageResult<T> {

	long total;
	boolean more;
	List<T> rows;

	public PageResult(PageInfo page, List<T>... rowList) {
		this.rows = page.getList();
		this.total = page.getTotal();
		this.more = page.isHasNextPage();
		if(rowList.length > 0){
			this.rows = rowList[0];
		}
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public boolean isMore() {
		return more;
	}

	public void setMore(boolean more) {
		this.more = more;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
