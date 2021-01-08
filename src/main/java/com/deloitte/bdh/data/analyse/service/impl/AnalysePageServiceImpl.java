package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.util.AesUtil;
import com.deloitte.bdh.common.util.Md5Util;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageMapper;
import com.deloitte.bdh.data.analyse.enums.PermittedActionEnum;
import com.deloitte.bdh.data.analyse.enums.ResourcesTypeEnum;
import com.deloitte.bdh.data.analyse.enums.ShareTypeEnum;
import com.deloitte.bdh.data.analyse.enums.YnTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePublicShare;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserResource;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.*;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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


    @Value("${bi.analyse.view.address}")
    private String viewAddress;

    @Value("${bi.analyse.public.address}")
    private String publicAddress;

    @Value("${bi.analyse.encryptPass}")
    private String encryptPass;

    @Resource
    private BiUiAnalysePublicShareService shareService;

    @Resource
    private AnalysePageConfigService configService;

    @Resource
    private AnalyseUserResourceService userResourceService;

    @Resource
    private AnalyseUserDataService userDataService;

    @Resource
    private BiUiAnalysePageMapper analysePageMapper;

    @Resource
    private AnalysePageHomepageService homepageService;

    @Override
    public PageResult<AnalysePageDto> getChildAnalysePageList(PageRequest<GetAnalysePageDto> request) {
        PageHelper.startPage(request.getPage(), request.getSize());
        SelectPublishedPageDto selectPublishedPageDto = new SelectPublishedPageDto();
        selectPublishedPageDto.setUserId(ThreadLocalHolder.getOperator());
        selectPublishedPageDto.setResourceType(ResourcesTypeEnum.PAGE.getCode());
        selectPublishedPageDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
        selectPublishedPageDto.setTenantId(ThreadLocalHolder.getTenantId());
        selectPublishedPageDto.setName(request.getData().getName());
        selectPublishedPageDto.setResourcesIds(Lists.newArrayList(request.getData().getCategoryId()));
        List<AnalysePageDto> pageList = analysePageMapper.selectPublishedPage(selectPublishedPageDto);
        //处理查询之后做操作返回total不正确
        PageInfo pageInfo = PageInfo.of(pageList);
        List<AnalysePageDto> pageDtoList = Lists.newArrayList();
        pageList.forEach(page -> {
            AnalysePageDto dto = new AnalysePageDto();
            BeanUtils.copyProperties(page, dto);
            pageDtoList.add(dto);
        });
        userResourceService.setPagePermission(pageDtoList);
        homepageService.fillHomePage(pageDtoList);
        pageInfo.setList(pageDtoList);
        return new PageResult<>(pageInfo);
    }

    @Override
    public AnalysePageDto getAnalysePage(String id) {
        if (StringUtils.isNotBlank(id)) {
            BiUiAnalysePage page = this.getById(id);
            if (null != page) {
                AnalysePageDto dto = new AnalysePageDto();
                BeanUtils.copyProperties(page, dto);
                return dto;
            }
        }
        return null;
    }

    @Override
    public AnalysePageDto createAnalysePage(RetRequest<CreateAnalysePageDto> request) {
        checkBiUiAnalysePageByName(request.getData().getCode(), request.getData().getName(), ThreadLocalHolder.getTenantId(), null);
        BiUiAnalysePage entity = new BiUiAnalysePage();
        BeanUtils.copyProperties(request.getData(), entity);
        entity.setTenantId(ThreadLocalHolder.getTenantId());
        entity.setIsEdit(YnTypeEnum.YES.getCode());
        this.save(entity);
        AnalysePageDto dto = new AnalysePageDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public AnalysePageDto copyAnalysePage(CopyAnalysePageDto request) {
        checkBiUiAnalysePageByName(request.getCode(), request.getName(), ThreadLocalHolder.getTenantId(), null);
        BiUiAnalysePage fromPage = this.getById(request.getFromPageId());
        if (null == fromPage) {
            throw new BizException("源报表不存在");
        }
        //复制page
        BiUiAnalysePage insertPage = new BiUiAnalysePage();
        BeanUtils.copyProperties(request, insertPage);
        insertPage.setIsEdit(YnTypeEnum.YES.getCode());
        this.save(insertPage);

        //复制page config
        BiUiAnalysePageConfig fromPageConfig = configService.getById(fromPage.getEditId());
        if (null != fromPageConfig) {
            BiUiAnalysePageConfig insertConfig = new BiUiAnalysePageConfig();
            insertConfig.setPageId(insertPage.getId());
            insertConfig.setContent(fromPageConfig.getContent());
            insertConfig.setTenantId(ThreadLocalHolder.getTenantId());
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
    public void batchDelAnalysePage(BatchDeleteAnalyseDto request) {
        List<String> pageIds = request.getIds();
        if (CollectionUtils.isEmpty(pageIds)) {
            throw new BizException("请选择要删除的报表");
        }
        //如果删除草稿箱的报表，不会直接删除page，而是删除config
        if (request.getType().equals(AnalyseConstants.PAGE_CONFIG_EDIT)) {
            List<BiUiAnalysePage> pages = this.listByIds(pageIds);
            pages.forEach(p -> p.setIsEdit(YnTypeEnum.NO.getCode()));
            updateBatchById(pages);
            return;
        }

        //删除config
        LambdaQueryWrapper<BiUiAnalysePageConfig> configQueryWrapper = new LambdaQueryWrapper<>();
        configQueryWrapper.in(BiUiAnalysePageConfig::getPageId, pageIds);
        configService.remove(configQueryWrapper);

        //删除可见编辑权限
        LambdaQueryWrapper<BiUiAnalyseUserResource> resourceQueryWrapper = new LambdaQueryWrapper<>();
        resourceQueryWrapper.in(BiUiAnalyseUserResource::getResourceId, pageIds);
        userResourceService.remove(resourceQueryWrapper);
        //删除page
        this.removeByIds(pageIds);
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
        this.updateById(entity);
        AnalysePageDto dto = new AnalysePageDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private void setAccessUrl(String pageId, String password, String isPublic) {

        //获取访问地址
        LambdaQueryWrapper<BiUiAnalysePublicShare> shareLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shareLambdaQueryWrapper.eq(BiUiAnalysePublicShare::getRefPageId, pageId);
        //公开状态
        List<String> typeList = Lists.newArrayList(ShareTypeEnum.ZERO.getKey(), ShareTypeEnum.ONE.getKey(), ShareTypeEnum.TWO.getKey());
        shareLambdaQueryWrapper.in(BiUiAnalysePublicShare::getType, typeList);
        BiUiAnalysePublicShare share = shareService.getOne(shareLambdaQueryWrapper);
        if (null == share) {
            share = new BiUiAnalysePublicShare();
            share.setRefPageId(pageId);
            share.setTenantId(ThreadLocalHolder.getTenantId());
            Map<String, Object> params = Maps.newHashMap();
            params.put("tenantCode", ThreadLocalHolder.getTenantCode());
            params.put("refPageId", pageId);
            share.setCode(AesUtil.encryptNoSymbol(JsonUtil.readObjToJson(params), encryptPass));

        }
        if (isPublic.equals(ShareTypeEnum.FALSE.getKey())) {
            share.setType(ShareTypeEnum.ZERO.getKey());
            share.setAddress(viewAddress);
        } else {
            share.setAddress(publicAddress);
            if (StringUtils.isNotEmpty(password)) {
                share.setType(ShareTypeEnum.TWO.getKey());
            } else {
                share.setType(ShareTypeEnum.ONE.getKey());
            }
        }
        if (StringUtils.isNotEmpty(password)) {
            share.setPassword(Md5Util.getMD5(password, encryptPass + ThreadLocalHolder.getTenantCode()));
        } else {
            share.setPassword(null);
        }
        shareService.saveOrUpdate(share);
    }

    @Transactional
    @Override
    public AnalysePageConfigDto publishAnalysePage(RetRequest<PublishAnalysePageDto> request) {

        PublishAnalysePageDto publishDto = request.getData();
        String internalFlag = publishDto.getInternalFlag();
        String pageId = publishDto.getPageId();
        String configId = publishDto.getConfigId();
        String categoryId = publishDto.getCategoryId();
        SaveResourcePermissionDto dto = publishDto.getSaveResourcePermissionDto();
        BiUiAnalysePageConfig originConfig = configService.getById(configId);
        BiUiAnalysePage originPage = getById(pageId);
        if (originPage == null) {
            throw new BizException("报表已经不存在了。");
        }

        if (internalFlag.equals(YesOrNoEnum.YES.getKey())) {
            updatePage(publishDto, originPage, originConfig);
        } else {
            String password = publishDto.getPassword();

            //获取公开状态
            String isPublic = publishDto.getIsPublic();
            if (isPublic.equals(ShareTypeEnum.TRUE.getKey())) {
                updatePage(publishDto, originPage, originConfig);
            } else {
                if (originPage.getParentId().equals(categoryId)) {
                    updatePage(publishDto, originPage, originConfig);
                } else {
                    List<BiUiAnalysePage> allPageList = list(new LambdaQueryWrapper<BiUiAnalysePage>()
                            .eq(BiUiAnalysePage::getParentId, categoryId)
                            .eq(BiUiAnalysePage::getOriginPageId, originPage.getOriginPageId()));
                    if (CollectionUtils.isEmpty(allPageList)) {
                        //新建config
                        BiUiAnalysePageConfig newConfig = new BiUiAnalysePageConfig();
                        if (originConfig != null) {
                            BeanUtils.copyProperties(originConfig, newConfig);
                        }
                        newConfig.setId(null);
                        newConfig.setPageId(null);
                        newConfig.setContent(publishDto.getContent());
                        newConfig.setTenantId(ThreadLocalHolder.getTenantId());
                        configService.save(newConfig);
                        //新建page
                        BiUiAnalysePage newPage = new BiUiAnalysePage();
                        BeanUtils.copyProperties(originPage, newPage);
                        newPage.setId(null);
                        newPage.setPublishId(newConfig.getId());
                        newPage.setParentId(categoryId);
                        newPage.setIsEdit(YnTypeEnum.NO.getCode());
                        newPage.setOriginPageId(originPage.getId());
                        save(newPage);
                        String newPageId = newPage.getId();
                        //保存pageId到config
                        newConfig.setPageId(newPageId);
                        configService.updateById(newConfig);
                        //把新的pageId传给权限操作
                        dto.setId(newPageId);
                    } else {
                        updatePage(publishDto, originPage, originConfig);
                    }
                }
            }

            //可见编辑权限
            userResourceService.saveResourcePermission(dto);
            //生成链接
            setAccessUrl(dto.getId(), password, isPublic);
            //数据权限
            userDataService.saveDataPermission(publishDto.getPermissionItemDtoList(), pageId);
        }

        return null;
    }

    private void updatePage(PublishAnalysePageDto dto, BiUiAnalysePage originPage, BiUiAnalysePageConfig originConfig) {

        //新建config
        BiUiAnalysePageConfig newConfig = new BiUiAnalysePageConfig();
        if (originConfig != null) {
            BeanUtils.copyProperties(originConfig, newConfig);
        }
        newConfig.setId(null);
        newConfig.setPageId(dto.getPageId());
        newConfig.setContent(dto.getContent());
        newConfig.setTenantId(ThreadLocalHolder.getTenantId());
        configService.save(newConfig);
        //更新page
        originPage.setPublishId(newConfig.getId());
        if (StringUtils.isEmpty(originPage.getOriginPageId())) {
            originPage.setOriginPageId(originPage.getId());
        }
        originPage.setIsEdit(YnTypeEnum.NO.getCode());
        updateById(originPage);
    }

    @Override
    public PageResult<AnalysePageDto> getAnalysePageDrafts(PageRequest<AnalyseNameDto> request) {
        PageHelper.startPage(request.getPage(), request.getSize());
        LambdaQueryWrapper<BiUiAnalysePage> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtil.isEmpty(ThreadLocalHolder.getTenantId())) {
            pageLambdaQueryWrapper.eq(BiUiAnalysePage::getTenantId, ThreadLocalHolder.getTenantId());
        }
        if (StringUtils.isNotBlank(request.getData().getName())) {
            pageLambdaQueryWrapper.like(BiUiAnalysePage::getName, request.getData().getName());
        }
        pageLambdaQueryWrapper.eq(BiUiAnalysePage::getIsEdit, YnTypeEnum.YES.getCode());
        pageLambdaQueryWrapper.eq(BiUiAnalysePage::getCreateUser, ThreadLocalHolder.getOperator());
        pageLambdaQueryWrapper.orderByDesc(BiUiAnalysePage::getCreateDate);
        List<BiUiAnalysePage> pageList = this.list(pageLambdaQueryWrapper);
        return getAnalysePageDtoPageResult(pageList);
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
            throw new BizException("已存在相同报表名称");
        }
        if (StringUtils.isNotBlank(code)) {
            //字母和数字
            String regEx = "[A-Z,a-z,0-9,-]*";
            Pattern pattern = Pattern.compile(regEx);
            if (!pattern.matcher(code).matches()) {
                throw new BizException("编码只能由字母和数字组成");
            }
        }
        query.clear();
        query.eq(BiUiAnalysePage::getTenantId, tenantId);
        query.eq(BiUiAnalysePage::getCode, code);
        if (currentId != null) {
            query.ne(BiUiAnalysePage::getId, currentId);
        }
        List<BiUiAnalysePage> codeList = list(query);
        if (CollectionUtils.isNotEmpty(codeList)) {
            throw new BizException("已存在相同报表编码");
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
