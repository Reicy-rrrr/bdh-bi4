package com.deloitte.bdh.data.report.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.report.dao.bi.BiUiReportPageConfigMapper;
import com.deloitte.bdh.data.report.model.BiUiReportPageConfig;
import com.deloitte.bdh.data.report.service.BiUiReportPageConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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
public class BiUiReportPageConfigServiceImpl extends AbstractService<BiUiReportPageConfigMapper, BiUiReportPageConfig> implements BiUiReportPageConfigService {
    @Resource
    BiUiReportPageConfigMapper biUiReportPageConfigMapper;

//    @Override
//    public PageResult<List<BiUiReportPageConfig>> getResources(GetResourcesDto dto) {
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
    public BiUiReportPageConfig getResource(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个resource 失败:id 不能为空");
        }
        return biUiReportPageConfigMapper.selectById(id);
    }

    @Override
    public BiUiReportPageConfig createResource(CreateResourcesDto dto) throws Exception {
        BiUiReportPageConfig entity = new BiUiReportPageConfig();
        BeanUtils.copyProperties(dto, entity);
        biUiReportPageConfigMapper.insert(entity);
        return entity;
    }

    @Override
    public void delResource(String id) throws Exception {
//        BiUiReportPageConfig inf = biUiReportPageConfigMapper.selectById(id);
        biUiReportPageConfigMapper.deleteById(id);
    }

    @Override
    public BiUiReportPageConfig updateResource(UpdateResourcesDto dto) throws Exception {
//        BiUiReportPageConfig inf = biUiReportPageConfigMapper.selectById(dto.getId());
        BiUiReportPageConfig entity = new BiUiReportPageConfig();
        BeanUtils.copyProperties(dto, entity);
        entity.setModifiedDate(LocalDateTime.now());
        biUiReportPageConfigMapper.updateById(entity);
        return entity;
    }
}
