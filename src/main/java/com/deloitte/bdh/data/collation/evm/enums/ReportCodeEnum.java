package com.deloitte.bdh.data.collation.evm.enums;


import com.deloitte.bdh.data.collation.evm.dto.Rule;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

public enum ReportCodeEnum {

    DEFAULT("DEFAULT", "资金表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            return null;
        }
    },

    ZCXLZTSPB("ZCXLZTSPB", "资产效率整体水平表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName(), SheetCodeEnum.lrb.getName());
            List<Rule> collusion = Lists.newArrayList(
                    new Rule("EVM0001", "总资产", "{N#zcfzb.EVMB039}")
                    , new Rule("EVM0002", "资产负债率", "{N#zcfzb.EVMB068}/{N#zcfzb.EVMB039}")
                    , new Rule("EVM0003", "现金", "{N#zcfzb.EVMB003}")
                    , new Rule("EVM0004", "存货", "{N#zcfzb.EVMB012}")
                    , new Rule("EVM0005", "存货周转率", "{N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG}")
                    , new Rule("EVM0006", "存货周转天数", "360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG})")
                    , new Rule("EVM0007", "净资产", "{N#zcfzb.EVMB080}")
                    , new Rule("EVM0008", "净资产收益率", "{N#lrb.EVMP024}/{N#zcfzb.EVMB080_AVG}")
                    , new Rule("EVM0009", "应收账款", "{N#zcfzb.EVMB008}")
                    , new Rule("EVM0010", "逾期应收账款", "{N#zcfzb.EVMB080}+111111")
                    , new Rule("EVM0011", "应收账款周转率", "{N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG}")
                    , new Rule("EVM0012", "总资产周转率 （次数）", "{N#lrb.EVMP001}/{N#zcfzb.EVMB039_AVG}")
                    , new Rule("EVM0013", "总资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB039_AVG})")
                    , new Rule("EVM0014", "固定资产", "{N#zcfzb.EVMB027}")
                    , new Rule("EVM0015", "原值", "1+1")
                    , new Rule("EVM0016", "累计折旧", "1+1")
                    , new Rule("EVM0017", "固定资产周转率", "{N#lrb.EVMP001}/{N#zcfzb.EVMB027_AVG}")
                    , new Rule("EVM0018", "固定资产周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB027_AVG})")
            );
            return new ImmutablePair<>(relySheets, collusion);
        }
    },

    ZJB("ZJB", "资金表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName(), SheetCodeEnum.lrb.getName(), SheetCodeEnum.xjllb.getName());
            List<Rule> collusion = Lists.newArrayList(
                    new Rule("EVM0001", "资金总额", "{N#zcfzb.EVMB003}+{N#zcfzb.EVMB004}")
                    , new Rule("EVM0002", "资金占总资产比率", "({N#zcfzb.EVMB003}+{N#zcfzb.EVMB004})/{N#zcfzb.EVMB039}")
                    , new Rule("EVM0003", "资金周转率", "{N#kmyeb.M132}/({N#zcfzb.EVMB003_AVG}+{N#zcfzb.EVMB004_AVG})")
                    , new Rule("EVM0004", "应收账款周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG})")
                    , new Rule("EVM0005", "应付账款周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG})")
                    , new Rule("EVM0006", "存货周转天数", "360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG})")
                    , new Rule("EVM0007", "现金周转天数", "(360/({N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG}))-(360/({N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG}))+(360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG}))")
                    , new Rule("EVM0008", "期末资金集中度", "1+1")
                    , new Rule("EVM0009", "资金计划报送及时率", "1+1")
                    , new Rule("EVM0010", "资金计划执行准确率", "1+1")
                    , new Rule("EVM0011", "经营活动现金流入", "{N#xjllb.EVMC005}")
                    , new Rule("EVM0012", "经营活动现金流出", "{N#xjllb.EVMC010}")
                    , new Rule("EVM0013", "经营活动产生的现金流量", "{N#xjllb.EVMC011}")
                    , new Rule("EVM0014", "投资活动现金流入", "{N#xjllb.EVMC018}")
                    , new Rule("EVM0015", "投资活动现金流出", "{N#xjllb.EVMC023}")
                    , new Rule("EVM0016", "投资活动所用的现金流量净额", "{N#xjllb.EVMC024}")
                    , new Rule("EVM0017", "融资活动现金流入", "{N#xjllb.EVMC029}")
                    , new Rule("EVM0018", "融资活动现金流出", "{N#xjllb.EVMC033}")
                    , new Rule("EVM0019", "融资活动所产生(使用)的现金流量净额", "{N#xjllb.EVMC034}")
                    , new Rule("EVM0020", "短期借款", "{N#zcfzb.EVMB042}")
                    , new Rule("EVM0021", "应付票据", "{N#zcfzb.EVMB045}")
                    , new Rule("EVM0022", "应付账款", "{N#zcfzb.EVMB046}")
                    , new Rule("EVM0023", "预收账款", "{N#zcfzb.EVMB047}")
                    , new Rule("EVM0024", "流动负债", "{N#zcfzb.EVMB042}+{N#zcfzb.EVMB045}+{N#zcfzb.EVMB046}+{N#zcfzb.EVMB047}")
                    , new Rule("EVM0025", "长期借款", "{N#zcfzb.EVMB057}")
                    , new Rule("EVM0026", "应付债券", "{N#zcfzb.EVMB058}")
                    , new Rule("EVM0027", "非流动负债", "{N#zcfzb.EVMB057}+{N#zcfzb.EVMB058}")
                    , new Rule("EVM0028", "流动比率", "{N#zcfzb.EVMB017}/{N#zcfzb.EVMB055}")
                    , new Rule("EVM0029", "速动比率", "({N#zcfzb.EVMB017}-{N#zcfzb.EVMB012})/{N#zcfzb.EVMB055}")
                    , new Rule("EVM0030", "现金比率", "({N#zcfzb.EVMB003}+{N#zcfzb.EVMB004}+{N#zcfzb.EVMB005})/{N#zcfzb.EVMB056}")
                    , new Rule("EVM0031", "流动资产合计", "{N#zcfzb.EVMB017}")
                    , new Rule("EVM0032", "流动负债合计", "{N#zcfzb.EVMB055}")
                    , new Rule("EVM0033", "资产负债率", "{N#zcfzb.EVMB068}/{N#zcfzb.EVMB039}")
                    , new Rule("EVM0034", "产权比率", "{N#zcfzb.EVMB068}/{N#zcfzb.EVMB080}")
                    , new Rule("EVM0035", "有形资产债务率", "{N#zcfzb.EVMB068}/({N#zcfzb.EVMB080}-{N#zcfzb.EVMB032})")
                    , new Rule("EVM0036", "资产总额", "{N#zcfzb.EVMB039}")
                    , new Rule("EVM0037", "负债总额", "{N#zcfzb.EVMB068}")
                    , new Rule("EVM0038", "利息偿付倍数", "({N#lrb.EVMP022}-{N#lrb.EVMP008})/{N#lrb.EVMP008}")
                    , new Rule("EVM0039", "经营活动现金流量", "{N#xjllb.EVMC011}")
                    , new Rule("EVM0040", "运营资金周转率", "{N#xjllb.EVMC011}/({N#zcfzb.EVMB017_AVG}-{N#zcfzb.EVMB055_AVG})")
                    , new Rule("EVM0041", "应收账款余额", "{N#zcfzb.EVMB008}")
                    , new Rule("EVM0042", "坏账准备", "{N#kmyeb.M020}")
                    , new Rule("EVM0043", "应收账款周转率", "{N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG}")
                    , new Rule("EVM0044", "应收账款周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG})")
                    , new Rule("EVM0045", "营业收入", "{N#lrb.EVMP001}")
                    , new Rule("EVM0046", "应收占比", "{N#zcfzb.EVMB008}/{N#lrb.EVMP001}")
                    , new Rule("EVM0047", "应收账款净额", "{N#zcfzb.EVMB008}-{N#kmyeb.M020}")
                    , new Rule("EVM0048", "应收账款净额", "{N#zcfzb.EVMB008}-{N#kmyeb.M020}")
                    , new Rule("EVM0049", "逾期应收账款余额", "11111")
                    , new Rule("EVM0050", "逾期应收款占比", "11111/{N#zcfzb.EVMB008}")
                    , new Rule("EVM0051", "应付账款余额", "{N#zcfzb.EVMB046}")
                    , new Rule("EVM0052", "应付账款周转率", "{N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG}")
                    , new Rule("EVM0053", "应付账款周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG})")
                    , new Rule("EVM0054", "库存余额", "{N#zcfzb.EVMB012}")
                    , new Rule("EVM0055", "库存周转率", "{N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG}")
                    , new Rule("EVM0056", "库存周转天数", "360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG})")
                    , new Rule("EVM0057", "存货资产占比", "{N#zcfzb.EVMP012}/{N#zcfzb.EVMB039}")

            );
            return new ImmutablePair<>(relySheets, collusion);
        }
    },

    ZCGCB("ZCGCB", "总资产构成表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName());
            List<Rule> collusion = Lists.newArrayList(
                    new Rule("EVMB001", "资产", "{N#zcfzb.EVMB001}")
                    , new Rule("EVMB002", "流动资产： ", "{N#zcfzb.EVMB002}")
                    , new Rule("EVMB003", "现金 ", "{N#zcfzb.EVMB003}")
                    , new Rule("EVMB004", "其他货币资金 ", "{N#zcfzb.EVMB004}")
                    , new Rule("EVMB005", "交易性金融资产 ", "{N#zcfzb.EVMB005}")
                    , new Rule("EVMB006", "衍生金融资产 ", "{N#zcfzb.EVMB006}")
                    , new Rule("EVMB007", "应收票据 ", "{N#zcfzb.EVMB007}")
                    , new Rule("EVMB008", "应收账款 ", "{N#zcfzb.EVMB008}")
                    , new Rule("EVMB009", "应收款项融资 ", "{N#zcfzb.EVMB009}")
                    , new Rule("EVMB010", "预付款项 ", "{N#zcfzb.EVMB010}")
                    , new Rule("EVMB011", "其他应收款 ", "{N#zcfzb.EVMB011}")
                    , new Rule("EVMB012", "存货 ", "{N#zcfzb.EVMB012}")
                    , new Rule("EVMB013", "合同资产 ", "{N#zcfzb.EVMB013}")
                    , new Rule("EVMB014", "持有待售资产 ", "{N#zcfzb.EVMB014}")
                    , new Rule("EVMB015", "一年内到期的非流动资产 ", "{N#zcfzb.EVMB015}")
                    , new Rule("EVMB016", "其他流动资产 ", "{N#zcfzb.EVMB016}")
                    , new Rule("EVMB017", "流动资产合计 ", "{N#zcfzb.EVMB017}")
                    , new Rule("EVMB018", "非流动资产： ", "{N#zcfzb.EVMB018}")
                    , new Rule("EVMB019", "可供出售金融资产 ", "{N#zcfzb.EVMB019}")
                    , new Rule("EVMB020", "债权投资 ", "{N#zcfzb.EVMB020}")
                    , new Rule("EVMB021", "其他债权投资 ", "{N#zcfzb.EVMB021}")
                    , new Rule("EVMB022", "长期应收款 ", "{N#zcfzb.EVMB022}")
                    , new Rule("EVMB023", "长期股权投资 ", "{N#zcfzb.EVMB023}")
                    , new Rule("EVMB024", "其他权益工具投资 ", "{N#zcfzb.EVMB024}")
                    , new Rule("EVMB025", "其他非流动金融资产 ", "{N#zcfzb.EVMB025}")
                    , new Rule("EVMB026", "投资性房地产 ", "{N#zcfzb.EVMB026}")
                    , new Rule("EVMB027", "固定资产 ", "{N#zcfzb.EVMB027}")
                    , new Rule("EVMB028", "在建工程 ", "{N#zcfzb.EVMB028}")
                    , new Rule("EVMB029", "生产性生物资产 ", "{N#zcfzb.EVMB029}")
                    , new Rule("EVMB030", "油气资产 ", "{N#zcfzb.EVMB030}")
                    , new Rule("EVMB031", "使用权资产 ", "{N#zcfzb.EVMB031}")
                    , new Rule("EVMB032", "无形资产 ", "{N#zcfzb.EVMB032}")
                    , new Rule("EVMB033", "开发支出 ", "{N#zcfzb.EVMB033}")
                    , new Rule("EVMB034", "商誉 ", "{N#zcfzb.EVMB034}")
                    , new Rule("EVMB035", "长期待摊费用 ", "{N#zcfzb.EVMB035}")
                    , new Rule("EVMB036", "递延所得税资产 ", "{N#zcfzb.EVMB036}")
                    , new Rule("EVMB037", "其他非流动资产 ", "{N#zcfzb.EVMB037}")
                    , new Rule("EVMB038", "非流动资产合计 ", "{N#zcfzb.EVMB038}")
                    , new Rule("EVMB039", "资产总计 ", "{N#zcfzb.EVMB039}")
                    , new Rule("EVMB040", "负债和所有者权益（或股东权益）", "{N#zcfzb.EVMB040}")
                    , new Rule("EVMB041", "流动负债： ", "{N#zcfzb.EVMB041}")
                    , new Rule("EVMB042", "短期借款 ", "{N#zcfzb.EVMB042}")
                    , new Rule("EVMB043", "交易性金融负债 ", "{N#zcfzb.EVMB043}")
                    , new Rule("EVMB044", "衍生金融负债 ", "{N#zcfzb.EVMB044}")
                    , new Rule("EVMB045", "应付票据 ", "{N#zcfzb.EVMB045}")
                    , new Rule("EVMB046", "应付账款 ", "{N#zcfzb.EVMB046}")
                    , new Rule("EVMB047", "预收款项 ", "{N#zcfzb.EVMB047}")
                    , new Rule("EVMB048", "合同负债 ", "{N#zcfzb.EVMB048}")
                    , new Rule("EVMB049", "应付职工薪酬 ", "{N#zcfzb.EVMB049}")
                    , new Rule("EVMB050", "应交税费 ", "{N#zcfzb.EVMB050}")
                    , new Rule("EVMB051", "其他应付款 ", "{N#zcfzb.EVMB051}")
                    , new Rule("EVMB052", "持有待售负债 ", "{N#zcfzb.EVMB052}")
                    , new Rule("EVMB053", "一年内到期的非流动负债 ", "{N#zcfzb.EVMB053}")
                    , new Rule("EVMB054", "其他流动负债 ", "{N#zcfzb.EVMB054}")
                    , new Rule("EVMB055", "流动负债合计 ", "{N#zcfzb.EVMB055}")
                    , new Rule("EVMB056", "非流动负债： ", "{N#zcfzb.EVMB056}")
                    , new Rule("EVMB057", "长期借款 ", "{N#zcfzb.EVMB057}")
                    , new Rule("EVMB058", "应付债券 ", "{N#zcfzb.EVMB058}")
                    , new Rule("EVMB059", "其中：优先股 ", "{N#zcfzb.EVMB059}")
                    , new Rule("EVMB060", "永续债 ", "{N#zcfzb.EVMB060}")
                    , new Rule("EVMB061", "租赁负债 ", "{N#zcfzb.EVMB061}")
                    , new Rule("EVMB062", "长期应付款 ", "{N#zcfzb.EVMB062}")
                    , new Rule("EVMB063", "预计负债 ", "{N#zcfzb.EVMB063}")
                    , new Rule("EVMB064", "递延收益 ", "{N#zcfzb.EVMB064}")
                    , new Rule("EVMB065", "递延所得税负债 ", "{N#zcfzb.EVMB065}")
                    , new Rule("EVMB066", "其他非流动负债 ", "{N#zcfzb.EVMB066}")
                    , new Rule("EVMB067", "非流动负债合计 ", "{N#zcfzb.EVMB067}")
                    , new Rule("EVMB068", "负债合计 ", "{N#zcfzb.EVMB068}")
                    , new Rule("EVMB069", "所有者权益（或股东权益）： ", "{N#zcfzb.EVMB069}")
                    , new Rule("EVMB070", "实收资本（或股本） ", "{N#zcfzb.EVMB070}")
                    , new Rule("EVMB071", "其他权益工具 ", "{N#zcfzb.EVMB071}")
                    , new Rule("EVMB072", "其中：优先股 ", "{N#zcfzb.EVMB072}")
                    , new Rule("EVMB073", "永续债 ", "{N#zcfzb.EVMB073}")
                    , new Rule("EVMB074", "资本公积 ", "{N#zcfzb.EVMB074}")
                    , new Rule("EVMB075", "减：库存股 ", "{N#zcfzb.EVMB075}")
                    , new Rule("EVMB076", "其他综合收益 ", "{N#zcfzb.EVMB076}")
                    , new Rule("EVMB077", "专项储备 ", "{N#zcfzb.EVMB077}")
                    , new Rule("EVMB078", "盈余公积 ", "{N#zcfzb.EVMB078}")
                    , new Rule("EVMB079", "未分配利润 ", "{N#zcfzb.EVMB079}")
                    , new Rule("EVMB080", "所有者权益（或股东权益）合计", "{N#zcfzb.EVMB080}")
                    , new Rule("EVMB081", "负债和所有者权益（或股东权益）总计", "{N#zcfzb.EVMB081}")

            );

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
