package com.deloitte.bdh.data.collation.evm.service.Impl;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.data.collation.evm.dto.Rule;
import com.deloitte.bdh.data.collation.evm.dto.Sheet;
import com.deloitte.bdh.data.collation.evm.enums.ReportCodeEnum;
import com.deloitte.bdh.data.collation.evm.enums.SheetCodeEnum;
import com.deloitte.bdh.data.collation.evm.service.AbstractReport;
import com.deloitte.bdh.data.collation.evm.utils.RuleParseUtil;
import com.deloitte.bdh.data.collation.model.EvmCapanalysisSum;
import com.deloitte.bdh.data.collation.service.EvmCapanalysisSumService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产负债效率整体水平表
 * 基于期间变量
 */
@Service(value = "zcfzReportServiceImpl")
public class ZcfzReportServiceImpl extends AbstractReport {
    @Resource
    private EvmCapanalysisSumService sumService;

    @Override
    protected ReportCodeEnum getType() {
        return ReportCodeEnum.ZCXLZTSPB;
    }

    @Override
    protected void clear() {
        sumService.remove(new LambdaQueryWrapper<EvmCapanalysisSum>().last("where 1=1"));
    }

    @Override
    protected List<LinkedHashMap<String, Object>> assembly(Map<String, Sheet> map) {
        //获取资产负债表的期间集合
        Sheet tempSheet = map.get(SheetCodeEnum.zcfzb.getName());
        if (null == tempSheet) {
            return null;
        }

        List<String> periods = tempSheet.yCellNo();
        if (CollectionUtils.isNotEmpty(periods)) {
            List<EvmCapanalysisSum> all = Lists.newArrayList();
            Map<String, EvmCapanalysisSum> last = Maps.newHashMap();
            for (String period : periods) {
                String type = period.length() == 4 ? "年报" : "月报";
                String periodTemp = period.length() == 4 ? period : period.substring(0, period.lastIndexOf("-"));
                String periodDate = period.length() == 4 ? period + "-12-31" : period;
                for (Rule rule : getType().relySheets().right) {
                    EvmCapanalysisSum lastReport = last.get(rule.getTargetCode());
                    String ytyValue = null == lastReport ? null : lastReport.getIndexValue();
                    EvmCapanalysisSum out = new EvmCapanalysisSum();
                    out.setType(type);
                    out.setPeriod(periodTemp);
                    out.setPeriodDate(periodDate);
                    out.setIndexCode(rule.getTargetCode());
                    out.setIndexName(rule.getTargetName());
                    out.setIndexValue(RuleParseUtil.value(rule.getExpression(), map, period));
                    out.setYtyValue(ytyValue);
                    //设置比率
                    if (null == ytyValue) {
                        out.setYtyRate(null);
                    } else {
                        if (BigDecimal.ZERO.compareTo(new BigDecimal(out.getIndexValue())) == 0 && BigDecimal.ZERO.compareTo(new BigDecimal(ytyValue)) == 0) {
                            out.setYtyRate("0");
                        }
                        if (BigDecimal.ZERO.compareTo(new BigDecimal(out.getIndexValue())) == 0 && BigDecimal.ZERO.compareTo(new BigDecimal(ytyValue)) != 0) {
                            out.setYtyRate("-100");
                        }
                        if (BigDecimal.ZERO.compareTo(new BigDecimal(out.getIndexValue())) != 0 && BigDecimal.ZERO.compareTo(new BigDecimal(ytyValue)) == 0) {
                            out.setYtyRate("100");
                        }
                        if (BigDecimal.ZERO.compareTo(new BigDecimal(out.getIndexValue())) != 0 && BigDecimal.ZERO.compareTo(new BigDecimal(ytyValue)) != 0) {
                            String ytyRate = (new BigDecimal(out.getIndexValue()).subtract(new BigDecimal(ytyValue)))
                                    .divide(new BigDecimal(ytyValue), 5, BigDecimal.ROUND_HALF_UP)
                                    .multiply(new BigDecimal("100"))
                                    .toString();
                            out.setYtyRate(ytyRate);
                        }
                    }
                    last.put(out.getIndexCode(), out);
                    all.add(out);
                }
            }
            sumService.saveBatch(all);
        }
        return null;
    }
}
