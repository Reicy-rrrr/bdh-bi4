package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageConfigMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.PublishAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageConfigService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
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
    public BiUiAnalysePageConfig getAnalysePageConfig(AnalysePageConfigDto req) throws Exception {
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

    private BiUiAnalysePageConfig getAnalysePageConfigByPageId(String pageId) {
        if (pageId == null) {
            throw new BizException("页面id不能为空");
        }
        BiUiAnalysePage page = biUiAnalysePageService.getAnalysePage(pageId);
        if (page == null) {
            throw new BizException("页面id不正确");
        }
        if (page.getEditId() != null) {
            return getById(page.getEditId());
        }
        LambdaQueryWrapper<BiUiAnalysePageConfig> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalysePageConfig::getPageId, pageId);
        List<BiUiAnalysePageConfig> configs = list(query);
        if (configs.size() > 1) {
            throw new BizException("找到多份数据:" + configs.size());
        }
        if (configs.size() == 1) {
            return configs.get(0);
        }
        return null;
    }

    @Override
    public BiUiAnalysePageConfig createAnalysePageConfig(RetRequest<CreateAnalysePageConfigsDto> request) {
        if (request.getData().getPageId() == null) {
            throw new BizException("页面id不能为空");
        }
        BiUiAnalysePage page = biUiAnalysePageService.getAnalysePage(request.getData().getPageId());
        if (page == null) {
            throw new BizException("页面id不正确");
        }
        BiUiAnalysePageConfig entity = new BiUiAnalysePageConfig();
        BeanUtils.copyProperties(request.getData(), entity);
        entity.setTenantId(request.getTenantId());
        entity.setCreateUser(request.getOperator());
        entity.setCreateDate(LocalDateTime.now());
        biUiReportPageConfigMapper.insert(entity);
        page.setEditId(entity.getId());
        biUiAnalysePageService.updateById(page);
        return entity;
    }

    @Override
    public BiUiAnalysePageConfig publishAnalysePageConfig(RetRequest<PublishAnalysePageConfigsDto> request) {
        if (request.getData().getPageId() == null) {
            throw new BizException("页面id不能为空");
        }
        BiUiAnalysePage page = biUiAnalysePageService.getAnalysePage(request.getData().getPageId());
        if (page == null) {
            throw new BizException("页面id不正确");
        }
        BiUiAnalysePageConfig editConfig = getAnalysePageConfigByPageId(request.getData().getPageId());
        if (editConfig == null) {
            throw new BizException("清先编辑页面并保存");
        }
        /**
         * 从editConfig复制一个publish对象
         */
        BiUiAnalysePageConfig publishConfig = new BiUiAnalysePageConfig();
        publishConfig.setPageId(editConfig.getPageId());
        publishConfig.setContent(editConfig.getContent());
        publishConfig.setTenantId(editConfig.getTenantId());
        publishConfig.setCreateUser(request.getOperator());
        publishConfig.setCreateDate(LocalDateTime.now());
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
        entity.setModifiedUser(AnalyseUtil.getCurrentUser());
        biUiReportPageConfigMapper.updateById(entity);
        return entity;
    }

    @Override
    public List<BiUiAnalysePageConfig> getAnalysePageConfigList(AnalysePageConfigDto data) throws Exception {
        String pageId = data.getPageId();
        if (pageId == null) {
            throw new Exception("页面id不能为空");
        }
        BiUiAnalysePage page = biUiAnalysePageService.getAnalysePage(pageId);
        if (page == null) {
            throw new Exception("页面id不正确");
        }
        LambdaQueryWrapper<BiUiAnalysePageConfig> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalysePageConfig::getPageId, pageId);
        List<BiUiAnalysePageConfig> configs = list(query);
        for (BiUiAnalysePageConfig config : configs) {
            if (page.getEditId() != null) {
                if (page.getEditId().equals(config.getId())) {
                    config.setStatus(AnalyseConstants.PAGE_CONFIG_EDIT);
                }
            }
            if (page.getPublishId() != null) {
                if (page.getPublishId().equals(config.getId())) {
                    config.setStatus(AnalyseConstants.PAGE_CONFIG_PUBLISH);
                }
            }
        }
        return configs;
    }
}
