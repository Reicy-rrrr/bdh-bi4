package com.deloitte.bdh.data.collation.dao.bi;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * DbSelectorMapper
 *
 * @author chenghzhang
 * @date 2020/11/25
 */
@Mapper
public interface DbSelectorMapper {
    /**
     * 执行查询sql
     *
     * @param querySql
     * @return
     */
    @Select("${querySql}")
    List<Map<String, Object>> executeQuery(@Param("querySql") String querySql);
}
