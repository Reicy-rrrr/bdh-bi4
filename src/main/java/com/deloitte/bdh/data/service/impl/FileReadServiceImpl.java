package com.deloitte.bdh.data.service.impl;

import com.deloitte.bdh.common.base.MongoHelper;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ExcelUtils;
import com.deloitte.bdh.data.enums.FileTypeEnum;
import com.deloitte.bdh.data.model.resp.FilePreReadResult;
import com.deloitte.bdh.data.model.resp.FileReadResult;
import com.deloitte.bdh.data.service.FileReadService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author chenghzhang
 */
@Service
public class FileReadServiceImpl implements FileReadService {

    private static final Logger logger = LoggerFactory.getLogger(FileReadServiceImpl.class);

    /**
     * 预览实例行数
     */
    private static final int PRE_READ_COUNT = 10;
    /**
     * 默认批量提交到mongo的记录数
     */
    private static final int BATCH_COMMIT_COUNT = 1000;

    @Autowired
    private MongoHelper mongoHelper;

    @Override
    public FilePreReadResult preRead(MultipartFile file) {
        FilePreReadResult readResult = null;
        checkFileFormat(file);
        String fileType = file.getContentType();
        switch (FileTypeEnum.values(fileType)) {
            case Csv:
                readResult = preReadCsv(file);
                break;
            case Excel_Xls:
                readResult = preReadExcel(file);
                break;
            case Excel_Xlsx:
                readResult = preReadExcel(file);
                break;
            case Excel_Xlsm:
                readResult = preReadExcel(file);
                break;
            default:
                throw new BizException("错误的文件类型，系统暂不支持！");
        }
        return readResult;
    }

    @Override
    public void read(MultipartFile file, Map<String, String> columnTypes, String collectionName) {
        switch (FileTypeEnum.values(file.getContentType())) {
            case Csv:
                readCsv(file, collectionName);
                break;
            case Excel_Xls:
                readExcel(file, collectionName);
                break;
            case Excel_Xlsx:
                readExcel(file, collectionName);
                break;
            case Excel_Xlsm:
                readExcel(file, collectionName);
                break;
            default:
                throw new BizException("错误的文件类型，系统暂不支持！");
        }
    }

    @Override
    public void read(byte[] bytes, String fileType, Map<String, String> columnTypes, String collectionName) {
        switch (FileTypeEnum.values(fileType)) {
            case Csv:
                readCsv(bytes, collectionName);
                break;
            case Excel_Xls:
                readExcel(bytes, collectionName);
                break;
            case Excel_Xlsx:
                readExcel(bytes, collectionName);
                break;
            case Excel_Xlsm:
                readExcel(bytes, collectionName);
                break;
            default:
                throw new BizException("错误的文件类型，系统暂不支持！");
        }
    }

    /**
     * 预读excel类型文件
     *
     * @param file Excel类型文件
     * @return com.deloitte.bdh.data.model.resp.FileReadResult
     */
    private FilePreReadResult preReadExcel(MultipartFile file) {
        FilePreReadResult readResult = new FilePreReadResult();
        List<String> headers = new ArrayList();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            if (workbook == null) {
                logger.error("读取Excel文件失败，上传文件内容为空！");
                throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
            }
            Sheet dataSheet = workbook.getSheetAt(0);
            // poi 行号从0开始
            if (dataSheet == null || (dataSheet.getLastRowNum()) <= 0) {
                logger.error("读取Excel文件失败，上传文件内容为空！");
                throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
            }

            Row headerRow = dataSheet.getRow(0);
            int lastCellNum = 0;
            if (headerRow == null || (lastCellNum = headerRow.getLastCellNum()) <= 1) {
                logger.error("读取Excel文件失败，上传文件首行内容为空！");
                throw new BizException("读取Excel文件失败，上传文件首行内容不能为空！");
            }

            Map<Integer, String> fields = Maps.newHashMap();
            for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                Cell cell = headerRow.getCell(cellIndex);
                if (cell == null) {
                    cell = headerRow.createCell(cellIndex);
                }
                String field = ExcelUtils.getCellStringValue(cell);
                if (StringUtils.isBlank(field)) {
                    logger.error("读取Excel文件失败，上传文件首行单元格[{}]内容为空！", cell.getAddress());
                    throw new BizException("读取Excel文件失败，上传文件首行单元格[" + cell.getAddress() + "]内容为空！");
                }
                headers.add(field);
                fields.put(cellIndex, field);
            }

