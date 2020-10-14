package com.deloitte.bdh.data.service.impl;

import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ExcelUtils;
import com.deloitte.bdh.common.util.FtpUtil;
import com.deloitte.bdh.common.util.UUIDUtil;
import com.deloitte.bdh.data.model.BiEtlDbFile;
import com.deloitte.bdh.data.model.resp.FtpUploadResult;
import com.deloitte.bdh.data.model.resp.JsonTemplate;
import com.deloitte.bdh.data.model.resp.JsonTemplateField;
import com.deloitte.bdh.data.service.FtpService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author chenghzhang
 */
@Service
public class FtpServiceImpl implements FtpService {

    private static final Logger logger = LoggerFactory.getLogger(FtpServiceImpl.class);

    private static final String FILE_SEPARATOR = "/";

    /**
     * ftp地址
     **/
    @Value("${ftp.server.host}")
    private String host;

    /**
     * ftp端口
     **/
    @Value("${ftp.server.port}")
    private int port;

    /**
     * ftp用户名
     **/
    @Value("${ftp.server.username}")
    private String username;

    /**
     * ftp密码
     **/
    @Value("${ftp.server.password}")
    private String password;

    /**
     * ftp服务器文件根目录
     */
    @Value("${ftp.server.root}")
    private String root;

    /**
     * ftp服务器文件存储路径
     */
    @Value("${ftp.server.path}")
    private String path;

    @Override
    public FtpUploadResult uploadExcelFile(MultipartFile file, String tenantId) {
        // 校验文件基本格式
        if (file == null || file.isEmpty()) {
            logger.warn("上传文件不能为空！");
            throw new BizException("上传文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            logger.warn("上传文件名不能为空");
            throw new BizException("上传文件名不能为空");
        }

        if (!checkExcelFormat(file)) {
            logger.warn("上传文件格式错误，文件名[{}]，文件类型[{}]。", fileName, file.getContentType());
            throw new BizException("上传文件格式错误");
        }

        // 文件上传地址：根路径 + 租户id + 日期（yyyyMMdd）
        StringBuilder pathBuilder = new StringBuilder(path);
        if (path.endsWith(FILE_SEPARATOR)) {
            pathBuilder.append(tenantId);
        } else {
            pathBuilder.append(FILE_SEPARATOR);
            pathBuilder.append(tenantId);
        }
        pathBuilder.append(FILE_SEPARATOR);
        pathBuilder.append(DateUtils.formatShortDate(new Date()));
        pathBuilder.append(FILE_SEPARATOR);
        String uploadPath = pathBuilder.toString();

        // 文件使用uuid重新命名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String finalName = UUIDUtil.generate() + suffix;

        // 上传文件
        FtpUtil ftp = new FtpUtil(host, port, username, password);
        try {
            boolean success = ftp.uploadFile(uploadPath, finalName, file.getInputStream());
            if (!success) {
                logger.warn("文件上传到ftp服务器失败");
                throw new BizException("文件上传到ftp服务器失败");
            }
            file.getInputStream().close();
        } catch (IOException e) {
            logger.error("文件上传到ftp服务器失败", e);
            throw new BizException("文件上传到ftp服务器失败");
        }

        BiEtlDbFile fileInfo = initFileInfo(file);
        fileInfo.setStoredFileName(finalName);
        fileInfo.setFilePath(uploadPath);
        fileInfo.setTenantId(tenantId);

        // 文件存储的完整全路径（访问时需要用完整路径）
        String remoteFullPath = root + uploadPath;
        return new FtpUploadResult(host, String.valueOf(port), username, password, remoteFullPath, finalName, fileInfo);
    }

    @Override
    public byte[] getFileBytes(String filePath, String fileName) {
        // 获取ftp文件流
        FtpUtil ftp = new FtpUtil(host, port, username, password);
        byte[] bytes = null;
        try {
            bytes = ftp.getFileBytesByName(filePath, fileName);
        } catch (Exception e) {
            logger.error("获取ftp服务器文件流失败", e);
            throw new BizException("获取ftp服务器文件流失败");
        }
        return bytes;
    }

    @Override
    public boolean deleteFile(String filePath, String fileName) {
        return false;
    }

    /**
     * 根据上传文件初始化需要保存的文件信息
     * @param file
     * @return
     */
    private BiEtlDbFile initFileInfo(MultipartFile file) {
        BiEtlDbFile fileInfo = new BiEtlDbFile();
        fileInfo.setOriginalFileName(file.getOriginalFilename());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setFileSize(file.getSize());
        return fileInfo;
    }

    /**
     * 根据上传文件初始化文件转换JSON格式模板
     *
     * @param file 上传文件
     * @return com.deloitte.bdh.data.model.resp.JsonTemplate
     */
    private JsonTemplate initJsonTemplate(MultipartFile file) {
        String fileName = file.getOriginalFilename().toLowerCase();
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return initJsonTemplateByExcel(file);
        } else if (fileName.endsWith(".csv")) {
            return initJsonTemplateByCsv(file);
        }
        return null;
    }

