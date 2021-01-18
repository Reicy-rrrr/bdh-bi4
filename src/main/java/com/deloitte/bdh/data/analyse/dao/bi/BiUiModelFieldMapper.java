package com.deloitte.bdh.data.analyse.dao.bi;

import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-21
 */
public interface BiUiModelFieldMapper extends Mapper<BiUiModelField> {

    List<Map<String, Object>> selectTable(@Param("tableName") String table);

}
