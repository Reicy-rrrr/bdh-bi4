package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.request.PreviewSqlDto;
import com.deloitte.bdh.data.collation.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * etl模板处理Service实现
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service
public class BiEtlModelHandlerServiceImpl implements BiEtlModelHandlerService {

    @Autowired
    private BiEtlDatabaseInfService biEtlDatabaseInfService;

    @Autowired
    private BiEtlModelService biEtlModelService;

    @Autowired
    private BiEtlDbRefService biEtlDbRefService;

    @Autowired
    private BiEtlMappingConfigService biEtlMappingConfigService;

    @Autowired
    private BiEtlMappingFieldService biEtlMappingFieldService;

    @Autowired
    private BiComponentService biComponentService;

    @Autowired
    private BiComponentConnectionService biComponentConnectionService;

    @Autowired
    private BiComponentParamsService biComponentParamsService;

    @Override
    public String previewSql(PreviewSqlDto dto) {
        // 模板id
        String modelId = dto.getModelId();
        if (StringUtils.isBlank(modelId)) {
            throw new BizException("模板id不能为空！");
        }
        // 组件id
        String componentId = dto.getComponentId();
        if (StringUtils.isBlank(componentId)) {
            throw new BizException("组件id不能为空");
        }

        return null;
    }

    @Override
    public String createSql(String modelId) {
        if (StringUtils.isBlank(modelId)) {
            throw new BizException("模板id不能为空！");
        }

        BiEtlModel model = biEtlModelService.getModel(modelId);
        if (model == null) {
            log.error("根据模板id[{}]未查询到模板信息！", modelId);
            throw new BizException("未查询到模板信息！");
        }

        String modelCode = model.getCode();
        LambdaQueryWrapper<BiComponent> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(BiComponent::getRefModelCode, modelCode);
        List<BiComponent> list = biComponentService.list(queryWrapper);
        return null;
    }
}
