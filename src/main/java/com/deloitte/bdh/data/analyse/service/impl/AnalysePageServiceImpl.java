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
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.constants.AnalyseTypeConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageMapper;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
import com.deloitte.bdh.data.analyse.enums.YnTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
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

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    BiUiDemoMapper biUiDemoMapper;

    @Override
    public PageResult<BiUiAnalysePage> getAnalysePages(RetRequest<GetAnalysePageDto> request) {
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper<>();
        if (!StringUtil.isEmpty(request.getTenantId())) {
            query.eq(BiUiAnalysePage::getTenantId, request.getTenantId());
        }
        // 根据数据源名称模糊查询
        if (StringUtils.isNotBlank(request.getData().getName())) {
            query.like(BiUiAnalysePage::getName, request.getData().getName());
        }
        query.orderByDesc(BiUiAnalysePage::getCreateDate);
        PageInfo<BiUiAnalysePage> pageInfo = new PageInfo(this.list(query));
        PageResult<BiUiAnalysePage> pageResult = new PageResult<>(pageInfo);
        return pageResult;
    }

    @Override
    public BiUiAnalysePage getAnalysePage(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个resource 失败:id 不能为空");
        }
        return this.getById(id);
    }

    @Override
    public BiUiAnalysePage createAnalysePage(RetRequest<CreateAnalysePageDto> request) {
        if (checkBiUiAnalysePageByName(request.getData().getName(), request.getTenantId(), null)) {
            BiUiAnalysePage entity = new BiUiAnalysePage();
            BeanUtils.copyProperties(request.getData(), entity);
            entity.setTenantId(request.getTenantId());
            entity.setCreateUser(request.getOperator());
            entity.setCreateDate(LocalDateTime.now());
            this.save(entity);
            return entity;
        } else {
            throw new BizException("已存在相同名称的文件夹");
        }
    }

    @Override
    public BiUiAnalysePage copyAnalysePage(CopyAnalysePageDto request) {
        if (!checkBiUiAnalysePageByName(request.getName(), request.getTenantId(), null)) {
            throw new BizException("已存在同名报表");
        }
        BiUiAnalysePage fromPage = this.getById(request.getFromPageId());
        if (null == fromPage) {
            throw new BizException("源报表不存在");
        }
        //复制page
        BiUiAnalysePage insertPage = new BiUiAnalysePage();
        BeanUtils.copyProperties(request, insertPage);
        insertPage.setCreateDate(LocalDateTime.now());
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
        return insertPage;
    }

    @Override
    public void delAnalysePage(String id) throws Exception {
        BiUiAnalysePage category = this.getById(id);
        if (category == null) {
            throw new Exception("错误的id");
        }
        if (AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT.equals(category.getInitType())) {
            throw new Exception("默认文件夹不能删除");
        }
        this.removeById(id);
    }

    @Override
    public void batchDelAnalysePage(BatchDeleteAnalyseDto request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            throw new BizException("请选择要删除的报表");
        }
        List<BiUiAnalysePage> pageList = this.listByIds(request.getIds());
        if (CollectionUtils.isNotEmpty(pageList)) {
            List<String> pageIds = Lists.newArrayList();
            for (BiUiAnalysePage page : pageList) {
                if (StringUtils.equals(AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT, page.getInitType())) {
                    throw new BizException("默认文件夹不能删除");
                }
                pageIds.add(page.getId());
            }
            //删除config
            LambdaQueryWrapper<BiUiAnalysePageConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(BiUiAnalysePageConfig::getPageId, pageIds);
            configService.remove(queryWrapper);

            //删除page
            this.removeByIds(pageIds);
        }
    }

    @Override
    public BiUiAnalysePage updateAnalysePage(UpdateAnalysePageDto dto) {
        BiUiAnalysePage entity = this.getById(dto.getId());
        if (checkBiUiAnalysePageByName(dto.getName(), entity.getTenantId(), entity.getId())) {
            entity.setName(dto.getName());
            entity.setDes(dto.getDes());
            entity.setModifiedDate(LocalDateTime.now());
            this.updateById(entity);
            return entity;
        } else {
            throw new BizException("已存在相同名称的文件夹");
        }
    }

    @Override
    public BiUiAnalysePageConfig publishAnalysePage(RetRequest<AnalysePageIdDto> request) {
        BiUiAnalysePage page = this.getById(request.getData().getPageId());
        if (page == null) {
            throw new BizException("页面id不正确");
        }
        BiUiAnalysePageConfig editConfig = configService.getById(page.getEditId());
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
        configService.save(publishConfig);
        /**
         * 这里的BiUiAnalysePageConfig 如果以前publish过,会变为历史版本,当前版本初始化就不会变更,存放在editId中
         */
        page.setPublishId(publishConfig.getId());
        page.setIsEdit(YnTypeEnum.NO.getName());
        this.updateById(page);
        return publishConfig;
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
        pageLambdaQueryWrapper.eq(BiUiAnalysePage::getIsEdit, YnTypeEnum.YES.getName());
        pageLambdaQueryWrapper.orderByDesc(BiUiAnalysePage::getCreateDate);
        List<BiUiAnalysePage> pageList = this.list(pageLambdaQueryWrapper);
        List<AnalysePageDto> pageDtoList = Lists.newArrayList();
        pageList.forEach(page -> {
            AnalysePageDto dto = new AnalysePageDto();
            BeanUtils.copyProperties(page, dto);
            pageDtoList.add(dto);
        });
        PageInfo<AnalysePageDto> pageInfo = PageInfo.of(pageDtoList);
        return new PageResult<>(pageInfo);
    }

    @Override
    public void delAnalysePageDrafts(RetRequest<BatchDeleteAnalyseDto> request) {
        LambdaQueryWrapper<BiUiAnalysePage> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        pageLambdaQueryWrapper.in(BiUiAnalysePage::getId, request.getData().getIds());
        pageLambdaQueryWrapper.eq(BiUiAnalysePage::getTenantId, request.getTenantId());
        List<BiUiAnalysePage> pageList = this.list(pageLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(pageList)) {
            pageList.forEach(page -> page.setIsEdit(YnTypeEnum.NO.getName()));
            this.updateBatchById(pageList);
        }
    }

    @Override
    public BaseComponentDataResponse getComponentData(BaseComponentDataRequest request) {
        String type = request.getType();
        if (AnalyseTypeConstants.TABLE.equals(type)) {
//            GridComponentDataRequest request = JSONObject.parseObject(JSONObject.toJSONString(data), GridComponentDataRequest.class);
            DataConfig dataConfig = request.getDataConfig();
            DataModel dataModel = dataConfig.getDataModel();
            List<DataModelField> x = dataModel.getX();
            Integer pageIndex = dataModel.getPageIndex();
            Integer pageSize = dataModel.getPageSize();
            String tableName = dataModel.getTableName();
            String[] fields = new String[x.size()];
            for (int i = 0; i < x.size(); i++) {
                fields[i] = x.get(i).getId().replace("O_", "") + " as " + x.get(i).getId();
            }
            String select = "select " + AnalyseUtil.join(",", fields);
            String querySql = select + " from " + tableName;
            if (pageIndex != null && pageSize != null && pageSize > 0) {
                querySql = querySql + " limit " + (pageIndex - 1) * pageSize + "," + pageIndex * pageSize;
            }
            List<Map<String, Object>> result = biUiDemoMapper.selectDemoList(querySql);
            //todo 需要知道那个列是主键,然后加到上面的sql中作为一定查询的列 as key
            result.forEach(item -> {
                item.put("key", UUID.randomUUID().toString());
            });
            BaseComponentDataResponse response = new BaseComponentDataResponse();
            response.setSql(select);
            Map rdata = new HashMap();
            rdata.put("rows", result);
            response.setData(rdata);
            return response;
        }
        return null;
    }

    public boolean checkBiUiAnalysePageByName(String name, String tenantId, String currentId) {
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalysePage::getTenantId, tenantId);
        query.eq(BiUiAnalysePage::getName, name);
        if (currentId != null) {
            query.ne(BiUiAnalysePage::getId, currentId);
        }
        List<BiUiAnalysePage> contents = list(query);
        if (contents.size() > 0) {
            return false;
        }
        return true;
    }

    public List<BiUiAnalysePage> getTenantAnalysePages(String tenantId) {
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper();
        query.eq(BiUiAnalysePage::getTenantId, tenantId);
        return this.list(query);
    }
}
