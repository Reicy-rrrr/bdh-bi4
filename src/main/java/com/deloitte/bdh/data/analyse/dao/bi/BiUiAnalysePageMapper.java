package com.deloitte.bdh.data.analyse.dao.bi;

import com.deloitte.bdh.common.base.Mapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.SelectPublishedPageDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface BiUiAnalysePageMapper extends Mapper<BiUiAnalysePage> {

    List<AnalysePageDto> selectPublishedPage(@Param("queryDto") SelectPublishedPageDto queryDto);

}
