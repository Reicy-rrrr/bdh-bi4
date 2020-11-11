package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelFieldTree;
import com.deloitte.bdh.data.analyse.model.request.GetDataTreeRequest;
import com.deloitte.bdh.data.analyse.model.request.SaveDataTreeRequest;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFolderTree;

import java.util.Collection;
import java.util.List;

public interface BiUiDBService {

    /**
     * 根据数据源获取表
     * @return
     */
    List<String> getAllTable();

    /**
     * 获取数据模型树
     * @param request
     * @return
     */
    List<AnalyseFolderTree> getDataTree(RetRequest<GetDataTreeRequest> request);

    /**
     * 保存数据模型树
     * @param request
     * @return
     */
    void saveDataTree(RetRequest<List<AnalyseFolderTree>> request);

}
