package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.DataUnitEnum;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
                .eq(BiDataSet::getCode, dataModel.getTableName()));
        if (null == dataSet) {
            throw new BizException(ResourceMessageEnum.DATA_SET_NOT_EXIST.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DATA_SET_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));
        }
        String sql = "SELECT MIN(" + field.getId() + ") AS MIN, MAX(" + field.getId() + ") AS MAX FROM " + dataSet.getTableName();
        BaseComponentDataResponse response = execute(dataModel, sql);
        List<Map<String, Object>> rows = response.getRows();
        if (CollectionUtils.isNotEmpty(rows)) {
            Map<String, Object> map = rows.get(0);
            //???????????????????????????
            if (null != field.getPrecision()) {
                map.put("precision", field.getPrecision());
            }
            if (StringUtils.isNotBlank(field.getDataUnit())) {
                map.put("dataUnit", DataUnitEnum.getDesc(field.getDataUnit()));
            }
        }
        return response;
    }


    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException(ResourceMessageEnum.DL_NOT_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_NOT_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (dataModel.getX().size() > 1) {
            throw new BizException(ResourceMessageEnum.DL_SIZE_ONE.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_SIZE_ONE.getMessage(), ThreadLocalHolder.getLang()));
        }
    }
}
