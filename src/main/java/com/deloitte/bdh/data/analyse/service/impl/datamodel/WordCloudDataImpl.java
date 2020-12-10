package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;


@Service("wordCloudDataImpl")
public class WordCloudDataImpl extends CategoryDataImpl implements AnalyseDataService {

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && dataModel.getY().size() > 1) {
            throw new BizException("字符区块名称绑定了字段时，字符区块大小只能绑定一个度量字段");
        }
    }
}
