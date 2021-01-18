package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.data.collation.nifi.template.TemplateEnum;
import org.apache.commons.lang3.StringUtils;


public enum BiProcessorsTypeEnum {

    SYNC_SOURCE("SYNC_SOURCE", "同步数据源的组件") {
        @Override
        public TemplateEnum includeProcessor(String str) {
            TemplateEnum templateEnum = null;
            SourceTypeEnum typeEnum = SourceTypeEnum.values(str);
            switch (typeEnum) {
                case Mysql:
                case Oracle:
                case SQLServer:
                case Hana:
                    templateEnum = TemplateEnum.SYNC_SQL;
                    break;
                case Hive:
                case Hive2:
                    templateEnum = TemplateEnum.SYNC_SQL;
                    break;
                case File_Csv:
                case File_Excel:
                    templateEnum = TemplateEnum.SYNC_SQL;
                    break;
                default:

            }
            return templateEnum;
        }
    },

    ETL_SOURCE("ETL_SOURCE", "ETL到数据库") {
        @Override
        public TemplateEnum includeProcessor(String str) {
            return TemplateEnum.OUT_SQL;
        }
    },


    ;


    private String type;

    private String typeDesc;


    BiProcessorsTypeEnum(String type, String typeDesc) {
        this.type = type;
        this.typeDesc = typeDesc;
    }


    public abstract TemplateEnum includeProcessor(String str);

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
