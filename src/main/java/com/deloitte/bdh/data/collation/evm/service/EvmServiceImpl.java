package com.deloitte.bdh.data.collation.evm.service;

import com.deloitte.bdh.data.collation.evm.enums.ReportCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EvmServiceImpl {
    @Resource(name = "zcfzReportServiceImpl")
    private ReportService zcfzReportService;
    @Resource(name = "zjbReportServiceImpl")
    private ReportService zjbReportService;
    @Resource(name = "zcgcReportServiceImpl")
    private ReportService zcgcReportService;
    @Resource(name = "ckglReportServiceImpl")
    private ReportService ckglReportServiceImpl;

    public void choose(String reportCode, String tableName) {
        ReportCodeEnum reportCodeEnum = ReportCodeEnum.valueOf(reportCode);
        switch (reportCodeEnum) {
            case ZCXLZTSPB:
                zcfzReportService.process(tableName);
                break;
            case ZJB:
                zjbReportService.process(tableName);
                break;
            case ZCGCB:
                zcgcReportService.process(tableName);
                break;
            case CKGL:
                ckglReportServiceImpl.process(tableName);
                break;
            default:
                ;
        }
    }
}
