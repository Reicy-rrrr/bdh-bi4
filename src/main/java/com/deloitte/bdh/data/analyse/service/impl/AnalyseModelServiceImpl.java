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
import com.deloitte.bdh.data.analyse.enums.DataImplEnum;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataUnitEnum;
import com.deloitte.bdh.data.analyse.enums.YnTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.request.GetAnalyseDataTreeDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFieldTree;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFolderTree;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.service.AnalyseModelService;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFieldService;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFolderService;
import com.deloitte.bdh.data.analyse.service.AnalyseModelService;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableInfo;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chenghzhang
 * @date 2020/10/27
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalyseModelServiceImpl implements AnalyseModelService {

    @Resource
    private DbHandler dbHandler;

    @Resource
    AnalyseModelFolderService folderService;

    @Resource
    AnalyseModelFieldService fieldService;

    @Override
    public List<TableInfo> getAllTable() {
        return dbHandler.getTableList();
    }

    @Override
    public List<BiDataSet> getDataSetTableList() {
        return dbHandler.getDataSetTableList();
    }


    @Override
    public void saveDataTree(RetRequest<List<AnalyseFolderTree>> request) {
        List<AnalyseFolderTree> folderTreeList = request.getData();
        List<BiUiModelFolder> folderList = Lists.newArrayList();
        List<BiUiModelField> fieldList = Lists.newArrayList();
        for (AnalyseFolderTree folderTree : folderTreeList) {
            BiUiModelFolder folder = new BiUiModelFolder();
            BeanUtils.copyProperties(folderTree, folder);
            folderList.add(folder);
            List<BiUiModelField> stepFieldList = fieldTreeToList(folderTree.getChildren());
            fieldList.addAll(stepFieldList);
        }
        //更新
        folderService.saveOrUpdateBatch(folderList);
        fieldService.saveOrUpdateBatch(fieldList);
    }

    @Transactional
    @Override
    public List<AnalyseFolderTree> getDataTree(RetRequest<GetAnalyseDataTreeDto> request) {
        Map<String, Object> result = getHistoryData(request);

        List<BiUiModelFolder> folderList = (List<BiUiModelFolder>) result.get("folder");

        List<BiUiModelField> fieldList = (List<BiUiModelField>) result.get("field");

        List<AnalyseFolderTree> dataTree = Lists.newArrayList();
        for (BiUiModelFolder folder : folderList) {
            AnalyseFolderTree folderTree = new AnalyseFolderTree();
            BeanUtils.copyProperties(folder, folderTree);

            //适配每个文件夹下的字段
            List<BiUiModelField> stepFieldList = Lists.newArrayList();
            for (BiUiModelField field : fieldList) {
                if (StringUtils.equals(field.getFolderId(), folder.getId())) {
                    stepFieldList.add(field);
                }
            }

            //递归字段树
            List<AnalyseFieldTree> fieldTree = buildFieldTree(stepFieldList, "0");

            folderTree.setChildren(fieldTree);
            dataTree.add(folderTree);
        }
        return dataTree;
    }

    @Override
    public BaseComponentDataResponse getComponentData(ComponentDataRequest request) throws Exception {
        String name = DataImplEnum.getImpl(request.getType(), request.getDataConfig().getTableType());
        BaseComponentDataResponse response = SpringUtil.getBean(name, AnalyseDataService.class).handle(request);
        response.setExtra(joinDataUnit(request, response));
        return response;
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
        reqAll.addAll(reqX);
        reqAll.addAll(reqY);
        reqAll.addAll(reqY2);
        reqAll.addAll(reqCategory);
        reqAll.add(scatterName);
        reqAll.add(scatterSize);

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
        dataUnitMap.put("dataUnit", dataUnitList);
        return dataUnitMap;
     * 返回前端传来的数据单位
    private Map<String, Object> joinDataUnit(ComponentDataRequest request, BaseComponentDataResponse response) {
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
        reqAll.addAll(reqX);
        reqAll.addAll(reqY);
        reqAll.addAll(reqY2);
        reqAll.addAll(reqCategory);
        reqAll.add(scatterName);
        reqAll.add(scatterSize);
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
        dataUnitMap.put("dataUnit", dataUnitList);
        return dataUnitMap;
    }

    /**
     * 获取历史数据
     * @param request
     * @return
     */
    private Map<String, Object> getHistoryData(RetRequest<GetAnalyseDataTreeDto> request) {
        LambdaQueryWrapper<BiUiModelFolder> folderQueryWrapper = new LambdaQueryWrapper<>();
        folderQueryWrapper.eq(BiUiModelFolder::getPageId, request.getData().getPageId());
        folderQueryWrapper.eq(BiUiModelFolder::getModelId, request.getData().getModelId());
        folderQueryWrapper.orderByAsc(BiUiModelFolder::getSortOrder);
        List<BiUiModelFolder> folderList = folderService.list(folderQueryWrapper);
        String wdId = null;
        String dlId= null;
        if (CollectionUtils.isEmpty(folderList)) {
            folderList = Lists.newArrayList();
            //初始化维度文件夹
            BiUiModelFolder wd = new BiUiModelFolder();
            wd.setModelId(request.getData().getModelId());
            wd.setPageId(request.getData().getPageId());
            wd.setParentId("0");
            wd.setName(DataModelTypeEnum.WD.getDesc());
            wd.setType(DataModelTypeEnum.WD.getCode());
            wd.setTenantId(ThreadLocalHolder.getTenantId());
            folderService.save(wd);
            wdId = wd.getId();

            //初始化度量文件夹
            BiUiModelFolder dl = new BiUiModelFolder();
            dl.setModelId(request.getData().getModelId());
            dl.setPageId(request.getData().getPageId());
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
        fieldQueryWrapper.eq(BiUiModelField::getPageId, request.getData().getPageId());
        fieldQueryWrapper.eq(BiUiModelField::getModelId, request.getData().getModelId());
        fieldQueryWrapper.orderByAsc(BiUiModelField::getSortOrder);
        List<BiUiModelField> fieldList = fieldService.list(fieldQueryWrapper);
        if (CollectionUtils.isEmpty(fieldList)) {
            List<TableColumn> columns = dbHandler.getColumns(request.getData().getModelId());
            //初始化字段数据
            for (TableColumn column : columns) {
                BiUiModelField field = new BiUiModelField();
                BeanUtils.copyProperties(column, field);
                field.setModelId(request.getData().getModelId());
                field.setFieldDesc(column.getDesc());
                field.setParentId("0");
                field.setPageId(request.getData().getPageId());
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
        Map<String, Object> result = Maps.newHashMap();
        result.put("folder", folderList);
        result.put("field", fieldList);
        return result;
    }

    /**
     * 递归转换成树
     * @param fieldList
     * @param parentId
     * @return
     */
    private List<AnalyseFieldTree> buildFieldTree(List<BiUiModelField> fieldList, String parentId) {
        List<AnalyseFieldTree> treeDataModels = Lists.newArrayList();
        for (BiUiModelField field : fieldList) {
            AnalyseFieldTree analyseFieldTree = new AnalyseFieldTree();
            BeanUtils.copyProperties(field, analyseFieldTree);
            analyseFieldTree.setDesc(field.getFieldDesc());
            if (parentId.equals(analyseFieldTree.getParentId())) {
                analyseFieldTree.setChildren(buildFieldTree(fieldList, analyseFieldTree.getId()));
                treeDataModels.add(analyseFieldTree);
            }
        }
        return treeDataModels;
    }

    /**
     * 逆向递归树转List
     * @param fieldTreeList
     * @return
     */
    private List<BiUiModelField> fieldTreeToList(List<AnalyseFieldTree> fieldTreeList) {
        List<BiUiModelField> result = Lists.newArrayList();
        for (AnalyseFieldTree tree : fieldTreeList) {
            BiUiModelField field = new BiUiModelField();
            BeanUtils.copyProperties(tree, field);
            result.add(field);
            List<AnalyseFieldTree> child = tree.getChildren();
            if (CollectionUtils.isNotEmpty(child)) {
                List<BiUiModelField> fieldList = fieldTreeToList(child);
                result.addAll(fieldList);
            }
        }
        return result;
    }
}
