package com.deloitte.bdh.data.service.impl;

import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.FtpUtil;
import com.deloitte.bdh.common.util.UUIDUtil;
import com.deloitte.bdh.data.model.resp.FtpUploadResult;
import com.deloitte.bdh.data.service.FtpService;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;


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
        String remotePath = pathBuilder.toString();

        // 文件使用uuid重新命名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String finalName = UUIDUtil.generate() + suffix;

        // 上传文件
        FtpUtil ftp = new FtpUtil(host, port, username, password);
        try {
            boolean success = ftp.uploadFile(remotePath, finalName, file.getInputStream());
            if (!success) {
                logger.warn("文件上传到ftp服务器失败");
                throw new BizException("文件上传到ftp服务器失败");
            }
        } catch (IOException e) {
            logger.error("文件上传到ftp服务器失败", e);
            throw new BizException("文件上传到ftp服务器失败");
        }
        return new FtpUploadResult(host, String.valueOf(port), username, password, remotePath, fileName);
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
