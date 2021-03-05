package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.AggregateTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.DataUnitEnum;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:普通表格
 */
@Service("tableNormalDataImpl")
public class TableNormalDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        Map<String, Object> customParams = dataModel.getCustomParams();
        if (MapUtils.isNotEmpty(customParams)) {
            String tableAggregate = MapUtils.getString(customParams, CustomParamsConstants.TABLE_AGGREGATE);
            if (StringUtils.equals(tableAggregate, "true")) {
                if (CollectionUtils.isNotEmpty(request.getDataConfig().getDataModel().getX())) {
                    for (DataModelField field : request.getDataConfig().getDataModel().getX()) {
                        if (StringUtils.equals(field.getQuota(), DataModelTypeEnum.DL.getCode())) {
                            field.setAggregateType(AggregateTypeEnum.NONE.getKey());
                        }
                    }
                }
            }
        }
        String sql = buildSql(request.getDataConfig().getDataModel());
        BaseComponentDataResponse response = execute(request.getDataConfig().getDataModel(), sql);
        List<Map<String, Object>> rows = response.getRows();

        Map<String, String> precisionMap = Maps.newHashMap();
        Map<String, String> dataUnitMap = Maps.newHashMap();
        for (DataModelField x : dataModel.getX()) {
            String colName = x.getId();
            if (StringUtils.isNotBlank(x.getAlias())) {
                colName = x.getAlias();
            }
            if (null != x.getPrecision()) {
                precisionMap.put(colName, x.getPrecision().toString());
            }
            if (StringUtils.isNotBlank(x.getDataUnit())) {
                dataUnitMap.put(colName, x.getDataUnit());
            }
        }

        List<Map<String, Object>> newRows = Lists.newArrayList();
        for (Map<String, Object> row : rows) {
            Map<String, Object> newRow = Maps.newHashMap();
            newRow.putAll(row);
            //设置精度和数据单位
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (null != MapUtils.getObject(precisionMap, entry.getKey())) {
                    newRow.put(entry.getKey() + "-precision", MapUtils.getObject(precisionMap, entry.getKey()));
                }
            }
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (null != MapUtils.getObject(dataUnitMap, entry.getKey())) {
                    newRow.put(entry.getKey() + "-dataUnit", DataUnitEnum.getDesc(MapUtils.getObject(dataUnitMap, entry.getKey())));
                }
            }
            newRows.add(newRow);
        }
        response.setRows(newRows);
        return response;
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isNotEmpty(dataModel.getY())) {
            throw new BizException(ResourceMessageEnum.NORMAL_TABLE_NO_Y.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.NORMAL_TABLE_NO_Y.getMessage(), ThreadLocalHolder.getLang()));
        }
    }
}
