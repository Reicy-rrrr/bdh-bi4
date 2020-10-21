package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.Service;
import com.deloitte.bdh.data.collation.database.vo.TableData;
import com.deloitte.bdh.data.collation.database.vo.TableSchema;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.request.*;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-09-24
 */
public interface BiEtlDatabaseInfService extends Service<BiEtlDatabaseInf> {

    /**
     * 基于租户获取数据源列表
     *
     * @param dto
     * @return
     */
    PageResult<List<BiEtlDatabaseInf>> getResources(GetResourcesDto dto);

    /**
     * 查看单个resource
     *
     * @param id
     * @return
     */
    BiEtlDatabaseInf getResource(String id);

    /**
     * 创建数据源
     *
     * @param dto
     * @return
     */
    BiEtlDatabaseInf createResource(CreateResourcesDto dto) throws Exception;

    /**
     * 创建文件数据源
     *
     * @param dto
     * @return
     */
    BiEtlDatabaseInf createFileResource(CreateFileResourcesDto dto) throws Exception;

    /**
     * 追加文件数据源
     *
     * @param dto
     * @return
     */
    BiEtlDatabaseInf appendFileResource(AppendFileResourcesDto dto) throws Exception;

    /**
     * 重置文件数据源（重新上传，清空之前上传的数据）
     *
     * @param dto
     * @return
     */
    BiEtlDatabaseInf resetFileResource(ResetFileResourcesDto dto) throws Exception;

    /**
     * 启用/禁用数据源
     *
     * @param dto
     * @return
     */
    BiEtlDatabaseInf runResource(RunResourcesDto dto) throws Exception;

    /**
     * del数据源
     *
     * @param dto
     * @return
     */
    void delResource(String id) throws Exception;

    /**
     * 修改数据源
     *
     * @param dto
     * @return
     */
    BiEtlDatabaseInf updateResource(UpdateResourcesDto dto) throws Exception;

    /**
     * 测试连接
     *
     * @param dto
     * @return
     */
    String testConnection(TestConnectionDto dto) throws Exception;

    /**
     * 获取数据源下所有表集合
     *
     * @param dbId
     * @return
     */
    List<String> getTables(String dbId) throws Exception;

    /**
     * 获取表所有字段集合
     *
     * @param dbId
     * @return
     */
    List<String> getFields(String dbId, String tableName) throws Exception;

    /**
     * 查询表结构
     *
     * @param dto 查询表结构dto
     * @return com.deloitte.bdh.data.collation.database.vo.TableSchema
     * @throws Exception
     */
    TableSchema getTableSchema(GetTableSchemaDto dto) throws Exception;

    /**
     * 查询表数据
     *
     * @param dto 查询表数据dto
     * @return com.deloitte.bdh.data.collation.database.vo.TableData
     * @throws Exception
     */
    TableData getTableData(GetTableDataDto dto) throws Exception;
}
