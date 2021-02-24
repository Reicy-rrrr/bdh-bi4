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
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产构成
 * 基于期间变量
 */
@Service(value = "zcgcReportServiceImpl")
public class ZcgcReportServiceImpl extends AbstractReport {
    @Override
    protected ReportCodeEnum getType() {
        return ReportCodeEnum.ZCGCB;
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
            Map<String, LinkedHashMap<String, Object>> last = Maps.newHashMap();
            for (String period : periods) {
                String type = period.length() == 4 ? "年报" : "月报";
                String periodTemp = period.length() == 4 ? period : period.substring(0, period.lastIndexOf("-"));
                String periodDate = period.length() == 4 ? period + "-12-31" : period;
                String EVMB080 = tempSheet.xCellValue("EVMB080", period);
                String EVMB039 = tempSheet.xCellValue("EVMB039", period);
                String EVMB068 = tempSheet.xCellValue("EVMB068", period);

                for (Rule rule : getType().relySheets().right) {
                    LinkedHashMap<String, Object> out = Maps.newLinkedHashMap();
                    out.put("type", type);
                    out.put("PERIOD", periodTemp);
                    out.put("PERIOD_DATE", periodDate);
                    out.put("INDEX_CODE", rule.getTargetCode());
                    out.put("INDEX_NAME", rule.getTargetName());
                    out.put("INDEX_VALUE", RuleParseUtil.value(rule.getExpression(), map, period));
                    out.put("CREATE_DATE", date);
                    out.put("VALUE1", setValue(EVMB080, MapUtils.getString(out, "INDEX_VALUE")));
                    out.put("VALUE2", setValue(EVMB039, MapUtils.getString(out, "INDEX_VALUE")));
                    out.put("VALUE3", setValue(EVMB068, MapUtils.getString(out, "INDEX_VALUE")));
                    last.put(rule.getTargetCode(), out);
                    all.add(out);
                }
            }
        }
        return all;
    }

    private String setValue(String evm, String value) {
        if (null == value) {
            return null;
        }
        if (BigDecimal.ZERO.compareTo(new BigDecimal(value)) == 0) {
            return "0";
        }
        return (new BigDecimal(value).divide(new BigDecimal(evm), 5, BigDecimal.ROUND_HALF_UP)).toString();
    }

}
