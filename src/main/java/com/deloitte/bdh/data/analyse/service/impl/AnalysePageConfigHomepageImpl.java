package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageHomepageMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageHomepage;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageHomepageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author:LIJUN
 * Date:05/01/2021
 * Description:
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalysePageConfigHomepageImpl extends AbstractService<BiUiAnalysePageHomepageMapper, BiUiAnalysePageHomepage> implements AnalysePageHomepageService {

    @Override
    public void setHomepage(List<AnalysePageDto> pageDtoList) {
        LambdaQueryWrapper<BiUiAnalysePageHomepage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BiUiAnalysePageHomepage::getUserId, ThreadLocalHolder.getOperator());
        BiUiAnalysePageHomepage homepage = getOne(queryWrapper);
        if (null != homepage) {
            for (AnalysePageDto pageDto : pageDtoList) {
                if (StringUtils.equals(pageDto.getId(), homepage.getPageId())) {
                    pageDto.setHomePage("1");
                }
            }
        }
    }
}
