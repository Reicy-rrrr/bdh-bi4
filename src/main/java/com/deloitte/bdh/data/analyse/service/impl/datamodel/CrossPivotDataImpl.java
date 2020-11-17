package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.datamodel.response.ListTree;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:交叉透视图
 */
@Service("crossPivotDataImpl")
public class CrossPivotDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        String sql = buildSql(request.getDataConfig().getDataModel());
        BaseComponentDataResponse response = execute(sql);
        buildColumns(request, response);
        return response;
    }

    private void buildColumns(BaseComponentDataRequest request, BaseComponentDataResponse response) {
        List<Map<String, Object>> rows = response.getRows();
        //构造要查询的行字段
        if (CollectionUtils.isNotEmpty(rows)) {
            DataModel dataModel = request.getDataConfig().getDataModel();
            String[] colNameXArr = new String[dataModel.getX().size()];
            for (int i = 0; i < dataModel.getX().size(); i++) {
                String colName = dataModel.getX().get(i).getId();
                if (StringUtils.isNotBlank(dataModel.getX().get(i).getAlias())) {
                    colName = dataModel.getX().get(i).getAlias();
                }
                colNameXArr[i] = colName;
            }
            //构造树形结构
            List<ListTree> x = buildTree(rows, 0, colNameXArr);

            String[] colNameYArr = new String[dataModel.getY().size()];
            for (int i = 0; i < dataModel.getY().size(); i++) {
                String colName = dataModel.getY().get(i).getId();
                if (StringUtils.isNotBlank(dataModel.getY().get(i).getAlias())) {
                    colName = dataModel.getY().get(i).getAlias();
                }
                colNameYArr[i] = colName;
            }
            List<ListTree> y = buildTree(rows, 0, colNameYArr);

            Map<String, List<ListTree>> columns = Maps.newHashMap();
            columns.put("x", x);
            columns.put("y", y);
            response.setColumns(columns);
        }

    }

    private List<ListTree> buildTree(List<Map<String, Object>> rows, int currentNode, String[] colNameArr) {
        List<ListTree> treeDataModels = Lists.newArrayList();

        Map<String, List<Map<String, Object>>> keyMap = Maps.newHashMap();
        for (Map<String, Object> row : rows) {
            for (int i = 0; i < colNameArr.length; i++) {
                if (i == currentNode) {
                    String name = MapUtils.getString(row, colNameArr[i]);
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
            tree.setName(key);
            if (currentNode != colNameArr.length) {
                tree.setChildren(buildTree(keyMap.get(key), currentNode + 1, colNameArr));
                treeDataModels.add(tree);
            }
        }
        return treeDataModels;
    }

    @Override
    protected void validate(DataModel dataModel) {

    }
}
