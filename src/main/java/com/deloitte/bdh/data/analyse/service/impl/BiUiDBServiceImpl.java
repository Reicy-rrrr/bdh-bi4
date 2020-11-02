package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.analyse.model.resp.TableColumnTree;
import com.deloitte.bdh.data.analyse.service.BiUiDBService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFieldService;
import com.deloitte.bdh.data.analyse.service.BiUiModelFolderService;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
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
    public List<TableColumn> getAllColumns(String tableName, String tenantId) {
        if (StringUtil.isEmpty(tableName)) {
            throw new RuntimeException("表不能为空");
        }
        if (StringUtil.isEmpty(tenantId)) {
            throw new RuntimeException("租户id不能为空");
        }
        List<TableColumn> columns = dbHandler.getColumns(tableName);
        Map<String, TableColumnTree> treeMap = new HashMap<>();
        Map<String, TableColumnTree> folderMap = new HashMap<>();
        TableColumnTree top = new TableColumnTree();
        top.setName(tableName);
        top.setModelType(AnalyseConstants.DATA_MODEL_TYPE_TOP);
        TableColumnTree wd = new TableColumnTree();
        wd.setName("维度");
        wd.setModelType(AnalyseConstants.DATA_MODEL_TYPE_TOP);
        TableColumnTree dl = new TableColumnTree();
        dl.setName("度量");
        dl.setModelType(AnalyseConstants.DATA_MODEL_TYPE_TOP);
        top.addChildren(wd);
        top.addChildren(dl);
        for (TableColumn column : columns) {
            TableColumnTree tree = new TableColumnTree();
            BeanUtils.copyProperties(column, tree);
            tree.setModelType(AnalyseConstants.DATA_MODEL_TYPE_FIELD);
            treeMap.put(column.getName(), tree);
        }
        List<BiUiModelFolder> folders = biUiModelFolderService.getTenantBiUiModelFolders(tenantId);
        for (BiUiModelFolder folder : folders) {
            TableColumnTree tree = new TableColumnTree();
            tree.setName(folder.getName());
            tree.setDataType(folder.getType());
            folderMap.put(folder.getId(), tree);
            tree.setModelType(AnalyseConstants.DATA_MODEL_TYPE_FOLDER);
        }
        List<BiUiModelField> fields = biUiModelFieldService.getTenantBiUiModelFields(tenantId);
        for (BiUiModelField field : fields) {
            TableColumnTree tree = new TableColumnTree();
            tree.setName(field.getSourceField());
            BeanUtils.copyProperties(field, tree);
            tree.setModelType(AnalyseConstants.DATA_MODEL_TYPE_FIELD);
            treeMap.put(tree.getName(), tree);
        }
        return columns;
    }
}
