package com.deloitte.bdh.data.collation.database;

import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface DbSelector {
    String test(DbContext context) throws Exception;

    List<String> getTables(DbContext context) throws Exception;

    List<String> getFields(DbContext context) throws Exception;

    TableSchema getTableSchema(DbContext context) throws Exception;

    TableData getTableData(DbContext context) throws Exception;

    long getTableCount(DbContext context) throws Exception;

    /**
     * 执行查询sql语句
     * @param context
     * @return
     */
    List<Map<String, Object>> executeQuery(DbContext context) throws Exception;

    /**
     * 执行分页查询sql语句
     * @param context
     * @return
     */
    PageInfo<Map<String, Object>> executePageQuery(DbContext context) throws Exception;
}
