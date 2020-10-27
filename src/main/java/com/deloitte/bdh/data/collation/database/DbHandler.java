package com.deloitte.bdh.data.collation.database;

import com.deloitte.bdh.data.collation.database.dto.CreateTableDto;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableField;

import java.util.List;

/**
 * 数据库处理器
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
public interface DbHandler {

    /**
     * 创建表
     *
     * @param dto
     * @return
     * @throws Exception
     */
    void createTable(CreateTableDto dto) throws Exception;

    /**
     * 创建表
     *
     * @param dbId            数据源id
     * @param targetTableName 目标表名
     * @param targetFields    目标字段
     * @return
     * @throws Exception
     */
    void createTable(String dbId, String targetTableName, List<TableField> targetFields) throws Exception;

    /**
     * 查询所有表名列表
     *
     * @return
     */
    List<String> getTables();

    /**
     * 查询表所有字段列表
     *
     * @param tableName 表名
     * @return
     */
    List<TableColumn> getColumns(String tableName);

    /**
     * 查询表数据量
     *
     * @param tableName 表名
     * @param condition 查询条件
     * @return
     */
    long getCount(String tableName, String condition);

    /**
     * 清空表数据
     *
     * @param tableName 表名
     * @return
     */
    long truncateTable(String tableName);
}
