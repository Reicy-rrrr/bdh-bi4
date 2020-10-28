package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageConfigMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.AnalysePageConfigReq;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.PublishAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageConfigService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageService;
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
    @Resource
    BiUiAnalysePageService biUiAnalysePageService;

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
            if (AnalyseConstants.PAGE_CONFIG_PUBLISH.equals(req.getType())) {
                return getPublishAnalysePageConfigByPageId(req.getPageId());
            }
            return getAnalysePageConfigByPageId(req.getPageId());
        } else {
            throw new Exception("id,pageId不能同时为空");
        }
    }

    private BiUiAnalysePageConfig getPublishAnalysePageConfigByPageId(String pageId) throws Exception {
        if (pageId == null) {
            throw new Exception("页面id不能为空");
        }
        BiUiAnalysePage page = biUiAnalysePageService.getAnalysePage(pageId);
        if (page == null) {
            throw new Exception("页面id不正确");
        }
        if (page.getPublishId() != null) {
            return getById(page.getPublishId());
        } else {
            throw new Exception("页面没有发布");
        }
    }

    private BiUiAnalysePageConfig getAnalysePageConfigByPageId(String pageId) throws Exception {
        if (pageId == null) {
            throw new Exception("页面id不能为空");
        }
        BiUiAnalysePage page = biUiAnalysePageService.getAnalysePage(pageId);
        if (page == null) {
            throw new Exception("页面id不正确");
        }
        if (page.getEditId() != null) {
            return getById(page.getEditId());
        }
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
        if (dto.getPageId() == null) {
            throw new Exception("页面id不能为空");
        }
        BiUiAnalysePage page = biUiAnalysePageService.getAnalysePage(dto.getPageId());
        if (page == null) {
            throw new Exception("页面id不正确");
        }
        BiUiAnalysePageConfig entity = new BiUiAnalysePageConfig();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateUser(AnalyseUtils.getCurrentUser());
        entity.setCreateDate(LocalDateTime.now());
        biUiReportPageConfigMapper.insert(entity);
        page.setEditId(entity.getId());
        biUiAnalysePageService.updateById(page);
        return entity;
    }

    @Override
    public BiUiAnalysePageConfig publishAnalysePageConfig(PublishAnalysePageConfigsDto dto) throws Exception {
        if (dto.getPageId() == null) {
            throw new Exception("页面id不能为空");
        }
        BiUiAnalysePage page = biUiAnalysePageService.getAnalysePage(dto.getPageId());
        if (page == null) {
            throw new Exception("页面id不正确");
        }
        BiUiAnalysePageConfig editConfig = getAnalysePageConfigByPageId(dto.getPageId());
        if (editConfig == null) {
            throw new Exception("清先编辑页面并保存");
        }
        /**
         * 从editConfig复制一个publish对象
         */
        BiUiAnalysePageConfig publishConfig = new BiUiAnalysePageConfig();
        publishConfig.setPageId(editConfig.getPageId());
        publishConfig.setContent(editConfig.getContent());
        publishConfig.setTenantId(editConfig.getTenantId());
        publishConfig.setCreateDate(LocalDateTime.now());
        publishConfig.setCreateUser(AnalyseUtils.getCurrentUser());
        biUiReportPageConfigMapper.insert(publishConfig);
        /**
         * 这里的BiUiAnalysePageConfig 如果以前publish过,会变为历史版本,当前版本初始化就不会变更,存放在editId中
         */
        page.setPublishId(publishConfig.getId());
        page.setEditId(editConfig.getId());
        biUiAnalysePageService.updateById(page);
        return publishConfig;
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
