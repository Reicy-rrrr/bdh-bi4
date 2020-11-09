package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.data.analyse.model.datamodel.DataModelFieldTree;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFolderTree;

import java.util.Collection;
import java.util.List;

public interface BiUiDBService {
    /**
     * 获取所有数据源
     * @return
     */
    public List<String> getAllDataSource();

    /**
     * 根据数据源获取表
     * @return
     */
    public List<String> getAllTable();

    /**
     * 根据表名获取字段
     * @param tableName
     * @return
     */
    Collection<DataModelFieldTree> getAllColumns(String tableName, String tenantId);

    List<AnalyseFolderTree> getDataTree(String pageId, String tableName);

}
