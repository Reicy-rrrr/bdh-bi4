package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.dao.bi.BiEtlDbFileMapper;
import com.deloitte.bdh.data.model.BiEtlDbFile;
import com.deloitte.bdh.data.model.request.BiEtlDbFileUploadDto;
import com.deloitte.bdh.data.model.resp.BiEtlDbFileUploadResp;
import com.deloitte.bdh.data.model.resp.FilePreReadResult;
import com.deloitte.bdh.data.model.resp.FtpUploadResult;
import com.deloitte.bdh.data.service.BiEtlDbFileService;
import com.deloitte.bdh.data.service.FileReadService;
import com.deloitte.bdh.data.service.FtpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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

    private BiEtlDbFileMapper biEtlDbFileMapper;

    private FtpService ftpService;

    private FileReadService fileReadService;

    @Override
    public BiEtlDbFileUploadResp upload(BiEtlDbFileUploadDto fileUploadDto) {
        MultipartFile file = fileUploadDto.getFile();
        // 租户id
        String tenantId = fileUploadDto.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            log.error("接收到的租户id为空，上传文件失败");
            throw new BizException("租户id不能为空");
        }

        String operator = fileUploadDto.getOperator();
        if (StringUtils.isBlank(operator)) {
            log.error("接收到的当前操作人id为空，上传文件失败");
            throw new BizException("操作人id不能为空");
        }
        // TODO 校验租户正确性，级租户与当前用户的管理关系
        FtpUploadResult uploadResult = ftpService.uploadExcelFile(file, tenantId);
        if (uploadResult == null) {
            log.error("文件保存到ftp服务器异常，上传文件失败");
            throw new BizException("文件上传ftp服务器失败！");
        }

        // 预读文件内容
        FilePreReadResult readResult = fileReadService.preRead(file);

        // 保存上传的文件信息
        BiEtlDbFile fileInfo = uploadResult.getFileInfo();
        fileInfo.setCreateUser(operator);
        fileInfo.setCreateDate(LocalDateTime.now());
        // TODO:设置过期时间
        fileInfo.setExpireDate(LocalDateTime.now());
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
        String fileName = dbFile.getStoredFileName();
        String filePath = dbFile.getFilePath();
        boolean flag = ftpService.deleteFile(filePath, fileName);
        if (!flag) {
            log.error("ftp服务器文件删除失败，导致文件信息删除失败！", fileId);
            throw new BizException("文件删除失败，请联系管理员！");
        }
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
            String fileName = dbFile.getStoredFileName();
            String filePath = dbFile.getFilePath();
            ftpService.deleteFile(filePath, fileName);
        });
        biEtlDbFileMapper.delete(queryWrapper);
        return Boolean.TRUE;
    }

    @Autowired
    public void setBiEtlDbFileMapper(BiEtlDbFileMapper biEtlDbFileMapper) {
        this.biEtlDbFileMapper = biEtlDbFileMapper;
    }

    @Autowired
    public void setFtpService(FtpService ftpService) {
        this.ftpService = ftpService;
    }

    @Autowired
    public void setFileReadService(FileReadService fileReadService) {
        this.fileReadService = fileReadService;
    }
}
