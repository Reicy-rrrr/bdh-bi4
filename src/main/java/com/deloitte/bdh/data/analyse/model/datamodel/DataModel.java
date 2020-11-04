package com.deloitte.bdh.data.analyse.model.datamodel;

import lombok.Data;

import java.util.List;

@Data
public class DataModel extends BaseComponentDataRequestConfig {
    /**
     * x轴相关配置
     */
    List<DataModelField> x;
    Integer limit = 10000;
    Boolean tableNotAggregate = false;
}
