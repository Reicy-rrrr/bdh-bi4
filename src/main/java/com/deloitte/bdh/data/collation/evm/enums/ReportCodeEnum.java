package com.deloitte.bdh.data.collation.evm.enums;


import com.deloitte.bdh.data.collation.evm.dto.Rule;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

public enum ReportCodeEnum {

    ZCXLZTSPB("ZCXLZTSPB", "资产效率整体水平表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName(), SheetCodeEnum.lrb.getName());
            List<Rule> collusion = Lists.newArrayList();
            Rule rule0 = new Rule(1, "EVM0001", "总资产", "{N#zcfz.EVMB039}");
            Rule rule1 = new Rule(2, "EVM0002", "资产负债率", "{N#zcfz.EVMB068}/{N#zcfz.EVMB039}");
            Rule rule2 = new Rule(3, "EVM0003", "现金", "{N#zcfz.EVMB003}");
            Rule rule3 = new Rule(4, "EVM0004", "存货", "{N#zcfz.EVMB012}");
            Rule rule4 = new Rule(5, "EVM0005", "存货周转率", "{N#lrb.EVMP002}/{N#zcfz.EVMB012_AVG}");
            Rule rule5 = new Rule(6, "EVM0006", "存货周转天数", "360/({N#lrb.EVMP002}/{N#zcfz.EVMB012_AVG})");
            Rule rule6 = new Rule(7, "EVM0007", "净资产", "{N#zcfz.EVMB080}");
            Rule rule7 = new Rule(8, "EVM0008", "净资产收益率", "{N#lrb.EVMP024}/{N#zcfz.EVMB080_AVG}");
            Rule rule8 = new Rule(9, "EVM0009", "应收账款", "{N#zcfz.EVMB080}");
            Rule rule9 = new Rule(10, "EVM0010", "逾期应收账款", "{N#zcfz.EVMB080}");
            Rule rule10 = new Rule(11, "EVM0011", "应收账款周转率", "{N#lrb.EVMP001}/{N#zcfz.EVMB008_AVG}");
            Rule rule11 = new Rule(12, "EVM0012", "总资产周转率 （次数）", "{N#lrb.EVMP001}/{N#zcfz.EVMB039_AVG}");
            Rule rule12 = new Rule(13, "EVM0013", "总资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfz.EVMB039_AVG})");
            Rule rule13 = new Rule(14, "EVM0014", "固定资产", "{N#zcfz.EVMB027}");
            Rule rule14 = new Rule(15, "EVM0015", "原值", "1+1");
            Rule rule15 = new Rule(16, "EVM0016", "累计折旧", "1+1");
            Rule rule16 = new Rule(17, "EVM0017", "固定资产周转率", "{N#lrb.EVMP001}/{N#zcfz.EVMB037_AVG}");
            Rule rule17 = new Rule(18, "EVM0018", "固定资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfz.EVMB037_AVG})");
            collusion.add(rule0);
            collusion.add(rule1);
            collusion.add(rule2);
            collusion.add(rule3);
            collusion.add(rule4);
            collusion.add(rule5);
            collusion.add(rule6);
            collusion.add(rule7);
            collusion.add(rule8);
            collusion.add(rule9);
            collusion.add(rule10);
            collusion.add(rule11);
            collusion.add(rule12);
            collusion.add(rule13);
            collusion.add(rule14);
            collusion.add(rule15);
            collusion.add(rule16);
            collusion.add(rule17);
            return new ImmutablePair<>(relySheets, collusion);
        }
    },

    ZJB("ZJB", "资金表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName(), SheetCodeEnum.lrb.getName(), SheetCodeEnum.xjllb.getName());
            List<Rule> collusion = Lists.newArrayList();
            Rule rule0 = new Rule(1, "EVM0001", "总资产", "{N#zcfz.EVMB039}");
            Rule rule1 = new Rule(2, "EVM0002", "资产负债率", "{N#zcfz.EVMB068}/{N#zcfz.EVMB039}");
            Rule rule2 = new Rule(3, "EVM0003", "现金", "{N#zcfz.EVMB003}");
            Rule rule3 = new Rule(4, "EVM0004", "存货", "{N#zcfz.EVMB012}");
            Rule rule4 = new Rule(5, "EVM0005", "存货周转率", "{N#lrb.EVMP002}/{N#zcfz.EVMB012_AVG}");
            Rule rule5 = new Rule(6, "EVM0006", "存货周转天数", "360/({N#lrb.EVMP002}/{N#zcfz.EVMB012_AVG})");
            Rule rule6 = new Rule(7, "EVM0007", "净资产", "{N#zcfz.EVMB080}");
            Rule rule7 = new Rule(8, "EVM0008", "净资产收益率", "{N#lrb.EVMP024}/{N#zcfz.EVMB080_AVG}");
            Rule rule8 = new Rule(9, "EVM0009", "应收账款", "{N#zcfz.EVMB080}");
            Rule rule9 = new Rule(10, "EVM0010", "逾期应收账款", "{N#zcfz.EVMB080}");
            Rule rule10 = new Rule(11, "EVM0011", "应收账款周转率", "{N#lrb.EVMP001}/{N#zcfz.EVMB008_AVG}");
            Rule rule11 = new Rule(12, "EVM0012", "总资产周转率 （次数）", "{N#lrb.EVMP001}/{N#zcfz.EVMB039_AVG}");
            Rule rule12 = new Rule(13, "EVM0013", "总资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfz.EVMB039_AVG})");
            Rule rule13 = new Rule(14, "EVM0014", "固定资产", "{N#zcfz.EVMB027}");
            Rule rule14 = new Rule(15, "EVM0015", "原值", "1+1");
            Rule rule15 = new Rule(16, "EVM0016", "累计折旧", "1+1");
            Rule rule16 = new Rule(17, "EVM0017", "固定资产周转率", "{N#lrb.EVMP001}/{N#zcfz.EVMB037_AVG}");
            Rule rule17 = new Rule(18, "EVM0018", "固定资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfz.EVMB037_AVG})");
            collusion.add(rule0);
            collusion.add(rule1);
            collusion.add(rule2);
            collusion.add(rule3);
            collusion.add(rule4);
            collusion.add(rule5);
            collusion.add(rule6);
            collusion.add(rule7);
            collusion.add(rule8);
            collusion.add(rule9);
            collusion.add(rule10);
            collusion.add(rule11);
            collusion.add(rule12);
            collusion.add(rule13);
            collusion.add(rule14);
            collusion.add(rule15);
            collusion.add(rule16);
            collusion.add(rule17);
            return new ImmutablePair<>(relySheets, collusion);
        }
    },

    ;

    private String name;

    private String value;


    ReportCodeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public abstract ImmutablePair<List<String>, List<Rule>> relySheets();

}
