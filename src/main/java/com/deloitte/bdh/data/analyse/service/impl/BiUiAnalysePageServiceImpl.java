package com.deloitte.bdh.data.analyse.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.constants.AnalyseTypeConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePageMapper;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.datamodel.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.GridComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.request.AnalysePageReq;
import com.deloitte.bdh.data.analyse.model.request.CreateAnalysePageDto;
import com.deloitte.bdh.data.analyse.model.request.GridDemoRequest;
import com.deloitte.bdh.data.analyse.model.request.UpdateAnalysePageDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtils;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    BiUiAnalysePageMapper biUiAnalysePageMapper;
    @Resource
    BiUiDemoMapper biUiDemoMapper;

    @Override
    public PageResult<List<BiUiAnalysePage>> getAnalysePages(AnalysePageReq dto) {
        LambdaQueryWrapper<BiUiAnalysePage> query = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            query.eq(BiUiAnalysePage::getTenantId, dto.getTenantId());
        }
        // 根据数据源名称模糊查询
        if (StringUtils.isNotBlank(dto.getName())) {
            query.like(BiUiAnalysePage::getName, dto.getName());
        }
        /**
         * 只查询已经发布过的页面
         */
        if (AnalyseConstants.PAGE_CONFIG_PUBLISH.equals(dto.getType())) {
            query.isNotNull(BiUiAnalysePage::getPublishId);
        }
        /**
         * 没有发布过的页面
         */
        if (AnalyseConstants.PAGE_CONFIG_EDIT.equals(dto.getType())) {
            query.isNull(BiUiAnalysePage::getPublishId);
        }
        query.orderByDesc(BiUiAnalysePage::getCreateDate);
        PageInfo<BiUiAnalysePage> pageInfo = new PageInfo(this.list(query));
        PageResult pageResult = new PageResult(pageInfo);
        return pageResult;
    }

    @Override
    public BiUiAnalysePage getAnalysePage(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个resource 失败:id 不能为空");
        }
        return biUiAnalysePageMapper.selectById(id);
    }

    @Override
    public BiUiAnalysePage createAnalysePage(CreateAnalysePageDto dto) throws Exception {
        if (checkBiUiAnalysePageByName(dto.getName(), dto.getTenantId(), null)) {
            BiUiAnalysePage entity = new BiUiAnalysePage();
            BeanUtils.copyProperties(dto, entity);
            entity.setCreateDate(LocalDateTime.now());
            biUiAnalysePageMapper.insert(entity);
            return entity;
        } else {
            throw new Exception("已存在相同名称的文件夹");
        }
    }

    @Override
    public void delAnalysePage(String id) throws Exception {
        BiUiAnalysePage category = biUiAnalysePageMapper.selectById(id);
        if (category == null) {
            throw new Exception("错误的id");
        }
        if (AnalyseConstants.CATEGORY_INIT_TYPE_DEFAULT.equals(category.getInitType())) {
            throw new Exception("默认文件夹不能删除");
        }
        biUiAnalysePageMapper.deleteById(id);
    }

    @Override
    public BiUiAnalysePage updateAnalysePage(UpdateAnalysePageDto dto) throws Exception {
        BiUiAnalysePage entity = biUiAnalysePageMapper.selectById(dto.getId());
        if (checkBiUiAnalysePageByName(dto.getName(), entity.getTenantId(), entity.getId())) {
            entity.setName(dto.getName());
            entity.setDes(dto.getDes());
            entity.setModifiedDate(LocalDateTime.now());
            biUiAnalysePageMapper.updateById(entity);
            return entity;
        } else {
            throw new Exception("已存在相同名称的文件夹");
        }
    }

    @Override
    public BaseComponentDataResponse getComponentDta(Map data) {
        String type = (String) data.get("type");
        if (AnalyseTypeConstants.GRID.equals(type)) {
            GridComponentDataRequest request = JSONObject.parseObject(JSONObject.toJSONString(data), GridComponentDataRequest.class);
            DataConfig dataConfig = request.getDataConfig();
            DataModel dataModel = dataConfig.getDataModel();
        }
        return null;
    }

    public List demoGridDemoRequest(GridDemoRequest data) {
        String select = "select " + AnalyseUtils.join(",", data.getColumns());
        String querySql = select + " from " + data.getTable();
        List<Map<String, Object>> result = biUiDemoMapper.selectDemoList(querySql);
        return result;
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
