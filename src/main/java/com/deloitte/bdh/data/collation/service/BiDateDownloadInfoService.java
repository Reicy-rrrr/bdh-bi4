package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.model.BiDateDownloadInfo;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.GetDownloadPageDto;

import java.util.List;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2021-03-02
 */
public interface BiDateDownloadInfoService extends Service<BiDateDownloadInfo> {

    void export(BiDateDownloadInfo info, BiDataSet dataSet) throws Exception;

    String downLoad(String id);

     PageResult<List<BiDateDownloadInfo>> downloadPage(GetDownloadPageDto dto);
}
