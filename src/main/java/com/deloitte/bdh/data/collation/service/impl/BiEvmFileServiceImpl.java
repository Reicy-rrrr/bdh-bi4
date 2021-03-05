package com.deloitte.bdh.data.collation.service.impl;

import java.time.LocalDateTime;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.AliyunOssUtil;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.common.util.UUIDUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.collation.enums.KafkaTypeEnum;
import com.deloitte.bdh.data.collation.model.BiEvmFile;
import com.deloitte.bdh.data.collation.dao.bi.BiEvmFileMapper;
import com.deloitte.bdh.data.collation.model.request.BiEtlDbFileUploadDto;
import com.deloitte.bdh.data.collation.mq.KafkaMessage;
import com.deloitte.bdh.data.collation.service.BiEvmFileConsumerService;
import com.deloitte.bdh.data.collation.service.BiEvmFileService;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2021-02-01
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEvmFileServiceImpl extends AbstractService<BiEvmFileMapper, BiEvmFile> implements BiEvmFileService {

    @Resource
    private BiEvmFileMapper fileMapper;
    @Autowired
    private AliyunOssUtil aliyunOss;
    @Autowired
    private BiEvmFileConsumerService consumerService;

    @Override
    public void uploadEvm(BiEtlDbFileUploadDto fileUploadDto) {
        // 租户id
        String tenantId = fileUploadDto.getTenantId();
        String operator = fileUploadDto.getOperator();
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
            throw new BizException(ResourceMessageEnum.EVM_3.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EVM_3.getMessage(), ThreadLocalHolder.getLang()));
        }

        // 发送消息去处理文件入库
        BiEvmFile biEvmFile = new BiEvmFile();
        biEvmFile.setBatchId(GenerateCodeUtil.generate());
        biEvmFile.setOriginalFileName(fileName);
        biEvmFile.setStoredFileName(finalName);
        biEvmFile.setStoredFileKey(storedFileKey);
        biEvmFile.setFileType(file.getContentType());
        biEvmFile.setFilePath(filePath);
        biEvmFile.setFileSize(String.valueOf(file.getSize()));
        biEvmFile.setCreateDate(LocalDateTime.now());
        biEvmFile.setCreateUser(operator);
        biEvmFile.setTenantId(tenantId);
        biEvmFile.setTables(fileUploadDto.getTables());
        fileMapper.insert(biEvmFile);

        biEvmFile.setCreateDate(null);
        KafkaMessage<BiEvmFile> message = new KafkaMessage<>(UUID.randomUUID().toString().replaceAll("-", ""), biEvmFile, KafkaTypeEnum.EVM_FILE.getType());
        ThreadLocalHolder.async(() -> consumerService.consumer(message));
    }
}
