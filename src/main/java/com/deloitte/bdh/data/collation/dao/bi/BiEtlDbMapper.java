package com.deloitte.bdh.data.collation.dao.bi;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    /**
     * 查询表字段信息
     *
     * @param querySql 查询sql
     * @return
     */
    List<Map<String, Object>> selectColumns(@Param("querySql") String querySql);

    /**
     * 查询表数据量
     *
     * @param querySql 查询sql
     * @return
     */
    long selectCount(@Param("querySql") String querySql);

    /**
     * 清空表数据
     *
     * @param truncateSql
     * @return
     */
    long truncateTable(@Param("truncateSql") String truncateSql);

    /**
     * 删除表数据
     *
     * @param deleteSql
     * @return
     */
    long delete(@Param("deleteSql") String deleteSql);

    /**
     * 执行查询sql
     *
     * @param querySql
     * @return
     */
    List<Map<String, Object>> executeQuery(@Param("querySql") String querySql);
}
