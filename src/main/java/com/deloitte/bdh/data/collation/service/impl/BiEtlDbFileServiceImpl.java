package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.AliyunOssUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.common.util.UUIDUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlDbFileMapper;
import com.deloitte.bdh.data.collation.enums.KafkaTypeEnum;
import com.deloitte.bdh.data.collation.model.BiEtlDbFile;
import com.deloitte.bdh.data.collation.model.FilePreReadResult;
import com.deloitte.bdh.data.collation.model.request.BiEtlDbFileUploadDto;
import com.deloitte.bdh.data.collation.model.resp.BiEtlDbFileUploadResp;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.BiEtlDbFileService;
import com.deloitte.bdh.data.collation.service.FileReadService;
import com.deloitte.bdh.data.collation.service.Producter;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 数据源文件服务实现类
 * </p>
 *
 * @author chenghzhang
 * @since 2020-10-12
 */
@Service
@DS(DSConstant.BI_DB)
@Slf4j
public class BiEtlDbFileServiceImpl extends AbstractService<BiEtlDbFileMapper, BiEtlDbFile> implements BiEtlDbFileService {

    @Resource
    private BiEtlDbFileMapper biEtlDbFileMapper;
    @Autowired
    private FileReadService fileReadService;
    @Autowired
    private AliyunOssUtil aliyunOss;
    @Autowired
    private Producter producter;


    @Override
    public BiEtlDbFileUploadResp upload(BiEtlDbFileUploadDto fileUploadDto) {
        // 租户id
        String tenantId = fileUploadDto.getTenantId();
        String operator = fileUploadDto.getOperator();
        MultipartFile file = fileUploadDto.getFile();

        String fileName = file.getOriginalFilename();
        // 文件使用uuid重新命名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String finalName = UUIDUtil.generate() + suffix;

        String filePath = AnalyseConstants.DOCUMENT_DIR + ThreadLocalHolder.getTenantCode() + "/bi/attachment/";
        String storedFileKey = null;
        try {
            storedFileKey = aliyunOss.uploadFile(file.getInputStream(), filePath, finalName, file.getContentType());
        } catch (IOException e) {
            throw new BizException("File upload error: 上传文件发生错误，请检查文件有效性！");
        }
        // 预读文件内容
        FilePreReadResult readResult = fileReadService.preRead(file);

        BiEtlDbFile fileInfo = new BiEtlDbFile();
        fileInfo.setOriginalFileName(file.getOriginalFilename());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFilePath(filePath);
        fileInfo.setStoredFileName(finalName);
        fileInfo.setStoredFileKey(storedFileKey);
        fileInfo.setCreateUser(operator);
        fileInfo.setCreateDate(LocalDateTime.now());
        fileInfo.setTenantId(tenantId);
        // 设置过期时间：默认180天
        fileInfo.setExpireDate(LocalDateTime.now().plusDays(180));
        fileInfo.setReadFlag(1);
        // 预上传的文件信息数据源id默认为-1
        fileInfo.setDbId("-1");
        this.save(fileInfo);

        BiEtlDbFileUploadResp uploadResp = new BiEtlDbFileUploadResp();
        BeanUtils.copyProperties(readResult, uploadResp);
        uploadResp.setFileId(fileInfo.getId());
        return uploadResp;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean delete(RetRequest<String> deleteRequest) {
        String fileId = deleteRequest.getData();
        if (StringUtils.isBlank(fileId)) {
            log.error("接受到的文件信息id为空，删除文件失败！");
            throw new BizException("文件信息id不能为空！");
        }

        BiEtlDbFile dbFile = this.getById(fileId);
        if (dbFile == null) {
            log.error("根据id[{}]未查询到文件信息id，删除文件失败！", fileId);
            throw new BizException("未查询到文件信息，错误的id！");
        }

        if (dbFile.getReadFlag() == 0) {
            log.error("根据id[{}]查询到文件为已读状态，不允许删除，删除文件失败！", fileId);
            throw new BizException("文件数据已经入库，不允许直接删除！");
        }

        // 先删除文件信息，在删除ftp文件（防止删除失败可以回滚）
        biEtlDbFileMapper.deleteById(fileId);

        String key = dbFile.getStoredFileKey();
        aliyunOss.deleteObject(key);
        return Boolean.TRUE;
    }

    @Override
    public Boolean deleteByDbId(String dbId) {
        if (StringUtils.isBlank(dbId)) {
            log.error("接受到的数据源信息id[{}]为空，删除文件失败！", dbId);
            throw new BizException("数据源信息id不能为空！");
        }
        LambdaQueryWrapper<BiEtlDbFile> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(BiEtlDbFile::getDbId, dbId);
        List<BiEtlDbFile> dbFiles = biEtlDbFileMapper.selectList(queryWrapper);
        // 依次删除ftp服务器上文件
        dbFiles.forEach(dbFile -> {
            String key = dbFile.getStoredFileKey();
            aliyunOss.deleteObject(key);
        });
        biEtlDbFileMapper.delete(queryWrapper);
        return Boolean.TRUE;
    }

    @Override
    public void uploadEvm(BiEtlDbFileUploadDto fileUploadDto) {
        MultipartFile file = fileUploadDto.getFile();
        String fileName = file.getOriginalFilename();
        // 文件使用uuid重新命名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String finalName = "EVM_" + UUIDUtil.generate() + suffix;

        String filePath = AnalyseConstants.DOCUMENT_DIR + ThreadLocalHolder.getTenantCode() + "/bi/attachment/";
        String storedFileKey = null;
        try {
            storedFileKey = aliyunOss.uploadFile(file.getInputStream(), filePath, finalName, file.getContentType());
        } catch (IOException e) {
            throw new BizException("File upload error: 上传文件发生错误，请检查文件有效性！");
        }
        // 发送消息去处理文件入库
        Map<String, String> request = Maps.newHashMap();
        request.put("fileName", finalName);
        request.put("filePath", filePath);
        request.put("storedFileKey", storedFileKey);

        KafkaMessage message = new KafkaMessage(UUID.randomUUID().toString().replaceAll("-", ""), request, KafkaTypeEnum.EVM_FILE.getType());
        producter.send(message);

    }

}
