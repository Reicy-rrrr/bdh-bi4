package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.request.GetAnalyseDataTreeDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseDataModelTree;
import com.deloitte.bdh.data.analyse.model.resp.SaveOrUpdateFolderDto;
import com.deloitte.bdh.data.collation.model.request.DataSetTableInfo;

import java.util.List;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
public interface AnalyseModelService {

    /**
     * 根据数据源获取表
     *
     * @return
     */
    List<DataSetTableInfo> getAllTable(String superUserFlag);

    /**
     * 获取数据模型树
     *
     * @param request
     * @return
     */
    List<AnalyseDataModelTree> getDataTree(RetRequest<GetAnalyseDataTreeDto> request) throws Exception;

    /**
     * 保存数据模型树
     *
     * @param request
     * @return
     */
    void saveDataTree(RetRequest<List<AnalyseDataModelTree>> request);

    /**
     * 保存或者更新层级
     *
     * @param request
     * @return
     */
    void saveOrUpdateFolder(RetRequest<SaveOrUpdateFolderDto> request);

    /**
     * 获取组件数据
     *
     * @param data
     * @return
     */
    BaseComponentDataResponse getComponentData(ComponentDataRequest data) throws Exception;

    void initDefaultData();

}
