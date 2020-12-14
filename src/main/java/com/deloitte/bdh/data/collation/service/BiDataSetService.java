package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.CreateDataSetDto;
import com.deloitte.bdh.data.collation.model.request.CreateDataSetFileDto;
import com.deloitte.bdh.data.collation.model.request.DataSetReNameDto;
import com.deloitte.bdh.data.collation.model.request.GetDataSetPageDto;
import com.deloitte.bdh.data.collation.model.resp.DataSetResp;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-12-10
 */
public interface BiDataSetService extends Service<BiDataSet> {

    List<BiDataSet> getFiles();

    PageResult<List<DataSetResp>> getDataSetPage(GetDataSetPageDto dto);

    void reName(DataSetReNameDto dto);

    void fileCreate(CreateDataSetFileDto dto);

    void create(CreateDataSetDto dto);

    List<BiDataSet> getTableList();

    List<TableColumn> getColumns(String tableDesc) throws Exception;
}
