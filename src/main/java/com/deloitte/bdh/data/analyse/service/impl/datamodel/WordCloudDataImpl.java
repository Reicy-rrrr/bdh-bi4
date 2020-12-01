package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;


@Service("wordCloudDataImpl")
public class WordCloudDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getY())) {
            dataModel.getY().forEach(y -> dataModel.getX().add(y));
        }
        return execute(buildSql(dataModel));
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && dataModel.getY().size() > 1) {
            throw new BizException("字符区块名称绑定了字段时，字符区块大小只能绑定一个度量字段");
        }
    }
}
