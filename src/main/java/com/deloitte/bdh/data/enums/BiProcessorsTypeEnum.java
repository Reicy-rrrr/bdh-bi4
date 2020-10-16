package com.deloitte.bdh.data.enums;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum BiProcessorsTypeEnum {

    JOIN_SOURCE("JOIN_SOURCE", "引入数据源的组件") {
        @Override
        public List<ProcessorTypeEnum> includeProcessor(String str) {
            List<ProcessorTypeEnum> list = Lists.newLinkedList();
            SourceTypeEnum typeEnum = SourceTypeEnum.values(str);
            switch (typeEnum) {
                case Mysql:
                    list.add(ProcessorTypeEnum.ExecuteSQL);
                    list.add(ProcessorTypeEnum.ConvertAvroToJSON);
                    list.add(ProcessorTypeEnum.UpdateAttribute);
                    break;
                case Oracle:
                    list.add(ProcessorTypeEnum.ExecuteSQL);
                    list.add(ProcessorTypeEnum.ConvertAvroToJSON);
                    list.add(ProcessorTypeEnum.UpdateAttribute);
                    break;
                case SQLServer:
                    list.add(ProcessorTypeEnum.ExecuteSQL);
                    list.add(ProcessorTypeEnum.ConvertAvroToJSON);
                    list.add(ProcessorTypeEnum.UpdateAttribute);
                    break;
                case Hive:
                    list.add(ProcessorTypeEnum.SelectHiveQL);
                    list.add(ProcessorTypeEnum.ConvertAvroToJSON);
                    list.add(ProcessorTypeEnum.UpdateAttribute);
                    break;
                case Hive2:
                    list.add(ProcessorTypeEnum.SelectHiveQL);
                    list.add(ProcessorTypeEnum.ConvertAvroToJSON);
                    list.add(ProcessorTypeEnum.UpdateAttribute);
                    break;
                case File_Csv:
                    list.add(ProcessorTypeEnum.GetMongo);
                    list.add(ProcessorTypeEnum.UpdateAttribute);
//                    list.add(ProcessorTypeEnum.GetFTP);
//                    list.add(ProcessorTypeEnum.UpdateAttribute);
//                    list.add(ProcessorTypeEnum.ConvertRecord);
                    break;
                case File_Excel:
                    list.add(ProcessorTypeEnum.GetMongo);
                    list.add(ProcessorTypeEnum.UpdateAttribute);
//                    list.add(ProcessorTypeEnum.GetFTP);
//                    list.add(ProcessorTypeEnum.ConvertExcelToCSVProcessor);
//                    list.add(ProcessorTypeEnum.UpdateAttribute);
//                    list.add(ProcessorTypeEnum.ConvertRecord);
                    break;
                default:
                    list.add(ProcessorTypeEnum.SelectHiveQL);
            }
            return list;
        }
    },

    OUT_SOURCE("OUT_SOURCE", "输出到数据库") {
        @Override
        public List<ProcessorTypeEnum> includeProcessor(String str) {
            List<ProcessorTypeEnum> list = Lists.newArrayList();
            list.add(ProcessorTypeEnum.ConvertJSONToSQL);
            list.add(ProcessorTypeEnum.PutSQL);
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
