package com.deloitte.bdh.data.collation.database;

import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.vo.TableData;
import com.deloitte.bdh.data.collation.database.vo.TableSchema;

import java.util.List;


public interface DbProcess {

    String test(DbContext context) throws Exception;

    List<String> getTables(DbContext context) throws Exception;

    List<String> getFields(DbContext context) throws Exception;

    /**
     * 查询表结构
     *
     * @param context
     * @return
     * @throws Exception
     */
    TableSchema getTableSchema(DbContext context) throws Exception;

    /**
     * 查询表数据
     *
     * @param context
     * @return
     * @throws Exception
     */
    TableData getTableData(DbContext context) throws Exception;
}
