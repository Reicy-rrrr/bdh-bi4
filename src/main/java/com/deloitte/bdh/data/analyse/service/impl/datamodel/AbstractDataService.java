package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
@Slf4j
public abstract class AbstractDataService {
    @Resource
    protected BiUiDemoMapper biUiDemoMapper;
    //todo 此处公共方法
}
