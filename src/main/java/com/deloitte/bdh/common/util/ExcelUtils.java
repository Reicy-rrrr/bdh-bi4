package com.deloitte.bdh.common.util;

import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);


    public static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
    public static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String CONTENT_TYPE_XLSM = "application/vnd.ms-excel.sheet.macroEnabled.12";

    private static String dateFormatPattern = "yyyyMMdd";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
    private static DecimalFormat decimalFormat = new DecimalFormat("###################.###########");

    private static NumberFormat numberFormat = NumberFormat.getInstance();

    static {
        numberFormat.setGroupingUsed(false);
    }

    /**
     * 获取单元格值
     *
     * @param cell 单元格
     * @return
     */
    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case BLANK:
                return "";
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return getNumericCellValue(cell);
            case ERROR:
                return Byte.toString(cell.getErrorCellValue());
            case FORMULA:
                return getFormulaCellValue(cell);
            default:
                throw new IllegalStateException("Unexpected cell type (" + cellType + ")");
        }
    }

    /**
     * 获取公式类型单元格值
     *
     * @param cell 单元格
     * @return
     */
    private static Object getFormulaCellValue(Cell cell) {
        CellValue cellValue = null;
        /*
         * 首先进行计算，计算异常就直接获取单元格的字符串或者数值型值
         */
        try {
            cellValue = cell.getRow().getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluate(cell);
        } catch (Exception e) {
            try {
                return getNumericCellValue(cell);
            } catch (IllegalStateException e1) {
                return cell.getStringCellValue();
            }
        }

        switch (cellValue.getCellTypeEnum()) {
            case BLANK:
                return "";
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return getNumericCellValue(cell);
            case ERROR:
                return Byte.toString(cell.getErrorCellValue());
            case FORMULA:
                return cellValue.getStringValue();
            default:
                throw new IllegalStateException("Unexpected cell type (" + cellValue.getCellTypeEnum() + ")");
        }
    }

    /**
     * 获取数值类型单元格值（包括数值和日期格式）
     *
     * @param cell 单元格
     * @return
     */
    private static Object getNumericCellValue(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else {
            String formatValue = numberFormat.format(cell.getNumericCellValue());
            if (formatValue.contains(".")) {
                return Double.parseDouble(formatValue);
            } else {
                return Integer.parseInt(formatValue);
            }
        }
    }

    /**
     * 获取单元格值的字符串（非字符串时转换为字符串）
     *
     * @param cell excel单元格
     * @return
     */
    public static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case BLANK:
                return "";
            case BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return getNumericCellStringValue(cell);
            case ERROR:
                return Byte.toString(cell.getErrorCellValue());
            case FORMULA:
                return getFormulaCellStringValue(cell);
            default:
                throw new IllegalStateException("Unexpected cell type (" + cellType + ")");
        }
    }

    /**
     * 获取数值类型单元值的字符串
     *
     * @param cell excel单元格
     * @return
     */
    private static String getNumericCellStringValue(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            if (StringUtils.isEmpty(dateFormatPattern)) {
                return dateFormat.format(cell.getDateCellValue());
            }
            return DateFormatUtils.format(cell.getDateCellValue(), dateFormatPattern);
        } else {
            return decimalFormat.format(cell.getNumericCellValue());
        }
    }

    /**
     * 获取公式类型单元格值的字符串
     *
     * @param cell excel单元格
     * @return
     */
    private static String getFormulaCellStringValue(Cell cell) {
        CellValue cellValue = null;
        /*
         * 首先进行计算，计算异常就直接获取单元格的字符串或者数值型值
         */
        try {
            cellValue = cell.getRow().getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluate(cell);
        } catch (Exception e) {
            try {
                return getNumericCellStringValue(cell);
            } catch (IllegalStateException e1) {
                return cell.getStringCellValue();
            }
        }

        switch (cellValue.getCellTypeEnum()) {
            case BLANK:
                return "";
            case BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return getNumericCellStringValue(cell);
            case ERROR:
                return Byte.toString(cell.getErrorCellValue());
            case FORMULA:
                return cellValue.getStringValue();
            default:
                throw new IllegalStateException("Unexpected cell type (" + cellValue.getCellType() + ")");
        }
    }

    public static void setCellValue(Sheet sheet, String cellIndex, Object cellValue) {
        CellAddress cellAddress = new CellAddress(cellIndex);
        Row row = sheet.getRow(cellAddress.getRow());//得到行
        Cell cell = row.getCell(cellAddress.getColumn());//得到列
        if (cell == null) {
            cell = row.createCell(cellAddress.getColumn());
        }
        setCellValue(cell, cellValue);
    }

    public static void setCellValue(Cell cell, Object cellValue) {
        if (cellValue instanceof Double) {
            cell.setCellValue((Double) cellValue);//改变数据
        } else if (cellValue instanceof Calendar) {
            cell.setCellValue((Calendar) cellValue);//改变数据
        } else if (cellValue instanceof RichTextString) {
            cell.setCellValue((RichTextString) cellValue);//改变数据
        } else if (cellValue instanceof Date) {
            cell.setCellValue((Date) cellValue);//改变数据
        }else if (cellValue instanceof BigDecimal) {
            cell.setCellValue(cellValue.toString());//改变数据
        } else {
            if(null == cellValue){
                cellValue="";
            }
            cell.setCellValue(String.valueOf(cellValue));
        }

        CellStyle style = cell.getCellStyle();
        // 设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.RIGHT);
        // 设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(style);
    }

    public static void setCellStringValue(Sheet sheet, String cellIndex, String cellValue) {
        CellAddress cellAddress = new CellAddress(cellIndex);
        Row row = sheet.getRow(cellAddress.getRow());//得到行
        Cell cell = row.getCell(cellAddress.getColumn());//得到列
        if (cell == null) {
            cell = row.createCell(cellAddress.getColumn());
        }

        if (isNumeric(cellValue)) {
            cell.setCellValue(Double.parseDouble(cellValue));
        } else {
            cell.setCellValue(cellValue);//改变数据
        }

        CellStyle style = cell.getCellStyle();
        // 设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.RIGHT);
        // 设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(style);
    }

    public static void setCellStringValue(Cell cell, String cellValue) {
        if (cell == null) {
            throw new NullPointerException();
        }
        if (isNumeric(cellValue)) {
            cell.setCellValue(Double.parseDouble(cellValue));
        } else {
            cell.setCellValue(cellValue);//改变数据
        }
    }

    /**
     * 校验导入文件的格式
     *
     * @param importFile 导入的文件
     * @return
     */
    public static boolean checkExcelFormat(MultipartFile importFile) {
        if (importFile == null) {
            return Boolean.FALSE;
        }
        String fileName = importFile.getOriginalFilename();
        if (!fileName.toLowerCase().endsWith(".xls") && !fileName.toLowerCase().endsWith(".xlsx")
                && !fileName.toLowerCase().endsWith(".xlsm")) {
            return Boolean.FALSE;
        }
        String fileType = importFile.getContentType();
        // xls xlsx xlsm
        if (!CONTENT_TYPE_XLS.equals(fileType) && !CONTENT_TYPE_XLSX.equals(fileType)
                && !CONTENT_TYPE_XLSM.equals(fileType)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        String numPattern = "-?[0-9]+\\.?[0-9]*";
        Pattern pattern = Pattern.compile(numPattern);
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static InputStream export(List<Map<String, Object>> data, List<TableColumn> columns) {
        ByteArrayOutputStream outputStream = null;
        InputStream inputStream = null;
        Workbook wb = new HSSFWorkbook();
        try {
            int rowSize = 0;
            Sheet sheet = wb.createSheet();
            Row row = sheet.createRow(rowSize);
            //设置header
            Map<String, Integer> properties = Maps.newHashMap();
            for (int i = 0; i < columns.size(); i++) {
                String name = columns.get(i).getName();
                properties.put(name, i);
                row.createCell(i).setCellValue(name);
            }

            for (int x = 0; x < data.size(); x++) {
                Row rowNew = sheet.createRow(1 + x);
                Map<String, Object> rowDate = data.get(x);
                for (Map.Entry<String, Integer> param : properties.entrySet()) {
                    Object value = rowDate.get(param.getKey());
                    Cell cell = rowNew.createCell(param.getValue());
                    setCellValue(cell, value);
                }

            }

            outputStream = new ByteArrayOutputStream();
            wb.write(outputStream);
            byte[] brray = outputStream.toByteArray();
            inputStream = new ByteArrayInputStream(brray);
        } catch (Exception e) {
            logger.error("文件导出失败：", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (Exception e) {
                logger.error("文件流关闭失败：", e);
            }
            try {
                wb.close();
            } catch (Exception e) {
                logger.error("Workbook 关闭失败：", e);
            }
        }
        return inputStream;
    }

}
