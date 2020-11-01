package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.resp.BiComponentTree;

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

}
