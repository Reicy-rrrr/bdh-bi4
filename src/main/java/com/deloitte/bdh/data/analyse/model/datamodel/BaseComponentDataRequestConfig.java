package com.deloitte.bdh.data.analyse.model.datamodel;

import lombok.Data;

import java.util.List;

@Data
public class BaseComponentDataRequestConfig {
    List<DataModelCondition> conditions;
    List<DataModelCondition> chartConditions;
    List<DataModelQuery> queries;
}
