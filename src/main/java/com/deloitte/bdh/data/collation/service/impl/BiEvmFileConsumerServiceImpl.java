package com.deloitte.bdh.data.collation.service.impl;

import java.math.BigDecimal;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.AliyunOssUtil;
import com.deloitte.bdh.common.util.ExcelUtils;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.evm.enums.SheetCodeEnum;
import com.deloitte.bdh.data.collation.evm.enums.TableMappingEnum;
import com.deloitte.bdh.data.collation.evm.service.EvmServiceImpl;
import com.deloitte.bdh.data.collation.model.BiEvmFile;
import com.deloitte.bdh.data.collation.model.BiReport;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.BiEvmFileConsumerService;
import com.deloitte.bdh.data.collation.service.BiReportService;
import com.google.common.collect.Maps;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    protected DbHandler dbHandler;
    @Resource
    private LocaleMessageService localeMessageService;

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
            throw new BizException(ResourceMessageEnum.EVM_1.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EVM_1.getMessage(), ThreadLocalHolder.getLang()));
        }

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            if (workbook == null) {
                log.error("读取Excel文件失败，上传文件内容为空!");
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            List<String> tables = new ArrayList<>(Arrays.asList(evmFile.getTables().split(",")));

            //中间表处理
            doZcfzb(workbook.getSheet(SheetCodeEnum.zcfzb.getValue()), evmFile.getBatchId());
            doLrb(workbook.getSheet(SheetCodeEnum.lrb.getValue()), evmFile.getBatchId());
            doXjllb(workbook.getSheet(SheetCodeEnum.xjllb.getValue()), evmFile.getBatchId());
            doKmyeb(workbook.getSheet(SheetCodeEnum.kmyeb.getValue()), evmFile.getBatchId());
            doCkgl(workbook.getSheet(SheetCodeEnum.ckgl.getValue()), evmFile.getBatchId());

            doYszkb(TableMappingEnum.getTableNameByEnum(tables, TableMappingEnum.EVM_CAPANALYSIS_AR),
                    workbook.getSheet(SheetCodeEnum.yszkb.getValue()), evmFile.getBatchId());
            doYfzkb(TableMappingEnum.getTableNameByEnum(tables, TableMappingEnum.EVM_CAPANALYSIS_AP),
                    workbook.getSheet(SheetCodeEnum.yfzkb.getValue()), evmFile.getBatchId());
            doChmxb(TableMappingEnum.getTableNameByEnum(tables, TableMappingEnum.EVM_CAPANALYSIS_INVENTORY),
                    workbook.getSheet(SheetCodeEnum.chmxb.getValue()), evmFile.getBatchId());
            doGdzczjb(TableMappingEnum.getTableNameByEnum(tables, TableMappingEnum.EVM_CAPANALYSIS_DEPRECIATION),
                    workbook.getSheet(SheetCodeEnum.gdzczjb.getValue()), evmFile.getBatchId());
            doJkbSum(TableMappingEnum.getTableNameByEnum(tables, TableMappingEnum.EVM_CAPANALYSIS_LOAN_SUM),
                    workbook.getSheet(SheetCodeEnum.jkb.getValue()), evmFile.getBatchId());
            doJkbCycle(TableMappingEnum.getTableNameByEnum(tables, TableMappingEnum.EVM_CAPANALYSIS_LOAN_CYCLE),
                    workbook.getSheet(SheetCodeEnum.jkb.getValue()), evmFile.getBatchId());
            doJtzjgl(TableMappingEnum.getTableNameByEnum(tables, TableMappingEnum.EVM_CAPANALYSIS_CAPITAL),
                    workbook.getSheet(SheetCodeEnum.jtzjgl.getValue()), evmFile.getBatchId());
            //三大报表处理
            List<ImmutablePair<TableMappingEnum, String>> enums = TableMappingEnum.get(tables);
            for (ImmutablePair<TableMappingEnum, String> pair : enums) {
                evmService.choose(pair.left.getValue().getName(), pair.right);
            }
        } catch (IOException | InvalidFormatException e) {
            log.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException(ResourceMessageEnum.EVM_3.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EVM_3.getMessage(), ThreadLocalHolder.getLang()));
        }
    }

    private void doZcfzb(Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(3);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }

            reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.zcfzb.getName()));

            //获取期间列数
            String type = typeCell.getStringCellValue();
            int colNums = sheet.getRow(1).getLastCellNum();

            //循环
            List<BiReport> list = Lists.newArrayList();

            for (int row = 2; row <= sheet.getLastRowNum(); row++) {
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
                    String tempValue = ExcelUtils.getNumericCellValueDefault(temp);
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doLrb(Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(3);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.lrb.getName()));

            //获取期间列数
            String type = typeCell.getStringCellValue();
            int colNums = sheet.getRow(1).getLastCellNum();

            //循环
            List<BiReport> list = Lists.newArrayList();

            for (int row = 2; row <= sheet.getLastRowNum() + 1; row++) {
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
                    String tempValue = ExcelUtils.getNumericCellValueDefault(temp);
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doXjllb(Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(3);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.xjllb.getName()));

            //获取期间列数
            String type = typeCell.getStringCellValue();
            int colNums = sheet.getRow(1).getLastCellNum();

            //循环
            List<BiReport> list = Lists.newArrayList();

            for (int row = 2; row <= sheet.getLastRowNum() + 1; row++) {
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
                    String tempValue = ExcelUtils.getNumericCellValueDefault(temp);
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doKmyeb(Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(4);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.kmyeb.getName()));

            //获取期间列数
            String type = typeCell.getStringCellValue();
            int colNums = sheet.getRow(1).getLastCellNum();

            //循环
            List<BiReport> list = Lists.newArrayList();

            for (int row = 2; row <= sheet.getLastRowNum() + 1; row++) {
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
                    String tempValue = ExcelUtils.getNumericCellValueDefault(temp);
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doYszkb(String tableName, Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(4);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            String type = typeCell.getStringCellValue();
            String date = DateUtils.formatStandardDateTime(new Date());

            List<LinkedHashMap<String, Object>> all = Lists.newArrayList();
            Map<String, BigDecimal> total360 = Maps.newHashMap();
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (null == row || null == row.getCell(0)) {
                    continue;
                }
                String period = DateUtils.stampToDateOfYear(row.getCell(0).getDateCellValue());

                LinkedHashMap<String, Object> map30 = Maps.newLinkedHashMap();
                map30.put("type", type);
                map30.put("PERIOD", period);
                map30.put("PERIOD_DATE", DateUtils.formatStandardDate(row.getCell(0).getDateCellValue()));
                map30.put("VENDOR_ID", row.getCell(1).getStringCellValue());
                map30.put("VENDOR_NAME", row.getCell(2).getStringCellValue());
                map30.put("SALE_CHANNEL", row.getCell(3).getStringCellValue());
                map30.put("CYCLE_TIME", "<=30天");
                map30.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(4)));
                map30.put("CREATE_DATE", date);

                LinkedHashMap<String, Object> map3060 = Maps.newLinkedHashMap(map30);
                map3060.put("CYCLE_TIME", "30-60天");
                map3060.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(5)));

                LinkedHashMap<String, Object> map6090 = Maps.newLinkedHashMap(map30);
                map6090.put("CYCLE_TIME", "60-90天");
                map6090.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(6)));

                LinkedHashMap<String, Object> map90180 = Maps.newLinkedHashMap(map30);
                map90180.put("CYCLE_TIME", "90-180天");
                map90180.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(7)));

                LinkedHashMap<String, Object> map180365 = Maps.newLinkedHashMap(map30);
                map180365.put("CYCLE_TIME", "180-365天");
                map180365.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(8)));

                LinkedHashMap<String, Object> map365 = Maps.newLinkedHashMap(map30);
                map365.put("CYCLE_TIME", "一年以上");
                map365.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(9)));
                all.add(map30);
                all.add(map3060);
                all.add(map6090);
                all.add(map90180);
                all.add(map180365);
                all.add(map365);

                if (total360.containsKey(period)) {
                    total360.put(period, total360.get(period).add(new BigDecimal(ExcelUtils.getNumericCellValueDefault(row.getCell(9)))));
                } else {
                    total360.put(period, new BigDecimal(ExcelUtils.getNumericCellValueDefault(row.getCell(9))));
                }
            }
            //处理入库
            process(all, tableName);

            //生成 360以上数据
            reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.yszkb.getName()));
            List<BiReport> tempList = Lists.newArrayList();
            for (Map.Entry<String, BigDecimal> entry : total360.entrySet()) {
                BiReport biReport = new BiReport();
                biReport.setBatchId(batchId);
                biReport.setReportCode(SheetCodeEnum.yszkb.name());
                biReport.setReportName(sheet.getSheetName());
                biReport.setRowNo("0");
                biReport.setIndexCode("YS_TOTAL");
                biReport.setCell1("应收一年以上合计");
                biReport.setTenantId(ThreadLocalHolder.getTenantId());
                biReport.setColNo("0");
                if ("年报".equals(type)) {
                    biReport.setPeriod(entry.getKey());
                } else {
                    biReport.setPeriod(entry.getKey() + "-12-31");
                }
                biReport.setCell2(entry.getValue().setScale(5, BigDecimal.ROUND_HALF_UP).toString());
                tempList.add(biReport);
            }
            reportService.saveBatch(tempList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doYfzkb(String tableName, Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(4);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            String type = typeCell.getStringCellValue();
            String date = DateUtils.formatStandardDateTime(new Date());

            List<LinkedHashMap<String, Object>> all = Lists.newArrayList();
            Map<String, BigDecimal> total360 = Maps.newHashMap();
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (null == row || null == row.getCell(0)) {
                    continue;
                }
                String period = DateUtils.stampToDateOfYear(row.getCell(0).getDateCellValue());

                LinkedHashMap<String, Object> map30 = Maps.newLinkedHashMap();
                map30.put("type", type);
                map30.put("PERIOD", period);
                map30.put("PERIOD_DATE", DateUtils.formatStandardDate(row.getCell(0).getDateCellValue()));
                map30.put("VENDOR_ID", row.getCell(1).getStringCellValue());
                map30.put("VENDOR_NAME", row.getCell(2).getStringCellValue());
                map30.put("CYCLE_TIME", "<=30天");
                map30.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(3)));
                map30.put("CREATE_DATE", date);

                LinkedHashMap<String, Object> map3060 = Maps.newLinkedHashMap(map30);
                map3060.put("CYCLE_TIME", "30-60天");
                map3060.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(4)));

                LinkedHashMap<String, Object> map6090 = Maps.newLinkedHashMap(map30);
                map6090.put("CYCLE_TIME", "60-90天");
                map6090.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(5)));

                LinkedHashMap<String, Object> map90180 = Maps.newLinkedHashMap(map30);
                map90180.put("CYCLE_TIME", "90-180天");
                map90180.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(6)));

                LinkedHashMap<String, Object> map180365 = Maps.newLinkedHashMap(map30);
                map180365.put("CYCLE_TIME", "180-365天");
                map180365.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(7)));

                LinkedHashMap<String, Object> map365 = Maps.newLinkedHashMap(map30);
                map365.put("CYCLE_TIME", "一年以上");
                map365.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(8)));
                all.add(map30);
                all.add(map3060);
                all.add(map6090);
                all.add(map90180);
                all.add(map180365);
                all.add(map365);

                if (total360.containsKey(period)) {
                    total360.put(period, total360.get(period).add(new BigDecimal(ExcelUtils.getNumericCellValueDefault(row.getCell(8)))));
                } else {
                    total360.put(period, new BigDecimal(ExcelUtils.getNumericCellValueDefault(row.getCell(8))));
                }
            }
            //处理入库
            process(all, tableName);

            //生成 360以上数据
            reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.yfzkb.getName()));
            List<BiReport> tempList = Lists.newArrayList();
            for (Map.Entry<String, BigDecimal> entry : total360.entrySet()) {
                BiReport biReport = new BiReport();
                biReport.setBatchId(batchId);
                biReport.setReportCode(SheetCodeEnum.yfzkb.name());
                biReport.setReportName(sheet.getSheetName());
                biReport.setRowNo("0");
                biReport.setIndexCode("YF_TOTAL");
                biReport.setCell1("应付一年以上合计");
                biReport.setTenantId(ThreadLocalHolder.getTenantId());
                biReport.setColNo("0");
                if ("年报".equals(type)) {
                    biReport.setPeriod(entry.getKey());
                } else {
                    biReport.setPeriod(entry.getKey() + "-12-31");
                }
                biReport.setCell2(entry.getValue().setScale(5, BigDecimal.ROUND_HALF_UP).toString());
                tempList.add(biReport);
            }
            reportService.saveBatch(tempList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void doChmxb(String tableName, Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(4);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            String type = typeCell.getStringCellValue();
            String date = DateUtils.formatStandardDateTime(new Date());

            List<LinkedHashMap<String, Object>> all = Lists.newArrayList();
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (null == row || null == row.getCell(0)) {
                    continue;
                }
                String period = DateUtils.stampToDateOfYear(row.getCell(0).getDateCellValue());

                LinkedHashMap<String, Object> map30 = Maps.newLinkedHashMap();
                map30.put("type", type);
                map30.put("PERIOD", period);
                map30.put("PERIOD_DATE", DateUtils.formatStandardDate(row.getCell(0).getDateCellValue()));
                map30.put("CLASS", row.getCell(1).getStringCellValue());
                map30.put("INVENTORY_ID", row.getCell(2).getStringCellValue());
                map30.put("INVENTORY_NAME", row.getCell(3).getStringCellValue());
                map30.put("BEGINNING_VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(4)));
                map30.put("ADD_VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(5)));
                map30.put("OUT_VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(6)));
                map30.put("ENDING_VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(7)));
                map30.put("CREATE_DATE", date);
                all.add(map30);
            }
            //处理入库
            process(all, tableName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doCkgl(Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(3);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            reportService.remove(new LambdaQueryWrapper<BiReport>().eq(BiReport::getReportCode, SheetCodeEnum.ckgl.getName()));

            //获取期间列数
            String type = typeCell.getStringCellValue();
            int colNums = sheet.getRow(1).getLastCellNum();

            //循环
            List<BiReport> list = Lists.newArrayList();

            for (int row = 2; row <= sheet.getLastRowNum() + 1; row++) {
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
                    biReport.setReportCode(SheetCodeEnum.ckgl.name());
                    biReport.setReportName(sheet.getSheetName());
                    biReport.setRowNo(String.valueOf(row));
                    biReport.setIndexCode(indexCode);
                    biReport.setCell1(sheet.getRow(row).getCell(1).getStringCellValue());
                    biReport.setTenantId(ThreadLocalHolder.getTenantId());
                    biReport.setColNo(String.valueOf(cell));
                    Cell temp = sheet.getRow(row).getCell(cell);
                    String tempValue = ExcelUtils.getNumericCellValueDefault(temp);
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doGdzczjb(String tableName, Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(4);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            String type = typeCell.getStringCellValue();
            String date = DateUtils.formatStandardDateTime(new Date());

            List<LinkedHashMap<String, Object>> all = Lists.newArrayList();
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (null == row || null == row.getCell(0)) {
                    continue;
                }
                String period = DateUtils.stampToDateOfYear(row.getCell(0).getDateCellValue());
                LinkedHashMap<String, Object> map30 = Maps.newLinkedHashMap();
                map30.put("type", type);
                map30.put("PERIOD", period);
                map30.put("PERIOD_DATE", DateUtils.formatStandardDate(row.getCell(0).getDateCellValue()));
                map30.put("FIXED_ASSETS_ITEM", row.getCell(1).getStringCellValue());
                map30.put("FIXED_ASSETS_CLASSIFY", row.getCell(2).getStringCellValue());
                map30.put("PURCHASE_DATE", DateUtils.formatStandardDate(row.getCell(3).getDateCellValue()));
                map30.put("START_DATE", DateUtils.formatStandardDate(row.getCell(4).getDateCellValue()));
                map30.put("SERVICE_LIFE", row.getCell(5).getNumericCellValue());
                map30.put("ORIGINAL_VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(6)));
                map30.put("RESIDUAL_VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(7)));
                map30.put("MONTHLY_DEPRECIATION", ExcelUtils.getNumericCellValueDefault(row.getCell(8)));
                map30.put("ACCUMULATED_DEPRECIATION", ExcelUtils.getNumericCellValueDefault(row.getCell(9)));
                map30.put("NET_VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(10)));
                map30.put("CREATE_DATE", date);
                all.add(map30);
            }
            //处理入库
            process(all, tableName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doJkbSum(String tableName, Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(4);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            String type = typeCell.getStringCellValue();
            String date = DateUtils.formatStandardDateTime(new Date());

            List<LinkedHashMap<String, Object>> all = Lists.newArrayList();
            for (int i = 2; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (null == row || null == row.getCell(0)) {
                    continue;
                }
                String period = DateUtils.stampToDateOfYear(row.getCell(0).getDateCellValue());
                LinkedHashMap<String, Object> map30 = Maps.newLinkedHashMap();
                map30.put("type", type);
                map30.put("PERIOD", period);
                map30.put("PERIOD_DATE", DateUtils.formatStandardDate(row.getCell(0).getDateCellValue()));
                map30.put("CONTRACT_CODE", row.getCell(1).getStringCellValue());
                map30.put("CONTRACT_SUM", ExcelUtils.getNumericCellValueDefault(row.getCell(2)));
                map30.put("OPENING_SUM", ExcelUtils.getNumericCellValueDefault(row.getCell(3)));
                map30.put("CLOSING_SUM", ExcelUtils.getNumericCellValueDefault(row.getCell(4)));
                map30.put("CREATE_DATE", date);
                all.add(map30);
            }
            //处理入库
            process(all, tableName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doJkbCycle(String tableName, Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(4);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            String type = typeCell.getStringCellValue();
            String date = DateUtils.formatStandardDateTime(new Date());

            List<LinkedHashMap<String, Object>> all = Lists.newArrayList();
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (null == row || null == row.getCell(0)) {
                    continue;
                }
                String period = DateUtils.stampToDateOfYear(row.getCell(0).getDateCellValue());

                LinkedHashMap<String, Object> map90 = Maps.newLinkedHashMap();
                map90.put("type", type);
                map90.put("PERIOD", period);
                map90.put("PERIOD_DATE", DateUtils.formatStandardDate(row.getCell(0).getDateCellValue()));
                map90.put("CONTRACT_CODE", row.getCell(1).getStringCellValue());
                map90.put("CYCLE_TIME", "还款日期<=90天");
                map90.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(5)));
                map90.put("CREATE_DATE", date);

                LinkedHashMap<String, Object> map90180 = Maps.newLinkedHashMap(map90);
                map90180.put("CYCLE_TIME", "还款日期90-180天");
                map90180.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(6)));

                LinkedHashMap<String, Object> map180360 = Maps.newLinkedHashMap(map90);
                map90.put("CYCLE_TIME", "还款日期180-360天");
                map90.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(7)));

                LinkedHashMap<String, Object> map360 = Maps.newLinkedHashMap(map90);
                map360.put("CYCLE_TIME", "还款日期＞360天");
                map360.put("VALUE", ExcelUtils.getNumericCellValueDefault(row.getCell(8)));

                all.add(map90);
                all.add(map90180);
                all.add(map180360);
                all.add(map360);
            }
            //处理入库
            process(all, tableName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void doJtzjgl(String tableName, Sheet sheet, String batchId) {
        try {
            if (sheet == null) {
                throw new BizException(ResourceMessageEnum.EVM_2.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_2.getMessage(), ThreadLocalHolder.getLang()));
            }
            //获取类型
            Cell typeCell = sheet.getRow(0).getCell(4);
            if (null == typeCell) {
                throw new BizException(ResourceMessageEnum.EVM_4.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EVM_4.getMessage(), ThreadLocalHolder.getLang()));
            }
            String type = typeCell.getStringCellValue();
            String date = DateUtils.formatStandardDateTime(new Date());

            Map<String, Map<String, Map<String, Object>>> map = Maps.newHashMap();
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (null == row || null == row.getCell(0)) {
                    continue;
                }
                String period = DateUtils.stampToDateOfYear(row.getCell(0).getDateCellValue());
                String code = row.getCell(1).getStringCellValue();
                String d = ExcelUtils.getNumericCellValueDefault(row.getCell(3));
                String e = ExcelUtils.getNumericCellValueDefault(row.getCell(4));
                Cell cellF = row.getCell(5);
                BigDecimal f = null == cellF ? BigDecimal.ZERO : YesOrNoEnum.YES.getvalue().equals(cellF.getStringCellValue()) ? BigDecimal.ONE : BigDecimal.ZERO;
                Cell cellG = row.getCell(6);
                BigDecimal g = null == cellG ? BigDecimal.ZERO : YesOrNoEnum.YES.getvalue().equals(cellG.getStringCellValue()) ? BigDecimal.ONE : BigDecimal.ZERO;
                Map<String, Object> codeMap = Maps.newHashMap();
                codeMap.put("a", DateUtils.formatStandardDate(row.getCell(0).getDateCellValue()));
                codeMap.put("d", d);
                codeMap.put("e", e);
                codeMap.put("f", f);
                codeMap.put("g", g);

                if (map.containsKey(period)) {
                    Map<String, Map<String, Object>> inner = map.get(period);
                    inner.put(code, codeMap);
                } else {
                    Map<String, Map<String, Object>> inner = Maps.newHashMap();
                    inner.put(code, codeMap);
                    map.put(period, inner);
                }
            }

            //处理入库
            List<LinkedHashMap<String, Object>> all = Lists.newArrayList();
            for (Map.Entry<String, Map<String, Map<String, Object>>> periodMap : map.entrySet()) {
                String period = periodMap.getKey();
                Map<String, Map<String, Object>> codeMaps = periodMap.getValue();
                //公司总数
                int totalCode = codeMaps.size();
                if (0 == totalCode) {
                    continue;
                }
                BigDecimal sumD = BigDecimal.ZERO;
                BigDecimal sumE = BigDecimal.ZERO;
                BigDecimal sumF = BigDecimal.ZERO;
                BigDecimal sumG = BigDecimal.ZERO;
                String periodDate = null;
                for (Map.Entry<String, Map<String, Object>> codeMap : codeMaps.entrySet()) {
                    Map<String, Object> inner = codeMap.getValue();
                    sumD = sumD.add(new BigDecimal((String) inner.get("d")));
                    sumE = sumE.add(new BigDecimal((String) inner.get("e")));
                    sumF = sumF.add((BigDecimal) inner.get("f"));
                    sumG = sumG.add((BigDecimal) inner.get("g"));
                    periodDate = (String) inner.get("a");
                }
                LinkedHashMap<String, Object> result = Maps.newLinkedHashMap();
                result.put("type", type);
                result.put("PERIOD", period);
                result.put("PERIOD_DATE", periodDate);
                result.put("CONCENTRATION", sumD.divide(sumE, 5, BigDecimal.ROUND_HALF_UP));
                result.put("ACCRACY", sumF.divide(new BigDecimal(totalCode), 5, BigDecimal.ROUND_HALF_UP));
                result.put("PROMPTNESS", sumG.divide(new BigDecimal(totalCode), 5, BigDecimal.ROUND_HALF_UP));
                result.put("CREATE_DATE", date);
                all.add(result);
            }
            process(all, tableName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processZcfzAvg(List<BiReport> tempList, String indexCode, BiReport biReport, int row,
                                int cell, Sheet sheet) {
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

        //处理应付账款
        if ("EVMB046".equals(indexCode)) {
            BiReport evmb078 = new BiReport();
            BeanUtils.copyProperties(biReport, evmb078);
            evmb078.setIndexCode("EVMB046_AVG");
            evmb078.setCell1("平均应付账款");
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

    private void process(List<LinkedHashMap<String, Object>> lines, String tableName) {
        if (dbHandler.isTableExists(tableName)) {
            dbHandler.truncateTable(tableName);
            dbHandler.executeInsert(tableName, lines);
        }
    }

}
