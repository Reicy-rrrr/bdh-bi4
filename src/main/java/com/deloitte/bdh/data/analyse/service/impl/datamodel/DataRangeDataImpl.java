package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * Author:LIJUN
 * Date:18/11/2020
 * Description:
 */
@Service("dataRangeDataImpl")
public class DataRangeDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        DataModelField field = dataModel.getX().get(0);
        String sql = "SELECT MIN(" + field.getId() + ") AS MIN, MAX(" + field.getId() + ") AS MAX FROM " + dataModel.getTableName();
        return execute(sql);
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
