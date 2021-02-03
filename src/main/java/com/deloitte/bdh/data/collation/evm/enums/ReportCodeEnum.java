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
            Rule rule0 = new Rule(1, "EVM0001", "总资产", "{N#zcfzb.EVMB039}");
            Rule rule1 = new Rule(2, "EVM0002", "资产负债率", "{N#zcfzb.EVMB068}/{N#zcfzb.EVMB039}");
            Rule rule2 = new Rule(3, "EVM0003", "现金", "{N#zcfzb.EVMB003}");
            Rule rule3 = new Rule(4, "EVM0004", "存货", "{N#zcfzb.EVMB012}");
            Rule rule4 = new Rule(5, "EVM0005", "存货周转率", "{N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG}");
            Rule rule5 = new Rule(6, "EVM0006", "存货周转天数", "360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG})");
            Rule rule6 = new Rule(7, "EVM0007", "净资产", "{N#zcfzb.EVMB080}");
            Rule rule7 = new Rule(8, "EVM0008", "净资产收益率", "{N#lrb.EVMP024}/{N#zcfzb.EVMB080_AVG}");
            Rule rule8 = new Rule(9, "EVM0009", "应收账款", "{N#zcfzb.EVMB080}");
            Rule rule9 = new Rule(10, "EVM0010", "逾期应收账款", "{N#zcfzb.EVMB080}");
            Rule rule10 = new Rule(11, "EVM0011", "应收账款周转率", "{N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG}");
            Rule rule11 = new Rule(12, "EVM0012", "总资产周转率 （次数）", "{N#lrb.EVMP001}/{N#zcfzb.EVMB039_AVG}");
            Rule rule12 = new Rule(13, "EVM0013", "总资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB039_AVG})");
            Rule rule13 = new Rule(14, "EVM0014", "固定资产", "{N#zcfzb.EVMB027}");
            Rule rule14 = new Rule(15, "EVM0015", "原值", "1+1");
            Rule rule15 = new Rule(16, "EVM0016", "累计折旧", "1+1");
            Rule rule16 = new Rule(17, "EVM0017", "固定资产周转率", "{N#lrb.EVMP001}/{N#zcfzb.EVMB037_AVG}");
            Rule rule17 = new Rule(18, "EVM0018", "固定资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB037_AVG})");
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
            Rule rule0 = new Rule(1, "EVM0001", "资金总额", "{N#zcfzb.EVMB003}+{N#zcfzb.EVMB004}");
            Rule rule1 = new Rule(2, "EVM0002", "资金占总资产比率", "({N#zcfzb.EVMB003}+{N#zcfzb.EVMB004})/{N#zcfzb.EVMB039}");
            Rule rule2 = new Rule(3, "EVM0003", "资金周转率", "({N#zcfzb.EVMB003_AVG}+{N#zcfzb.EVMB004_AVG})");
            Rule rule3 = new Rule(4, "EVM0004", "应收账款周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG})");
            Rule rule4 = new Rule(5, "EVM0005", "应付账款周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG})");
            Rule rule5 = new Rule(6, "EVM0006", "存货周转天数", "360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG})");
            Rule rule6 = new Rule(7, "EVM0007", "现金周转天数", "(360/({N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG}))+(360/({N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG}))+(360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG}))");
            Rule rule7 = new Rule(8, "EVM0008", "期末资金集中度", "1+1");
            Rule rule8 = new Rule(9, "EVM0009", "资金计划报送及时率", "1+1");
            Rule rule9 = new Rule(10, "EVM0010", "资金计划执行准确率", "1+1");
            Rule rule10 = new Rule(11, "EVM0011", "经营活动现金流入", "{N#xjllb.EVMC005}");
            Rule rule11 = new Rule(12, "EVM0012", "经营活动现金流出", "{N#xjllb.EVMC010}");
            Rule rule12 = new Rule(13, "EVM0013", "经营活动产生的现金流量", "{N#xjllb.EVMC011}");
            Rule rule13 = new Rule(14, "EVM0014", "投资活动现金流入", "{N#xjllb.EVMC018}");
            Rule rule14 = new Rule(15, "EVM0015", "投资活动现金流出", "{N#xjllb.EVMC023}");
            Rule rule15 = new Rule(16, "EVM0016", "投资活动所用的现金流量净额", "{N#xjllb.EVMC024}");
            Rule rule16 = new Rule(17, "EVM0017", "融资活动现金流入", "{N#xjllb.EVMC029}");
            Rule rule17 = new Rule(18, "EVM0018", "融资活动现金流出", "{N#xjllb.EVMC033}");
            Rule rule18 = new Rule(19, "EVM0019", "融资活动所产生(使用)的现金流量净额", "{N#xjllb.EVMC034}");
            Rule rule19 = new Rule(20, "EVM0020", "短期借款", "{N#zcfzb.EVMB042}");
            Rule rule20 = new Rule(21, "EVM0021", "应付票据", "{N#zcfzb.EVMB045}");
            Rule rule21 = new Rule(22, "EVM0022", "应付账款", "{N#zcfzb.EVMB046}");
            Rule rule22 = new Rule(23, "EVM0023", "预收账款", "{N#zcfzb.EVMB047}");
            Rule rule23 = new Rule(24, "EVM0024", "流动负债", "{N#zcfzb.EVMB042}+{N#zcfzb.EVMB045}+{N#zcfzb.EVMB046}+{N#zcfzb.EVMB047}");
            Rule rule24 = new Rule(25, "EVM0025", "长期借款", "{N#zcfzb.EVMB057}");
            Rule rule25 = new Rule(26, "EVM0026", "应付债券", "{N#zcfzb.EVMB058}");
            Rule rule26 = new Rule(27, "EVM0027", "非流动负债", "{N#zcfzb.EVMB057}+{N#zcfzb.EVMB058}");
            Rule rule27 = new Rule(28, "EVM0028", "流动比率", "{N#zcfzb.EVMB017}/{N#zcfzb.EVMB055}");
            Rule rule28 = new Rule(29, "EVM0029", "速动比率", "({N#zcfzb.EVMB017}-{N#zcfzb.EVMB012})/{N#zcfzb.EVMB056}");
            Rule rule29 = new Rule(30, "EVM0030", "现金比率", "({N#zcfzb.EVMB003}+{N#zcfzb.EVMB004}+{N#zcfzb.EVMB005})/{N#zcfzb.EVMB057}");
            Rule rule30 = new Rule(31, "EVM0031", "流动资产合计", "{N#zcfzb.EVMB017}");
            Rule rule31 = new Rule(32, "EVM0032", "流动负债合计", "{N#zcfzb.EVMB055}");
            Rule rule32 = new Rule(33, "EVM0033", "资产负债率", "{N#zcfzb.EVMB068}/{N#zcfzb.EVMB039}");
            Rule rule33 = new Rule(34, "EVM0034", "产权比率", "{N#zcfzb.EVMB068}/{N#zcfzb.EVMB080}");
            Rule rule34 = new Rule(35, "EVM0035", "有形资产债务率", "{N#zcfzb.EVMB068}/({N#zcfzb.EVMB080}-{N#zcfzb.EVMB032})");
            Rule rule35 = new Rule(36, "EVM0036", "资产总额", "{N#zcfzb.EVMB039}");
            Rule rule36 = new Rule(18, "EVM0037", "负债总额", "{N#zcfzb.EVMB068}");
            Rule rule37 = new Rule(18, "EVM0038", "利息偿付倍数", "({N#lrb.EVMP022}-{N#lrb.EVMP008})/{N#lrb.EVMP008}");

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
            collusion.add(rule18);
            collusion.add(rule19);
            collusion.add(rule20);
            collusion.add(rule21);
            collusion.add(rule22);
            collusion.add(rule23);
            collusion.add(rule24);
            collusion.add(rule25);
            collusion.add(rule26);
            collusion.add(rule27);
            collusion.add(rule28);
            collusion.add(rule29);
            collusion.add(rule30);
            collusion.add(rule31);
            collusion.add(rule32);
            collusion.add(rule33);
            collusion.add(rule34);
            collusion.add(rule35);
            collusion.add(rule36);
            collusion.add(rule37);
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
