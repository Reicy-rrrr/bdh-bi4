package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Author:LIJUN
 * Date:18/11/2020
 * Description:
 */
@Service("dataRangeDataImpl")
public class DataRangeDataImpl extends AbstractDataService implements AnalyseDataService {

    @Resource
    private BiDataSetService dataSetService;

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        DataModelField field = dataModel.getX().get(0);
        BiDataSet dataSet = dataSetService.getOne(new LambdaQueryWrapper<BiDataSet>()
                .eq(BiDataSet::getTableDesc, dataModel.getTableName()));
        if (null == dataSet) {
            throw new RuntimeException("未在数据集找到目标对象");
        }
        String sql = "SELECT MIN(" + field.getId() + ") AS MIN, MAX(" + field.getId() + ") AS MAX FROM " + dataSet.getTableName();
        return execute(dataModel, sql);
    }


    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException("度量不能为空");
        }
        if (dataModel.getX().size() > 1) {
            throw new BizException("度量数量只能为1");
        }
    }
}
