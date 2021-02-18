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
            Rule rule9 = new Rule(10, "EVM0010", "逾期应收账款", "{N#zcfzb.EVMB080}+111111");
            Rule rule10 = new Rule(11, "EVM0011", "应收账款周转率", "{N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG}");
            Rule rule11 = new Rule(12, "EVM0012", "总资产周转率 （次数）", "{N#lrb.EVMP001}/{N#zcfzb.EVMB039_AVG}");
            Rule rule12 = new Rule(13, "EVM0013", "总资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB039_AVG})");
            Rule rule13 = new Rule(14, "EVM0014", "固定资产", "{N#zcfzb.EVMB027}");
            Rule rule14 = new Rule(15, "EVM0015", "原值", "1+1");
            Rule rule15 = new Rule(16, "EVM0016", "累计折旧", "1+1");
            Rule rule16 = new Rule(17, "EVM0017", "固定资产周转率", "{N#lrb.EVMP001}/{N#zcfzb.EVMB027_AVG}");
            Rule rule17 = new Rule(18, "EVM0018", "固定资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB027_AVG})");
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
            Rule rule2 = new Rule(3, "EVM0003", "资金周转率", "{N#kmyeb.M132}/({N#zcfzb.EVMB003_AVG}+{N#zcfzb.EVMB004_AVG})");
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

    ZCGCB("ZCGCB", "总资产构成表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName());
            List<Rule> collusion = Lists.newArrayList();
            Rule rule1 = new Rule(1, "EVMB001", "资产", "{N#zcfzb.EVMB001}");
            Rule rule2 = new Rule(2, "EVMB002", "流动资产： ", "{N#zcfzb.EVMB002}");
            Rule rule3 = new Rule(3, "EVMB003", "现金 ", "{N#zcfzb.EVMB003}");
            Rule rule4 = new Rule(4, "EVMB004", "其他货币资金 ", "{N#zcfzb.EVMB004}");
            Rule rule5 = new Rule(5, "EVMB005", "交易性金融资产 ", "{N#zcfzb.EVMB005}");
            Rule rule6 = new Rule(6, "EVMB006", "衍生金融资产 ", "{N#zcfzb.EVMB006}");
            Rule rule7 = new Rule(7, "EVMB007", "应收票据 ", "{N#zcfzb.EVMB007}");
            Rule rule8 = new Rule(8, "EVMB008", "应收账款 ", "{N#zcfzb.EVMB008}");
            Rule rule9 = new Rule(9, "EVMB009", "应收款项融资 ", "{N#zcfzb.EVMB009}");
            Rule rule10 = new Rule(10, "EVMB010", "预付款项 ", "{N#zcfzb.EVMB010}");
            Rule rule11 = new Rule(11, "EVMB011", "其他应收款 ", "{N#zcfzb.EVMB011}");
            Rule rule12 = new Rule(12, "EVMB012", "存货 ", "{N#zcfzb.EVMB012}");
            Rule rule13 = new Rule(13, "EVMB013", "合同资产 ", "{N#zcfzb.EVMB013}");
            Rule rule14 = new Rule(14, "EVMB014", "持有待售资产 ", "{N#zcfzb.EVMB014}");
            Rule rule15 = new Rule(15, "EVMB015", "一年内到期的非流动资产 ", "{N#zcfzb.EVMB015}");
            Rule rule16 = new Rule(16, "EVMB016", "其他流动资产 ", "{N#zcfzb.EVMB016}");
            Rule rule17 = new Rule(17, "EVMB017", "流动资产合计 ", "{N#zcfzb.EVMB017}");
            Rule rule18 = new Rule(18, "EVMB018", "非流动资产： ", "{N#zcfzb.EVMB018}");
            Rule rule19 = new Rule(19, "EVMB019", "可供出售金融资产 ", "{N#zcfzb.EVMB019}");
            Rule rule20 = new Rule(20, "EVMB020", "债权投资 ", "{N#zcfzb.EVMB020}");
            Rule rule21 = new Rule(21, "EVMB021", "其他债权投资 ", "{N#zcfzb.EVMB021}");
            Rule rule22 = new Rule(22, "EVMB022", "长期应收款 ", "{N#zcfzb.EVMB022}");
            Rule rule23 = new Rule(23, "EVMB023", "长期股权投资 ", "{N#zcfzb.EVMB023}");
            Rule rule24 = new Rule(24, "EVMB024", "其他权益工具投资 ", "{N#zcfzb.EVMB024}");
            Rule rule25 = new Rule(25, "EVMB025", "其他非流动金融资产 ", "{N#zcfzb.EVMB025}");
            Rule rule26 = new Rule(26, "EVMB026", "投资性房地产 ", "{N#zcfzb.EVMB026}");
            Rule rule27 = new Rule(27, "EVMB027", "固定资产 ", "{N#zcfzb.EVMB027}");
            Rule rule28 = new Rule(28, "EVMB028", "在建工程 ", "{N#zcfzb.EVMB028}");
            Rule rule29 = new Rule(29, "EVMB029", "生产性生物资产 ", "{N#zcfzb.EVMB029}");
            Rule rule30 = new Rule(30, "EVMB030", "油气资产 ", "{N#zcfzb.EVMB030}");
            Rule rule31 = new Rule(31, "EVMB031", "使用权资产 ", "{N#zcfzb.EVMB031}");
            Rule rule32 = new Rule(32, "EVMB032", "无形资产 ", "{N#zcfzb.EVMB032}");
            Rule rule33 = new Rule(33, "EVMB033", "开发支出 ", "{N#zcfzb.EVMB033}");
            Rule rule34 = new Rule(34, "EVMB034", "商誉 ", "{N#zcfzb.EVMB034}");
            Rule rule35 = new Rule(35, "EVMB035", "长期待摊费用 ", "{N#zcfzb.EVMB035}");
            Rule rule36 = new Rule(36, "EVMB036", "递延所得税资产 ", "{N#zcfzb.EVMB036}");
            Rule rule37 = new Rule(37, "EVMB037", "其他非流动资产 ", "{N#zcfzb.EVMB037}");
            Rule rule38 = new Rule(38, "EVMB038", "非流动资产合计 ", "{N#zcfzb.EVMB038}");
            Rule rule39 = new Rule(39, "EVMB039", "资产总计 ", "{N#zcfzb.EVMB039}");
            Rule rule40 = new Rule(40, "EVMB040", "负债和所有者权益（或股东权益）", "{N#zcfzb.EVMB040}");
            Rule rule41 = new Rule(41, "EVMB041", "流动负债： ", "{N#zcfzb.EVMB041}");
            Rule rule42 = new Rule(42, "EVMB042", "短期借款 ", "{N#zcfzb.EVMB042}");
            Rule rule43 = new Rule(43, "EVMB043", "交易性金融负债 ", "{N#zcfzb.EVMB043}");
            Rule rule44 = new Rule(44, "EVMB044", "衍生金融负债 ", "{N#zcfzb.EVMB044}");
            Rule rule45 = new Rule(45, "EVMB045", "应付票据 ", "{N#zcfzb.EVMB045}");
            Rule rule46 = new Rule(46, "EVMB046", "应付账款 ", "{N#zcfzb.EVMB046}");
            Rule rule47 = new Rule(47, "EVMB047", "预收款项 ", "{N#zcfzb.EVMB047}");
            Rule rule48 = new Rule(48, "EVMB048", "合同负债 ", "{N#zcfzb.EVMB048}");
            Rule rule49 = new Rule(49, "EVMB049", "应付职工薪酬 ", "{N#zcfzb.EVMB049}");
            Rule rule50 = new Rule(50, "EVMB050", "应交税费 ", "{N#zcfzb.EVMB050}");
            Rule rule51 = new Rule(51, "EVMB051", "其他应付款 ", "{N#zcfzb.EVMB051}");
            Rule rule52 = new Rule(52, "EVMB052", "持有待售负债 ", "{N#zcfzb.EVMB052}");
            Rule rule53 = new Rule(53, "EVMB053", "一年内到期的非流动负债 ", "{N#zcfzb.EVMB053}");
            Rule rule54 = new Rule(54, "EVMB054", "其他流动负债 ", "{N#zcfzb.EVMB054}");
            Rule rule55 = new Rule(55, "EVMB055", "流动负债合计 ", "{N#zcfzb.EVMB055}");
            Rule rule56 = new Rule(56, "EVMB056", "非流动负债： ", "{N#zcfzb.EVMB056}");
            Rule rule57 = new Rule(57, "EVMB057", "长期借款 ", "{N#zcfzb.EVMB057}");
            Rule rule58 = new Rule(58, "EVMB058", "应付债券 ", "{N#zcfzb.EVMB058}");
            Rule rule59 = new Rule(59, "EVMB059", "其中：优先股 ", "{N#zcfzb.EVMB059}");
            Rule rule60 = new Rule(60, "EVMB060", "永续债 ", "{N#zcfzb.EVMB060}");
            Rule rule61 = new Rule(61, "EVMB061", "租赁负债 ", "{N#zcfzb.EVMB061}");
            Rule rule62 = new Rule(62, "EVMB062", "长期应付款 ", "{N#zcfzb.EVMB062}");
            Rule rule63 = new Rule(63, "EVMB063", "预计负债 ", "{N#zcfzb.EVMB063}");
            Rule rule64 = new Rule(64, "EVMB064", "递延收益 ", "{N#zcfzb.EVMB064}");
            Rule rule65 = new Rule(65, "EVMB065", "递延所得税负债 ", "{N#zcfzb.EVMB065}");
            Rule rule66 = new Rule(66, "EVMB066", "其他非流动负债 ", "{N#zcfzb.EVMB066}");
            Rule rule67 = new Rule(67, "EVMB067", "非流动负债合计 ", "{N#zcfzb.EVMB067}");
            Rule rule68 = new Rule(68, "EVMB068", "负债合计 ", "{N#zcfzb.EVMB068}");
            Rule rule69 = new Rule(69, "EVMB069", "所有者权益（或股东权益）： ", "{N#zcfzb.EVMB069}");
            Rule rule70 = new Rule(70, "EVMB070", "实收资本（或股本） ", "{N#zcfzb.EVMB070}");
            Rule rule71 = new Rule(71, "EVMB071", "其他权益工具 ", "{N#zcfzb.EVMB071}");
            Rule rule72 = new Rule(72, "EVMB072", "其中：优先股 ", "{N#zcfzb.EVMB072}");
            Rule rule73 = new Rule(73, "EVMB073", "永续债 ", "{N#zcfzb.EVMB073}");
            Rule rule74 = new Rule(74, "EVMB074", "资本公积 ", "{N#zcfzb.EVMB074}");
            Rule rule75 = new Rule(75, "EVMB075", "减：库存股 ", "{N#zcfzb.EVMB075}");
            Rule rule76 = new Rule(76, "EVMB076", "其他综合收益 ", "{N#zcfzb.EVMB076}");
            Rule rule77 = new Rule(77, "EVMB077", "专项储备 ", "{N#zcfzb.EVMB077}");
            Rule rule78 = new Rule(78, "EVMB078", "盈余公积 ", "{N#zcfzb.EVMB078}");
            Rule rule79 = new Rule(79, "EVMB079", "未分配利润 ", "{N#zcfzb.EVMB079}");
            Rule rule80 = new Rule(80, "EVMB080", "所有者权益（或股东权益）合计", "{N#zcfzb.EVMB080}");
            Rule rule81 = new Rule(81, "EVMB081", "负债和所有者权益（或股东权益）总计", "{N#zcfzb.EVMB081}");

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
            collusion.add(rule38);
            collusion.add(rule39);
            collusion.add(rule40);
            collusion.add(rule41);
            collusion.add(rule42);
            collusion.add(rule43);
            collusion.add(rule44);
            collusion.add(rule45);
            collusion.add(rule46);
            collusion.add(rule47);
            collusion.add(rule48);
            collusion.add(rule49);
            collusion.add(rule50);
            collusion.add(rule51);
            collusion.add(rule52);
            collusion.add(rule53);
            collusion.add(rule54);
            collusion.add(rule55);
            collusion.add(rule56);
            collusion.add(rule57);
            collusion.add(rule58);
            collusion.add(rule59);
            collusion.add(rule60);
            collusion.add(rule61);
            collusion.add(rule62);
            collusion.add(rule63);
            collusion.add(rule64);
            collusion.add(rule65);
            collusion.add(rule66);
            collusion.add(rule67);
            collusion.add(rule68);
            collusion.add(rule69);
            collusion.add(rule70);
            collusion.add(rule71);
            collusion.add(rule72);
            collusion.add(rule73);
            collusion.add(rule74);
            collusion.add(rule75);
            collusion.add(rule76);
            collusion.add(rule77);
            collusion.add(rule78);
            collusion.add(rule79);
            collusion.add(rule80);
            collusion.add(rule81);

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
