package com.deloitte.bdh.data.collation.component.impl;

import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 整理组件
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service("arrangeComponent")
public class ArrangeComponent implements ComponentHandler {

    @Autowired
    private DbHandler dbHandler;

    @Override
    public void handle(ComponentModel component) {

    }
}
