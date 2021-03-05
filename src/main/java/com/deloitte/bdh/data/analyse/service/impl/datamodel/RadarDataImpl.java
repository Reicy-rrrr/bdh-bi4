package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("radarDataImpl")
public class RadarDataImpl extends CategoryDataImpl implements AnalyseDataService {

    @Override
    protected void validate(DataModel dataModel) {

        if (dataModel.getCategory().size() > 1) {
            throw new BizException(ResourceMessageEnum.CATEGORY_SIZE_ONE.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.CATEGORY_SIZE_ONE.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getCategory()) && dataModel.getY().size() > 1) {
            throw new BizException(ResourceMessageEnum.RADAR_INDEX.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.RADAR_INDEX.getMessage(), ThreadLocalHolder.getLang()));
        }
    }
}
