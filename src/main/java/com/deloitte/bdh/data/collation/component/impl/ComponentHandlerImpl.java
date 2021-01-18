package com.deloitte.bdh.data.collation.component.impl;

import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 组件处理器实现
 *
 * @author chenghzhang
 * @date 2020/10/28
 */
@Slf4j
@Service("componentHandler")
public class ComponentHandlerImpl implements ComponentHandler {

    @Override
    public void handle(ComponentModel component) {
        ComponentTypeEnum componentType = component.getTypeEnum();
        SpringUtil.getBean(componentType.getName(), ComponentHandler.class).handle(component);
    }
}
