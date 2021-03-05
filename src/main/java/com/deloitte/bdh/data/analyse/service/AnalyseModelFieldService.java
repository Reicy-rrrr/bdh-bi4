package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-21
 */
public interface AnalyseModelFieldService extends Service<BiUiModelField> {

    /**
     * getTables
     *
     * @param tableName
     * @return
     */
    Map<String, List<String>> getTables(String tableName);
}
