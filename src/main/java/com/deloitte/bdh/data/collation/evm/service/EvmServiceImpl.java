package com.deloitte.bdh.data.collation.evm.service;

import com.deloitte.bdh.data.collation.evm.enums.ReportCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EvmServiceImpl {
    @Resource
    private ReportService zcfzReportService;

    public void choose(String reportCode) {
        ReportCodeEnum reportCodeEnum = ReportCodeEnum.valueOf(reportCode);
        switch (reportCodeEnum) {
            case ZCXLZTSPB:
                zcfzReportService.process();
                break;
            default:
                ;
        }
    }
}
