package com.deloitte.bdh.data.collation.service.impl;

import java.math.BigDecimal;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.AliyunOssUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.model.BiEvmFile;
import com.deloitte.bdh.data.collation.model.BiReport;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.BiEvmFileConsumerService;
import com.deloitte.bdh.data.collation.service.BiReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
@DS(DSConstant.BI_DB)
public class BiEvmFileConsumerServiceImpl implements BiEvmFileConsumerService {
    @Autowired
    private AliyunOssUtil aliyunOss;
    @Resource
    private BiReportService reportService;

    @Override
    public void consumer(KafkaMessage<BiEvmFile> message) {
        log.info("BiEvmFileConsumerServiceImpl.begin :  body:" + message.getBody());
        // 文件信息id
        BiEvmFile evmFile = message.getBody(BiEvmFile.class);

        // 从oss服务器获取文件
        InputStream fileStream = aliyunOss.getFileStream(evmFile.getFilePath(), evmFile.getStoredFileName());
        // 读取文件
        readIntoDb(fileStream, evmFile.getBatchId());
        // 设置文件的关联数据源id
        //todo 解析文件
    }

    public void readIntoDb(InputStream stream, String batchId) {
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(stream);
        } catch (IOException e) {
            throw new BizException("File read error: 读取文件错误！");
        }
        readExcelIntoDb(bytes, batchId);
    }

    private void readExcelIntoDb(byte[] bytes, String batchId) {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            if (workbook == null) {
                log.error("读取Excel文件失败，上传文件内容为空！");
                throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
            }
            doZcfzb(workbook.getSheet("资产负债表"), batchId);
            Sheet dataSheet = workbook.getSheetAt(0);
            // poi 行号从0开始
            if (dataSheet == null || (dataSheet.getLastRowNum()) <= 0) {
                log.error("读取Excel文件失败，上传文件内容为空！");
                throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
            }
        } catch (IOException e) {
            log.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        } catch (InvalidFormatException e) {
            log.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        }
    }

    private void doZcfzb(Sheet sheet, String batchId) {
        int rowNos = sheet.getLastRowNum();
        if (rowNos != 82) {
            throw new BizException("资产负债表行数不正确");
        }
        //获取类型
        Cell typeCell = sheet.getRow(0).getCell(3);
        if (null == typeCell) {
            throw new BizException("未获取到报表类型");
        }
        String type = typeCell.getStringCellValue();

        //获取期间列数
        int colNums = sheet.getRow(1).getLastCellNum();

        //循环
        List<BiReport> list = Lists.newArrayList();

        for (int row = 2; row < sheet.getLastRowNum() + 1; row++) {
            List<BiReport> tempList = Lists.newArrayList();
            String indexCode = sheet.getRow(row).getCell(0).getStringCellValue();
            for (int cell = 2; cell < colNums; cell++) {
                BiReport biReport = new BiReport();
                biReport.setBatchId(batchId);
                biReport.setReportCode("zcfz");
                biReport.setReportName(sheet.getSheetName());
                biReport.setRowNo(String.valueOf(row));
                biReport.setIndexCode(indexCode);
                biReport.setCell1(sheet.getRow(row).getCell(1).getStringCellValue());
                biReport.setTenantId(ThreadLocalHolder.getTenantId());
                biReport.setColNo(String.valueOf(cell));
                Cell temp = sheet.getRow(row).getCell(cell);
                String tempValue = null == temp ? "0" : temp.getNumericCellValue() + "";
                biReport.setCell2(tempValue);
                if ("年报".equals(type)) {
                    biReport.setPeriod(DateUtils.stampToDateOfYear(sheet.getRow(1).getCell(cell).getDateCellValue()));
                } else {
                    biReport.setPeriod(DateUtils.formatStandardDate(sheet.getRow(1).getCell(cell).getDateCellValue()));
                }
                //处理平均净资产
                if ("EVMB080".equals(indexCode)) {
                    BiReport evmb078 = new BiReport();
                    BeanUtils.copyProperties(biReport, evmb078);
                    evmb078.setIndexCode("EVMB080_AVG");
                    evmb078.setCell1("平均净资产");
                    if (2 < cell) {
                        Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                        String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                        evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                                .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                        tempList.add(evmb078);
                    }
                }
                //应收账款平均余额
                if ("EVMB008".equals(indexCode)) {
                    BiReport evmb078 = new BiReport();
                    BeanUtils.copyProperties(biReport, evmb078);
                    evmb078.setIndexCode("EVMB008_AVG");
                    evmb078.setCell1("应收账款平均余额");
                    if (2 < cell) {
                        Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                        String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                        evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                                .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                        tempList.add(evmb078);
                    }
                }

                //固定资产平均余额
                if ("EVMB027".equals(indexCode)) {
                    BiReport evmb078 = new BiReport();
                    BeanUtils.copyProperties(biReport, evmb078);
                    evmb078.setIndexCode("EVMB027_AVG");
                    evmb078.setCell1("固定资产平均余额");
                    if (2 < cell) {
                        Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                        String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                        evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                                .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                        tempList.add(evmb078);
                    }
                }

                //平均存货
                if ("EVMB012".equals(indexCode)) {
                    BiReport evmb078 = new BiReport();
                    BeanUtils.copyProperties(biReport, evmb078);
                    evmb078.setIndexCode("EVMB012_AVG");
                    evmb078.setCell1("平均存货");
                    if (2 < cell) {
                        Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                        String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                        evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                                .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                        tempList.add(evmb078);
                    }
                }
                //总资产平均水平
                if ("EVMB039".equals(indexCode)) {
                    BiReport evmb078 = new BiReport();
                    BeanUtils.copyProperties(biReport, evmb078);
                    evmb078.setIndexCode("EVMB039_AVG");
                    evmb078.setCell1("总资产平均水平");
                    if (2 < cell) {
                        Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                        String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                        evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                                .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                        tempList.add(evmb078);
                    }
                }

                tempList.add(biReport);
            }
            list.addAll(tempList);
        }
        reportService.saveBatch(list);
    }
}
