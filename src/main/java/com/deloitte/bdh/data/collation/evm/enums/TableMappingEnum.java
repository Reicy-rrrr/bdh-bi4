package com.deloitte.bdh.data.collation.evm.enums;

import com.beust.jcommander.internal.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

public enum TableMappingEnum {

    EVM_CAPANALYSIS_SUM("EVM_CAPANALYSIS_SUM", ReportCodeEnum.ZCXLZTSPB),
    EVM_CAPANALYSIS_FUND("EVM_CAPANALYSIS_FUND", ReportCodeEnum.ZJB),

    ;
    private String name;
    private ReportCodeEnum value;


    TableMappingEnum(String name, ReportCodeEnum value) {
        this.name = name;
        this.value = value;
    }


    public ReportCodeEnum getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getTableName(List<String> list) {
        for (String var : list) {
            if (var.indexOf(this.getName()) >= 0) {
                return var;
            }
        }
        return null;
    }

    public static List<ImmutablePair<TableMappingEnum, String>> get(List<String> tables) {
        List<ImmutablePair<TableMappingEnum, String>> result = Lists.newArrayList();
        TableMappingEnum[] enums = TableMappingEnum.values();
        for (TableMappingEnum anEnum : enums) {
            String tableName = anEnum.getTableName(tables);
            if (null != tableName) {
                result.add(new ImmutablePair<>(anEnum, tableName));
            }
        }
        return result;
    }
}
