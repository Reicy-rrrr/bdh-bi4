package com.deloitte.bdh.data.collation.component;

import com.deloitte.bdh.data.collation.component.model.ComponentModel;

/**
 * 组件接口
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
public interface ComponentHandler extends Component {
    /**
     * 处理组件
     *
     * @param component 组件模型对象
     * @return String
     */
    void handle(ComponentModel component);
}
