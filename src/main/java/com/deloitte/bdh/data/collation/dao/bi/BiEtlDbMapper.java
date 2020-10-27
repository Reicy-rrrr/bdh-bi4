package com.deloitte.bdh.data.collation.dao.bi;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * BiEtlDbMapper
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Mapper
@Repository
public interface BiEtlDbMapper {
    /**
     * 创建表
     *
     * @param createSql 表创建sql
     * @return
     */
    int createTable(@Param("createSql") String createSql);
}
