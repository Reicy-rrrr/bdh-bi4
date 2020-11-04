package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelFieldTree;
import com.deloitte.bdh.data.analyse.service.BiUiDBService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFieldService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFolderService;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
            tree.setName(field.getSourceField());
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
}
