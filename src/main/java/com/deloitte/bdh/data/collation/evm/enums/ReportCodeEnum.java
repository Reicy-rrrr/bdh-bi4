package com.deloitte.bdh.data.collation.evm.enums;


import com.deloitte.bdh.data.collation.evm.dto.Rule;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

public enum ReportCodeEnum {

    ZCXLZTSPB("ZCXLZTSPB", "资产效率整体水平表") {
        @Override
        public ImmutablePair<List<String>, List<Rule>> relySheets() {
            List<String> relySheets = Lists.newArrayList("zcfz", "yfzkmx");
            List<Rule> collusion = Lists.newArrayList();
//            Rule test = new Rule(1, "EVM01001", "总资产", "{N#zcfz.ldzchj}");
//            Rule test2 = new Rule(2, "EVM01002", "资产负债率", "{N#zcfz.yszk}/{N#zcfz.yfkx}");
//            Rule test3 = new Rule(3, "EVM01003", "现金", "{N#zcfz.xj}");
            Rule test4 = new Rule(4, "EVM01004", "存货", "{N#zcfz.qthbzj}/({P#zcfz.ch}+{N#zcfz.ch})/2");
            Rule test5 = new Rule(5, "EVM01005", "存货周转率", "{N#zcfz.qthbzj}/({A#zcfz.ch}+{N#zcfz.ch})/2");
            Rule test6 = new Rule(6, "EVM01006", "存货周转天数", "360/({N#zcfz.qthbzj}/{N#zcfz.ch})");
//            Rule test7 = new Rule(7, "EVM01007", "净资产", "{H#zcfz.sr}/{H#yfzkmx.sun(xy30)}");
//            Rule test8 = new Rule(8, "EVM01008", "净资产收益率", "{H#zcfz.sr}/{H#yfzkmx.sun(xy30)}");
//            Rule test9 = new Rule(9, "EVM01009", "应收账款", "{H#zcfz.sr}/{H#yfzkmx.sun(xy30)}");
//            Rule test10 = new Rule(10, "EVM010010", "逾期应收账款", "{H#zcfz.sr}/{H#yfzkmx.sun(xy30)}");

//            collusion.add(test);
//            collusion.add(test2);
//            collusion.add(test3);
            collusion.add(test4);
            collusion.add(test5);
            collusion.add(test6);
//            collusion.add(test7);
//            collusion.add(test8);
//            collusion.add(test9);
//            collusion.add(test10);

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
