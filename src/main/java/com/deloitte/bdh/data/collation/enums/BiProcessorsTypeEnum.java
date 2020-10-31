package com.deloitte.bdh.data.collation.enums;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum BiProcessorsTypeEnum {

    SYNC_SOURCE("SYNC_SOURCE", "同步数据源的组件") {
        @Override
        public List<ProcessorTypeEnum> includeProcessor(String str) {
            List<ProcessorTypeEnum> list = Lists.newLinkedList();
            SourceTypeEnum typeEnum = SourceTypeEnum.values(str);
            switch (typeEnum) {
                case Mysql:
                case Oracle:
                case SQLServer:
                case Hana:
                    list.add(ProcessorTypeEnum.QueryDatabaseTable);
                    list.add(ProcessorTypeEnum.PutDatabaseRecord);
                    break;
                case Hive:
                case Hive2:
                    list.add(ProcessorTypeEnum.SelectHiveQL);
                    list.add(ProcessorTypeEnum.ConvertAvroToJSON);
                    list.add(ProcessorTypeEnum.ConvertJSONToSQL);
                    list.add(ProcessorTypeEnum.PutSQL);
                    break;
                case File_Csv:
                case File_Excel:
                    list.add(ProcessorTypeEnum.GetMongo);
                    list.add(ProcessorTypeEnum.UpdateAttribute);
                    list.add(ProcessorTypeEnum.ConvertJSONToSQL);
                    list.add(ProcessorTypeEnum.PutSQL);
                    break;
                default:

            }
            return list;
        }
    },

    ETL_SOURCE("ETL_SOURCE", "ETL到数据库") {
        @Override
        public List<ProcessorTypeEnum> includeProcessor(String str) {
            List<ProcessorTypeEnum> list = Lists.newArrayList();
            list.add(ProcessorTypeEnum.ExecuteSQL);
            list.add(ProcessorTypeEnum.PutDatabaseRecord);
            return list;
        }
    },


    ;


    private String type;

    private String typeDesc;


    BiProcessorsTypeEnum(String type, String typeDesc) {
        this.type = type;
        this.typeDesc = typeDesc;
    }


    public abstract List<ProcessorTypeEnum> includeProcessor(String str);

    public static String getTypeDesc(String type) {
        BiProcessorsTypeEnum[] enums = BiProcessorsTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i].getTypeDesc();
            }
        }
        throw new RuntimeException("未找到对应的目标");
    }

    public static BiProcessorsTypeEnum getEnum(String type) {
        BiProcessorsTypeEnum[] enums = BiProcessorsTypeEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (StringUtils.equals(type, enums[i].getType())) {
                return enums[i];
            }
        }
        throw new RuntimeException("未找到对应的目标");
    }

    public String getType() {
        return type;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

}
