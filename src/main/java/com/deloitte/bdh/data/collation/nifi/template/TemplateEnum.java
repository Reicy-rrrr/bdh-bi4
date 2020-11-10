package com.deloitte.bdh.data.collation.nifi.template;

import com.google.common.collect.Lists;

import java.util.List;

public enum TemplateEnum {

    SYNC_SQL("SYNC_SQL", "同步数据源的组件") {
        @Override
        public List<String> includeProcessor() {
            List<String> list = Lists.newLinkedList();
            list.add("tQueryDatabaseTable");
            list.add("tPutDatabaseRecord");
            return list;
        }
    },

    OUT_SQL("OUT_SQL", "ETL到数据库") {
        @Override
        public List<String> includeProcessor() {
            List<String> list = Lists.newLinkedList();
            list.add("tExecuteSQL");
            list.add("tPutDatabaseRecord");
            return list;
        }
    },


    ;


    private String key;
    private String value;

    TemplateEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public abstract List<String> includeProcessor();

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
