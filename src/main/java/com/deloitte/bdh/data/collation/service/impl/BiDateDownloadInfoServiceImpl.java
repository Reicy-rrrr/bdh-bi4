package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.AliyunOssUtil;
import com.deloitte.bdh.common.util.ExcelUtils;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.common.util.ZipUtil;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.collation.controller.BiTenantConfigController;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.enums.DownLoadTStatusEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.model.BiDateDownloadInfo;
import com.deloitte.bdh.data.collation.dao.bi.BiDateDownloadInfoMapper;
import com.deloitte.bdh.data.collation.model.request.GetDownloadPageDto;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.deloitte.bdh.data.collation.service.BiDateDownloadInfoService;
import com.deloitte.bdh.common.base.AbstractService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2021-03-02
 */
@Service
@DS(DSConstant.BI_DB)
public class BiDateDownloadInfoServiceImpl extends AbstractService<BiDateDownloadInfoMapper, BiDateDownloadInfo> implements BiDateDownloadInfoService {
    @Resource
    private BiDateDownloadInfoMapper dateDownloadInfoMapper;
    @Autowired
    private AliyunOssUtil aliyunOss;
    @Resource
    private BiDataSetService dataSetService;

    @Override
    public void export(BiDateDownloadInfo info, BiDataSet dataSet) throws Exception {
        long begin = System.currentTimeMillis();
        String zipFilePath = "/home/portal/bi_dataset/t_" + ThreadLocalHolder.getTenantCode()
                + "/" + DateUtils.formatShortDate(new Date()) + "/" + dataSet.getCode() + "/";
        List<TableColumn> columns = dataSetService.getColumns(dataSet.getCode());
        Integer page = 1;
        Integer pageSize = 60000;
        boolean more;
        List<String> zipFiles = Lists.newArrayList();
        try {
            do {
                String fileName = dataSet.getTableDesc() + "_" + System.currentTimeMillis() + ".xls";
                TableData data = dataSetService.getDataInfoPage(dataSet, page, pageSize);
                more = data.isMore();
                if (more) {
                    page++;
                }
                List<Map<String, Object>> list = data.getRows();
                InputStream inputStream = ExcelUtils.export(list, columns);
                ExcelUtils.create(zipFilePath, fileName, inputStream);
                zipFiles.add(zipFilePath + fileName);
            } while (more);
        } catch (Exception e) {
            log.error("压缩文件错误：", e);
        }

        //压缩
        if (CollectionUtils.isEmpty(zipFiles)) {
            info.setStatus(DownLoadTStatusEnum.FAIL.getKey());
        } else {
            String filePath = AnalyseConstants.DOCUMENT_DIR + ThreadLocalHolder.getTenantCode() + "/bi/dataset/";
            if (page > 1) {
                String zipFileName = info.getName() + System.currentTimeMillis() + ".zip";
                boolean success = ZipUtil.toZip(zipFilePath + zipFileName, zipFiles);
                if (!success) {
                    info.setStatus(DownLoadTStatusEnum.FAIL.getKey());
                } else {
                    String storedFileKey = aliyunOss.uploadFile2OSS(new FileInputStream(new File(zipFilePath + zipFileName)), filePath, zipFileName);
                    info.setFileName(zipFileName);
                    info.setPath(filePath);
                    info.setStoreFileKey(storedFileKey);
                    info.setStatus(DownLoadTStatusEnum.SUCCESS.getKey());
                }
            } else {
                String storedFileKey = aliyunOss.uploadFile2OSS(new FileInputStream(new File(zipFiles.get(0)))
                        , filePath, zipFiles.get(0).substring(zipFiles.get(0).lastIndexOf("/") + 1));
                info.setFileName(zipFiles.get(0).substring(zipFiles.get(0).lastIndexOf("/") + 1));
                info.setPath(filePath);
                info.setStoreFileKey(storedFileKey);
                info.setStatus(DownLoadTStatusEnum.SUCCESS.getKey());
            }

        }

        //生成excel 再更新状态
        info.setProcessTime((System.currentTimeMillis() - begin) / 1000 + "");
        dateDownloadInfoMapper.updateById(info);

        //删除压缩的文件夹
        File file = new File(zipFilePath);
        ZipUtil.deleteFolder(file);
    }

    @Override
    public String downLoad(String id) {
        BiDateDownloadInfo info = dateDownloadInfoMapper.selectById(id);
        if (null == info) {
            throw new BizException(ResourceMessageEnum.DOWNLOAD_FAIL_1.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DOWNLOAD_FAIL_1.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (DownLoadTStatusEnum.ING.getKey().equalsIgnoreCase(info.getStatus())) {
            throw new BizException(ResourceMessageEnum.DOWNLOAD_FAIL_2.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DOWNLOAD_FAIL_2.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (DownLoadTStatusEnum.FAIL.getKey().equalsIgnoreCase(info.getStatus())) {
            throw new BizException(ResourceMessageEnum.DOWNLOAD_FAIL_3.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DOWNLOAD_FAIL_3.getMessage(), ThreadLocalHolder.getLang()));
        }
        String url = aliyunOss.getImgUrl(info.getPath(), info.getFileName());
        return url;
    }


    @Override
    public PageResult<List<BiDateDownloadInfo>> downloadPage(GetDownloadPageDto dto) {
        LambdaQueryWrapper<BiDateDownloadInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.equals(dto.getSuperUserFlag(), YesOrNoEnum.YES.getKey())) {
            lambdaQueryWrapper.in(BiDateDownloadInfo::getCreateUser, ThreadLocalHolder.getOperator(), BiTenantConfigController.OPERATOR);
        }
        if (StringUtils.isNotBlank(dto.getStatus())) {
            lambdaQueryWrapper.eq(BiDateDownloadInfo::getStatus, dto.getStatus());
        }
        lambdaQueryWrapper.orderByDesc(BiDateDownloadInfo::getCreateDate);
        PageInfo<BiDateDownloadInfo> pageInfo = new PageInfo<>(this.list(lambdaQueryWrapper));
        return new PageResult<>(pageInfo);
    }

}
