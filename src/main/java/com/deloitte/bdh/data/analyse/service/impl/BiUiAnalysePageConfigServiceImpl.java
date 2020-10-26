package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageConfigMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.AnalysePageConfigReq;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageConfigService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@Service
@DS(DSConstant.BI_DB)
public class BiUiAnalysePageConfigServiceImpl extends AbstractService<BiUiAnalysePageConfigMapper, BiUiAnalysePageConfig> implements BiUiAnalysePageConfigService {
    @Resource
    BiUiAnalysePageConfigMapper biUiReportPageConfigMapper;

//    @Override
//    public PageResult<List<BiUiReportPageConfig>> getAnalysePageConfigs(GetAnalysePageConfigsDto dto) {
//        LambdaQueryWrapper<BiUiReportPageConfig> query = new LambdaQueryWrapper();
//        if (!StringUtil.isEmpty(dto.getTenantId())) {
//            query.eq(BiUiReportPageConfig::getTenantId, dto.getTenantId());
//        }
//        query.orderByDesc(BiUiReportPageConfig::getCreateDate);
//        PageInfo<BiUiReportPageConfig> pageInfo = new PageInfo(this.list(query));
//        PageResult pageResult = new PageResult(pageInfo);
//        return pageResult;
//    }

    @Override
    public BiUiAnalysePageConfig getAnalysePageConfig(AnalysePageConfigReq req) throws Exception {
        if (!StringUtil.isEmpty(req.getId())) {
            return biUiReportPageConfigMapper.selectById(req.getId());
        } else if (!StringUtil.isEmpty(req.getPageId())) {
            return getAnalysePageConfigByPageId(req.getPageId());
        } else {
            throw new Exception("id,pageId不能同时为空");
        }
    }

    private BiUiAnalysePageConfig getAnalysePageConfigByPageId(String pageId) throws Exception {
        LambdaQueryWrapper<BiUiAnalysePageConfig> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalysePageConfig::getPageId, pageId);
        List<BiUiAnalysePageConfig> configs = list(query);
        if (configs.size() > 1) {
            throw new Exception("找到多份数据:" + configs.size());
        }
        if (configs.size() == 1) {
            return configs.get(0);
        }
        return null;
    }

    @Override
    public BiUiAnalysePageConfig createAnalysePageConfig(CreateAnalysePageConfigsDto dto) throws Exception {
        BiUiAnalysePageConfig entity = new BiUiAnalysePageConfig();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateUser(AnalyseUtils.getCurrentUser());
        entity.setCreateDate(LocalDateTime.now());
        biUiReportPageConfigMapper.insert(entity);
        return entity;
    }

    @Override
    public void delAnalysePageConfig(String id) throws Exception {
//        BiUiReportPageConfig inf = biUiReportPageConfigMapper.selectById(id);
        biUiReportPageConfigMapper.deleteById(id);
    }

    @Override
    public BiUiAnalysePageConfig updateAnalysePageConfig(UpdateAnalysePageConfigsDto dto) throws Exception {
//        BiUiReportPageConfig inf = biUiReportPageConfigMapper.selectById(dto.getId());
        BiUiAnalysePageConfig entity = new BiUiAnalysePageConfig();
        BeanUtils.copyProperties(dto, entity);
        entity.setModifiedDate(LocalDateTime.now());
        entity.setModifiedUser(AnalyseUtils.getCurrentUser());
        biUiReportPageConfigMapper.updateById(entity);
        return entity;
    }
}
