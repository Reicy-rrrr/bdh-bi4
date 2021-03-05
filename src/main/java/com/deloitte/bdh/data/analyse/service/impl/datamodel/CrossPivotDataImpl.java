package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataUnitEnum;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.datamodel.response.ListTree;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFieldService;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:交叉透视图
 */
@Service("crossPivotDataImpl")
public class CrossPivotDataImpl extends AbstractDataService implements AnalyseDataService {

    @Resource
    private AnalyseModelFieldService fieldService;

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        List<DataModelField> originalX = Lists.newArrayList(dataModel.getX());
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getY())) {
            dataModel.getY().forEach(field -> dataModel.getX().add(field));
        }

        String sql = buildSql(request.getDataConfig().getDataModel());
        BaseComponentDataResponse response = execute(dataModel, sql);
        if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
            String viewDetail = MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.VIEW_DETAIL);
            if (StringUtils.equals(viewDetail, "true")) {
                return response;
            }
        }
        //设置原始X轴数据构造树
        request.getDataConfig().getDataModel().setX(originalX);
        buildColumns(request, response);
        return response;
    }

    private void buildColumns(ComponentDataRequest request, BaseComponentDataResponse response) {
        List<Map<String, Object>> rows = response.getRows();
        //构造要查询的行字段
        if (CollectionUtils.isNotEmpty(rows)) {
            Map<String, List<ListTree>> columns = Maps.newHashMap();
            DataModel dataModel = request.getDataConfig().getDataModel();
            if (CollectionUtils.isNotEmpty(dataModel.getX())) {
                List<String> colNameWDList = Lists.newArrayList();
                List<String> colNameDLList = Lists.newArrayList();
                Map<String, String> precisionMap = Maps.newHashMap();
                Map<String, String> dataUnitMap = Maps.newHashMap();
                for (int i = 0; i < dataModel.getX().size(); i++) {
                    String quota = dataModel.getX().get(i).getQuota();
                    String colName = dataModel.getX().get(i).getId();
                    if (StringUtils.isNotBlank(dataModel.getX().get(i).getAlias())) {
                        colName = dataModel.getX().get(i).getAlias();
                    }
                    if (StringUtils.equals(DataModelTypeEnum.WD.getCode(), quota)) {
                        colNameWDList.add(colName);
                    } else {
                        colNameDLList.add(colName);
                    }
                    if (null != dataModel.getX().get(i).getPrecision()) {
                        precisionMap.put(colName, dataModel.getX().get(i).getPrecision().toString());
                    }
                    if (StringUtils.isNotBlank(dataModel.getX().get(i).getDataUnit())) {
                        dataUnitMap.put(colName, dataModel.getX().get(i).getDataUnit());
                    }
                }
                List<Map<String, Object>> newRows = Lists.newArrayList();
                for (Map<String, Object> row : rows) {
                    Map<String, Object> newRow = Maps.newHashMap();
                    newRow.putAll(row);
                    //设置精度和数据单位
                    for (Map.Entry<String, Object> entry : row.entrySet()) {
                        if (null != MapUtils.getObject(precisionMap, entry.getKey())) {
                            newRow.put(entry.getKey() + "-precision", MapUtils.getObject(precisionMap, entry.getKey()));
                        }
                    }
                    for (Map.Entry<String, Object> entry : row.entrySet()) {
                        if (null != MapUtils.getObject(dataUnitMap, entry.getKey())) {
                            newRow.put(entry.getKey() + "-dataUnit", DataUnitEnum.getDesc(MapUtils.getObject(dataUnitMap, entry.getKey())));
                        }
                    }
                    newRows.add(newRow);
                }
                response.setRows(newRows);
                String[] colNameWDArr = colNameWDList.toArray(new String[0]);
                String[] colNameDLArr = colNameDLList.toArray(new String[0]);

                //构造树形结构
                List<ListTree> x = buildTree(newRows, "", 0, colNameWDArr, colNameDLArr, precisionMap, dataUnitMap);
                columns.put("x", x);
            }

            if (CollectionUtils.isNotEmpty(dataModel.getY())) {
                String[] colNameYArr = new String[dataModel.getY().size()];
                for (int i = 0; i < dataModel.getY().size(); i++) {
                    String colName = dataModel.getY().get(i).getId();
                    colNameYArr[i] = colName;
                }
                List<ListTree> y = buildY(dataModel.getTableName(), colNameYArr);
                columns.put("y", y);
            }
            response.setColumns(columns);
        }
    }

    private List<ListTree> buildTree(List<Map<String, Object>> rows, String parentName, int currentNode, String[] colNameWDArr,
                                     String[] colNameDLArr, Map<String, String> precisionMap, Map<String, String> dataUnitMap) {
        List<ListTree> treeDataModels = Lists.newArrayList();

        Map<String, List<Map<String, Object>>> keyMap = Maps.newHashMap();
        for (Map<String, Object> row : rows) {
            for (int i = 0; i < colNameWDArr.length; i++) {
                String name = MapUtils.getString(row, colNameWDArr[i]);
                if (i == currentNode) {
                    if (keyMap.containsKey(name)) {
                        keyMap.get(name).add(row);
                    } else {
                        List<Map<String, Object>> list = Lists.newArrayList();
                        list.add(row);
                        keyMap.put(name, list);
                    }
                }

            }
        }
        for (String key : keyMap.keySet()) {
            ListTree tree = new ListTree();
            tree.setTitle(key);
            if (StringUtils.isNotBlank(parentName)) {
                tree.setDataIndex(parentName + "_" + key);
            } else {
                tree.setDataIndex(key);
            }
            if (currentNode != colNameWDArr.length) {
                if (StringUtils.isNotBlank(parentName)) {
                    tree.setKey(parentName + "_" + key);
                } else {
                    tree.setKey(key);
                }
//                tree.setKey(colNameArr[currentNode]);
                tree.setChildren(buildTree(keyMap.get(key), tree.getKey(), currentNode + 1, colNameWDArr,
                        colNameDLArr, precisionMap, dataUnitMap));
                treeDataModels.add(tree);
                //长度大于1才添加度量为最后一级
                if (null != colNameDLArr && colNameDLArr.length > 1) {
                    if (CollectionUtils.isEmpty(tree.getChildren())) {
                        List<ListTree> lastTree = Lists.newArrayList();
                        for (String dl : colNameDLArr) {
                            ListTree dlTree = new ListTree();
                            dlTree.setKey(tree.getKey() + dl);
                            dlTree.setTitle(dl);
                            dlTree.setDataIndex(tree.getKey() + dl);
                            dlTree.setChildren(Lists.newArrayList());
                            lastTree.add(dlTree);
                        }
                        tree.setChildren(lastTree);
                    }
                }

            }
        }
        return treeDataModels;
    }

    private List<ListTree> buildY(String tableName, String[] colNameArr) {
        List<ListTree> treeDataModels = Lists.newArrayList();
        if (null != colNameArr && colNameArr.length > 0) {
            LambdaQueryWrapper<BiUiModelField> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BiUiModelField::getModelId, tableName);
            queryWrapper.in(BiUiModelField::getName, Lists.newArrayList(colNameArr));
            List<BiUiModelField> fieldList = fieldService.list(queryWrapper);
            Map<String, String> nameDescMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(fieldList)) {
                fieldList.forEach(field -> nameDescMap.put(field.getName(), field.getFieldDesc()));
            }
            for (String colName : colNameArr) {
                ListTree tree = new ListTree();
                tree.setKey(colName);
                tree.setTitle(MapUtils.getString(nameDescMap, colName));
                treeDataModels.add(tree);
            }
        }
        return treeDataModels;
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException("请至少绑定一个维度字段");
        }
        int wd = 0;
        int dl = 0;
        for (DataModelField field : dataModel.getX()) {
            if (StringUtils.equals(field.getQuota(), DataModelTypeEnum.WD.getCode())) {
                wd += 1;
            }
            if (StringUtils.equals(field.getQuota(), DataModelTypeEnum.DL.getCode())) {
                dl += 1;
            }
        }
        if (wd == 0) {
            throw new BizException(ResourceMessageEnum.WD_MUST_ONE.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.WD_MUST_ONE.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (dl == 0) {
            throw new BizException(ResourceMessageEnum.DL_MUST_ONE.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_MUST_ONE.getMessage(), ThreadLocalHolder.getLang()));
        }
    }
}
