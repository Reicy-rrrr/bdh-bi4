package com.deloitte.bdh.data.collation.component.impl;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.service.BiEtlMappingFieldService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 聚合组件实现
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("groupComponent")
public class GroupComponent implements ComponentHandler {

    @Autowired
    private BiEtlMappingFieldService biEtlMappingFieldService;

    @Override
    public void handle(ComponentModel component) {
        String componentCode = component.getCode();
        List<ComponentModel> fromComponents = component.getFrom();
        if (CollectionUtils.isEmpty(fromComponents)) {
            log.error("组件[{}]未查询到上层组件，处理失败！", componentCode);
            throw new BizException("聚合组件不能单独存在，处理失败！");
        }

        if (fromComponents.size() > 1) {
            log.error("组件[{}]查询到[{}]个上层组件，处理失败！", componentCode, fromComponents.size());
            throw new BizException("聚合组件只能有一个上层组件，处理失败！");
        }

        ComponentModel fromComponent = fromComponents.get(0);
        component.setTableName(component.getCode());
        component.setSql(fromComponent.getSql());

        // TODO:根据实际情况设置
        List<Triple> fromMappings = fromComponent.getFieldMappings();
        // 组装连接组件的字段
        List<String> fields = fromMappings.stream().map(Triple<String, String, String>::getLeft)
                .collect(Collectors.toList());
        component.setFields(fields);
        component.setFieldMappings(fromMappings);
    }
}
