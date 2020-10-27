package com.deloitte.bdh.data.collation.database;

import com.deloitte.bdh.data.collation.database.dto.CreateTableDto;

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
}
