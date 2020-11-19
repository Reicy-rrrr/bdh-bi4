package com.deloitte.bdh.data.collation.database;

import com.deloitte.bdh.data.collation.database.dto.CreateTableDto;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableField;

import java.util.List;
import java.util.Map;

/**
 * 数据库处理器
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
public interface DbHandler {

    /**
     * 创建表（如果表已存在，先删除）
     *
     * @param dto
     * @return
     * @throws Exception
     */
    void createTable(CreateTableDto dto) throws Exception;

    /**
     * 创建表（如果表已存在，先删除）
     *
     * @param dbId            数据源id
     * @param targetTableName 目标表名
     * @param targetFields    目标字段
     * @return
     * @throws Exception
     */
    void createTable(String dbId, String targetTableName, List<TableField> targetFields) throws Exception;

    /**
     * 创建表（如果表已存在，先删除）
     *
     * @param targetTableName 目标表名
     * @param targetFields    目标字段
     * @return
     * @throws Exception
     */
    void createTable(String targetTableName, List<TableField> targetFields) throws Exception;


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
     * 查询表所有字段列表
     *
     * @param tableName 表名
     * @return
     */
    List<TableField> getTableFields(String tableName);

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

    /**
     * 删除表数据
     *
     * @param tableName 表名
     * @param condition 条件
     * @return
     */
    long delete(String tableName, String condition);

    /**
     * 删除表
     *
     * @param tableName 表名
     * @return
     */
    void drop(String tableName);

    /**
     * 检查表是否存在
     *
     * @param tableName 表名
     * @return
     */
    boolean isTableExists(String tableName);

    /**
     * 执行查询sql
     *
     * @param querySql
     * @return
     */
    List<Map<String, Object>> executeQuery(String querySql);

    /**
     * 执行查询批量插入
     *
     * @param tableName 目标表名
     * @param rows      数据行
     * @return
     */
    long executeInsert(String tableName, List<Map<String, Object>> rows);

    /**
     * 查询数据源组件表字段
     *
     * @param sourceComponentCode 数据源组件code
     * @return
     */
    List<TableField> getTargetTableFields(String sourceComponentCode);

    /**
     * 查询表数据量
     *
     * @return
     */
    long getCountLocal(String query);
}