    /**
     * 根据Excel文件初始化文件转换JSON格式模板
     *
     * @param file Excel类型文件
     * @return com.deloitte.bdh.data.model.resp.JsonTemplate
     */
    private JsonTemplate initJsonTemplateByExcel(MultipartFile file) {
        JsonTemplate jsonTemplate = new JsonTemplate();
        List<JsonTemplateField> fields = new ArrayList();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            if (workbook == null) {
                logger.error("初始化JSON转换模板失败，上传文件内容为空！");
                throw new BizException("初始化JSON转换模板失败，上传文件内容不能为空！");
            }
            Sheet dataSheet = workbook.getSheetAt(0);
            // poi 行号从0开始
            if (dataSheet == null || (dataSheet.getLastRowNum()) <= 0) {
                logger.error("初始化JSON转换模板失败，上传文件内容为空！");
                throw new BizException("初始化JSON转换模板失败，上传文件内容不能为空！");
            }

            Row headerRow = dataSheet.getRow(0);
            int lastCellNum = 0;
            if (headerRow == null || (lastCellNum = headerRow.getLastCellNum()) <= 1) {
                logger.error("初始化JSON转换模板失败，上传文件首行内容为空！");
                throw new BizException("初始化JSON转换模板失败，上传文件首行内容不能为空！");
            }

            for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
                Cell cell = headerRow.getCell(cellIndex);
                if (cell == null) {
                    cell = headerRow.createCell(cellIndex);
                }
                String field = ExcelUtils.getCellStringValue(cell);
                if (StringUtils.isBlank(field)) {
                    logger.error("初始化JSON转换模板失败，上传文件首行单元格[{}]内容为空！", cell.getAddress());
                    throw new BizException("初始化JSON转换模板失败，上传文件首行单元格[" + cell.getAddress() + "]内容为空！");
                }
                JsonTemplateField templateField = new JsonTemplateField(field);
                fields.add(templateField);
            }
            jsonTemplate.setFields(fields);
        } catch (IOException e) {
            logger.error("初始化JSON转换模板失败，程序运行错误！", e);
            throw new BizException("初始化JSON转换模板失败，程序运行错误！");
        } catch (InvalidFormatException e) {
            logger.error("初始化JSON转换模板失败，程序运行错误！", e);
            throw new BizException("初始化JSON转换模板失败，程序运行错误！");
        }
        return jsonTemplate;
    }

    /**
     * 根据CSV文件初始化文件转换JSON格式模板
     *
     * @param file CSV类型文件
     * @return com.deloitte.bdh.data.model.resp.JsonTemplate
     */
    private JsonTemplate initJsonTemplateByCsv(MultipartFile file) {
        JsonTemplate jsonTemplate = new JsonTemplate();
        List<JsonTemplateField> fields = new ArrayList();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "GBK"))) {
            // 第一行信息，为标题信息
            String headerLine = reader.readLine();
            if (StringUtils.isBlank(headerLine)) {
                logger.error("初始化JSON转换模板失败，上传文件首行内容为空！");
                throw new BizException("初始化JSON转换模板失败，上传文件首行内容为空！");
            }

            String[] items = headerLine.split(",");
            for (int i = 0; i < items.length; i++) {
                JsonTemplateField templateField = new JsonTemplateField(items[i]);
                fields.add(templateField);
            }
        } catch (Exception e) {
            logger.error("初始化JSON转换模板失败，程序运行错误！", e);
            throw new BizException("初始化JSON转换模板失败，程序运行错误！");
        }
        jsonTemplate.setFields(fields);
        return jsonTemplate;
    }

    /**
     * 校验导入文件的格式
     *
     * @param importFile 导入的文件
     * @return
     */
    private boolean checkExcelFormat(MultipartFile importFile) {
        if (importFile == null) {
            return Boolean.FALSE;
        }
        String fileName = importFile.getOriginalFilename();
        if (!fileName.toLowerCase().endsWith(".xls") && !fileName.toLowerCase().endsWith(".xlsx")
                && !fileName.toLowerCase().endsWith(".xlsm") && !fileName.toLowerCase().endsWith(".csv")) {
            return Boolean.FALSE;
        }
        String fileType = importFile.getContentType();
        // csv xls xlsx xlsm
        String contentTypeCsv = "text/csv";
        String contentTypeXls = "application/vnd.ms-excel";
        String contentTypeXlsx = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String contentTypeXlsm = "application/vnd.ms-excel.sheet.macroEnabled.12";
        if (!contentTypeCsv.equals(fileType) && !contentTypeXls.equals(fileType)
                && !contentTypeXlsx.equals(fileType) && !contentTypeXlsx.equals(contentTypeXlsm)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
