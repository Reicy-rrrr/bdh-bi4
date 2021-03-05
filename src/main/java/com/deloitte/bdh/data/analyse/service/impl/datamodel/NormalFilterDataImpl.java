package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Author:LIJUN
 * Date:18/11/2020
 * Description:
 */
@Service("normalFilterDataImpl")
public class NormalFilterDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    protected void before(DataModel dataModel) {
        List<String> ids = com.beust.jcommander.internal.Lists.newArrayList();
        List<DataModelField> newX = com.beust.jcommander.internal.Lists.newArrayList();
        for (DataModelField field : dataModel.getX()) {
            if (!ids.contains(field.getId())) {
                ids.add(field.getId());
                field.setNeedGroup(true);
                newX.add(field);
            }
        }
        dataModel.setX(newX);
    }

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
        DataModelField field = request.getDataConfig().getDataModel().getX().get(0);
        String sql = buildSql(request.getDataConfig().getDataModel());
        BaseComponentDataResponse response = execute(request.getDataConfig().getDataModel(), sql);
        List<Map<String, Object>> rows = response.getRows();
        List<Map<String, Object>> newRows = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(rows)) {
            for (Map<String, Object> map : rows) {
                Map<String, Object> newMap = Maps.newHashMap();
                newMap.put("value", MapUtils.getString(map, field.getId()));
                newRows.add(newMap);
            }
            response.setRows(newRows);
        }
        return response;
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException(ResourceMessageEnum.WD_NOT_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.WD_NOT_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (dataModel.getX().size() > 1) {
            throw new BizException(ResourceMessageEnum.WD_SIZE_ONE.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.WD_SIZE_ONE.getMessage(), ThreadLocalHolder.getLang()));
        }
    }
}
