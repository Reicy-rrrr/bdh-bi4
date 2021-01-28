package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.model.request.CreateDataSetDto;
import com.deloitte.bdh.data.collation.model.request.CreateDataSetFileDto;
import com.deloitte.bdh.data.collation.model.request.DataSetReNameDto;
import com.deloitte.bdh.data.collation.model.request.GetDataSetInfoDto;
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

    void initDataSet();

    List<DataSetResp> getFiles(String superUserFlag);

    PageResult<List<DataSetResp>> getDataSetPage(GetDataSetPageDto dto);

    void reName(DataSetReNameDto dto);

    void fileCreate(CreateDataSetFileDto dto);

    void create(CreateDataSetDto dto);

    List<BiDataSet> getTableList(String superUserFlag);

    List<TableColumn> getColumns(String code) throws Exception;

    TableData getDataSetInfoPage(GetDataSetInfoDto dto) throws Exception;

    void delete(String code, boolean canDel);

    void delRelationByDbId(String id);

}
