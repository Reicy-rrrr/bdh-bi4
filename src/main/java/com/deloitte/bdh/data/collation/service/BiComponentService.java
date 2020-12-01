package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.BiComponentTree;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
public interface BiComponentService extends Service<BiComponent> {

    /**
     * 查询组件树
     * componentCode != null: 以componentCode为根节点查询组件树
     * componentCode == null: 查询modelCode对应模板下的组件树
     *
     * @param modelCode
     * @param componentCode
     * @return
     */
    BiComponentTree selectTree(String modelCode, String componentCode);

    /**
     * 基于模板编码停止运行 停止同步，删除etl
     *
     * @param modelCode
     * @return
     */
    void stopAndDelComponents(String modelCode) throws Exception;

    void validate(String modelCode);

    void removeResourceComponent(BiComponent component) throws Exception;

    void removeOut(BiComponent component);

    void remove(BiComponent component);

    String addOutComponent(String querySql, String tableName, BiEtlModel biEtlModel) throws Exception;

    String getProcessorsGroupId(String componentCode);

    /**
     * 判断数据源组件是否正在同步过程中
     *
     * @param componentCode
     * @return
     */
    boolean isSync(String componentCode);

}