            readResult.setHeaders(headers);
            List<Document> lines = Lists.newArrayList();
            for (int rowIndex = 1; rowIndex < dataSheet.getLastRowNum(); rowIndex++) {
                Row row = dataSheet.getRow(rowIndex);
                Document document = new Document();
                for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                    Cell cell = row.getCell(cellIndex);
                    if (cell == null) {
                        cell = row.createCell(cellIndex);
                    }
                    Object value = ExcelUtils.getCellValue(cell);
                    document.put(fields.get(cellIndex), value);
                }
                lines.add(document);
                if (lines.size() >= PRE_READ_COUNT) {
                    break;
                }
            }

            readResult.setLines(lines);
            LinkedHashMap<String, String> columnTypes = new LinkedHashMap();
            // TODO:默认数据类型都为String
            headers.forEach(field -> {
                columnTypes.put(field, "String");
            });
            readResult.setColumns(columnTypes);
        } catch (IOException e) {
            logger.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        } catch (InvalidFormatException e) {
            logger.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        }
        return readResult;
    }

    /**
     * 预读csv类型文件
     *
     * @param file csv类型文件
     * @return com.deloitte.bdh.data.model.resp.FileReadResult
     */
    private FilePreReadResult preReadCsv(MultipartFile file) {
        FilePreReadResult readResult = new FilePreReadResult();
        List<String> headers = new ArrayList();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "GBK"))) {
            // 第一行信息，为标题信息
            String headerLine = reader.readLine();
            if (StringUtils.isBlank(headerLine)) {
                logger.error("初始化JSON转换模板失败，上传文件首行内容为空！");
                throw new BizException("初始化JSON转换模板失败，上传文件首行内容为空！");
            }

            String[] items = headerLine.split(",");
            Map<Integer, String> fields = Maps.newHashMap();
            for (int i = 0; i < items.length; i++) {
                fields.put(i, items[i]);
                headers.add(items[i]);
            }
            readResult.setHeaders(headers);

            List<Document> lines = Lists.newArrayList();
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Document document = new Document();
                for (int i = 0; i < values.length; i++) {
                    document.put(fields.get(i), values[i]);
                }

                lines.add(document);
                if (lines.size() >= PRE_READ_COUNT) {
                    break;
                }
            }
            readResult.setLines(lines);

            LinkedHashMap<String, String> columnTypes = new LinkedHashMap();
            headers.forEach(field -> {
                columnTypes.put(field, "String");
            });
            readResult.setColumns(columnTypes);
        } catch (Exception e) {
            logger.error("读取Csv文件失败，程序运行错误！", e);
            throw new BizException("读取Csv文件失败，程序运行错误！");
        }
        return readResult;
    }

    /**
     * 读取excel类型文件并存储数据到mongodb
     *
     * @param file Excel类型文件
     * @return com.deloitte.bdh.data.model.resp.FileReadResult
     */
    private void readExcel(MultipartFile file, String collectionName) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            if (workbook == null) {
                logger.error("读取Excel文件失败，上传文件内容为空！");
                throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
            }
            Sheet dataSheet = workbook.getSheetAt(0);
            // poi 行号从0开始
            if (dataSheet == null || (dataSheet.getLastRowNum()) <= 0) {
                logger.error("读取Excel文件失败，上传文件内容为空！");
                throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
            }
            readSheet(dataSheet, collectionName);
        } catch (IOException e) {
            logger.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        } catch (InvalidFormatException e) {
            logger.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        }
    }

    private void readExcel(byte[] bytes, String collectionName) {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            if (workbook == null) {
                logger.error("读取Excel文件失败，上传文件内容为空！");
                throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
            }
            Sheet dataSheet = workbook.getSheetAt(0);
            // poi 行号从0开始
            if (dataSheet == null || (dataSheet.getLastRowNum()) <= 0) {
                logger.error("读取Excel文件失败，上传文件内容为空！");
                throw new BizException("读取Excel文件失败，上传文件内容不能为空！");
            }
            readSheet(dataSheet, collectionName);
        } catch (IOException e) {
            logger.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        } catch (InvalidFormatException e) {
            logger.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        }
    }

    /**
     * 读取excel类型文件并存储数据到mongodb
     *
     * @param sheet Excel数据表格
     * @return com.deloitte.bdh.data.model.resp.FileReadResult
     */
    private void readSheet(Sheet sheet, String collectionName) {
        Row headerRow = sheet.getRow(0);
        int lastCellNum = 0;
        if (headerRow == null || (lastCellNum = headerRow.getLastCellNum()) <= 1) {
            logger.error("读取Excel文件失败，上传文件首行内容为空！");
            throw new BizException("读取Excel文件失败，上传文件首行内容不能为空！");
        }

        Map<Integer, String> fields = Maps.newHashMap();
        for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
            Cell cell = headerRow.getCell(cellIndex);
            if (cell == null) {
                cell = headerRow.createCell(cellIndex);
            }
            String field = ExcelUtils.getCellStringValue(cell);
            if (StringUtils.isBlank(field)) {
                logger.error("读取Excel文件失败，上传文件首行单元格[{}]内容为空！", cell.getAddress());
                throw new BizException("读取Excel文件失败，上传文件首行单元格[" + cell.getAddress() + "]内容为空！");
            }
            fields.put(cellIndex, field);
        }

        List<Document> documents = Lists.newArrayList();
        for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            Document document = new Document();
            for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                if (cell == null) {
                    cell = row.createCell(cellIndex);
                }
                Object value = ExcelUtils.getCellValue(cell);
                document.put(fields.get(cellIndex), value);
            }
            documents.add(document);
            if (documents.size() >= BATCH_COMMIT_COUNT) {
                mongoHelper.insertBatch(documents, collectionName);
                documents.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(documents)) {
            mongoHelper.insertBatch(documents, collectionName);
        }
    }

    /**
     * 读取csv类型文件并存储数据到mongodb
     *
     * @param file csv类型文件
     */
    private void readCsv(MultipartFile file, String collectionName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "GBK"))) {
            // 第一行信息，为标题信息
            String headerLine = reader.readLine();
            if (StringUtils.isBlank(headerLine)) {
                logger.error("初始化JSON转换模板失败，上传文件首行内容为空！");
                throw new BizException("初始化JSON转换模板失败，上传文件首行内容为空！");
            }

            String[] items = headerLine.split(",");
            Map<Integer, String> fields = Maps.newHashMap();
            for (int i = 0; i < items.length; i++) {
                fields.put(i, items[i]);
            }

            List<Document> documents = Lists.newArrayList();
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Document document = new Document();
                for (int i = 0; i < values.length; i++) {
                    document.put(fields.get(i), values[i]);
                }
                documents.add(document);
                if (documents.size() >= BATCH_COMMIT_COUNT) {
                    mongoHelper.insertBatch(documents, collectionName);
                    documents.clear();
                }
            }
            if (CollectionUtils.isNotEmpty(documents)) {
                mongoHelper.insertBatch(documents, collectionName);
            }
        } catch (Exception e) {
            logger.error("读取Csv文件失败，程序运行错误！", e);
            throw new BizException("读取Csv文件失败，程序运行错误！");
        }
    }

    /**
     * 读取csv类型文件并存储数据到mongodb
     *
     * @param bytes csv类型文件字节数组
     * @return com.deloitte.bdh.data.model.resp.FileReadResult
     */
    private void readCsv(byte[] bytes, String collectionName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), "GBK"))) {
            readCsvByBufferedReader(reader, collectionName);
        } catch (Exception e) {
            logger.error("读取Csv文件失败，程序运行错误！", e);
            throw new BizException("读取Csv文件失败，程序运行错误！");
        }
    }

    /**
     * 读取csv类型文件并存储数据到mongodb
     *
     * @param reader
     * @return com.deloitte.bdh.data.model.resp.FileReadResult
     */
    private void readCsvByBufferedReader(BufferedReader reader, String collectionName) throws IOException {
        // 第一行信息，为标题信息
        String headerLine = reader.readLine();
        if (StringUtils.isBlank(headerLine)) {
            logger.error("初始化JSON转换模板失败，上传文件首行内容为空！");
            throw new BizException("初始化JSON转换模板失败，上传文件首行内容为空！");
        }

        String[] items = headerLine.split(",");
        Map<Integer, String> fields = Maps.newHashMap();
        for (int i = 0; i < items.length; i++) {
            fields.put(i, items[i]);
        }

        List<Document> documents = Lists.newArrayList();
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            Document document = new Document();
            for (int i = 0; i < values.length; i++) {
                document.put(fields.get(i), values[i]);
            }
            documents.add(document);
            if (documents.size() >= BATCH_COMMIT_COUNT) {
                mongoHelper.insertBatch(documents, collectionName);
                documents.clear();
            }
        }

        if (CollectionUtils.isNotEmpty(documents)) {
            mongoHelper.insertBatch(documents, collectionName);
        }
    }

    /**
     * 校验导入文件的格式
     *
     * @param importFile 导入的文件
     * @return
     */
    private void checkFileFormat(MultipartFile importFile) {
        if (importFile == null) {
            throw new BizException("上传文件不能为空！");
        }

        String fileType = importFile.getContentType();
        String fileName = importFile.getOriginalFilename().toLowerCase();
        FileTypeEnum fileTypeEnum = FileTypeEnum.values(fileType);
        if (!fileName.endsWith(fileTypeEnum.getValue())) {
            throw new BizException("文件类型与文件名称不匹配！");
        }
    }
}
