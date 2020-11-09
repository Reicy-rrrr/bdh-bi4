package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiModelFieldMapper;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiModelFolderMapper;
import com.deloitte.bdh.data.analyse.enums.FolderTypeEnum;
import com.deloitte.bdh.data.analyse.enums.YnTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelFieldTree;
import com.deloitte.bdh.data.analyse.model.request.DataTreeRequest;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFieldTree;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFolderTree;
import com.deloitte.bdh.data.analyse.service.BiUiDBService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFieldService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFolderService;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenghzhang
 * @date 2020/10/27
 */
@Service
@DS(DSConstant.BI_DB)
public class BiUiDBServiceImpl implements BiUiDBService {

    @Autowired
    private DbHandler dbHandler;

    @Resource
    private BiUiModelFolderMapper folderMapper;

    @Resource
    private BiUiModelFieldMapper fieldMapper;

    @Resource
    BiUiModelFolderService biUiModelFolderService;
    @Resource
    BiUiModelFieldService biUiModelFieldService;

    @Override
    public List<String> getAllDataSource() {
        return null;
    }

    @Override
    public List<String> getAllTable() {
        return dbHandler.getTables();
    }

    @Override
    public Collection<DataModelFieldTree> getAllColumns(String tableName, String tenantId) {
        if (StringUtil.isEmpty(tableName)) {
            throw new RuntimeException("表不能为空");
        }
        if (StringUtil.isEmpty(tenantId)) {
            throw new RuntimeException("租户id不能为空");
        }
        List<TableColumn> columns = dbHandler.getColumns(tableName);
        Map<String, DataModelFieldTree> treeMap = new LinkedHashMap<>();
        Map<String, DataModelFieldTree> folderMap = new LinkedHashMap<>();

        DataModelFieldTree top = new DataModelFieldTree();
        top.setName(tableName);
        top.setModelType(AnalyseConstants.DATA_MODEL_TYPE_TOP);
        top.setId(AnalyseConstants.DATA_MODEL_TYPE_TOP);
        folderMap.put(AnalyseConstants.DATA_MODEL_TYPE_TOP, top);

        DataModelFieldTree wd = new DataModelFieldTree();
        wd.setName("维度");
        wd.setModelType(AnalyseConstants.DATA_MODEL_TYPE_TOP_WD);
        wd.setId(AnalyseConstants.DATA_MODEL_TYPE_TOP_WD);
        folderMap.put(AnalyseConstants.DATA_MODEL_TYPE_TOP_WD, wd);

        DataModelFieldTree dl = new DataModelFieldTree();
        dl.setName("度量");
        dl.setModelType(AnalyseConstants.DATA_MODEL_TYPE_TOP_DL);
        dl.setId(AnalyseConstants.DATA_MODEL_TYPE_TOP_DL);
        folderMap.put(AnalyseConstants.DATA_MODEL_TYPE_TOP_DL, dl);

        top.addChildren(wd);
        top.addChildren(dl);
        /**
         * 表当前信息
         */
        for (TableColumn column : columns) {
            DataModelFieldTree tree = new DataModelFieldTree();
            BeanUtils.copyProperties(column, tree);
            tree.setModelType(AnalyseConstants.DATA_MODEL_TYPE_FIELD);
            tree.setId("O_" + column.getName());
            treeMap.put(column.getName(), tree);
        }
        /**
         * 表历史文件夹
         */
        List<BiUiModelFolder> folders = biUiModelFolderService.getTenantBiUiModelFolders(tenantId);
        for (BiUiModelFolder folder : folders) {
            DataModelFieldTree tree = new DataModelFieldTree();
            tree.setName(folder.getName());
            tree.setDataType(folder.getType());
            tree.setId("F_" + folder.getName());
            folderMap.put(folder.getId(), tree);
            tree.setModelType(AnalyseConstants.DATA_MODEL_TYPE_FOLDER);
        }
        /**
         * 组件文件夹树
         */
        for (BiUiModelFolder folder : folders) {
            if (folder.getParentId() != null) {
                DataModelFieldTree parent = folderMap.get(folder.getParentId());
                if (parent != null) {
                    DataModelFieldTree child = folderMap.get(folder.getId());
                    if (child != null) {
                        parent.addChildren(child);
                    }
                }
            }
        }
        /**
         * 表历史配置,覆盖实际情况
         */
        List<BiUiModelField> fields = biUiModelFieldService.getTenantBiUiModelFields(tenantId);
        for (BiUiModelField field : fields) {
            DataModelFieldTree tree = new DataModelFieldTree();
            tree.setName(field.getName());
            BeanUtils.copyProperties(field, tree);
            tree.setModelType(AnalyseConstants.DATA_MODEL_TYPE_FIELD);
            treeMap.put(tree.getName(), tree);
        }
        /**
         * 组建field树
         */
        for (DataModelFieldTree child : treeMap.values()) {
            if (child.getFolderId() != null) {
                DataModelFieldTree parent = folderMap.get(child.getFolderId());
                if (parent != null) {
                    parent.addChildren(child);
                }
            } else {
                if (child.getIsDimention() != null) {
                    wd.addChildren(child);
                } else if (child.getIsMensure() != null) {
                    dl.addChildren(child);
                } else {
                    wd.addChildren(child);
                }
            }
        }
        return top.getChildren();
    }

