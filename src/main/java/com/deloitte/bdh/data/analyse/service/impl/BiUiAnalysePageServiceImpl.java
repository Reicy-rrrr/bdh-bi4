package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.constants.AnalyseTypeConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageMapper;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageConfigService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
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
public class BiUiAnalysePageServiceImpl extends AbstractService<BiUiAnalysePageMapper, BiUiAnalysePage> implements BiUiAnalysePageService {

    @Resource
    BiUiAnalysePageConfigService configService;

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
        /**
         * 只查询已经发布过的页面
         */
        if (AnalyseConstants.PAGE_CONFIG_PUBLISH.equals(request.getData().getType())) {
            query.isNotNull(BiUiAnalysePage::getPublishId);
        }
        /**
         * 没有发布过的页面
         */
        if (AnalyseConstants.PAGE_CONFIG_EDIT.equals(request.getData().getType())) {
            query.isNull(BiUiAnalysePage::getPublishId);
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
    public void batchDelAnalysePage(BatchDelAnalysePageDto request) {
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
