package com.deloitte.bdh.data.collation.dao.bi;

import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.common.base.Mapper;
import com.deloitte.bdh.data.collation.model.request.SelectDataSetDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lw
 * @since 2020-12-10
 */
public interface BiDataSetMapper extends Mapper<BiDataSet> {

    List<BiDataSet> selectDataSetCategory(@Param("queryDto") SelectDataSetDto queryDto);
}
