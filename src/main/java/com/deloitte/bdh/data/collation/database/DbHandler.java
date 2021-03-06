package com.deloitte.bdh.data.collation.database;

import com.deloitte.bdh.data.collation.database.dto.CreateTableDto;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.database.po.TableInfo;
import com.github.pagehelper.PageInfo;

import java.util.LinkedHashMap;
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
    void createTable(String dbId, String targetTableName, List<TableField> targetFields);

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
     * @return List<String>
     */
    List<String> getTableNameList();

    /**
     * 查询所有表列表
     *
     * @return List<TableInfo>
     */
    List<TableInfo> getTableList();

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
     * 删除字段
     *
     * @param tableName 表名
     * @return
     */
    void dropFields(String tableName, String... field);

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
     * 执行查询sql
     *
     * @param querySql
     * @return
     */
    List<LinkedHashMap<String, Object>> executeQueryLinked(String querySql);

    /**
     * 执行分页查询sql
     *
     * @param querySql 查询sql
     * @param page     当前页
     * @param size     每页记录数
     * @return PageInfo
     */
    PageInfo<Map<String, Object>> executePageQuery(String querySql, Integer page, Integer size);

    /**
     * 执行查询批量插入
     *
     * @param tableName 目标表名
     * @param rows      数据行
     * @return
     */
    long executeInsert(String tableName, List<LinkedHashMap<String, Object>> rows);

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

    /**
     * 组装上下文
     *
     * @return
     */
    DbContext getDbContext(String dbId);

    /**
     * 获取建表语句
     * @param tableName
     * @return
     */
    String getCreateSql(String tableName);
}
