package com.deloitte.bdh.data.analyse.model.datamodel;

import lombok.Data;

import java.util.HashMap;

@Data
public class DataModelQuery extends HashMap {
    public String getName() {
        return (String) get("name");
    }

    public String getValue() {
        return (String) get("value");
    }
}
