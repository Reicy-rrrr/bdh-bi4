package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.DataSetResp;
import com.deloitte.bdh.data.collation.model.resp.DataSetTree;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-12-10
 */
public interface BiDataSetService extends Service<BiDataSet> {

    void initDataSet();

    List<DataSetResp> getFiles(String superUserFlag);

    List<DataSetResp> getDataSetByCode(GetDataSetByCodeDto dto);

    PageResult<List<DataSetResp>> getDataSetPage(GetDataSetPageDto dto);

    void reName(DataSetReNameDto dto);

    void fileCreate(CreateDataSetFileDto dto);

    void create(CreateDataSetDto dto);

    List<DataSetTableInfo> getTableList(String superUserFlag);

    List<TableColumn> getColumns(String code) throws Exception;

    TableData getDataInfoPage(GetDataSetInfoDto dto) throws Exception;

    TableData getDataInfoPage(BiDataSet dataSet, Integer page, Integer size) throws Exception;

    List<Map<String, Object>> getDataInfo(String id) throws Exception;

    void delete(String code, boolean canDel);

    void delRelationByDbId(String id);

}
