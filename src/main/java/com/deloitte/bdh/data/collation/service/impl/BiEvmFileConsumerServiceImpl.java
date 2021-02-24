package com.deloitte.bdh.data.collation.service.impl;

import java.math.BigDecimal;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.AliyunOssUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.evm.enums.SheetCodeEnum;
import com.deloitte.bdh.data.collation.evm.enums.TableMappingEnum;
import com.deloitte.bdh.data.collation.evm.service.EvmServiceImpl;
import com.deloitte.bdh.data.collation.model.BiEvmFile;
import com.deloitte.bdh.data.collation.model.BiReport;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.BiEvmFileConsumerService;
import com.deloitte.bdh.data.collation.service.BiReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.codehaus.jackson.type.TypeReference;
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
    @Autowired
    private EvmServiceImpl evmService;

    @Override
    public void consumer(KafkaMessage<BiEvmFile> message) {
        // 文件信息id
        BiEvmFile evmFile = message.getBody(BiEvmFile.class);
        // 从oss服务器获取文件
        InputStream fileStream = aliyunOss.getFileStream(evmFile.getFilePath(), evmFile.getStoredFileName());
        // 解析文件
        readExcelIntoDb(fileStream, evmFile);
    }

    private void readExcelIntoDb(InputStream fileStream, BiEvmFile evmFile) {
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(fileStream);
        } catch (IOException e) {
            throw new BizException("File read error: 读取文件错误！");
        }

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            if (workbook == null) {
                log.error("读取Excel文件失败，上传文件内容为空!");
                throw new BizException("读取Excel文件失败，上传文件内容不能为空!");
            }
            //todo check
            doZcfzb(workbook.getSheet(SheetCodeEnum.zcfzb.getValue()), evmFile.getBatchId());
            doLrb(workbook.getSheet(SheetCodeEnum.lrb.getValue()), evmFile.getBatchId());
            doXjllb(workbook.getSheet(SheetCodeEnum.xjllb.getValue()), evmFile.getBatchId());
            doKmyeb(workbook.getSheet(SheetCodeEnum.kmyeb.getValue()), evmFile.getBatchId());

            List<String> tables = JsonUtil.string2Obj(evmFile.getTables(), new TypeReference<List<String>>() {
            });
            List<ImmutablePair<TableMappingEnum, String>> enums = TableMappingEnum.get(tables);
            for (ImmutablePair<TableMappingEnum, String> pair : enums) {
                evmService.choose(pair.left.getValue().getName(), pair.right);
            }
        } catch (IOException | InvalidFormatException e) {
            log.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        }
    }

    private void doZcfzb(Sheet sheet, String batchId) {
        if (sheet == null) {
            throw new BizException("读取Excel文件失败，上传文件内容不能为空!");
        }
        //获取类型
        Cell typeCell = sheet.getRow(0).getCell(3);
        if (null == typeCell) {
            throw new BizException("未获取到报表期间类型");
        }

        reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.zcfzb.getName()));

        //获取期间列数
        String type = typeCell.getStringCellValue();
        int colNums = sheet.getRow(1).getLastCellNum();

        //循环
        List<BiReport> list = Lists.newArrayList();

        for (int row = 2; row < sheet.getLastRowNum(); row++) {
            List<BiReport> tempList = Lists.newArrayList();
            Row indexRow = sheet.getRow(row);
            if (null == indexRow) {
                continue;
            }
            Cell indexCell = indexRow.getCell(0);
            if (null == indexCell) {
                continue;
            }
            String indexCode = indexCell.getStringCellValue();
            if (StringUtils.isBlank(indexCode)) {
                continue;
            }
            for (int cell = 2; cell < colNums; cell++) {
                BiReport biReport = new BiReport();
                biReport.setBatchId(batchId);
                biReport.setReportCode(SheetCodeEnum.zcfzb.getName());
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

                processZcfzAvg(tempList, indexCode, biReport, row, cell, sheet);
                tempList.add(biReport);
            }
            list.addAll(tempList);
        }
        reportService.saveBatch(list);
    }

    private void doLrb(Sheet sheet, String batchId) {
        if (sheet == null) {
            throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
        }
        //获取类型
        Cell typeCell = sheet.getRow(0).getCell(3);
        if (null == typeCell) {
            throw new BizException("未获取到报表期间类型");
        }
        reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.lrb.getName()));

        //获取期间列数
        String type = typeCell.getStringCellValue();
        int colNums = sheet.getRow(1).getLastCellNum();

        //循环
        List<BiReport> list = Lists.newArrayList();

        for (int row = 2; row < sheet.getLastRowNum() + 1; row++) {
            List<BiReport> tempList = Lists.newArrayList();
            Row indexRow = sheet.getRow(row);
            if (null == indexRow) {
                continue;
            }
            Cell indexCell = indexRow.getCell(0);
            if (null == indexCell) {
                continue;
            }
            String indexCode = indexCell.getStringCellValue();
            if (StringUtils.isBlank(indexCode)) {
                continue;
            }
            for (int cell = 2; cell < colNums; cell++) {
                BiReport biReport = new BiReport();
                biReport.setBatchId(batchId);
                biReport.setReportCode(SheetCodeEnum.lrb.name());
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
                tempList.add(biReport);
            }
            list.addAll(tempList);
        }
        reportService.saveBatch(list);
    }

    private void doXjllb(Sheet sheet, String batchId) {
        if (sheet == null) {
            throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
        }
        //获取类型
        Cell typeCell = sheet.getRow(0).getCell(3);
        if (null == typeCell) {
            throw new BizException("未获取到报表期间类型");
        }
        reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.xjllb.getName()));

        //获取期间列数
        String type = typeCell.getStringCellValue();
        int colNums = sheet.getRow(1).getLastCellNum();

        //循环
        List<BiReport> list = Lists.newArrayList();

        for (int row = 2; row < sheet.getLastRowNum() + 1; row++) {
            List<BiReport> tempList = Lists.newArrayList();
            Row indexRow = sheet.getRow(row);
            if (null == indexRow) {
                continue;
            }
            Cell indexCell = indexRow.getCell(0);
            if (null == indexCell) {
                continue;
            }
            String indexCode = indexCell.getStringCellValue();
            if (StringUtils.isBlank(indexCode)) {
                continue;
            }
            for (int cell = 2; cell < colNums; cell++) {
                BiReport biReport = new BiReport();
                biReport.setBatchId(batchId);
                biReport.setReportCode(SheetCodeEnum.xjllb.name());
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
                tempList.add(biReport);
            }
            list.addAll(tempList);
        }
        reportService.saveBatch(list);
    }

    private void doKmyeb(Sheet sheet, String batchId) {
        if (sheet == null) {
            throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
        }
        //获取类型
        Cell typeCell = sheet.getRow(0).getCell(4);
        if (null == typeCell) {
            throw new BizException("未获取到报表期间类型");
        }
        reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.kmyeb.getName()));

        //获取期间列数
        String type = typeCell.getStringCellValue();
        int colNums = sheet.getRow(1).getLastCellNum();

        //循环
        List<BiReport> list = Lists.newArrayList();

        for (int row = 2; row < sheet.getLastRowNum() + 1; row++) {
            List<BiReport> tempList = Lists.newArrayList();
            Row indexRow = sheet.getRow(row);
            if (null == indexRow) {
                continue;
            }
            Cell indexCell = indexRow.getCell(0);
            if (null == indexCell) {
                continue;
            }
            String indexCode = indexCell.getStringCellValue();
            if (StringUtils.isBlank(indexCode)) {
                continue;
            }
            for (int cell = 3; cell < colNums; cell++) {
                BiReport biReport = new BiReport();
                biReport.setBatchId(batchId);
                biReport.setReportCode(SheetCodeEnum.kmyeb.name());
                biReport.setReportName(sheet.getSheetName());
                biReport.setRowNo(String.valueOf(row));
                biReport.setIndexCode(indexCode);
                biReport.setCell1(sheet.getRow(row).getCell(2).getStringCellValue());
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
                tempList.add(biReport);
            }
            list.addAll(tempList);
        }
        reportService.saveBatch(list);
    }


    private void processZcfzAvg(List<BiReport> tempList, String indexCode, BiReport biReport, int row, int cell, Sheet sheet) {
        if ("EVMB001".equals(indexCode)) {
            BiReport evmb078 = new BiReport();
            BeanUtils.copyProperties(biReport, evmb078);
            evmb078.setIndexCode("EVMB001_AVG");
            evmb078.setCell1("营业收入余额");
            if (2 < cell) {
                Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                        .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                tempList.add(evmb078);
            }
        }

        if ("EVMB002".equals(indexCode)) {
            BiReport evmb078 = new BiReport();
            BeanUtils.copyProperties(biReport, evmb078);
            evmb078.setIndexCode("EVMB002_AVG");
            evmb078.setCell1("营业成本余额");
            if (2 < cell) {
                Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                        .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                tempList.add(evmb078);
            }
        }
        //期初资金总额余额
        if ("EVMB003".equals(indexCode)) {
            BiReport evmb078 = new BiReport();
            BeanUtils.copyProperties(biReport, evmb078);
            evmb078.setIndexCode("EVMB003_AVG");
            evmb078.setCell1("期初资金总额余额");
            if (2 < cell) {
                Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                        .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                tempList.add(evmb078);
            }
        }

        if ("EVMB004".equals(indexCode)) {
            BiReport evmb078 = new BiReport();
            BeanUtils.copyProperties(biReport, evmb078);
            evmb078.setIndexCode("EVMB004_AVG");
            evmb078.setCell1("期末资金总额余额");
            if (2 < cell) {
                Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                        .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                tempList.add(evmb078);
            }
        }

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

        if ("EVMB017".equals(indexCode)) {
            BiReport evmb078 = new BiReport();
            BeanUtils.copyProperties(biReport, evmb078);
            evmb078.setIndexCode("EVMB017_AVG");
            evmb078.setCell1("平均流动资产");
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

        if ("EVMB055".equals(indexCode)) {
            BiReport evmb078 = new BiReport();
            BeanUtils.copyProperties(biReport, evmb078);
            evmb078.setIndexCode("EVMB055_AVG");
            evmb078.setCell1("平均流动负债");
            if (2 < cell) {
                Cell lastTemp = sheet.getRow(row).getCell(cell - 1);
                String lastTempValue = null == lastTemp ? "0" : lastTemp.getNumericCellValue() + "";
                evmb078.setCell2((new BigDecimal(lastTempValue).add(new BigDecimal(biReport.getCell2())))
                        .divide(new BigDecimal("2"), 5, BigDecimal.ROUND_HALF_UP).toString());
                tempList.add(evmb078);
            }
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
    }

}
