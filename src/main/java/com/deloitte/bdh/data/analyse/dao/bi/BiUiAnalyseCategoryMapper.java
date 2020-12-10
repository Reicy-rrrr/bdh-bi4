package com.deloitte.bdh.data.analyse.dao.bi;

import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.common.base.Mapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.request.SelectCategoryDto;
import com.deloitte.bdh.data.analyse.model.request.SelectPublishedPageDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseCategoryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
public interface BiUiAnalyseCategoryMapper extends Mapper<BiUiAnalyseCategory> {

    List<AnalyseCategoryDto> selectCategory(@Param("queryDto") SelectCategoryDto queryDto);

}
