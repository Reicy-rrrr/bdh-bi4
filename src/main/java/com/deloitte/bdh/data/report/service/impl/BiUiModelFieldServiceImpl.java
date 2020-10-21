package com.deloitte.bdh.data.report.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.deloitte.bdh.data.report.dao.bi.BiUiModelFieldMapper;
import com.deloitte.bdh.data.report.model.BiUiModelField;
import com.deloitte.bdh.data.report.service.BiUiModelFieldService;
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
 * @since 2020-10-21
 */
@Service
@DS(DSConstant.BI_DB)
public class BiUiModelFieldServiceImpl extends AbstractService<BiUiModelFieldMapper, BiUiModelField> implements BiUiModelFieldService {
    @Resource
    BiUiModelFieldMapper biUiModelFieldMapper;

    @Override
    public BiUiModelField getResource(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个resource 失败:id 不能为空");
        }
        return biUiModelFieldMapper.selectById(id);
    }

    @Override
    public BiUiModelField createResource(CreateResourcesDto dto) throws Exception {
        BiUiModelField entity = new BiUiModelField();
        BeanUtils.copyProperties(dto, entity);
        biUiModelFieldMapper.insert(entity);
        return entity;
    }

    @Override
    public void delResource(String id) throws Exception {
//        BiUiModelField inf = biUiModelFieldMapper.selectById(id);
        biUiModelFieldMapper.deleteById(id);
    }

    @Override
    public BiUiModelField updateResource(UpdateResourcesDto dto) throws Exception {
//        BiUiModelField inf = biUiModelFieldMapper.selectById(dto.getId());
        BiUiModelField entity = new BiUiModelField();
        BeanUtils.copyProperties(dto, entity);
        entity.setModifiedDate(LocalDateTime.now());
        biUiModelFieldMapper.updateById(entity);
        return entity;
    }
}
