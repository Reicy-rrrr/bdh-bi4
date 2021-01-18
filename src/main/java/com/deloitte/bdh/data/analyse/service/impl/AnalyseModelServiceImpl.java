package com.deloitte.bdh.data.analyse.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.*;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseCategory;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.request.GetAnalyseDataTreeDto;
import com.deloitte.bdh.data.analyse.model.resp.*;
import com.deloitte.bdh.data.analyse.service.*;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.model.request.DataSetTableInfo;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chenghzhang
 * @date 2020/10/27
 */
@Slf4j
@Service
@DS(DSConstant.BI_DB)
public class AnalyseModelServiceImpl implements AnalyseModelService {

    @Resource
    private AnalyseModelFolderService folderService;
    @Resource
    private AnalyseModelFieldService fieldService;
    @Resource
    private BiDataSetService dataSetService;
    @Resource
    private AnalyseCategoryService categoryService;
    @Resource
    private AnalysePageService pageService;

    @Override
    public List<DataSetTableInfo> getAllTable() {
        List<DataSetTableInfo> tableInfos = Lists.newArrayList();
        List<BiDataSet> dataSetList = dataSetService.getTableList();
        if (CollectionUtils.isNotEmpty(dataSetList)) {
            for (BiDataSet dataSet : dataSetList) {
                DataSetTableInfo tableInfo = new DataSetTableInfo();
                tableInfo.setId(dataSet.getId());
                tableInfo.setCode(dataSet.getCode());
                tableInfo.setToTableName(dataSet.getTableName());
                tableInfo.setToTableDesc(dataSet.getTableDesc());
                tableInfos.add(tableInfo);
            }
        }
        return tableInfos;
    }

    @Override
    public void saveDataTree(RetRequest<List<AnalyseDataModelTree>> request) {
        List<AnalyseDataModelTree> dataModelTreeList = request.getData();
        List<BiUiModelFolder> folderList = Lists.newArrayList();
        List<BiUiModelField> fieldList = Lists.newArrayList();
        List<AnalyseDataModelTree> dataModelList = dataModelTreeToList(dataModelTreeList);
        String modelId = "";
        for (AnalyseDataModelTree dataModel : dataModelList) {
            if (StringUtils.equals(dataModel.getChildrenType(), TreeChildrenTypeEnum.FOLDER.getCode())) {
                BiUiModelFolder folder = new BiUiModelFolder();
                modelId = dataModel.getModelId();
                BeanUtils.copyProperties(dataModel, folder);
                folderList.add(folder);
            }
            if (StringUtils.equals(dataModel.getChildrenType(), TreeChildrenTypeEnum.FIELD.getCode())) {
                BiUiModelField field = new BiUiModelField();
                BeanUtils.copyProperties(dataModel, field);
                fieldList.add(field);
            }
        }
        //如果做了删除操作先删除文件夹
        List<BiUiModelFolder> oldFolderList = folderService.list(new LambdaQueryWrapper<BiUiModelFolder>()
                .eq(BiUiModelFolder::getModelId, modelId));
        if (CollectionUtils.isNotEmpty(oldFolderList)) {
            //求差集
            List<String> oldFolderIdList = Lists.newArrayList();
            oldFolderList.forEach(folder -> oldFolderIdList.add(folder.getId()));

            List<String> folderIdList = Lists.newArrayList();
            folderList.forEach(folder -> folderIdList.add(folder.getId()));

            oldFolderIdList.removeAll(folderIdList);
            if (CollectionUtils.isNotEmpty(oldFolderIdList)) {
                folderService.removeByIds(oldFolderIdList);
            }
        }
        //更新
        folderService.updateBatchById(folderList);
        fieldService.updateBatchById(fieldList);
    }

    @Override
    public void saveOrUpdateFolder(RetRequest<SaveOrUpdateFolderDto> request) {
        BiUiModelFolder folder = new BiUiModelFolder();
        BeanUtils.copyProperties(request.getData(), folder);
        folder.setCreateUser(ThreadLocalHolder.getOperator());
        folder.setTenantId(ThreadLocalHolder.getTenantId());
        folderService.saveOrUpdate(folder);
    }

