package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiModelFolderMapper;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFolderService;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
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
 * @since 2020-10-21
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalyseModelFolderServiceImpl extends AbstractService<BiUiModelFolderMapper, BiUiModelFolder> implements AnalyseModelFolderService {
    @Resource
    BiUiModelFolderMapper biUiModelFolderMapper;

    @Override
    public BiUiModelFolder getResource(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个resource 失败:id 不能为空");
        }
        return biUiModelFolderMapper.selectById(id);
    }

    @Override
    public BiUiModelFolder createResource(CreateResourcesDto dto) {
        BiUiModelFolder entity = new BiUiModelFolder();
        BeanUtils.copyProperties(dto, entity);
        biUiModelFolderMapper.insert(entity);
        return entity;
    }

    @Override
    public void delResource(String id) {
//        BiUiModelFolder inf = biUiModelFolderMapper.selectById(id);
        biUiModelFolderMapper.deleteById(id);
    }

    @Override
    public BiUiModelFolder updateResource(UpdateResourcesDto dto) {
//        BiUiModelFolder inf = biUiModelFolderMapper.selectById(dto.getId());
        BiUiModelFolder entity = new BiUiModelFolder();
        BeanUtils.copyProperties(dto, entity);
        entity.setModifiedDate(LocalDateTime.now());
        biUiModelFolderMapper.updateById(entity);
        return entity;
    }
}