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
import com.deloitte.bdh.data.analyse.enums.YnTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageConfigService;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
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
public class AnalysePageConfigServiceImpl extends AbstractService<BiUiAnalysePageConfigMapper, BiUiAnalysePageConfig> implements AnalysePageConfigService {
    @Resource
    BiUiAnalysePageConfigMapper biUiReportPageConfigMapper;
    @Resource
    AnalysePageService analysePageService;

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
        BiUiAnalysePage page = analysePageService.getAnalysePage(pageId);
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
        BiUiAnalysePage page = analysePageService.getAnalysePage(pageId);
        if (page == null) {
            throw new BizException("页面id不正确");
        }
        if (page.getEditId() != null) {
            return getById(page.getEditId());
        }
        LambdaQueryWrapper<BiUiAnalysePageConfig> query = new LambdaQueryWrapper<>();
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
        BiUiAnalysePage page = analysePageService.getById(request.getData().getPageId());
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
        page.setIsEdit(YnTypeEnum.YES.getName());
        analysePageService.updateById(page);
        return entity;
    }

    @Override
    public void delAnalysePageConfig(String id) throws Exception {
        biUiReportPageConfigMapper.deleteById(id);
    }

    @Override
    public BiUiAnalysePageConfig updateAnalysePageConfig(UpdateAnalysePageConfigsDto dto) throws Exception {

        BiUiAnalysePageConfig config = this.getById(dto.getId());
        if (null == config) {
            throw new BizException("配置不存在");
        }
        config.setContent(dto.getContent());
        config.setModifiedDate(LocalDateTime.now());
        config.setModifiedUser(AnalyseUtil.getCurrentUser());
        biUiReportPageConfigMapper.updateById(config);
        BiUiAnalysePage page = analysePageService.getById(config.getPageId());
        page.setIsEdit(YnTypeEnum.YES.getName());
        return config;
    }

    @Override
    public List<BiUiAnalysePageConfig> getAnalysePageConfigList(AnalysePageConfigDto data) throws Exception {
        String pageId = data.getPageId();
        if (pageId == null) {
            throw new Exception("页面id不能为空");
        }
        BiUiAnalysePage page = analysePageService.getAnalysePage(pageId);
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
