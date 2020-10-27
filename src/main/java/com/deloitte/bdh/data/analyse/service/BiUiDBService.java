package com.deloitte.bdh.data.analyse.service;

import java.util.List;

public interface BiUiDBService {
    /**
     * 获取所有数据源
     * @return
     */
    public List<String> getAllDataSource();

    /**
     * 根据数据源获取表
     * @param dataSource
     * @return
     */
    public List<String> getAllTable(String dataSource);

    /**
     * 根据表名获取字段
     * @param tableName
     * @return
     */
    public List<String> getAllColumns(String tableName);
}
