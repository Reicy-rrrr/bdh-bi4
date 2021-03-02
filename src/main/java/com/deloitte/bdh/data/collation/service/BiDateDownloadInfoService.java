package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.model.BiDateDownloadInfo;
import com.deloitte.bdh.common.base.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2021-03-02
 */
public interface BiDateDownloadInfoService extends Service<BiDateDownloadInfo> {

    void export(BiDateDownloadInfo info, List<TableColumn> columns, List<Map<String, Object>> list);

    String downLoad(String id);
}
