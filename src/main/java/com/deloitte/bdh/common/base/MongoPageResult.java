package com.deloitte.bdh.common.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * mongo分页查询结果
 *
 * @author chenghzhang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoPageResult<T> {
	@ApiModelProperty(value = "总条数", example = "100")
	long total;
	@ApiModelProperty(value = "是否更多", example = "true")
	boolean more;
	@ApiModelProperty(value = "头信息", example = "")
	List<String> headers;
	@ApiModelProperty(value = "数据行", example = "")
	List<T> rows;
}
