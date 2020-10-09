package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.dao.bi.BiEtlDbServiceMapper;
import com.deloitte.bdh.data.model.BiEtlDbService;
import com.deloitte.bdh.data.service.BiEtlDbCSService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chenghzhang
 * @since 2020-09-30
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlDbCSServiceImpl extends AbstractService<BiEtlDbServiceMapper, BiEtlDbService> implements BiEtlDbCSService {

    @Autowired
    private BiEtlDbServiceMapper biEtlDbServiceMapper;

    @Override
    public String insert(BiEtlDbService biEtlDbService) {
        biEtlDbServiceMapper.insert(biEtlDbService);
        return biEtlDbService.getId();
    }

    @Override
    public int update(BiEtlDbService biEtlDbService) {
        if (StringUtils.isBlank(biEtlDbService.getId())) {
            throw new BizException("id不能为空");
        }
        int i = biEtlDbServiceMapper.updateById(biEtlDbService);
        return i;
    }

    @Override
    public int deleteById(String id) {
        int i = biEtlDbServiceMapper.deleteById(id);
        return i;
    }

    @Override
    public List<BiEtlDbService> list(BiEtlDbService biEtlDbService) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (biEtlDbService == null) {
            return biEtlDbServiceMapper.selectList(queryWrapper);
        }

        if (StringUtils.isNotBlank(biEtlDbService.getDbId())) {
            queryWrapper.eq("DB_ID", biEtlDbService.getDbId());
        }

        if (StringUtils.isNotBlank(biEtlDbService.getPropertyName())) {
            queryWrapper.eq("PROPERTY_NAME", biEtlDbService.getPropertyName());
        }
        return biEtlDbServiceMapper.selectList(queryWrapper);
    }
}
