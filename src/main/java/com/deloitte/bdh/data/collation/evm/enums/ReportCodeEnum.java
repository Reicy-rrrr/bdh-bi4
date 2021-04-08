package com.deloitte.bdh.data.collation.evm.enums;


import com.deloitte.bdh.data.collation.evm.dto.Rule;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

public enum ReportCodeEnum {

    DEFAULT("DEFAULT", "默认") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            return null;
        }
    },

    ZCXLZTSPB("ZCXLZTSPB", "资产效率整体水平表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName(), SheetCodeEnum.lrb.getName(), SheetCodeEnum.kmyeb.getName(), SheetCodeEnum.yszkb.getName());
            List<Rule> collusion = Lists.newArrayList(
                    new Rule("EVM0001", "总资产", "Total assets", "{N#zcfzb.EVMB039}")
                    , new Rule("EVM0002", "资产负债率", "Debt to asset ratio ", "{N#zcfzb.EVMB068}/{N#zcfzb.EVMB039}")
                    , new Rule("EVM0003", "现金", "Cash", "{N#zcfzb.EVMB003}")
                    , new Rule("EVM0004", "存货", "Inventories", "{N#zcfzb.EVMB012}")
                    , new Rule("EVM0005", "存货周转率", "Inventory turnover", "{N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG}")
                    , new Rule("EVM0006", "存货周转天数", "Days sales of Inventory (DSI)", "360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG})")
                    , new Rule("EVM0007", "净资产", "Equity", "{N#zcfzb.EVMB080}")
                    , new Rule("EVM0008", "净资产收益率", "Return on equity", "{N#lrb.EVMP024}/{N#zcfzb.EVMB080_AVG}")
                    , new Rule("EVM0009", "应收账款", "Accounts receivable", "{N#zcfzb.EVMB008}")
                    , new Rule("EVM0010", "逾期应收账款", "Overdue accounts receivable", "{N#yszkb.YS_TOTAL}")
                    , new Rule("EVM0011", "应收账款周转率", "Accounts receivable turnover", "{N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG}")
                    , new Rule("EVM0012", "总资产周转率 （次数）", "Total assets turnover", "{N#lrb.EVMP001}/{N#zcfzb.EVMB039_AVG}")
                    , new Rule("EVM0013", "总资产周转天数", "Total assets turnover (in number of days)", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB039_AVG})")
                    , new Rule("EVM0014", "固定资产", "Property, plant and equipment (PP&E)", "{N#zcfzb.EVMB027}")
                    , new Rule("EVM0015", "原值", "Cost of PP&E", "{N#kmyeb.M051}+{N#kmyeb.M052}")
                    , new Rule("EVM0016", "累计折旧", "Accumulated depreciation", "{N#kmyeb.M052}")
                    , new Rule("EVM0017", "固定资产周转率", "PP&E turnover", "{N#lrb.EVMP001}/{N#zcfzb.EVMB027_AVG}")
                    , new Rule("EVM0018", "固定资产周转天数", "PP&E turnover (in number of days)", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB027_AVG})")
                    , new Rule("EVM0019", "现金及现金等价物", "Cash and cash equivalents", "{N#zcfzb.EVMB003}+{N#zcfzb.EVMB004}")
                    , new Rule("EVM0020", "以公允价值计量且其变动计入当期损益的金融资产", "FVTPL", "{N#zcfzb.EVMB005}+{N#zcfzb.EVMB006}")
                    , new Rule("EVM0022", "预付款项", "Prepayments", "{N#zcfzb.EVMB010}")
                    , new Rule("EVM0023", "其他应收款", "Other receivables", "{N#zcfzb.EVMB011}")
                    , new Rule("EVM0025", "其他流动资产", "Other current assets", "{N#zcfzb.EVMB016}")
                    , new Rule("EVM0026", "其他", "Others", "{N#zcfzb.EVMB005}+{N#zcfzb.EVMB006}+{N#zcfzb.EVMB008}+{N#zcfzb.EVMB010}+{N#zcfzb.EVMB011}+{N#zcfzb.EVMB012}+{N#zcfzb.EVMB016}")
                    , new Rule("EVM0027", "固定资产成新率", "Wear rate of PP&E", "{N#zcfzb.EVMB027}/({N#kmyeb.M051}+{N#kmyeb.M052})")
                    , new Rule("EVM0028", "每百元固定资产收入", "Revenue per hundred yuan of PP&E", "{N#lrb.EVMP001}/{N#zcfzb.EVMB027}")
                    , new Rule("EVM0029", "每百元固定资产利润", "Profit per hundred yuan of PP&E", "{N#lrb.EVMP027}/{N#zcfzb.EVMB028}")
                    , new Rule("EVM0030", "流动资产期末余额", "Closing balance of current assets", "{N#zcfzb.EVMB017}")
                    , new Rule("EVM0031", "流动资产周转率", "Current assets turnover", "{N#lrb.EVMP001}/{N#zcfzb.EVMB017_AVG}")
                    , new Rule("EVM0032", "流动资产周转天数", "Current assets turnover (in number of days)", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB017_AVG})")
                    , new Rule("EVM0033", "两金规模", "Total accounts receivable and inventories", "{N#zcfzb.EVMB008}+{N#zcfzb.EVMB0012}")


            );
            return new ImmutablePair<>(relySheets, collusion);
        }
    },

    ZJB("ZJB", "资金表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName(), SheetCodeEnum.lrb.getName(), SheetCodeEnum.xjllb.getName(), SheetCodeEnum.yszkb.getName(), SheetCodeEnum.kmyeb.getName());
            List<Rule> collusion = Lists.newArrayList(
                    new Rule("EVM0001", "资金总额", "Cash", "{N#zcfzb.EVMB003}+{N#zcfzb.EVMB004}","资金")
                    , new Rule("EVM0002", "资金占总资产比率", "Cash to asset ratio", "({N#zcfzb.EVMB003}+{N#zcfzb.EVMB004})/{N#zcfzb.EVMB039}","资金")
                    , new Rule("EVM0003", "资金周转率", "Cash turnover", "{N#kmyeb.M132}/({N#zcfzb.EVMB003_AVG}+{N#zcfzb.EVMB004_AVG})","资金")
//                    , new Rule("EVM0004", "应收账款周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG})")
//                    , new Rule("EVM0005", "应付账款周转天数", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG})")
//                    , new Rule("EVM0006", "存货周转天数", "360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG})")
                    , new Rule("EVM0007", "现金周转天数", "Cash conversion cycle", "(360/({N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG}))-(360/({N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG}))+(360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG}))","资金")
                    , new Rule("EVM0008", "期末资金集中度", "Period end capital concentration", "1+1","资金规划")
                    , new Rule("EVM0009", "资金计划报送及时率", "% of on-time cash plan submission", "1+1","资金规划")
                    , new Rule("EVM0010", "资金计划执行准确率", "% of timely excution of cash plan", "1+1","资金规划")
                    , new Rule("EVM0011", "经营活动现金流入", "Cash inflow from operations", "{N#xjllb.EVMC005}","经营活动现金流量")
                    , new Rule("EVM0012", "经营活动现金流出", "Cash outflow from operations", "{N#xjllb.EVMC010}","经营活动现金流量")
                    , new Rule("EVM0013", "经营活动产生的现金流量", "Net Cash generated from operations", "{N#xjllb.EVMC011}","经营活动现金流量")
                    , new Rule("EVM0014", "投资活动现金流入", "Cash inflow from investments", "{N#xjllb.EVMC018}","经营活动现金流量")
                    , new Rule("EVM0015", "投资活动现金流出", "Cash outflow from investments", "{N#xjllb.EVMC023}","经营活动现金流量")
                    , new Rule("EVM0016", "投资活动所用的现金流量净额", "Net cash used in investments", "{N#xjllb.EVMC024}","经营活动现金流量")
                    , new Rule("EVM0017", "融资活动现金流入", "Cash inflow from financing", "{N#xjllb.EVMC029}","经营活动现金流量")
                    , new Rule("EVM0018", "融资活动现金流出", "Cash outflow from financing", "{N#xjllb.EVMC033}","经营活动现金流量")
                    , new Rule("EVM0019", "融资活动所产生(使用)的现金流量净额", "Net cash used in/generated from financing", "{N#xjllb.EVMC034}","经营活动现金流量")
                    , new Rule("EVM0020", "短期借款", "Short-term loans", "{N#zcfzb.EVMB042}","债务结构")
                    , new Rule("EVM0021", "应付票据", "Notes payable", "{N#zcfzb.EVMB045}","债务结构")
                    , new Rule("EVM0022", "应付账款", "Accounts payable", "{N#zcfzb.EVMB046}","债务结构")
                    , new Rule("EVM0023", "预收账款", "Advance from customers", "{N#zcfzb.EVMB047}","债务结构")
                    , new Rule("EVM0024", "流动负债", "Current liabilities", "{N#zcfzb.EVMB042}+{N#zcfzb.EVMB045}+{N#zcfzb.EVMB046}+{N#zcfzb.EVMB047}","债务结构")
                    , new Rule("EVM0025", "长期借款", "Long-term loans", "{N#zcfzb.EVMB057}","债务结构")
                    , new Rule("EVM0026", "应付债券", "Bonds payable", "{N#zcfzb.EVMB058}","债务结构")
                    , new Rule("EVM0027", "非流动负债", "Non-current liabilities", "{N#zcfzb.EVMB057}+{N#zcfzb.EVMB058}","债务结构")
                    , new Rule("EVM0028", "流动比率", "Liquidity ratio", "{N#zcfzb.EVMB017}/{N#zcfzb.EVMB055}","短期偿债能力")
                    , new Rule("EVM0029", "速动比率", "Quick ratio", "({N#zcfzb.EVMB017}-{N#zcfzb.EVMB012})/{N#zcfzb.EVMB055}","短期偿债能力")
                    , new Rule("EVM0030", "现金比率", "Cash ratio", "({N#zcfzb.EVMB003}+{N#zcfzb.EVMB004}+{N#zcfzb.EVMB005})/{N#zcfzb.EVMB055}","短期偿债能力")
                    , new Rule("EVM0031", "流动资产合计", "Total current assets", "{N#zcfzb.EVMB017}","短期偿债能力")
                    , new Rule("EVM0032", "流动负债合计", "Total current liabilities", "{N#zcfzb.EVMB055}","短期偿债能力")
                    , new Rule("EVM0033", "资产负债率", "Debt to asset ratio", "{N#zcfzb.EVMB068}/{N#zcfzb.EVMB039}","长期偿债能力")
                    , new Rule("EVM0034", "产权比率", "Equity ratio", "{N#zcfzb.EVMB068}/{N#zcfzb.EVMB080}","长期偿债能力")
                    , new Rule("EVM0035", "有形资产债务率", "Debt to tangible assets ratio", "{N#zcfzb.EVMB068}/({N#zcfzb.EVMB080}-{N#zcfzb.EVMB032})","长期偿债能力")
                    , new Rule("EVM0036", "资产总额", "Total assets", "{N#zcfzb.EVMB039}","长期偿债能力")
                    , new Rule("EVM0037", "负债总额", "Total liabilities", "{N#zcfzb.EVMB068}","长期偿债能力")
                    , new Rule("EVM0038", "利息偿付倍数", "Interest coverage ratio", "({N#lrb.EVMP022}-{N#lrb.EVMP008})/{N#lrb.EVMP008}","长期偿债能力")
                    , new Rule("EVM0039", "经营活动现金流量", "Cash flow from operations", "{N#xjllb.EVMC011}","资金")
                    , new Rule("EVM0040", "运营资金周转率", "Operating capital turnover", "{N#xjllb.EVMC011}/({N#zcfzb.EVMB017_AVG}-{N#zcfzb.EVMB055_AVG})","资金")
                    , new Rule("EVM0041", "应收账款余额", "Closing balance of accounts receivable", "{N#zcfzb.EVMB008}","应收")
                    , new Rule("EVM0042", "坏账准备", "Bad debt provision", "{N#kmyeb.M020}","应收")
                    , new Rule("EVM0043", "应收账款周转率", "Accounts receivable turnover", "{N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG}","应收")
                    , new Rule("EVM0044", "应收账款周转天数", "Accounts receivable turnover (in number of days)", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB008_AVG})","应收")
                    , new Rule("EVM0045", "营业收入", "Operating income", "{N#lrb.EVMP001}","应收")
                    , new Rule("EVM0046", "应收占比", "Accounts receivable to sales ratio", "{N#zcfzb.EVMB008}/{N#lrb.EVMP001}","应收")
                    , new Rule("EVM0047", "应收账款净额", "Net accounts receivable", "{N#zcfzb.EVMB008}-{N#kmyeb.M020}","应收")
                    , new Rule("EVM0048", "应收账款净额", "Net accounts receivable", "{N#zcfzb.EVMB008}-{N#kmyeb.M020}","应收")
                    , new Rule("EVM0049", "逾期应收账款余额", "Balance of overdue accounts receivable", "{N#yszkb.YS_TOTAL}","应收")
                    , new Rule("EVM0050", "逾期应收款占比", "Overdue accounts ratio", "{N#yszkb.YS_TOTAL}/{N#zcfzb.EVMB008}","应收")
                    , new Rule("EVM0051", "应付账款余额", "Accounts payable", "{N#zcfzb.EVMB046}","应付")
                    , new Rule("EVM0052", "应付账款周转率", "Accounts payable turnover", "{N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG}","应付")
                    , new Rule("EVM0053", "应付账款周转天数", "Accounts payable turnover (in number of days)", "360/({N#lrb.EVMP001}/{N#zcfzb.EVMB046_AVG})","应付")
                    , new Rule("EVM0054", "库存余额", "Balance of inventories", "{N#zcfzb.EVMB012}","库存")
                    , new Rule("EVM0055", "库存周转率", "Inventories turnover", "{N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG}","库存")
                    , new Rule("EVM0056", "库存周转天数", "Days sales of Inventory (DSI)", "360/({N#lrb.EVMP002}/{N#zcfzb.EVMB012_AVG})","库存")
                    , new Rule("EVM0057", "存货资产占比", "Inventories to asset ratio", "{N#zcfzb.EVMB012}/{N#zcfzb.EVMB039}","库存")

            );
            return new ImmutablePair<>(relySheets, collusion);
        }
    },

    ZCGCB("ZCGCB", "总资产构成表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName());
            List<Rule> collusion = Lists.newArrayList(
                    new Rule("EVMB001", "资产", "Assets", "{N#zcfzb.EVMB001}")
                    , new Rule("EVMB002", "流动资产： ", "Current Assets:", "{N#zcfzb.EVMB002}")
                    , new Rule("EVMB003", "现金 ", "Cash", "{N#zcfzb.EVMB003}")
                    , new Rule("EVMB004", "其他货币资金 ", "Other monetary assets", "{N#zcfzb.EVMB004}")
                    , new Rule("EVMB005", "交易性金融资产 ", "Financial assets held for trading", "{N#zcfzb.EVMB005}")
                    , new Rule("EVMB006", "衍生金融资产 ", "Derivative financial assets", "{N#zcfzb.EVMB006}")
                    , new Rule("EVMB007", "应收票据 ", "Notes receivable", "{N#zcfzb.EVMB007}")
                    , new Rule("EVMB008", "应收账款 ", "Accounts receivable", "{N#zcfzb.EVMB008}")
                    , new Rule("EVMB009", "应收款项融资 ", "Accounts receivable financing", "{N#zcfzb.EVMB009}")
                    , new Rule("EVMB010", "预付款项 ", "Prepayments", "{N#zcfzb.EVMB010}")
                    , new Rule("EVMB011", "其他应收款 ", "Other receivables", "{N#zcfzb.EVMB011}")
                    , new Rule("EVMB012", "存货 ", "Inventories", "{N#zcfzb.EVMB012}")
                    , new Rule("EVMB013", "合同资产 ", "Contract assets", "{N#zcfzb.EVMB013}")
                    , new Rule("EVMB014", "持有待售资产 ", "Assets held for sale", "{N#zcfzb.EVMB014}")
                    , new Rule("EVMB015", "一年内到期的非流动资产 ", "Non-current assets maturing within one year", "{N#zcfzb.EVMB015}")
                    , new Rule("EVMB016", "其他流动资产 ", "Other current assets", "{N#zcfzb.EVMB016}")
                    , new Rule("EVMB017", "流动资产合计 ", "Total current assets", "{N#zcfzb.EVMB017}")
                    , new Rule("EVMB018", "非流动资产： ", "Non-current Assets:", "{N#zcfzb.EVMB018}")
                    , new Rule("EVMB019", "可供出售金融资产 ", "Financial assets available for sale", "{N#zcfzb.EVMB019}")
                    , new Rule("EVMB020", "债权投资 ", "Debt investment", "{N#zcfzb.EVMB020}")
                    , new Rule("EVMB021", "其他债权投资 ", "Other debt investment", "{N#zcfzb.EVMB021}")
                    , new Rule("EVMB022", "长期应收款 ", "Long-term receivables", "{N#zcfzb.EVMB022}")
                    , new Rule("EVMB023", "长期股权投资 ", "Long-term equity investments", "{N#zcfzb.EVMB023}")
                    , new Rule("EVMB024", "其他权益工具投资 ", "Other equity investments", "{N#zcfzb.EVMB024}")
                    , new Rule("EVMB025", "其他非流动金融资产 ", "Other non-current financial assets", "{N#zcfzb.EVMB025}")
                    , new Rule("EVMB026", "投资性房地产 ", "Investment real estate", "{N#zcfzb.EVMB026}")
                    , new Rule("EVMB027", "固定资产 ", "Property, plant and equipment", "{N#zcfzb.EVMB027}")
                    , new Rule("EVMB028", "在建工程 ", "Construction in progress", "{N#zcfzb.EVMB028}")
                    , new Rule("EVMB029", "生产性生物资产 ", "Productive biological assets", "{N#zcfzb.EVMB029}")
                    , new Rule("EVMB030", "油气资产 ", "Oil and gas assets", "{N#zcfzb.EVMB030}")
                    , new Rule("EVMB031", "使用权资产 ", "Right-of-use assets", "{N#zcfzb.EVMB031}")
                    , new Rule("EVMB032", "无形资产 ", "Intangible assets", "{N#zcfzb.EVMB032}")
                    , new Rule("EVMB033", "开发支出 ", "R&D expenses", "{N#zcfzb.EVMB033}")
                    , new Rule("EVMB034", "商誉 ", "Goodwill", "{N#zcfzb.EVMB034}")
                    , new Rule("EVMB035", "长期待摊费用 ", "Long-term deferred expenses", "{N#zcfzb.EVMB035}")
                    , new Rule("EVMB036", "递延所得税资产 ", "Deferred income tax assets", "{N#zcfzb.EVMB036}")
                    , new Rule("EVMB037", "其他非流动资产 ", "Other non-current assets", "{N#zcfzb.EVMB037}")
                    , new Rule("EVMB038", "非流动资产合计 ", "Total non-current assets", "{N#zcfzb.EVMB038}")
                    , new Rule("EVMB039", "资产总计 ", "Total assets", "{N#zcfzb.EVMB039}")
                    , new Rule("EVMB040", "负债和所有者权益（或股东权益）", "Liabilities and Owners' Equity", "{N#zcfzb.EVMB040}")
                    , new Rule("EVMB041", "流动负债： ", "Current Liabilities:", "{N#zcfzb.EVMB041}")
                    , new Rule("EVMB042", "短期借款 ", "Short-term loans", "{N#zcfzb.EVMB042}")
                    , new Rule("EVMB043", "交易性金融负债 ", "Financial liabilities held for trading", "{N#zcfzb.EVMB043}")
                    , new Rule("EVMB044", "衍生金融负债 ", "Derivative financial liabilities", "{N#zcfzb.EVMB044}")
                    , new Rule("EVMB045", "应付票据 ", "Notes payable", "{N#zcfzb.EVMB045}")
                    , new Rule("EVMB046", "应付账款 ", "Accounts payable", "{N#zcfzb.EVMB046}")
                    , new Rule("EVMB047", "预收款项 ", "Advance from customers", "{N#zcfzb.EVMB047}")
                    , new Rule("EVMB048", "合同负债 ", "Contract liabilities", "{N#zcfzb.EVMB048}")
                    , new Rule("EVMB049", "应付职工薪酬 ", "Payroll payable", "{N#zcfzb.EVMB049}")
                    , new Rule("EVMB050", "应交税费 ", "Taxes payable", "{N#zcfzb.EVMB050}")
                    , new Rule("EVMB051", "其他应付款 ", "Other payables", "{N#zcfzb.EVMB051}")
                    , new Rule("EVMB052", "持有待售负债 ", "Liabilities held for sale", "{N#zcfzb.EVMB052}")
                    , new Rule("EVMB053", "一年内到期的非流动负债 ", "Non-current liabilities maturing within one year", "{N#zcfzb.EVMB053}")
                    , new Rule("EVMB054", "其他流动负债 ", "Other currents liabilities", "{N#zcfzb.EVMB054}")
                    , new Rule("EVMB055", "流动负债合计 ", "Total current liabilities", "{N#zcfzb.EVMB055}")
                    , new Rule("EVMB056", "非流动负债： ", "Non-current liabilities:", "{N#zcfzb.EVMB056}")
                    , new Rule("EVMB057", "长期借款 ", "Long-term loans", "{N#zcfzb.EVMB057}")
                    , new Rule("EVMB058", "应付债券 ", "Bonds payable", "{N#zcfzb.EVMB058}")
                    , new Rule("EVMB059", "其中：优先股 ", "Including: Preferred stock", "{N#zcfzb.EVMB059}")
                    , new Rule("EVMB060", "永续债 ", "Perpetual bond", "{N#zcfzb.EVMB060}")
                    , new Rule("EVMB061", "租赁负债 ", "Lease liabilities", "{N#zcfzb.EVMB061}")
                    , new Rule("EVMB062", "长期应付款 ", "Long-term payable", "{N#zcfzb.EVMB062}")
                    , new Rule("EVMB063", "预计负债 ", "Accrued liabilities", "{N#zcfzb.EVMB063}")
                    , new Rule("EVMB064", "递延收益 ", "Deferred revenue", "{N#zcfzb.EVMB064}")
                    , new Rule("EVMB065", "递延所得税负债 ", "Deferred income tax liabilities", "{N#zcfzb.EVMB065}")
                    , new Rule("EVMB066", "其他非流动负债 ", "Other non-current liabilities", "{N#zcfzb.EVMB066}")
                    , new Rule("EVMB067", "非流动负债合计 ", "Other non-current liabilities", "{N#zcfzb.EVMB067}")
                    , new Rule("EVMB068", "负债合计 ", "Total liabilities", "{N#zcfzb.EVMB068}")
                    , new Rule("EVMB069", "所有者权益（或股东权益）： ", "Owners' equity:", "{N#zcfzb.EVMB069}")
                    , new Rule("EVMB070", "实收资本（或股本） ", "Paid-in capital", "{N#zcfzb.EVMB070}")
                    , new Rule("EVMB071", "其他权益工具 ", "Other equity instruments", "{N#zcfzb.EVMB071}")
                    , new Rule("EVMB072", "其中：优先股 ", "including: Preferred stock", "{N#zcfzb.EVMB072}")
                    , new Rule("EVMB073", "永续债 ", "Perpetual bond", "{N#zcfzb.EVMB073}")
                    , new Rule("EVMB074", "资本公积 ", "Capital reserve", "{N#zcfzb.EVMB074}")
                    , new Rule("EVMB075", "减：库存股 ", "Less: Treasury stock", "{N#zcfzb.EVMB075}")
                    , new Rule("EVMB076", "其他综合收益 ", "Other comprehensive income", "{N#zcfzb.EVMB076}")
                    , new Rule("EVMB077", "专项储备 ", "Special reserves", "{N#zcfzb.EVMB077}")
                    , new Rule("EVMB078", "盈余公积 ", "Surplus reserves", "{N#zcfzb.EVMB078}")
                    , new Rule("EVMB079", "未分配利润 ", "Retained profits after appropriation", "{N#zcfzb.EVMB079}")
                    , new Rule("EVMB080", "所有者权益（或股东权益）合计", "Total owners' equity", "{N#zcfzb.EVMB080}")
                    , new Rule("EVMB081", "负债和所有者权益（或股东权益）总计", "Total liabilities and owners' equity", "{N#zcfzb.EVMB081}")

            );

            return new ImmutablePair<>(relySheets, collusion);
        }
    },

    CKGL("CKGL", "仓库管理") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList(SheetCodeEnum.zcfzb.getName(), SheetCodeEnum.ckgl.getName());
            List<Rule> collusion = Lists.newArrayList(
                    new Rule("EVM0001", "仓库成本", "cost","{N#ckgl.WH001}")
                    , new Rule("EVM0002", "仓库维修费用", "main_cost","{N#ckgl.WH002}")
                    , new Rule("EVM0003", "单位库存成本", "unitcost","{N#ckgl.WH001}/{N#zcfzb.EVMB012_AVG}")
                    , new Rule("EVM0004", "单位仓库维修费用", "unitmain_cost","{N#ckgl.WH002}/{N#zcfzb.EVMB012_AVG}")
                    , new Rule("EVM0005", "仓库安全事件报告率", "reporting_rate","{N#ckgl.WH004}/{N#ckgl.WH003}")
                    , new Rule("EVM0006", "仓库安全事件解决率", "resolution_rate","{N#ckgl.WH005}/{N#ckgl.WH003}")

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