    @Transactional
    @Override
    public List<AnalyseDataModelTree> getDataTree(RetRequest<GetAnalyseDataTreeDto> request) throws Exception {
        Map<String, Object> result = getHistoryData(request);

        List<BiUiModelFolder> folderList = (List<BiUiModelFolder>) result.get("folder");

        List<BiUiModelField> fieldList = (List<BiUiModelField>) result.get("field");

        Map<String, List<BiUiModelField>> fieldMap = Maps.newHashMap();
        for (BiUiModelField field : fieldList) {
            List<BiUiModelField> stepFieldList = fieldMap.get(field.getFolderId());
            if (CollectionUtils.isNotEmpty(stepFieldList)) {
                stepFieldList.add(field);
            } else {
                stepFieldList = Lists.newArrayList();
                stepFieldList.add(field);
                fieldMap.put(field.getFolderId(), stepFieldList);
            }
        }

        //递归字段树
        List<AnalyseDataModelTree> dataTree = buildTree(folderList, fieldMap, "0");
        return dataTree;
    }

    @Override
    public BaseComponentDataResponse getComponentData(ComponentDataRequest request) throws Exception {
        String name = DataImplEnum.getImpl(request.getType(), request.getDataConfig().getTableType());
        BaseComponentDataResponse response = new BaseComponentDataResponse();
        try {
            response = SpringUtil.getBean(name, AnalyseDataService.class).handle(request);
            Map<String, Object> extraMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(response.getExtra())) {
                extraMap = response.getExtra();
            }
            if (MapUtils.isNotEmpty(joinDataUnit(request, response))) {
                extraMap.putAll(joinDataUnit(request, response));
            }
            response.setExtra(extraMap);
        } catch (SQLException e) {
            log.error(e.getMessage());
            response.setRows(null);
        }
            return response;
    }

    @Override
    public void initDefaultData() {
        BiUiAnalyseCategory myAnalyse = new BiUiAnalyseCategory();
        myAnalyse.setParentId("0");
        myAnalyse.setName("默认文件夹");
        myAnalyse.setType(CategoryTypeEnum.CUSTOMER.getCode());
        myAnalyse.setDes("默认文件夹");
        myAnalyse.setTenantId(ThreadLocalHolder.getTenantId());
        categoryService.save(myAnalyse);

        BiUiAnalyseCategory component = new BiUiAnalyseCategory();
        component.setParentId("0");
        component.setName("默认文件夹");
        component.setType(CategoryTypeEnum.COMPONENT.getCode());
        component.setDes("默认文件夹");
        component.setTenantId(ThreadLocalHolder.getTenantId());
        categoryService.save(component);

        BiUiAnalysePage page = new BiUiAnalysePage();
        page.setName("默认仪表板");
        page.setDes("默认仪表板");
        page.setType("dashboard");
        page.setParentId(myAnalyse.getId());
        page.setIsEdit(YnTypeEnum.NO.getCode());
        page.setDeloitteFlag("0");
        page.setTenantId(ThreadLocalHolder.getTenantId());
        pageService.save(page);

    }

    /*
     * 返回前端传来的数据单位
     */
    private Map<String, Object> joinDataUnit(ComponentDataRequest request, BaseComponentDataResponse response) {

        DataModel dataModel = request.getDataConfig().getDataModel();
        //存放所有入参中符合条件的字段
        List<DataModelField> reqAll = Lists.newArrayList();
        List<DataModelField> reqX = dataModel.getX();
        List<DataModelField> reqY = dataModel.getY();
        List<DataModelField> reqY2 = dataModel.getY2();
        List<DataModelField> reqCategory = dataModel.getCategory();
        //其他参数目前只有这两个
        DataModelField scatterName = JSONObject.parseObject(JSON.toJSONString(MapUtils.getObject(dataModel.getCustomParams(),
                CustomParamsConstants.SCATTER_NAME)), DataModelField.class);
        DataModelField scatterSize = JSONObject.parseObject(JSON.toJSONString(MapUtils.getObject(dataModel.getCustomParams(),
                CustomParamsConstants.SCATTER_SIZE)), DataModelField.class);
        DataModelField symbolSize = JSONObject.parseObject(JSON.toJSONString(MapUtils.getObject(dataModel.getCustomParams(),
                CustomParamsConstants.SYMBOL_SIZE)), DataModelField.class);
        reqAll.addAll(reqX);
        reqAll.addAll(reqY);
        reqAll.addAll(reqY2);
        reqAll.addAll(reqCategory);
        reqAll.add(scatterName);
        reqAll.add(scatterSize);
        reqAll.add(symbolSize);

        Map<String, Object> dataUnitMap = Maps.newHashMap();
        List<Object> dataUnitList = Lists.newArrayList();
        for (DataModelField dataModelField : reqAll) {
            //如果是度量，且数据单位不为空，则返回
            if (Objects.nonNull(dataModelField) && dataModelField.getQuota().equals(DataModelTypeEnum.DL.getCode())) {
                if (StringUtils.isNotEmpty(dataModelField.getDataUnit())) {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("id", dataModelField.getId());
                    map.put("alias", dataModelField.getAlias());
                    map.put("dataUnit", DataUnitEnum.getDesc(dataModelField.getDataUnit()));
                    dataUnitList.add(map);
                }
            }
        }
        if (CollectionUtils.isEmpty(dataUnitList)) {
            return null;
        }
        dataUnitMap.put("dataUnit", dataUnitList);
        return dataUnitMap;
    }

    /**
     * 获取历史数据
     *
     * @param request
     * @return
     */
    private Map<String, Object> getHistoryData(RetRequest<GetAnalyseDataTreeDto> request) throws Exception {
        LambdaQueryWrapper<BiUiModelFolder> folderQueryWrapper = new LambdaQueryWrapper<>();
        folderQueryWrapper.eq(BiUiModelFolder::getModelId, request.getData().getModelId());
        List<BiUiModelFolder> folderList = folderService.list(folderQueryWrapper);
        String wdId = null;
        String dlId = null;
        Map<String, Object> result = Maps.newHashMap();

        if (CollectionUtils.isEmpty(folderList)) {
            folderList = Lists.newArrayList();
            //初始化维度文件夹
            BiUiModelFolder wd = new BiUiModelFolder();
            wd.setModelId(request.getData().getModelId());
            wd.setParentId("0");
            wd.setName(DataModelTypeEnum.WD.getDesc());
            wd.setType(DataModelTypeEnum.WD.getCode());
            wd.setTenantId(ThreadLocalHolder.getTenantId());
            folderService.save(wd);
            wdId = wd.getId();

            //初始化度量文件夹
            BiUiModelFolder dl = new BiUiModelFolder();
            dl.setModelId(request.getData().getModelId());
            dl.setParentId("0");
            dl.setName(DataModelTypeEnum.DL.getDesc());
            dl.setType(DataModelTypeEnum.DL.getCode());
            dl.setIp(request.getIp());
            dl.setTenantId(ThreadLocalHolder.getTenantId());
            folderService.save(dl);
            dlId = dl.getId();

            folderList.add(wd);
            folderList.add(dl);
        }

        LambdaQueryWrapper<BiUiModelField> fieldQueryWrapper = new LambdaQueryWrapper<>();
        fieldQueryWrapper.eq(BiUiModelField::getModelId, request.getData().getModelId());
        List<BiUiModelField> fieldList = fieldService.list(fieldQueryWrapper);
        if (CollectionUtils.isEmpty(fieldList)) {
            // 引入数据集
            List<TableColumn> columns = dataSetService.getColumns(request.getData().getModelId());
            //初始化字段数据
            for (TableColumn column : columns) {
                BiUiModelField field = new BiUiModelField();
                BeanUtils.copyProperties(column, field);
                field.setModelId(request.getData().getModelId());
                field.setFieldDesc(column.getDesc());
                field.setParentId("0");
                if (AnalyseConstants.MENSURE_TYPE.contains(column.getDataType().toUpperCase())) {
                    field.setFolderId(dlId);
                    field.setIsDimention(YnTypeEnum.NO.getCode());
                    field.setIsMensure(YnTypeEnum.YES.getCode());
                } else {
                    field.setFolderId(wdId);
                    field.setIsDimention(YnTypeEnum.YES.getCode());
                    field.setIsMensure(YnTypeEnum.NO.getCode());
                }
                field.setTenantId(ThreadLocalHolder.getTenantId());
                fieldService.save(field);
                fieldList.add(field);
            }
        }
        result.put("folder", folderList);
        result.put("field", fieldList);
        return result;
    }

    /**
     * 递归转换成树
     *
     * @param folderList
     * @param fieldMap
     * @param parentId
     * @return
     */
    private List<AnalyseDataModelTree> buildTree(List<BiUiModelFolder> folderList, Map<String, List<BiUiModelField>> fieldMap, String parentId) {
        List<AnalyseDataModelTree> treeDataModels = Lists.newArrayList();
        for (BiUiModelFolder folder : folderList) {
            AnalyseDataModelTree dataModelTree = new AnalyseDataModelTree();
            BeanUtils.copyProperties(folder, dataModelTree);

            if (parentId.equals(dataModelTree.getParentId())) {
                dataModelTree.setChildren(buildTree(folderList, fieldMap, dataModelTree.getId()));
                dataModelTree.setChildrenType(TreeChildrenTypeEnum.FOLDER.getCode());
                treeDataModels.add(dataModelTree);

                //将字段和文件夹放到同一个children，通过children type区分
                List<BiUiModelField> fieldList = fieldMap.get(folder.getId());
//                List<AnalyseDataModelTree> fieldTreeList = buildFieldTree(fieldList, "0");
                List<AnalyseDataModelTree> fieldTreeList = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(fieldTreeList)) {
                    for (BiUiModelField field : fieldList) {
                        AnalyseDataModelTree tree = new AnalyseDataModelTree();
                        BeanUtils.copyProperties(field, tree);
                        tree.setDesc(field.getFieldDesc());
                        tree.setChildrenType(TreeChildrenTypeEnum.FIELD.getCode());
                        fieldTreeList.add(tree);
                    }
                    if (CollectionUtils.isEmpty(dataModelTree.getChildren())) {
                        List<AnalyseDataModelTree> children = Lists.newArrayList();
                        dataModelTree.setChildren(children);
                    }
                    dataModelTree.getChildren().addAll(fieldTreeList);
                }
            }
        }
        return treeDataModels;
    }

    private List<AnalyseDataModelTree> buildFieldTree(List<BiUiModelField> fieldList, String parentId) {
        List<AnalyseDataModelTree> treeDataModels = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(fieldList)) {
            for (BiUiModelField field : fieldList) {
                AnalyseDataModelTree dataModelTree = new AnalyseDataModelTree();
                BeanUtils.copyProperties(field, dataModelTree);
                dataModelTree.setDesc(field.getFieldDesc());
                dataModelTree.setChildrenType(TreeChildrenTypeEnum.FIELD.getCode());

                if (parentId.equals(dataModelTree.getParentId())) {
                    dataModelTree.setChildren(buildFieldTree(fieldList, dataModelTree.getId()));
                    treeDataModels.add(dataModelTree);

                }
            }
        }
        return treeDataModels;
    }

    /**
     * 逆向递归树转List
     *
     * @param dataModelTreeList
     * @return
     */
    private List<AnalyseDataModelTree> dataModelTreeToList(List<AnalyseDataModelTree> dataModelTreeList) {
        List<AnalyseDataModelTree> result = Lists.newArrayList();
        for (AnalyseDataModelTree tree : dataModelTreeList) {
            AnalyseDataModelTree dataModel = new AnalyseDataModelTree();
            BeanUtils.copyProperties(tree, dataModel);
            dataModel.setChildren(null);
            result.add(dataModel);
            List<AnalyseDataModelTree> child = tree.getChildren();
            if (CollectionUtils.isNotEmpty(child)) {
                List<AnalyseDataModelTree> dataModelList = dataModelTreeToList(child);
                result.addAll(dataModelList);
            }
        }
        return result;
    }
}
