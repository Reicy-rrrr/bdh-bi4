package com.deloitte.bdh.data.collation.evm.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.data.collation.evm.dto.Rule;
import com.deloitte.bdh.data.collation.evm.dto.Sheet;
import com.deloitte.bdh.data.collation.evm.enums.ReportCodeEnum;
import com.deloitte.bdh.data.collation.evm.utils.RuleParseUtil;
import com.deloitte.bdh.data.collation.model.BiReportOut;
import com.deloitte.bdh.data.collation.service.BiReportOutService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
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
    private BiReportOutService outService;

    @Override
    protected ReportCodeEnum getType() {
        return ReportCodeEnum.ZCXLZTSPB;
    }

    @Override
    protected List<LinkedHashMap<String, Object>> assembly(Map<String, Sheet> map) {
        //获取资产负债表的期间集合
        List<String> periods = map.get("zcfz").yCellNo();
        if (CollectionUtils.isNotEmpty(periods)) {
            List<BiReportOut> all = Lists.newArrayList();
            Map<String, BiReportOut> last = Maps.newHashMap();
            for (String period : periods) {
                String periodDate = period.length() == 4 ? "年报" : "月报";
                for (Rule rule : getType().relySheets().right) {
                    BiReportOut lastReport = last.get(rule.getTargetCode());
                    String ytyValue = null == lastReport ? null : lastReport.getIndexValue();
                    BiReportOut out = new BiReportOut();
                    out.setPeriod(period);
                    out.setPeriodDate(periodDate);
                    out.setReportName(getType().getValue());
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
                    out.setCreateDate(LocalDateTime.now());
                    out.setCreateUser("1");
                    out.setTenantId("1");
                    last.put(out.getIndexCode(), out);
                    all.add(out);
                }
            }
            outService.saveBatch(all);
        }
        return null;
    }
}
