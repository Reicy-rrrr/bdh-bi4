package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.annotation.NoInterceptor;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.AliyunOssUtil;
import com.deloitte.bdh.common.util.ExcelUtils;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.constants.AnalyseConstants;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.enums.DownLoadTStatusEnum;
import com.deloitte.bdh.data.collation.model.BiDateDownloadInfo;
import com.deloitte.bdh.data.collation.dao.bi.BiDateDownloadInfoMapper;
import com.deloitte.bdh.data.collation.service.BiDateDownloadInfoService;
import com.deloitte.bdh.common.base.AbstractService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.io.InputStream;
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

    @Override
    public void export(BiDateDownloadInfo info, List<TableColumn> columns, List<Map<String, Object>> list) {
        InputStream inputStream = ExcelUtils.export(list, columns);
        if (null == inputStream) {
            info.setStatus(DownLoadTStatusEnum.FAIL.getKey());
        } else {
            String fileName = info.getName() + System.currentTimeMillis();
            String filePath = AnalyseConstants.DOCUMENT_DIR + ThreadLocalHolder.getTenantCode() + "/bi/dataset/";
            String storedFileKey = aliyunOss.uploadFile2OSS(inputStream, filePath, fileName);
            info.setFileName(fileName);
            info.setPath(filePath);
            info.setStoreFileKey(storedFileKey);
        }
        //生成excel 再更新状态
        dateDownloadInfoMapper.updateById(info);
    }

    @Override
    public String downLoad(String id) {
        BiDateDownloadInfo info = dateDownloadInfoMapper.selectById(id);
        if (DownLoadTStatusEnum.ING.getKey().equalsIgnoreCase(info.getStatus())) {
            throw new RuntimeException("下载失败:当前数据正在生成种");
        }
        if (DownLoadTStatusEnum.FAIL.getKey().equalsIgnoreCase(info.getStatus())) {
            throw new RuntimeException("下载失败:文件生成失败,请重新生成");
        }
        String url = aliyunOss.getImgUrl(info.getPath(), info.getFileName());
        return url;
    }


}