    @Transactional
    @Override
    public List<AnalyseFolderTree> getDataTree(RetRequest<DataTreeRequest> request) {
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

    /**
     * 获取历史数据
     * @param request
     * @return
     */
    private Map<String, Object> getHistoryData(RetRequest<DataTreeRequest> request) {
        QueryWrapper<BiUiModelFolder> folderQueryWrapper = new QueryWrapper<>();
        folderQueryWrapper.eq("PAGE_ID", request.getData().getPageId());
        List<BiUiModelFolder> folderList = folderMapper.selectList(folderQueryWrapper);
        String wdId = null;
        if (CollectionUtils.isEmpty(folderList)) {
            folderList = Lists.newArrayList();
            //初始化维度数据
            BiUiModelFolder wd = new BiUiModelFolder();
            wd.setPageId(request.getData().getPageId());
            wd.setParentId("0");
            wd.setName(FolderTypeEnum.WD.getDesc());
            wd.setType(FolderTypeEnum.WD.getType());
            wd.setTenantId(request.getTenantId());
            wd.setIp(request.getIp());
            wd.setCreateUser(request.getOperator());
            wd.setCreateDate(LocalDateTime.now());
            folderMapper.insert(wd);
            wdId = wd.getId();

            //初始化度量数据
            BiUiModelFolder dl = new BiUiModelFolder();
            dl.setPageId(request.getData().getPageId());
            dl.setParentId("0");
            dl.setName(FolderTypeEnum.DL.getDesc());
            dl.setType(FolderTypeEnum.DL.getType());
            dl.setIp(request.getIp());
            dl.setTenantId(request.getTenantId());
            dl.setCreateUser(request.getOperator());
            dl.setCreateDate(LocalDateTime.now());
            folderMapper.insert(dl);

            folderList.add(wd);
            folderList.add(dl);
        }

        QueryWrapper<BiUiModelField> fieldQueryWrapper = new QueryWrapper<>();
        fieldQueryWrapper.eq("PAGE_ID", request.getData().getPageId());
        List<BiUiModelField> fieldList = fieldMapper.selectList(fieldQueryWrapper);
        if (CollectionUtils.isEmpty(fieldList)) {
            List<TableColumn> columns = dbHandler.getColumns(request.getData().getTableName());
            //初始化字段数据
            for (TableColumn column : columns) {
                BiUiModelField field = new BiUiModelField();
                BeanUtils.copyProperties(column, field);
                field.setFieldDesc(column.getDesc());
                field.setParentId("0");
                field.setPageId(request.getData().getPageId());
                field.setFolderId(wdId);
                field.setIsDimention(YnTypeEnum.YES.getName());
                field.setTenantId(request.getTenantId());
                field.setIp(request.getIp());
                field.setCreateUser("0");
                field.setCreateDate(LocalDateTime.now());
                fieldMapper.insert(field);
                fieldList.add(field);
            }
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("folder", folderList);
        result.put("field", fieldList);
        return result;
    }

    private static List<AnalyseFieldTree> buildFieldTree(List<BiUiModelField> fieldList, String parentId) {
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
}
