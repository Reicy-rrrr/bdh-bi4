package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("wordCloudDataImpl")
public class WordCloudDataImpl extends CategoryDataImpl implements AnalyseDataService {

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && dataModel.getY().size() > 1) {
            throw new BizException(ResourceMessageEnum.DL_SIZE_ONE.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_SIZE_ONE.getMessage(), ThreadLocalHolder.getLang()));
        }
    }
}
