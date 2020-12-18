package com.deloitte.bdh.data.collation.service.impl;

import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ExcelUtils;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.DataTypeEnum;
import com.deloitte.bdh.data.collation.enums.FileTypeEnum;
import com.deloitte.bdh.data.collation.model.FilePreReadResult;
import com.deloitte.bdh.data.collation.service.FileReadService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.opencsv.CSVReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


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
     * 默认批量提交到数据库的记录数
     */
    private static final int BATCH_COMMIT_COUNT = 1000;

    @Autowired
    private DbHandler dbHandler;

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
    public void readIntoDB(byte[] bytes, String fileType, Map<String, TableField> columnTypes, String tableName) {
        switch (FileTypeEnum.values(fileType)) {
            case Csv:
                readCsvIntoDB(bytes, tableName, columnTypes);
                break;
            case Excel_Xls:
                readExcelIntoDb(bytes, tableName, columnTypes);
                break;
            case Excel_Xlsx:
                readExcelIntoDb(bytes, tableName, columnTypes);
                break;
            case Excel_Xlsm:
                readExcelIntoDb(bytes, tableName, columnTypes);
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
        // 列数据类型
        Map<Integer, Set<DataTypeEnum>> dataTypes = Maps.newHashMap();
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

                if (headers.contains(field)) {
                    logger.error("读取Excel文件失败，上传文件首行存在重复内容[{}]！", field);
                    throw new BizException("读取Excel文件失败，上传文件首行存在重复内容[" + field + "]！");
                } else {
                    headers.add(field);
                }
                fields.put(cellIndex, field);
            }

            readResult.setHeaders(headers);
            List<Map<String, Object>> lines = Lists.newArrayList();
            for (int rowIndex = 1; rowIndex < dataSheet.getLastRowNum(); rowIndex++) {
                Row row = dataSheet.getRow(rowIndex);
                Map<String, Object> document = Maps.newHashMap();
                for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                    Cell cell = row.getCell(cellIndex);
                    if (cell == null) {
                        cell = row.createCell(cellIndex);
                    }

                    Object value = ExcelUtils.getCellValue(cell);
                    DataTypeEnum dataType = getDataType(value);
                    if (DataTypeEnum.Date.equals(dataType)) {
                        document.put(fields.get(cellIndex), DateUtils.formatStandardDate((Date) value));
                    } else if (DataTypeEnum.DateTime.equals(dataType)) {
                        document.put(fields.get(cellIndex), DateUtils.formatStandardDateTime((Date) value));
                    } else {
                        document.put(fields.get(cellIndex), value);
                    }
                    if (dataTypes.containsKey(cellIndex)) {
                        dataTypes.get(cellIndex).add(dataType);
                    } else {
                        dataTypes.put(cellIndex, Sets.newHashSet(dataType));
                    }
                }
                lines.add(document);
                if (lines.size() >= PRE_READ_COUNT) {
                    break;
                }
            }

            readResult.setLines(lines);
            readResult.setColumns(initColumnTypes(headers, dataTypes));
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
        // 列数据类型
        Map<Integer, Set<DataTypeEnum>> dataTypes = Maps.newHashMap();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), getCharsetName(file.getBytes())))) {
            // 第一行信息，为标题信息
            String[] headerItems = csvReader.readNext();
            if (headerItems == null || headerItems.length == 0) {
                logger.error("初始化JSON转换模板失败，上传文件首行内容为空！");
                throw new BizException("初始化JSON转换模板失败，上传文件首行内容为空！");
            }

            Map<Integer, String> fields = Maps.newHashMap();
            for (int i = 0; i < headerItems.length; i++) {
                fields.put(i, headerItems[i]);
                headers.add(headerItems[i]);
            }
            readResult.setHeaders(headers);

            List<Map<String, Object>> lines = Lists.newArrayList();
            String[] lineItems = null;
            while ((lineItems = csvReader.readNext()) != null) {
                Map<String, Object> document = Maps.newHashMap();
                for (int index = 0; index < lineItems.length; index++) {
                    document.put(fields.get(index), lineItems[index]);

                    String value = lineItems[index];
                    DataTypeEnum dataType = getDataType(value);
                    if (dataTypes.containsKey(index)) {
                        dataTypes.get(index).add(dataType);
                    } else {
                        dataTypes.put(index, Sets.newHashSet(dataType));
                    }
                }

                lines.add(document);
                if (lines.size() >= PRE_READ_COUNT) {
                    break;
                }
            }
            readResult.setLines(lines);
            readResult.setColumns(initColumnTypes(headers, dataTypes));
        } catch (Exception e) {
            logger.error("读取Csv文件失败，程序运行错误！", e);
            throw new BizException("读取Csv文件失败，程序运行错误！");
        }
        return readResult;
    }

    /**
     * 读取excel文件到关系型数据库
     *
     * @param bytes     文件字节数组
     * @param tableName 数据库表名
     * @param columns   字段信息
     */
    private void readExcelIntoDb(byte[] bytes, String tableName, Map<String, TableField> columns) {
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
            readSheetIntoDb(dataSheet, tableName, columns);
        } catch (IOException e) {
            logger.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        } catch (InvalidFormatException e) {
            logger.error("读取Excel文件失败，程序运行错误！", e);
            throw new BizException("读取Excel文件失败，程序运行错误！");
        }
    }

    /**
     * 读取excel文件到关系型数据库
     *
     * @param sheet     Excel数据表格
     * @param tableName 数据库表名
     * @param columns   字段信息
     * @return com.deloitte.bdh.data.model.resp.FileReadResult
     */
    private void readSheetIntoDb(Sheet sheet, String tableName, Map<String, TableField> columns) {
        Row headerRow = sheet.getRow(0);
        int lastCellNum = 0;
        if (headerRow == null || (lastCellNum = headerRow.getLastCellNum()) <= 1) {
            logger.error("读取Excel文件失败，上传文件首行内容为空！");
            throw new BizException("读取Excel文件失败，上传文件首行内容不能为空！");
        }

        List<Triple<String, String, DataTypeEnum>> mappings = Lists.newArrayList();
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

            TableField tableField = MapUtils.getObject(columns, field);
            Triple<String, String, DataTypeEnum> mapping = new ImmutableTriple(field, tableField.getName(), DataTypeEnum.get(tableField.getType()));
            mappings.add(mapping);
        }

        List<Map<String, Object>> lines = Lists.newArrayList();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            Map<String, Object> line = Maps.newLinkedHashMap();
            for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                if (cell == null) {
                    cell = row.createCell(cellIndex);
                }

                Object value = ExcelUtils.getCellValue(cell);
                Object finalValue = getDataValue(value, mappings.get(cellIndex).getRight());
                line.put(mappings.get(cellIndex).getMiddle(), finalValue);
            }
            lines.add(line);
            if (lines.size() >= BATCH_COMMIT_COUNT) {
                dbHandler.executeInsert(tableName, lines);
                lines.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(lines)) {
            dbHandler.executeInsert(tableName, lines);
        }
    }

    /**
     * 读取csv类型文件并存储数据到关系型数据库
     *
     * @param bytes     csv类型文件字节数组
     * @param tableName 数据库表名
     * @param columns   字段信息
     */
    private void readCsvIntoDB(byte[] bytes, String tableName, Map<String, TableField> columns) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(bytes), getCharsetName(bytes)))) {
            String[] headerItems = reader.readNext();
            if (headerItems == null || headerItems.length == 0) {
                logger.error("读取CSV文件失败，上传文件首行内容为空！");
                throw new BizException("读取CSV文件失败，上传文件首行内容为空！");
            }

            List<Triple<String, String, DataTypeEnum>> mappings = Lists.newArrayList();
            Map<Integer, String> fields = Maps.newHashMap();
            for (int i = 0; i < headerItems.length; i++) {
                TableField tableField = MapUtils.getObject(columns, headerItems[i]);
                Triple<String, String, DataTypeEnum> mapping = new ImmutableTriple(headerItems[i], tableField.getName(), DataTypeEnum.get(tableField.getType()));
                mappings.add(mapping);
            }

            List<Map<String, Object>> lines = Lists.newArrayList();
            String[] lineItems = null;
            while ((lineItems = reader.readNext()) != null) {
                Map<String, Object> line = Maps.newLinkedHashMap();
                for (int index = 0; index < lineItems.length; index++) {
                    Object finalValue = getDataValue(lineItems[index], mappings.get(index).getRight());
                    line.put(mappings.get(index).getMiddle(), finalValue);
                }
                lines.add(line);
                if (lines.size() >= BATCH_COMMIT_COUNT) {
                    dbHandler.executeInsert(tableName, lines);
                    lines.clear();
                }
            }

            if (CollectionUtils.isNotEmpty(lines)) {
                dbHandler.executeInsert(tableName, lines);
            }
        } catch (Exception e) {
            logger.error("读取Csv文件失败，程序运行错误！", e);
            throw new BizException("读取Csv文件失败，程序运行错误！");
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

    /**
     * 获取数据类型
     *
     * @param value
     * @return
     */
    private DataTypeEnum getDataType(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            Date date = (Date) value;
            if (String.valueOf(date.getTime()).endsWith("00000")) {
                return DataTypeEnum.Date;
            }
            return DataTypeEnum.DateTime;
        } else if (value instanceof Integer) {
            return DataTypeEnum.Integer;
        } else if (value instanceof Float) {
            return DataTypeEnum.Float;
        } else {
            try {
                String stringValue = String.valueOf(value);
                if (stringValue.indexOf(".") >= 0) {
                    Double.parseDouble(stringValue);
                    return DataTypeEnum.Float;
                } else {
                    Integer.parseInt(stringValue);
                    return DataTypeEnum.Integer;
                }
            } catch (Exception e) {
                return DataTypeEnum.Text;
            }
        }
    }

    private LinkedHashMap<String, String> initColumnTypes(List<String> headers, Map<Integer, Set<DataTypeEnum>> dataTypes) {
        LinkedHashMap<String, String> columnTypes = Maps.newLinkedHashMap();
        if (CollectionUtils.isEmpty(headers) || dataTypes == null || dataTypes.isEmpty()) {
            return columnTypes;
        }
        for (int index = 0; index < headers.size(); index++) {
            String headerName = headers.get(index);
            Set<DataTypeEnum> types = MapUtils.getObject(dataTypes, index);
            if (types.contains(DataTypeEnum.Text)) {
                columnTypes.put(headerName, DataTypeEnum.Text.getType());
            } else if (types.contains(DataTypeEnum.Float)) {
                columnTypes.put(headerName, DataTypeEnum.Float.getType());
            } else if (types.contains(DataTypeEnum.Integer)) {
                columnTypes.put(headerName, DataTypeEnum.Integer.getType());
            } else if (types.contains(DataTypeEnum.DateTime)) {
                columnTypes.put(headerName, DataTypeEnum.DateTime.getType());
            } else {
                columnTypes.put(headerName, DataTypeEnum.Date.getType());
            }
        }
        return columnTypes;
    }

    private Object getDataValue(Object source, DataTypeEnum dataType) {
        Object target = null;
        switch (dataType) {
            case Integer:
                if (source instanceof Integer) {
                    target = source;
                } else if (source instanceof Double) {
                    target = ((Double) source).intValue();
                } else if (source instanceof Float) {
                    target = ((Float) source).intValue();
                } else if (source instanceof Date) {
                    target = 0;
                } else if (source instanceof String) {
                    try {
                        target = Integer.valueOf((String) source);
                    } catch (Exception e) {
                        target = 0;
                    }
                } else {
                    target = 0;
                }
                break;
            case Float:
                if (source instanceof Double || source instanceof Float) {
                    target = source;
                } else if (source instanceof Integer) {
                    target = Double.valueOf((Integer) source);
                } else if (source instanceof Date) {
                    target = 0D;
                } else if (source instanceof String) {
                    try {
                        target = Double.valueOf((String) source);
                    } catch (Exception e) {
                        target = 0D;
                    }
                } else {
                    target = 0D;
                }
                break;
            case Date:
                if (source instanceof Date) {
                    target = source;
                } else if (source instanceof Double || source instanceof Float) {
                    target = null;
                } else if (source instanceof Integer) {
                    target = null;
                } else if (source instanceof String) {
                    try {
                        target = DateUtils.parseStandardDate((String) source);
                    } catch (Exception e) {
                        target = null;
                    }
                } else {
                    target = null;
                }
                break;
            case DateTime:
                if (source instanceof Date) {
                    target = source;
                } else if (source instanceof Double || source instanceof Float) {
                    target = null;
                } else if (source instanceof Integer) {
                    target = null;
                } else if (source instanceof String) {
                    try {
                        target = DateUtils.parseStandardDateTime((String) source);
                    } catch (Exception e) {
                        target = null;
                    }
                } else {
                    target = null;
                }
                break;
            case Text:
                // 字符串长度限制为255
                if (source instanceof String) {
                    String value = (String) source;
                    if (value.length() > 255) {
                        value = value.substring(0, 255);
                    }
                    target = value;
                } else if (source instanceof Date) {
                    target = DateUtils.formatStandardDateTime((Date) source);
                } else {
                    String value = String.valueOf(source);
                    if (value.length() > 255) {
                        value = value.substring(0, 255);
                    }
                    target = value;
                }
                break;
            default:

        }
        return target;
    }

    /**
     * 获取编码
     *
     * @param bytes 文件字节数组
     * @return
     */
    private String getCharsetName(byte[] bytes) {
        String DEFAULT_ENCODING = "UTF-8";
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        return encoding;
    }
}
