package com.deloitte.bdh.data.collation.evm.enums;

import com.beust.jcommander.internal.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

public enum TableMappingEnum {

    EVM_CAPANALYSIS_SUM("EVM_CAPANALYSIS_SUM", ReportCodeEnum.ZCXLZTSPB),
    EVM_CAPANALYSIS_FUND("EVM_CAPANALYSIS_FUND", ReportCodeEnum.ZJB),
    EVM_CAPANALYSIS_STRUCT("EVM_CAPANALYSIS_STRUCT", ReportCodeEnum.ZCGCB),
    EVM_CAPANALYSIS_AR("EVM_CAPANALYSIS_AR", ReportCodeEnum.DEFAULT),
    EVM_CAPANALYSIS_AP("EVM_CAPANALYSIS_AP", ReportCodeEnum.DEFAULT),
    EVM_CAPANALYSIS_INVENTORY("EVM_CAPANALYSIS_INVENTORY", ReportCodeEnum.DEFAULT),
    EVM_CAPANALYSIS_IJ("EVM_CAPANALYSIS_IJ", ReportCodeEnum.DEFAULT),
    EVM_CAPANALYSIS_LOAN_SUM("EVM_CAPANALYSIS_LOAN_SUM", ReportCodeEnum.DEFAULT),
    EVM_CAPANALYSIS_LOAN_CYCLE("EVM_CAPANALYSIS_LOAN_CYCLE", ReportCodeEnum.DEFAULT),
    EVM_CAPANALYSIS_DEPRECIATION("EVM_CAPANALYSIS_DEPRECIATION", ReportCodeEnum.DEFAULT),

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
            if (var.toUpperCase().indexOf(this.getName()) >= 0) {
                return var;
            }
        }
        return null;
    }

    public static String getTableNameByEnum(List<String> list, TableMappingEnum tableMappingEnum) {
        for (String var : list) {
            if (var.toUpperCase().indexOf(tableMappingEnum.getName()) >= 0) {
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
