package com.deloitte.bdh.data.analyse.dao.bi;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-21
 */
public interface BiUiDemoMapper {
    /**
     * 查询表字段信息
     *
     * @param querySql 查询sql
     * @return
     */
    List<Map<String, Object>> selectDemoList(@Param("querySql") String querySql);
}
