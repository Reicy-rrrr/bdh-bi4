package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageConfigMapper;
import com.deloitte.bdh.data.analyse.enums.YnTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.request.GetAnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageConfigsDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageConfigService;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
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
    AnalysePageService analysePageService;

    @Override
    public AnalysePageConfigDto getAnalysePageConfig(RetRequest<GetAnalysePageConfigDto> request) {
        if (AnalyseConstants.PAGE_CONFIG_PUBLISH.equals(request.getData().getType())) {
            return getPublishAnalysePageConfigByPageId(request.getData().getPageId());
        }
        return getEditAnalysePageConfigByPageId(request.getData().getPageId());
    }

    private AnalysePageConfigDto getPublishAnalysePageConfigByPageId(String pageId) {
        BiUiAnalysePage page = analysePageService.getById(pageId);
        if (page == null) {
            throw new BizException("报表不存在");
        }
        if (StringUtils.isNotBlank(page.getPublishId())) {
            BiUiAnalysePageConfig config= getById(page.getPublishId());
            AnalysePageConfigDto dto = new AnalysePageConfigDto();
            BeanUtils.copyProperties(config, dto);
            return dto;
        }
        return null;
    }

    private AnalysePageConfigDto getEditAnalysePageConfigByPageId(String pageId) {
        BiUiAnalysePage page = analysePageService.getById(pageId);
        if (page == null) {
            throw new BizException("报表不存在");
        }
        if (StringUtils.isNotBlank(page.getEditId())) {
            BiUiAnalysePageConfig config= getById(page.getEditId());
            AnalysePageConfigDto dto = new AnalysePageConfigDto();
            BeanUtils.copyProperties(config, dto);
            return dto;
        }
        return null;
    }

    @Override
    public AnalysePageConfigDto createAnalysePageConfig(RetRequest<CreateAnalysePageConfigsDto> request) {
        if (request.getData().getPageId() == null) {
            throw new BizException("页面id不能为空");
        }
        BiUiAnalysePage page = analysePageService.getById(request.getData().getPageId());
        if (page == null) {
            throw new BizException("报表不存在");
        }
        BiUiAnalysePageConfig config = new BiUiAnalysePageConfig();
        BeanUtils.copyProperties(request.getData(), config);
        config.setTenantId(request.getTenantId());
        config.setCreateUser(request.getOperator());
        config.setCreateDate(LocalDateTime.now());
        this.save(config);
        page.setEditId(config.getId());
        page.setIsEdit(YnTypeEnum.YES.getCode());
        analysePageService.updateById(page);
        AnalysePageConfigDto dto = new AnalysePageConfigDto();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }

    @Override
    public void delAnalysePageConfig(String id) {
        this.removeById(id);
    }

    @Override
    public AnalysePageConfigDto updateAnalysePageConfig(RetRequest<UpdateAnalysePageConfigsDto> request) {

        BiUiAnalysePageConfig config = this.getById(request.getData().getId());
        if (null == config) {
            throw new BizException("配置不存在");
        }
        config.setContent(request.getData().getContent());
        config.setModifiedDate(LocalDateTime.now());
        config.setModifiedUser(AnalyseUtil.getCurrentUser());
        this.updateById(config);
        BiUiAnalysePage page = analysePageService.getById(config.getPageId());
        page.setIsEdit(YnTypeEnum.YES.getCode());
        analysePageService.updateById(page);
        AnalysePageConfigDto dto = new AnalysePageConfigDto();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }

    @Override
    public List<AnalysePageConfigDto> getAnalysePageConfigList(GetAnalysePageConfigDto data) {
        String pageId = data.getPageId();
        if (pageId == null) {
            throw new BizException("页面id不能为空");
        }
        BiUiAnalysePage page = analysePageService.getById(pageId);
        if (page == null) {
            throw new BizException("页面id不正确");
        }
        LambdaQueryWrapper<BiUiAnalysePageConfig> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalysePageConfig::getPageId, pageId);
        query.orderByDesc(BiUiAnalysePageConfig::getCreateDate);
        List<BiUiAnalysePageConfig> configs = list(query);

        List<AnalysePageConfigDto> dtoList = Lists.newArrayList();
        for (BiUiAnalysePageConfig config : configs) {
            AnalysePageConfigDto dto = new AnalysePageConfigDto();
            BeanUtils.copyProperties(config, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
