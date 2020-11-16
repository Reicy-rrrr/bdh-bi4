package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiDemoMapper;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.model.datamodel.response.ListTree;
import com.deloitte.bdh.data.analyse.model.resp.AnalyseFieldTree;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        String sql = buildSql(request.getDataConfig().getDataModel());
        BaseComponentDataResponse response = execute(sql);
        buildColumns(request, response);
        return response;
    }

    private void buildColumns(BaseComponentDataRequest request, BaseComponentDataResponse response) {
        List<Map<String, Object>> rows = response.getRows();
        if (CollectionUtils.isNotEmpty(rows)) {
            for (DataModelField field : request.getDataConfig().getDataModel().getX()) {
                String col = field.getId();
                if (StringUtils.isNotBlank(field.getAlias())) {
                    col = field.getAlias();
                }

            }
        }




    }

//    private List<ListTree> buildFieldTree(List<Map<String, Object>> rows, DataModelField parentField) {
//        List<ListTree> treeDataModels = Lists.newArrayList();
//        for (Map<String, Object> row : rows) {
//            ListTree tree = new ListTree();
//            String col = parentField.getId();
//            if (StringUtils.isNotBlank(parentField.getAlias())) {
//                col = parentField.getAlias();
//            }
//            String value = MapUtils.getString(row, col);
//            tree.setName(value);
//            if (StringUtils.equals(value, preValue)) {
//                tree.setChildren(buildFieldTree(rows, preField, field));
//                treeDataModels.add(tree);
//            }
//        }
//        return treeDataModels;
//    }

    @Override
    protected void validate(DataModel dataModel) {

    }
}
