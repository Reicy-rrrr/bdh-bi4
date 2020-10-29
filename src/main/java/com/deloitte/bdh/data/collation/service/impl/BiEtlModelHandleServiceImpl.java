package com.deloitte.bdh.data.collation.service.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.collation.component.ComponentHandler;
import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.request.PreviewSqlDto;
import com.deloitte.bdh.data.collation.model.resp.BiComponentTree;
import com.deloitte.bdh.data.collation.service.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * etl模板处理Service实现
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Slf4j
@Service
@DS(DSConstant.BI_DB)
public class BiEtlModelHandleServiceImpl implements BiEtlModelHandleService {

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

    @Autowired
    private DbHandler dbHandler;

    @Autowired
    private ComponentHandler componentHandler;

    @Override
    public String previewSql(PreviewSqlDto dto) {
        // 模板code
        String modelCode = dto.getModelCode();
        if (StringUtils.isBlank(modelCode)) {
            throw new BizException("模板code不能为空！");
        }
        // 组件code
        String componentCode = dto.getComponentCode();
        if (StringUtils.isBlank(componentCode)) {
            throw new BizException("组件code不能为空");
        }
        // 根据组件查询组件树
        BiComponentTree componentTree = biComponentService.selectTree(modelCode, componentCode);
        // 根据组件查询组件树
        ComponentModel componentModel = new ComponentModel();
        convertToModel(componentTree, componentModel);
        componentModel.setLast(true);
        handleComponent(componentModel);
        String resultSql = SQLUtils.formatMySql(componentModel.getSql(), SQLUtils.DEFAULT_FORMAT_OPTION);
        return resultSql;
    }

    @Override
    public String createSql(String modelCode) {
        if (StringUtils.isBlank(modelCode)) {
            throw new BizException("模板id不能为空！");
        }

        LambdaQueryWrapper<BiEtlModel> modelWrapper = new LambdaQueryWrapper();
        modelWrapper.eq(BiEtlModel::getCode, modelCode);
        BiEtlModel model = biEtlModelService.getOne(modelWrapper);
        if (model == null) {
            log.error("根据模板id[{}]未查询到模板信息！", modelCode);
            throw new BizException("未查询到模板信息！");
        }
        // 根据模板查询组件树
        BiComponentTree componentTree = biComponentService.selectTree(model.getCode(), null);
        // 根据组件查询组件树
        ComponentModel componentModel = new ComponentModel();
        convertToModel(componentTree, componentModel);
        componentModel.setLast(true);
        handleComponent(componentModel);
        return componentModel.getSql();
    }

    /**
     * 处理组件
     *
     * @param component
     */
    private void handleComponent(ComponentModel component) {
        while (true) {
            List<ComponentModel> untreatedModels = Lists.newArrayList();
            getUnhandledComponent(component, untreatedModels);
            if (CollectionUtils.isEmpty(untreatedModels)) {
                break;
            }

            // 批量查询组件参数
            List<String> componentCodes = untreatedModels.stream().map(ComponentModel::getCode).collect(Collectors.toList());
            LambdaQueryWrapper<BiComponentParams> paramWrapper = new LambdaQueryWrapper();
            paramWrapper.in(BiComponentParams::getRefComponentCode, componentCodes);
            List<BiComponentParams> list = biComponentParamsService.list(paramWrapper);

            for (ComponentModel untreatedModel : untreatedModels) {
                ComponentTypeEnum componentType = ComponentTypeEnum.values(untreatedModel.getType());
                untreatedModel.setTypeEnum(componentType);
                // 获取当前组件参数
                List<BiComponentParams> currParams = list.stream()
                        .filter(param -> param.getRefComponentCode().equals(untreatedModel.getCode()))
                        .collect(Collectors.toList());
                untreatedModel.setParams(currParams);
                componentHandler.handle(untreatedModel);
                untreatedModel.setHandled(true);
            }
            untreatedModels.clear();
        }
    }

    /**
     * 递归转换组件
     *
     * @param componentTree  组件树
     * @param componentModel 组件模型
     */
    private void convertToModel(BiComponentTree componentTree, ComponentModel componentModel) {
        BeanUtils.copyProperties(componentTree, componentModel, "from");
        List<BiComponentTree> fromComponents = componentTree.getFrom();
        // 没有从组件：结束转换
        if (CollectionUtils.isEmpty(fromComponents)) {
            return;
        }

        List<ComponentModel> fromModels = Lists.newArrayList();
        for (BiComponentTree currComponent : fromComponents) {
            ComponentModel fromModel = new ComponentModel();
            BeanUtils.copyProperties(currComponent, fromModel, "from");
            fromModels.add(fromModel);
            if (CollectionUtils.isEmpty(currComponent.getFrom())) {
                continue;
            }
            // 递归调用
            convertToModel(currComponent, fromModel);
        }
        componentModel.setFrom(fromModels);
    }

    /**
     * 递归获取组件模型最外层还未处理的组件集合（not all）
     *
     * @param component           组件树模型
     * @param unhandledComponents 未处理的组件模型集合
     */
    private void getUnhandledComponent(ComponentModel component,
                                       List<ComponentModel> unhandledComponents) {
        if (unhandledComponents == null) {
            unhandledComponents = Lists.newArrayList();
        }
        // 若当前组件已经处理，则无需再往下查询
        if (component.isHandled()) {
            return;
        }

        // 如果当前组件没有从组件，则当前组件需要处理
        List<ComponentModel> fromModels = component.getFrom();
        if (CollectionUtils.isEmpty(fromModels)) {
            unhandledComponents.add(component);
            return;
        }

        // 所有从组件已经处理完
        boolean allFromProcessed = true;
        for (ComponentModel fromModel : fromModels) {
            // 当前从组件已经处理，直接跳过
            if (fromModel.isHandled()) {
                continue;
            }
            // 当前从组件没有处理，继续递归向下搜索
            allFromProcessed = false;
            getUnhandledComponent(fromModel, unhandledComponents);
        }

        // 所有从组件已经处理完，则轮到当前处理当前组件
        if (allFromProcessed) {
            unhandledComponents.add(component);
        }
    }
}
