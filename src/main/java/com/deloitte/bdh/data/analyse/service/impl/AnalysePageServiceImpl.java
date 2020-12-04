package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageMapper;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
import com.deloitte.bdh.data.analyse.enums.YnTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFieldService;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFolderService;
import com.deloitte.bdh.data.analyse.service.AnalysePageConfigService;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class AnalysePageServiceImpl extends AbstractService<BiUiAnalysePageMapper, BiUiAnalysePage> implements AnalysePageService {

    @Resource
    AnalysePageConfigService configService;

    @Resource
    AnalyseModelFolderService folderService;

    @Resource
    AnalyseModelFieldService fieldService;

    @Override
    public PageResult<AnalysePageDto> getChildAnalysePageList(PageRequest<GetAnalysePageDto> request) {
        PageHelper.startPage(request.getPage(), request.getSize());
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalysePage::getTenantId, request.getTenantId());
        if (StringUtils.isNotBlank(request.getData().getName())) {
            query.like(BiUiAnalysePage::getName, request.getData().getName());
        }
        if (StringUtils.isNotBlank(request.getData().getCategoryId())) {
            query.eq(BiUiAnalysePage::getParentId, request.getData().getCategoryId());
        }
        query.isNotNull(BiUiAnalysePage::getPublishId);
        query.eq(BiUiAnalysePage::getIsEdit, YnTypeEnum.NO.getCode());
        query.orderByDesc(BiUiAnalysePage::getCreateDate);
        List<BiUiAnalysePage> pageList = this.list(query);
        return getAnalysePageDtoPageResult(pageList);
    }

    @Override
    public AnalysePageDto getAnalysePage(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BizException("参数不能为空");
        }
        BiUiAnalysePage page = this.getById(id);
        if (null != page) {
            AnalysePageDto dto = new AnalysePageDto();
            BeanUtils.copyProperties(page, dto);
            return dto;
        }
        return null;
    }

    @Override
    public AnalysePageDto createAnalysePage(RetRequest<CreateAnalysePageDto> request) {
        checkBiUiAnalysePageByName(request.getData().getCode(), request.getData().getName(), request.getTenantId(), null);
        BiUiAnalysePage entity = new BiUiAnalysePage();
        BeanUtils.copyProperties(request.getData(), entity);
        entity.setTenantId(request.getTenantId());
        entity.setIsEdit(YnTypeEnum.YES.getCode());
        entity.setCreateUser(request.getOperator());
        entity.setCreateDate(LocalDateTime.now());
        this.save(entity);
        AnalysePageDto dto = new AnalysePageDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public AnalysePageDto copyAnalysePage(CopyAnalysePageDto request) {
        checkBiUiAnalysePageByName(request.getCode(), request.getName(), request.getTenantId(), null);
        BiUiAnalysePage fromPage = this.getById(request.getFromPageId());
        if (null == fromPage) {
            throw new BizException("源报表不存在");
        }
        //复制page
        BiUiAnalysePage insertPage = new BiUiAnalysePage();
        BeanUtils.copyProperties(request, insertPage);
        insertPage.setCreateDate(LocalDateTime.now());
        insertPage.setIsEdit(YnTypeEnum.YES.getCode());
        this.save(insertPage);

        //复制page config
        BiUiAnalysePageConfig fromPageConfig = configService.getById(fromPage.getEditId());
        if (null != fromPageConfig) {
            BiUiAnalysePageConfig insertConfig = new BiUiAnalysePageConfig();
            insertConfig.setPageId(insertPage.getId());
            insertConfig.setContent(fromPageConfig.getContent());
            insertConfig.setTenantId(request.getTenantId());
            insertConfig.setCreateUser(request.getCreateUser());
            insertConfig.setCreateDate(LocalDateTime.now());
            configService.save(insertConfig);
            insertPage.setEditId(insertConfig.getId());
            this.updateById(insertPage);
        }
        AnalysePageDto dto = new AnalysePageDto();
        BeanUtils.copyProperties(insertPage, dto);
        return dto;
    }

    @Override
    @Transactional
    public void delAnalysePage(String id) {
        BiUiAnalysePage page = this.getById(id);
        if (page == null) {
            throw new BizException("报表错误");
        }
        delPage(Lists.newArrayList(page.getId()));
    }

    @Override
    @Transactional
    public void batchDelAnalysePage(BatchDeleteAnalyseDto request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            throw new BizException("请选择要删除的报表");
        }
        List<BiUiAnalysePage> pageList = this.listByIds(request.getIds());
        if (CollectionUtils.isNotEmpty(pageList)) {
            List<String> pageIds = request.getIds();
            delPage(pageIds);
        }
    }

    @Override
    public AnalysePageDto updateAnalysePage(RetRequest<UpdateAnalysePageDto> request) {
        BiUiAnalysePage entity = this.getById(request.getData().getId());
        if (null == entity) {
            throw new BizException("报表错误");
        }
        checkBiUiAnalysePageByName(request.getData().getCode(), request.getData().getName(), entity.getTenantId(), entity.getId());
        entity.setName(request.getData().getName());
        entity.setDes(request.getData().getDes());
        entity.setModifiedDate(LocalDateTime.now());
        this.updateById(entity);
        AnalysePageDto dto = new AnalysePageDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public AnalysePageConfigDto publishAnalysePage(RetRequest<AnalysePageIdDto> request) {
        BiUiAnalysePage page = this.getById(request.getData().getPageId());
        if (page == null) {
            throw new BizException("报表不存在");
        }

        BiUiAnalysePageConfig config;
        if (StringUtils.isNotBlank(request.getData().getConfigId())) {
            config = configService.getById(request.getData().getConfigId());
            if (null == config) {
                throw new BizException("配置不存在");
            }
            config.setContent(request.getData().getContent());
            config.setModifiedDate(LocalDateTime.now());
            config.setModifiedUser(AnalyseUtil.getCurrentUser());
        } else {
            config = new BiUiAnalysePageConfig();
            BeanUtils.copyProperties(request.getData(), config);
            config.setTenantId(request.getTenantId());
            config.setCreateUser(request.getOperator());
            config.setCreateDate(LocalDateTime.now());
        }
        configService.saveOrUpdate(config);

        //复制一个publish对象
        BiUiAnalysePageConfig publishConfig = new BiUiAnalysePageConfig();
        publishConfig.setPageId(config.getPageId());
        publishConfig.setContent(config.getContent());
        publishConfig.setTenantId(config.getTenantId());
        publishConfig.setCreateUser(request.getOperator());
        publishConfig.setCreateDate(LocalDateTime.now());
        configService.save(publishConfig);
        //如果以前publish过,会变为历史版本,当前版本初始化就不会变更,存放在editId中
        page.setPublishId(publishConfig.getId());
        page.setEditId(config.getId());
        page.setIsEdit(YnTypeEnum.NO.getCode());
        this.updateById(page);
        AnalysePageConfigDto dto = new AnalysePageConfigDto();
        BeanUtils.copyProperties(publishConfig, dto);
        return dto;
    }

    @Override
    public PageResult<AnalysePageDto> getAnalysePageDrafts(PageRequest<AnalyseNameDto> request) {
        PageHelper.startPage(request.getPage(), request.getSize());
        LambdaQueryWrapper<BiUiAnalysePage> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtil.isEmpty(request.getTenantId())) {
            pageLambdaQueryWrapper.eq(BiUiAnalysePage::getTenantId, request.getTenantId());
        }
        if (StringUtils.isNotBlank(request.getData().getName())) {
            pageLambdaQueryWrapper.like(BiUiAnalysePage::getName, request.getData().getName());
        }
        pageLambdaQueryWrapper.eq(BiUiAnalysePage::getIsEdit, YnTypeEnum.YES.getCode());
        pageLambdaQueryWrapper.orderByDesc(BiUiAnalysePage::getCreateDate);
        List<BiUiAnalysePage> pageList = this.list(pageLambdaQueryWrapper);
        return getAnalysePageDtoPageResult(pageList);
    }

    @Override
    public void delAnalysePageDrafts(RetRequest<BatchDeleteAnalyseDto> request) {
        LambdaQueryWrapper<BiUiAnalysePage> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pageLambdaQueryWrapper.in(BiUiAnalysePage::getId, request.getData().getIds());
        pageLambdaQueryWrapper.eq(BiUiAnalysePage::getTenantId, request.getTenantId());
        List<BiUiAnalysePage> pageList = this.list(pageLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(pageList)) {
            pageList.forEach(page -> page.setIsEdit(YnTypeEnum.NO.getCode()));
            this.updateBatchById(pageList);
        }
    }

    private void delPage(List<String> pageIds) {
        if (CollectionUtils.isNotEmpty(pageIds)) {
            //删除度量维度配置
            LambdaQueryWrapper<BiUiModelFolder> folderQueryWrapper = new LambdaQueryWrapper<>();
            folderQueryWrapper.in(BiUiModelFolder::getPageId, pageIds);
            folderService.remove(folderQueryWrapper);
            LambdaQueryWrapper<BiUiModelField> fieldQueryWrapper = new LambdaQueryWrapper<>();
            fieldQueryWrapper.in(BiUiModelField::getPageId, pageIds);
            fieldService.remove(fieldQueryWrapper);
            //删除config
            LambdaQueryWrapper<BiUiAnalysePageConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(BiUiAnalysePageConfig::getPageId, pageIds);
            configService.remove(queryWrapper);

            //删除page
            this.removeByIds(pageIds);
        }
    }

    private void checkBiUiAnalysePageByName(String code, String name, String tenantId, String currentId) {
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper<>();
        query.eq(BiUiAnalysePage::getTenantId, tenantId);
        query.eq(BiUiAnalysePage::getName, name);
        if (currentId != null) {
            query.ne(BiUiAnalysePage::getId, currentId);
        }
        List<BiUiAnalysePage> nameList = list(query);
        if (CollectionUtils.isNotEmpty(nameList)) {
            throw new BizException("存在同名报表");
        }
        query.clear();
        query.eq(BiUiAnalysePage::getTenantId, tenantId);
        query.eq(BiUiAnalysePage::getCode, code);
        if (currentId != null) {
            query.ne(BiUiAnalysePage::getId, currentId);
        }
        List<BiUiAnalysePage> codeList = list(query);
        if (CollectionUtils.isNotEmpty(codeList)) {
            throw new BizException("存在同名编码");
        }
    }

    private PageResult<AnalysePageDto> getAnalysePageDtoPageResult(List<BiUiAnalysePage> pageList) {
        //处理查询之后做操作返回total不正确
        PageInfo pageInfo = PageInfo.of(pageList);
        List<AnalysePageDto> pageDtoList = Lists.newArrayList();
        pageList.forEach(page -> {
            AnalysePageDto dto = new AnalysePageDto();
            BeanUtils.copyProperties(page, dto);
            pageDtoList.add(dto);
        });
        pageInfo.setList(pageDtoList);
        return new PageResult<>(pageInfo);
    }

}
