package com.deloitte.bdh.data.enums;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum BiProcessorsTypeEnum {

    JOIN_SOURCE("JOIN_SOURCE", "引入数据源的组件") {
        @Override
        public List<ProcessorTypeEnum> includeProcessor(String str) {
            List<ProcessorTypeEnum> list = Lists.newArrayList();
            SourceTypeEnum typeEnum = SourceTypeEnum.values(str);
            switch (typeEnum) {
                case Mysql_8:
                    list.add(ProcessorTypeEnum.ExecuteSQL);
                    break;
                case Mysql_7:
                    list.add(ProcessorTypeEnum.ExecuteSQL);
                    break;
                case File_Csv:
                    list.add(ProcessorTypeEnum.FetchSFTP);
                    break;
                case File_Excel:
                    list.add(ProcessorTypeEnum.FetchSFTP);
                    break;
                default:
                    list.add(ProcessorTypeEnum.SelectHiveQL);
            }
            return list;
        }
    },

    INTO_SOURCE("INTO_SOURCE", "输出到数据库") {
        @Override
        public List<ProcessorTypeEnum> includeProcessor(String str) {
            List<ProcessorTypeEnum> list = Lists.newArrayList();
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

    public String getType() {
        return type;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

}
