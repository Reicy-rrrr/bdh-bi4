package com.deloitte.bdh.data.analyse.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageRequest;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.util.*;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageMapper;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
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
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.enums.DataSetTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @Resource
    private BiDataSetService dataSetService;

    @Resource
    private BiUiDemoMapper demoMapper;

    @Override
    public PageResult<AnalysePageDto> getChildAnalysePageList(PageRequest<GetAnalysePageDto> request) {
        if (0 == request.getSize()) {
            PageHelper.startPage(request.getPage(), request.getSize(), true, false, true);
        } else {
            PageHelper.startPage(request.getPage(), request.getSize());
        }
        List<AnalysePageDto> pageList = Lists.newArrayList();
        if (StringUtils.equals(request.getData().getSuperUserFlag(), YesOrNoEnum.YES.getKey())) {
            LambdaQueryWrapper<BiUiAnalysePage> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BiUiAnalysePage::getParentId, request.getData().getCategoryId());
            if (StringUtils.isNotBlank(request.getData().getName())) {
                queryWrapper.like(BiUiAnalysePage::getName, request.getData().getName());
            }
            queryWrapper.orderByDesc(BiUiAnalysePage::getCreateDate);
            List<BiUiAnalysePage> list = list(queryWrapper);
            for (BiUiAnalysePage page : list) {
                AnalysePageDto dto = new AnalysePageDto();
                BeanUtils.copyProperties(page, dto);
                pageList.add(dto);
            }
        } else {
            SelectPublishedPageDto selectPublishedPageDto = new SelectPublishedPageDto();
            selectPublishedPageDto.setUserId(ThreadLocalHolder.getOperator());
            selectPublishedPageDto.setResourceType(ResourcesTypeEnum.PAGE.getCode());
            selectPublishedPageDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
            selectPublishedPageDto.setTenantId(ThreadLocalHolder.getTenantId());
            selectPublishedPageDto.setName(request.getData().getName());
            selectPublishedPageDto.setResourcesIds(Lists.newArrayList(request.getData().getCategoryId()));
            pageList = analysePageMapper.selectPublishedPage(selectPublishedPageDto);
        }

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
//        checkBiUiAnalysePageByName(request.getData().getCode(), request.getData().getName(), ThreadLocalHolder.getTenantId(), null);
        BiUiAnalysePage entity = new BiUiAnalysePage();
        BeanUtils.copyProperties(request.getData(), entity);
        entity.setCode(GenerateCodeUtil.generate());
        entity.setTenantId(ThreadLocalHolder.getTenantId());
        entity.setIsEdit(YnTypeEnum.YES.getCode());
        this.save(entity);
        AnalysePageDto dto = new AnalysePageDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    @Transactional
    public AnalysePageDto copyDeloittePage(CopyDeloittePageDto request) {
        checkBiUiAnalysePageByName(request.getCode(), request.getName(), ThreadLocalHolder.getTenantId(), null);
        BiUiAnalysePage fromPage = this.getById(request.getFromPageId());
        if (null == fromPage) {
            throw new BizException("源报表不存在");
        }
        //复制数据集
        BiUiAnalysePageConfig fromPageConfig = configService.getById(fromPage.getEditId());
        if (null == fromPageConfig) {
            throw new BizException("请先编辑源报表");
        }
        JSONObject content = (JSONObject) JSONObject.parse(fromPageConfig.getContent());
        JSONArray childrenArr = content.getJSONArray("children");
        if (null == childrenArr) {
            throw new BizException("源报表未配置图表");
        }
        List<String> originCodeList = Lists.newArrayList();
        for (int i = 0; i < childrenArr.size(); i++) {
            JSONObject data = childrenArr.getJSONObject(i).getJSONObject("data");
            if (data.size() != 0) {
                originCodeList.add(data.getString("tableCode"));
            }
        }
        Map<String, String> codeMap = Maps.newHashMap();
        for (String code : originCodeList) {
            LambdaQueryWrapper<BiDataSet> dataSetQueryWrapper = new LambdaQueryWrapper<>();
            dataSetQueryWrapper.eq(BiDataSet::getCode, code);
            BiDataSet dataSet = dataSetService.getOne(dataSetQueryWrapper);
            BiDataSet newDataSet = new BiDataSet();
            String newCode = GenerateCodeUtil.generate();
            String tableName = "COPY_" + newCode;
            newDataSet.setCode(newCode);
            newDataSet.setType(DataSetTypeEnum.COPY.getKey());
            newDataSet.setTableName(tableName);
            newDataSet.setTableDesc(dataSet.getTableDesc());
            newDataSet.setRefModelCode(dataSet.getRefModelCode());
            newDataSet.setParentId(request.getDataSetCategoryId());
            newDataSet.setComments(request.getDataSetName());
            newDataSet.setIsFile(YesOrNoEnum.NO.getKey());
            newDataSet.setTenantId(ThreadLocalHolder.getTenantId());
            dataSetService.save(newDataSet);
            String existSql = "select * from information_schema.TABLES where TABLE_NAME = '" + dataSet.getTableName() + "';";
            if (CollectionUtils.isEmpty(demoMapper.selectDemoList(existSql))) {
                throw new BizException("只能复制本地库报表");
            }
            String sql = "create table " + tableName + " like " + dataSet.getTableName() + ";";
            demoMapper.selectDemoList(sql);
            String insertSql = "insert into " + tableName + " select * from " + dataSet.getTableName() + ";";
            demoMapper.selectDemoList(insertSql);
            codeMap.put(code, newCode);
        }

        //复制page
        BiUiAnalysePage insertPage = new BiUiAnalysePage();
        insertPage.setType("dashboard");
        insertPage.setName(request.getName());
        insertPage.setCode(request.getCode());
        insertPage.setParentId(request.getCategoryId());
        insertPage.setIsPublic(YesOrNoEnum.YES.getKey());
        insertPage.setIsEdit(YnTypeEnum.NO.getCode());
        insertPage.setTenantId(ThreadLocalHolder.getTenantId());
        insertPage.setDeloitteFlag(YesOrNoEnum.NO.getKey());
        insertPage.setOriginPageId(null);
        this.save(insertPage);

        //替换content
        content.put("page", insertPage);
        for (int i = 0; i < childrenArr.size(); i++) {
            JSONObject data = childrenArr.getJSONObject(i).getJSONObject("data");
            if (data.size() != 0) {
                data.put("tableCode", codeMap.get(data.getString("tableCode")));
            }
        }

        //复制config
        BiUiAnalysePageConfig editConfig = new BiUiAnalysePageConfig();
        editConfig.setPageId(insertPage.getId());
        editConfig.setContent(content.toJSONString());
        editConfig.setTenantId(ThreadLocalHolder.getTenantId());
        configService.save(editConfig);
        BiUiAnalysePageConfig publishConfig = new BiUiAnalysePageConfig();
        publishConfig.setPageId(insertPage.getId());
        publishConfig.setContent(content.toJSONString());
        publishConfig.setTenantId(ThreadLocalHolder.getTenantId());
        configService.save(publishConfig);
        insertPage.setEditId(editConfig.getId());
        insertPage.setPublishId(publishConfig.getId());
        this.updateById(insertPage);
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
        //如果删除草稿箱的报表
        if (request.getType().equals(AnalyseConstants.PAGE_CONFIG_EDIT)) {
            List<BiUiAnalysePage> pages = this.listByIds(pageIds);
            for (BiUiAnalysePage page : pages) {
                //如果发布过
                if (StringUtils.isNotBlank(page.getPublishId())) {
                    BiUiAnalysePageConfig publishConfig = configService.getById(page.getPublishId());
                    BiUiAnalysePageConfig editConfig = configService.getById(page.getEditId());
                    editConfig.setContent(publishConfig.getContent());
                    configService.updateById(editConfig);
                    page.setIsEdit(YnTypeEnum.NO.getCode());
                    updateById(page);
                } else {
                    removeById(page.getId());
                    //删除config
                    configService.removeById(page.getEditId());
                }
            }
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
    public AnalysePageConfigDto publishAnalysePage(PublishAnalysePageDto request) {
        String pageId = request.getPageId();
        String categoryId = request.getCategoryId();
        SaveResourcePermissionDto permissionDto = request.getSaveResourcePermissionDto();
        BiUiAnalysePageConfig originConfig = configService.getById(request.getConfigId());
        BiUiAnalysePage originPage = getById(request.getPageId());
        if (originPage == null) {
            throw new BizException("报表已经不存在了。");
        }

        if (StringUtils.equals(request.getDeloitteFlag(), YesOrNoEnum.YES.getKey())) {
            updatePage(request, originPage, originConfig, "false");
        } else {
            String password = request.getPassword();

            //获取公开状态s
            String isPublic = request.getIsPublic();
            if (isPublic.equals(ShareTypeEnum.TRUE.getKey())) {
                updatePage(request, originPage, originConfig, isPublic);
            } else {
//                if (originPage.getParentId().equals(categoryId)) {
//                    updatePage(request, originPage, originConfig, isPublic);
//                } else {
                    List<BiUiAnalysePage> allPageList = list(new LambdaQueryWrapper<BiUiAnalysePage>()
                            .eq(BiUiAnalysePage::getParentId, categoryId)
                            .eq(BiUiAnalysePage::getOriginPageId, originPage.getOriginPageId()));
                    if (CollectionUtils.isEmpty(allPageList)) {
                        //新建config
                        BiUiAnalysePageConfig newConfig = new BiUiAnalysePageConfig();
                        if (originConfig != null) {
                            BeanUtils.copyProperties(originConfig, newConfig);
                            originConfig.setContent(request.getContent());
                            configService.updateById(originConfig);
                        }
                        newConfig.setId(null);
                        newConfig.setPageId(null);
                        newConfig.setContent(request.getContent());
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
                        if (StringUtils.isNotBlank(isPublic)){
                            if (isPublic.equals(ShareTypeEnum.TRUE.getKey())) {
                                newPage.setIsPublic(YesOrNoEnum.YES.getKey());
                            } else {
                                newPage.setIsPublic(YesOrNoEnum.NO.getKey());
                            }
                        }
                        save(newPage);
                        String newPageId = newPage.getId();
                        //保存pageId到config
                        newConfig.setPageId(newPageId);
                        configService.updateById(newConfig);
                        //把新的pageId传给权限操作
                        if (null != permissionDto) {
                            pageId = newPageId;
                            permissionDto.setId(newPageId);
                        }
                    } else {
                        updatePage(request, originPage, originConfig, isPublic);
                    }
//                }
            }

            //可见编辑权限
            userResourceService.saveResourcePermission(permissionDto);
            //生成链接
            setAccessUrl(pageId, password, isPublic);
            //数据权限
            userDataService.saveDataPermission(request.getPermissionItemDtoList(), request.getPageId());
        }
        return null;
    }

    
    private void updatePage(PublishAnalysePageDto dto, BiUiAnalysePage originPage, BiUiAnalysePageConfig originConfig, String isPublic) {

        //新建config
        BiUiAnalysePageConfig newConfig = new BiUiAnalysePageConfig();
        if (originConfig != null) {
            BeanUtils.copyProperties(originConfig, newConfig);
            originConfig.setContent(dto.getContent());
            configService.updateById(originConfig);
        }
        newConfig.setId(null);
        newConfig.setPageId(dto.getPageId());
        newConfig.setContent(dto.getContent());
        newConfig.setTenantId(ThreadLocalHolder.getTenantId());
        configService.save(newConfig);
        //更新page
        if (StringUtils.isNotBlank(isPublic)){
            if (isPublic.equals(ShareTypeEnum.TRUE.getKey())) {
                originPage.setIsPublic(YesOrNoEnum.YES.getKey());
            } else {
                originPage.setIsPublic(YesOrNoEnum.NO.getKey());
            }
        }
        originPage.setPublishId(newConfig.getId());
        if (StringUtils.isEmpty(originPage.getOriginPageId())) {
            originPage.setOriginPageId(originPage.getId());
        }
        originPage.setParentId(dto.getCategoryId());
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
        if (!StringUtils.equals(YesOrNoEnum.YES.getKey(), request.getData().getSuperUserFlag())) {
            pageLambdaQueryWrapper.eq(BiUiAnalysePage::getCreateUser, ThreadLocalHolder.getOperator());
        }
        pageLambdaQueryWrapper.orderByDesc(BiUiAnalysePage::getCreateDate);
        List<BiUiAnalysePage> pageList = this.list(pageLambdaQueryWrapper);
        return getAnalysePageDtoPageResult(pageList);
    }

    @Override
    public void replaceDataSet(ReplaceDataSetDto dto) throws Exception {
        BiUiAnalysePage page = getById(dto.getPageId());
        if (null == page) {
            throw new BizException("报表不存在");
        }
        List<String> configIdList = Lists.newArrayList();
        configIdList.add(page.getEditId());
        if (StringUtils.isNotBlank(page.getPublishId())) {
            configIdList.add(page.getPublishId());
        }
        //校验数据集字段类型
        List<BiUiAnalysePageConfig> configList = configService.listByIds(configIdList);
        for (ReplaceItemDto itemDto : dto.getReplaceItemDtoList()) {
            BiDataSet fromDataSet = dataSetService.getById(itemDto.getFromDataSetCode());
            BiDataSet toDataSet = dataSetService.getById(itemDto.getToDataSetCode());
            List<TableColumn> fromFieldList = dataSetService.getColumns(fromDataSet.getCode());
            List<TableColumn> toFieldList = dataSetService.getColumns(toDataSet.getCode());
            validReplaceField(fromFieldList, toFieldList);
        }
        Map<String, ReplaceItemDto> itemDtoMap = dto.getReplaceItemDtoList().stream().collect(
                Collectors.toMap(ReplaceItemDto::getFromDataSetCode, a -> a, (k1, k2) -> k1));
        //替换数据
        for (BiUiAnalysePageConfig config : configList) {
            JSONObject content = (JSONObject) JSONObject.parse(config.getContent());
            JSONArray childrenArr = content.getJSONArray("children");
            for (int i = 0; i < childrenArr.size(); i++) {
                JSONObject data = childrenArr.getJSONObject(i).getJSONObject("data");
                if (data.size() != 0 && null != MapUtils.getObject(itemDtoMap, data.getString("tableCode"))) {
                    ReplaceItemDto itemDto = MapUtils.getObject(itemDtoMap, data.getString("tableCode"));
                    data.put("tableCode", itemDto.getToDataSetCode());
                }
            }
            config.setContent(content.toJSONString());
            configService.updateById(config);
        }
    }

    private void validReplaceField(List<TableColumn> fromFieldList, List<TableColumn> toFieldList){
        Map<String, TableColumn> fromMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(fromFieldList)) {
            fromMap = fromFieldList.stream().collect(Collectors.toMap(TableColumn::getName,
                    a -> a, (k1, k2) -> k1));
        }
        for (TableColumn column : toFieldList) {
            if (null == MapUtils.getObject(fromMap, column.getName())) {
                throw new BizException(column.getName() + "未找到对应字段");
            }
            TableColumn fromColumn = MapUtils.getObject(fromMap, column.getName());
            if (!org.apache.commons.lang.StringUtils.equals(column.getDataType(), fromColumn.getDataType())) {
                throw new BizException(column.getName() + "字段类型不匹配");
            }
        }
    }

    private void checkBiUiAnalysePageByName(String code, String name, String tenantId, String currentId) {
//        query.eq(BiUiAnalysePage::getTenantId, tenantId);
//        query.eq(BiUiAnalysePage::getName, name);
//        if (currentId != null) {
//            query.ne(BiUiAnalysePage::getId, currentId);
//        }
//        List<BiUiAnalysePage> nameList = list(query);
//        if (CollectionUtils.isNotEmpty(nameList)) {
//            throw new BizException("已存在相同报表名称");
//        }
        if (StringUtils.isNotBlank(code)) {
            //字母和数字
            String regEx = "[A-Z,a-z,0-9,-]*";
            Pattern pattern = Pattern.compile(regEx);
            if (!pattern.matcher(code).matches()) {
                throw new BizException("编码只能由字母和数字组成");
            }
        }
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper<>();
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
