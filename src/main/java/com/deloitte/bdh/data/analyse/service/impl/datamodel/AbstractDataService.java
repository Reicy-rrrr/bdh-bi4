package com.deloitte.bdh.data.analyse.service.impl.datamodel;


import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.sql.DataSourceSelection;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author:LIJUN
 * Date:13/11/2020
 * Description:
 */
@Slf4j
public abstract class AbstractDataService {
    @Resource
    protected DataSourceSelection sourceSelection;


    protected abstract void validate(DataModel dataModel);

    @Deprecated
    protected BaseComponentDataResponse execute(DataModel dataModel, Sql sql) {
        return execute(dataModel, sql.build());
    }

    protected BaseComponentDataResponse execute(DataModel dataModel, String sql) {
        return execute(dataModel, sql, list -> list);
    }

    protected BaseComponentDataResponse execute(DataModel dataModel, String sql, Rows rows) {
        return execute(dataModel, () -> sql, rows);
    }

    final protected BaseComponentDataResponse execute(DataModel dataModel, Sql sqlInterface, Rows rowsInterface) {
        BaseComponentDataResponse response = new BaseComponentDataResponse();
        List<Map<String, Object>> list = null;
        String sql = sqlInterface.build();
        if (StringUtils.isNotBlank(sql)) {
            list = sourceSelection.execute(dataModel, sql);
        }
//        setExtraField(dataModel, list);
        response.setRows(rowsInterface.set(list));
        response.setTotal(sourceSelection.getCount(dataModel));
        response.setSql(sql);
        return response;
    }

    private void setExtraField(DataModel dataModel, List<Map<String, Object>> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, String> precisionMap = Maps.newHashMap();
            Map<String, String> dataUnitMap = Maps.newHashMap();
            for (DataModelField field : dataModel.getX()) {
                String name = field.getId();
                if (StringUtils.isNotBlank(field.getAlias())) {
                    name = field.getAlias();
                }
                if (null != field.getPrecision()) {
                    precisionMap.put(name, field.getPrecision().toString());
                }
                if (StringUtils.isNotBlank(field.getDataUnit())) {
                    dataUnitMap.put(name, field.getDataUnit());
                }
            }
            for (Map<String, Object> map : list) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (null != MapUtils.getObject(precisionMap, entry.getKey())) {
                        map.put(entry.getKey() + "-precision", MapUtils.getObject(precisionMap, entry.getKey()));
                    }
                    if (null != MapUtils.getObject(dataUnitMap, entry.getKey())) {
                        map.put(entry.getKey() + "-dataUnit", MapUtils.getObject(dataUnitMap, entry.getKey()));
                    }
                }
            }
        }
    }

    final protected String buildSql(DataModel dataModel) {
        validate(dataModel);
        //剔除重复的字段
        before(dataModel);
        return sourceSelection.buildSql(dataModel);
    }

    protected void before(DataModel dataModel) {
        List<String> ids = Lists.newArrayList();
        List<DataModelField> newX = Lists.newArrayList();
        for (DataModelField field : dataModel.getX()) {
            if (!ids.contains(field.getId())) {
                ids.add(field.getId());
                newX.add(field);
            }
        }
        dataModel.setX(newX);
    }


    public interface Sql {
        String build();
    }

    public interface Rows {
        List<Map<String, Object>> set(List<Map<String, Object>> list);
    }

    public String getColName(DataModelField dataModelField) {

        if (Objects.isNull(dataModelField)) {
            return "";
        }
        String colName = dataModelField.getId();
        if (StringUtils.isNotBlank(dataModelField.getAlias())) {
            colName = dataModelField.getAlias();
        }
        return colName;
    }

}
