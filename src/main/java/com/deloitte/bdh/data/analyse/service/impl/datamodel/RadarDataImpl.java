package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;


@Service("radarDataImpl")
public class RadarDataImpl extends CategoryDataImpl implements AnalyseDataService {

    @Override
    protected void validate(DataModel dataModel) {

        if (dataModel.getX().size() > 1) {
            throw new BizException("最多可拖入1个外圈");
        }
        if (dataModel.getCategory().size() > 1) {
            throw new BizException("最多可拖入1个颜色图例");
        }
        if (CollectionUtils.isNotEmpty(dataModel.getX()) && CollectionUtils.isNotEmpty(dataModel.getCategory()) && dataModel.getY().size() > 1) {
            throw new BizException("外圈、颜色都绑定了字段时，只能绑定一个指标字段");
        }
    }
}
