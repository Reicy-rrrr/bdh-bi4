package com.deloitte.bdh.data.collation.evm.service.Impl;

import java.math.BigDecimal;

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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产负债效率整体水平表
 * 基于期间变量
 */
@Service(value = "zcfzReportServiceImpl")
public class ZcfzReportServiceImpl extends AbstractReport {
    @Override
    protected ReportCodeEnum getType() {
        return ReportCodeEnum.ZCXLZTSPB;
    }

    @Override
    protected List<LinkedHashMap<String, Object>> assembly(Map<String, Sheet> map) {
        List<LinkedHashMap<String, Object>> all = Lists.newArrayList();
        String date = DateUtils.formatStandardDateTime(new Date());

        //获取资产负债表的期间集合
        Sheet tempSheet = map.get(SheetCodeEnum.zcfzb.getName());
        if (null == tempSheet) {
            return null;
        }

        List<String> periods = tempSheet.yCellNo();
        if (CollectionUtils.isNotEmpty(periods)) {
            Map<String, LinkedHashMap<String, Object>> last = Maps.newHashMap();
            for (String period : periods) {
                String type = period.length() == 4 ? "年报" : "月报";
                String periodTemp = period.length() == 4 ? period : period.substring(0, period.lastIndexOf("-"));
                String periodDate = period.length() == 4 ? period + "-12-31" : period;
                for (Rule rule : getType().relySheets().right) {
                    LinkedHashMap<String, Object> lastReport = last.get(rule.getTargetCode());
                    String ytyValue = null == lastReport ? null : MapUtils.getString(lastReport, "INDEX_VALUE");
                    LinkedHashMap<String, Object> out = new LinkedHashMap<>();
                    out.put("type", type);
                    out.put("PERIOD", periodTemp);
                    out.put("PERIOD_DATE", periodDate);
                    out.put("INDEX_CODE", rule.getTargetCode());
                    out.put("INDEX_NAME", rule.getTargetName());
                    out.put("INDEX_VALUE", RuleParseUtil.value(rule.getExpression(), map, period));
                    out.put("YTY_VALUE", ytyValue);
                    out.put("CREATE_DATE", date);
                    out.put("TOTAL_ASSETS", null);
                    out.put("TOTAL_ASSETS_TURNOVER",null);
                    out.put("CURASSETS_TO_RATE",null);
                    out.put("CURASSETS_TO_DATE",null);

                    if ("EVM0001".equals(rule.getTargetCode())) {
                        out.put("TOTAL_ASSETS", out.get("INDEX_VALUE"));
                    }
                    if ("EVM0012".equals(rule.getTargetCode())) {
                        out.put("TOTAL_ASSETS_TURNOVER", out.get("INDEX_VALUE"));
                    }
                    if ("EVM0031".equals(rule.getTargetCode())) {
                        out.put("CURASSETS_TO_RATE", out.get("INDEX_VALUE"));
                    }
                    if ("EVM0032".equals(rule.getTargetCode())) {
                        out.put("CURASSETS_TO_DATE", out.get("INDEX_VALUE"));
                    }
                    //设置同比
                    setYtyValue(ytyValue, out);
                    last.put(rule.getTargetCode(), out);
                    all.add(out);
                }
            }
        }
        return all;
    }

    private void setYtyValue(String ytyValue, LinkedHashMap<String, Object> out) {
        String indexValue = MapUtils.getString(out, "INDEX_VALUE");
        if (null == ytyValue) {
            out.put("YTY_RATE", null);
        } else {
            if (BigDecimal.ZERO.compareTo(new BigDecimal(indexValue)) == 0 && BigDecimal.ZERO.compareTo(new BigDecimal(ytyValue)) == 0) {
                out.put("YTY_RATE", "0");
            }
            if (BigDecimal.ZERO.compareTo(new BigDecimal(indexValue)) == 0 && BigDecimal.ZERO.compareTo(new BigDecimal(ytyValue)) != 0) {
                out.put("YTY_RATE", "-1");
            }
            if (BigDecimal.ZERO.compareTo(new BigDecimal(indexValue)) != 0 && BigDecimal.ZERO.compareTo(new BigDecimal(ytyValue)) == 0) {
                out.put("YTY_RATE", "1");
            }
            if (BigDecimal.ZERO.compareTo(new BigDecimal(indexValue)) != 0 && BigDecimal.ZERO.compareTo(new BigDecimal(ytyValue)) != 0) {
                String ytyRate = (new BigDecimal(indexValue).subtract(new BigDecimal(ytyValue)))
                        .divide(new BigDecimal(ytyValue), 5, BigDecimal.ROUND_HALF_UP)
                        .toString();
                out.put("YTY_RATE", ytyRate);

            }
        }
    }

}
