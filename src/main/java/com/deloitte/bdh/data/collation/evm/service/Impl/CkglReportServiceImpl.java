package com.deloitte.bdh.data.collation.evm.service.Impl;

import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.data.collation.evm.dto.Rule;
import com.deloitte.bdh.data.collation.evm.dto.Sheet;
import com.deloitte.bdh.data.collation.evm.enums.ReportCodeEnum;
import com.deloitte.bdh.data.collation.evm.enums.SheetCodeEnum;
import com.deloitte.bdh.data.collation.evm.service.AbstractReport;
import com.deloitte.bdh.data.collation.evm.utils.RuleParseUtil;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库管理
 * 基于期间变量
 */
@Service(value = "ckglReportServiceImpl")
public class CkglReportServiceImpl extends AbstractReport {
    @Override
    protected ReportCodeEnum getType() {
        return ReportCodeEnum.CKGL;
    }

    @Override
    protected List<LinkedHashMap<String, Object>> assembly(Map<String, Sheet> map) {
        List<LinkedHashMap<String, Object>> all = Lists.newArrayList();
        String date = DateUtils.formatStandardDateTime(new Date());
        //获取资产负债表的期间集合
        Sheet tempSheet = map.get(SheetCodeEnum.zcfzb.getName());
        if (null == tempSheet) {
            return all;
        }

        List<String> periods = tempSheet.yCellNo();
        if (CollectionUtils.isNotEmpty(periods)) {
            for (String period : periods) {
                String type = period.length() == 4 ? "年报" : "月报";
                String periodTemp = period.length() == 4 ? period : period.substring(0, period.lastIndexOf("-"));
                String periodDate = period.length() == 4 ? period + "-12-31" : period;
                for (Rule rule : getType().relySheets().right) {
                    LinkedHashMap<String, Object> out = Maps.newLinkedHashMap();
                    out.put("type", type);
                    out.put("PERIOD", periodTemp);
                    out.put("PERIOD_DATE", periodDate);
                    out.put("Item", rule.getTargetName());
                    out.put("value", RuleParseUtil.value(rule.getExpression(), map, period));
                    out.put("CREATE_DATE", date);
                    out.put("unitcost", null);
                    out.put("unitmain_cost", null);
                    out.put("reporting_rate", null);
                    out.put("resolution_rate", null);

                    if ("EVM0003".equals(rule.getTargetCode())) {
                        out.put("unitcost", out.get("value"));
                    }
                    if ("EVM0004".equals(rule.getTargetCode())) {
                        out.put("unitmain_cost", out.get("value"));
                    }
                    if ("EVM0005".equals(rule.getTargetCode())) {
                        out.put("reporting_rate", out.get("value"));
                    }
                    if ("EVM0006".equals(rule.getTargetCode())) {
                        out.put("resolution_rate", out.get("value"));
                    }
                    all.add(out);
                }
            }
        }
        return all;
    }

}
