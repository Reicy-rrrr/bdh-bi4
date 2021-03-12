package com.deloitte.bdh.data.collation.service.impl;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.enums.PermittedActionEnum;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.enums.ResourcesTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalyseUserResource;
import com.deloitte.bdh.data.analyse.service.AnalyseUserResourceService;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.model.BiUiModelFolder;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFieldService;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFolderService;
import com.deloitte.bdh.data.collation.controller.BiTenantConfigController;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlDbMapper;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.service.BiComponentService;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.google.common.collect.Lists;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.dao.bi.BiDataSetMapper;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.deloitte.bdh.data.collation.enums.DataSetTypeEnum;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.resp.DataSetResp;
import com.deloitte.bdh.data.collation.service.BiComponentParamsService;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-12-10
 */
@Service
@DS(DSConstant.BI_DB)
public class BiDataSetServiceImpl extends AbstractService<BiDataSetMapper, BiDataSet> implements BiDataSetService {

    @Resource
    private BiDataSetMapper setMapper;
    @Resource
    private BiEtlModelService modelService;
    @Resource
    private BiEtlDatabaseInfService databaseInfService;
    @Autowired
    private BiComponentParamsService componentParamsService;
    @Autowired
    private BiComponentService componentService;
    @Resource
    private DbHandler dbHandler;
    @Resource
    private DbSelector dbSelector;
    @Resource
    private AnalyseModelFolderService folderService;
    @Resource
    private AnalyseModelFieldService fieldService;
    @Resource
    private AnalyseUserResourceService userResourceService;

    @Resource
    private BiEtlDbMapper biEtlDbMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initDataSet() {
        if (0 == this.count()) {
            BiDataSet set = new BiDataSet();
            set.setTableName("默认文件夹");
            set.setTableDesc("默认文件夹");
            set.setParentId("0");
            set.setCode(GenerateCodeUtil.generate());
            set.setIsFile(YesOrNoEnum.YES.getKey());
            set.setTenantId(ThreadLocalHolder.getTenantId());
            setMapper.insert(set);
            //设置初始数据
            initDataSet(set.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DataSetResp> getFiles(String superUserFlag) {
        List<BiDataSet> setList;
        List<DataSetResp> respList = Lists.newArrayList();
        if (StringUtils.equals(superUserFlag, YesOrNoEnum.YES.getKey())) {
            setList = setMapper.selectList(new LambdaQueryWrapper<BiDataSet>()
                    .eq(BiDataSet::getParentId, "0")
                    .eq(BiDataSet::getIsFile, YesOrNoEnum.YES.getKey())
                    .orderByDesc(BiDataSet::getCreateDate)
            );
        } else {
            SelectDataSetDto selectDataSetDto = new SelectDataSetDto();
            selectDataSetDto.setUserId(ThreadLocalHolder.getOperator());
            selectDataSetDto.setResourceType(ResourcesTypeEnum.DATA_SET_CATEGORY.getCode());
            selectDataSetDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
            selectDataSetDto.setTenantId(ThreadLocalHolder.getTenantId());
            selectDataSetDto.setParentIdList(Lists.newArrayList("0"));
            selectDataSetDto.setIsFile(YesOrNoEnum.YES.getKey());
//            List<String> userList = Lists.newArrayList(ThreadLocalHolder.getOperator(), BiTenantConfigController.OPERATOR);
//            selectDataSetDto.setCreateUserList(userList);
            setList = setMapper.selectDataSetCategory(selectDataSetDto);
        }

        if (CollectionUtils.isNotEmpty(setList)) {
            for (BiDataSet dataSet : setList) {
                DataSetResp resp = new DataSetResp();
                BeanUtils.copyProperties(dataSet, resp);
                respList.add(resp);
            }
            userResourceService.setDataSetPermission(respList, ResourcesTypeEnum.DATA_SET_CATEGORY, superUserFlag);
        }
        return respList;
    }

    @Override
    public List<DataSetResp> getDataSetByCode(GetDataSetByCodeDto dto) {
        List<DataSetResp> respList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dto.getCodeList())) {
            LambdaQueryWrapper<BiDataSet> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(BiDataSet::getCode, dto.getCodeList());
            List<BiDataSet> list = list(queryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(dataSet -> {
                    DataSetResp resp = new DataSetResp();
                    BeanUtils.copyProperties(dataSet, resp);
                    respList.add(resp);
                });
            }
        }
        return respList;
    }

    @Override
    public PageResult<List<DataSetResp>> getDataSetPage(GetDataSetPageDto dto) {
        List<String> parentIdList = Lists.newArrayList(dto.getFileId());
        PageInfo<BiDataSet> pageInfo = getDataSetPage(parentIdList, dto.getSuperUserFlag());
        List<DataSetResp> respList = transfer(pageInfo.getList());
        userResourceService.setDataSetPermission(respList, ResourcesTypeEnum.DATA_SET, dto.getSuperUserFlag());
        PageInfo<DataSetResp> pageResult = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, pageResult);
        pageResult.setList(respList);
        return new PageResult<>(pageResult);
    }

    private PageInfo<BiDataSet> getDataSetPage(List<String> parentIdList, String superUserFlag) {
        PageInfo<BiDataSet> dataSetList;
        if (StringUtils.equals(superUserFlag, YesOrNoEnum.YES.getKey())) {
            LambdaQueryWrapper<BiDataSet> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(BiDataSet::getParentId, parentIdList);
            queryWrapper.eq(BiDataSet::getIsFile, YesOrNoEnum.NO.getKey());
            queryWrapper.eq(BiDataSet::getTenantId, ThreadLocalHolder.getTenantId());
            dataSetList = new PageInfo<>(list(queryWrapper));
        } else {
            SelectDataSetDto selectDataSetDto = new SelectDataSetDto();
            selectDataSetDto.setUserId(ThreadLocalHolder.getOperator());
            selectDataSetDto.setResourceType(ResourcesTypeEnum.DATA_SET.getCode());
            selectDataSetDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
            selectDataSetDto.setTenantId(ThreadLocalHolder.getTenantId());
            selectDataSetDto.setParentIdList(parentIdList);
            selectDataSetDto.setIsFile(YesOrNoEnum.NO.getKey());
            dataSetList = new PageInfo<>(setMapper.selectDataSetCategory(selectDataSetDto));
        }
        return dataSetList;
    }

    private List<DataSetResp> getDataSet(List<String> parentIdList, String superUserFlag) {
        List<BiDataSet> dataSetList;
        if (StringUtils.equals(superUserFlag, YesOrNoEnum.YES.getKey())) {
            LambdaQueryWrapper<BiDataSet> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(BiDataSet::getParentId, parentIdList);
            queryWrapper.eq(BiDataSet::getIsFile, YesOrNoEnum.NO.getKey());
            queryWrapper.eq(BiDataSet::getTenantId, ThreadLocalHolder.getTenantId());
            dataSetList = list(queryWrapper);
        } else {
            SelectDataSetDto selectDataSetDto = new SelectDataSetDto();
            selectDataSetDto.setUserId(ThreadLocalHolder.getOperator());
            selectDataSetDto.setResourceType(ResourcesTypeEnum.DATA_SET.getCode());
            selectDataSetDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
            selectDataSetDto.setTenantId(ThreadLocalHolder.getTenantId());
            selectDataSetDto.setParentIdList(parentIdList);
            selectDataSetDto.setIsFile(YesOrNoEnum.NO.getKey());
            dataSetList = setMapper.selectDataSetCategory(selectDataSetDto);
        }
        return transfer(dataSetList);
    }

    private List<DataSetResp> transfer(List<BiDataSet> list) {
        List<DataSetResp> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (BiDataSet dataSet : list) {
                DataSetResp dataSetResp = new DataSetResp();
                BeanUtils.copyProperties(dataSet, dataSetResp);
                result.add(dataSetResp);
            }
            //获取模板编码
            List<String> modelCodeList = result.stream().filter(s -> StringUtils.isNotBlank(s.getRefModelCode()))
                    .map(DataSetResp::getRefModelCode).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(modelCodeList)) {
                //获取model
                List<BiEtlModel> modelList = modelService.list(new LambdaQueryWrapper<BiEtlModel>().in(BiEtlModel::getCode, modelCodeList));
                for (DataSetResp setResp : result) {
                    if (StringUtils.isNotBlank(setResp.getRefModelCode())) {
                        BiEtlModel model = getModel(modelList, setResp.getRefModelCode());
                        if (null != model) {
                            setResp.setModelName(model.getName());
                            if (null != model.getLastExecuteDate()) {
                                setResp.setLastExecuteDate(model.getLastExecuteDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reName(DataSetReNameDto dto) {
        BiDataSet dataSet = setMapper.selectById(dto.getId());
        if (null == dataSet) {
            throw new BizException(ResourceMessageEnum.DATA_SET_NOT_EXIST.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DATA_SET_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (dataSet.getCreateUser().equals(BiTenantConfigController.OPERATOR)) {
            throw new BizException(ResourceMessageEnum.DEFAULT_DATA_LOCK.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DEFAULT_DATA_LOCK.getMessage(), ThreadLocalHolder.getLang()));
        }
        String newTableDesc = dto.getToTableDesc();
        //别名有变化
        if (!dataSet.getTableDesc().equals(newTableDesc)) {
            dataSet.setTableDesc(newTableDesc);
            setMapper.updateById(dataSet);

            //数据整理则修改组件
            if (DataSetTypeEnum.MODEL.getKey().equals(dataSet.getType())) {
                BiComponentParams param = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                        .eq(BiComponentParams::getParamKey, ComponentCons.TO_TABLE_DESC)
                        .eq(BiComponentParams::getRefModelCode, dataSet.getRefModelCode())
                        .eq(BiComponentParams::getRefComponentCode, dataSet.getCode())
                );
                if (null == param) {
                    throw new BizException(ResourceMessageEnum.TARGET_MODEL_NOT_EXIST.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.TARGET_MODEL_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));
                }
                param.setParamValue(newTableDesc);
                componentParamsService.updateById(param);
            }
        }

        //描述变更
        String comments = StringUtils.isBlank(dto.getComments()) ? " " : dto.getComments();
        if (!comments.equals(dataSet.getComments())) {
            dataSet.setComments(comments);
            setMapper.updateById(dataSet);
            //数据整理则修改组件
            if (DataSetTypeEnum.MODEL.getKey().equals(dataSet.getType())) {
                BiComponent component = componentService.getOne(new LambdaQueryWrapper<BiComponent>()
                        .eq(BiComponent::getCode, dataSet.getCode())
                );
                if (null == component) {
                    throw new BizException(ResourceMessageEnum.TARGET_MODEL_NOT_EXIST.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.TARGET_MODEL_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));
                }
                component.setComments(comments);
                componentService.updateById(component);
            }
        }

    }

    @Override
    public void fileCreate(CreateDataSetFileDto dto) {
        BiDataSet dataSet = new BiDataSet();
        dataSet.setCode(GenerateCodeUtil.generate());
        dataSet.setTableName(dto.getFolderName());
        dataSet.setTableDesc(dto.getFolderName());
        dataSet.setParentId("0");
        if (StringUtils.isNotBlank(dto.getFolderId())) {
            dataSet.setParentId(dto.getFolderId());
        }
        dataSet.setIsFile(YesOrNoEnum.YES.getKey());
        dataSet.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(dataSet);

        //保存权限
        dto.getPermissionDto().setId(dataSet.getId());
        dto.getPermissionDto().setResourceType(ResourcesTypeEnum.DATA_SET_CATEGORY.getCode());
        userResourceService.saveResourcePermission(dto.getPermissionDto());
    }

    @Override
    public void create(CreateDataSetDto dto) {
        String tableDesc = dto.getTableNameDesc();
        BiDataSet dataSet = new BiDataSet();
        dataSet.setCode(GenerateCodeUtil.generate());
        // 数据集合类型（0, "数据直连"，1, "数据整理"）
        dataSet.setType(DataSetTypeEnum.DIRECT.getKey());
        dataSet.setRefSourceId(dto.getRefSourceId());
        dataSet.setTableName(dto.getTableName());
        dataSet.setTableDesc(tableDesc);
        dataSet.setParentId(dto.getFolderId());
        dataSet.setIsFile(YesOrNoEnum.NO.getKey());
        dataSet.setTenantId(ThreadLocalHolder.getTenantId());
        dataSet.setComments(dto.getComments());
        setMapper.insert(dataSet);

        //保存权限
        if (null != dto.getPermissionDto()) {
            dto.getPermissionDto().setId(dataSet.getId());
            dto.getPermissionDto().setResourceType(ResourcesTypeEnum.DATA_SET.getCode());
            userResourceService.saveResourcePermission(dto.getPermissionDto());
        }
    }

    @Override
    public List<DataSetTableInfo> getTableList(String superUserFlag) {

        List<DataSetResp> fileList = getFiles(superUserFlag);
        List<String> parentIdList = Lists.newArrayList();
        fileList.forEach(file -> parentIdList.add(file.getId()));
        List<DataSetResp> dataSetRespList = getDataSet(parentIdList, superUserFlag);
        Map<String, List<DataSetResp>> parentIdMap = dataSetRespList.stream().collect(Collectors.groupingBy(DataSetResp::getParentId));

        //整理数据
        List<DataSetTableInfo> dataSetTree = Lists.newArrayList();
        for (DataSetResp file : fileList) {
            DataSetTableInfo fileTree = new DataSetTableInfo();
            fileTree.setId(file.getId());
            fileTree.setCode(file.getCode());
            fileTree.setToTableName(file.getTableName());
            fileTree.setToTableDesc(file.getTableDesc());
            fileTree.setTitle(file.getTableDesc());
            fileTree.setValue(file.getCode());
            fileTree.setIsFile(file.getIsFile());
            List<DataSetResp> dataSetList = parentIdMap.get(file.getId());
            List<DataSetTableInfo> child = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(dataSetList)) {
                for (DataSetResp dataSet : dataSetList) {
                    DataSetTableInfo tree = new DataSetTableInfo();
                    tree.setId(dataSet.getId());
                    tree.setCode(dataSet.getCode());
                    tree.setToTableName(dataSet.getTableName());
                    tree.setToTableDesc(dataSet.getTableDesc());
                    tree.setTitle(dataSet.getTableDesc());
                    tree.setValue(dataSet.getCode());
                    tree.setIsFile(dataSet.getIsFile());
                    child.add(tree);
                }
                fileTree.setChildren(child);
            }
            dataSetTree.add(fileTree);
        }
        return dataSetTree;

//        // 查询所有数据集
//        List<BiDataSet> dataSetList;
//        if (StringUtils.equals(superUserFlag, YesOrNoEnum.YES.getKey())) {
//            LambdaQueryWrapper<BiDataSet> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(BiDataSet::getIsFile, YesOrNoEnum.NO.getKey());
//            queryWrapper.eq(BiDataSet::getTenantId, ThreadLocalHolder.getTenantId());
//            dataSetList = list(queryWrapper);
//        } else {
//            SelectDataSetDto selectDataSetDto = new SelectDataSetDto();
//            selectDataSetDto.setUserId(ThreadLocalHolder.getOperator());
//            selectDataSetDto.setResourceType(ResourcesTypeEnum.DATA_SET.getCode());
//            selectDataSetDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
//            selectDataSetDto.setTenantId(ThreadLocalHolder.getTenantId());
//            selectDataSetDto.setIsFile(YesOrNoEnum.NO.getKey());
//            dataSetList = setMapper.selectDataSetCategory(selectDataSetDto);
//        }
//        return dataSetList;
    }

    @Override
    public List<TableColumn> getColumns(String code) throws Exception {
        BiDataSet biDataSet = setMapper.selectOne(new LambdaQueryWrapper<BiDataSet>()
                .eq(BiDataSet::getCode, code));
        if (null == biDataSet) {
            throw new RuntimeException("未找到目标对象");
        }
        //判断数据集类型
        DataSetTypeEnum setTypeEnum = DataSetTypeEnum.getEnumByKey(biDataSet.getType());
        List<TableColumn> columns = Lists.newArrayList();
        switch (setTypeEnum) {
            case MODEL:
            case COPY:
            case DEFAULT:
                //本地查询
                columns = dbHandler.getColumns(biDataSet.getTableName());
                break;
            default:
                if (StringUtils.isBlank(biDataSet.getRefSourceId())) {
                    throw new BizException(ResourceMessageEnum.DATA_SOURCE_NOT_EXIST.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DATA_SOURCE_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));
                }
                //远程查询
                DbContext context = dbHandler.getDbContext(biDataSet.getRefSourceId());
                context.setTableName(biDataSet.getTableName());
                // 查询表结构
                TableSchema tableSchema = dbSelector.getTableSchema(context);
                List<TableField> tableFields = tableSchema.getColumns();
                if (CollectionUtils.isNotEmpty(tableFields)) {
                    for (TableField field : tableFields) {
                        TableColumn column = new TableColumn();
                        BeanUtils.copyProperties(field, column);
                        columns.add(column);
                    }
                }
        }
        return columns;
    }

    @Override
    public TableData getDataInfoPage(GetDataSetInfoDto dto) throws Exception {
        BiDataSet dataSet = setMapper.selectById(dto.getId());
        if (null == dataSet) {
            throw new BizException(ResourceMessageEnum.DATA_SET_NOT_EXIST.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DATA_SET_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));
        }

        TableData tableData = new TableData();
        if (DataSetTypeEnum.DIRECT.getKey().equals(dataSet.getType())) {
            DbContext context = new DbContext();
            context.setDbId(dataSet.getRefSourceId());
            context.setTableName(dataSet.getTableName());
            context.setPage(dto.getPage());
            context.setSize(dto.getSize());
            tableData = dbSelector.getTableData(context);
        } else {
            if (DataSetTypeEnum.MODEL.getKey().equals(dataSet.getType())) {
                //判断是否在整理中
                BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, dataSet.getRefModelCode()));
                if (YesOrNoEnum.YES.getKey().equals(model.getSyncStatus())) {
                    throw new BizException(ResourceMessageEnum.DATA_SET_SYNC.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DATA_SET_SYNC.getMessage(), ThreadLocalHolder.getLang()));
                }
            }
            String querySql = "SELECT * FROM " + dataSet.getTableName();
            PageInfo<Map<String, Object>> pageInfo = dbHandler.executePageQuery(querySql, dto.getPage(), dto.getSize());
            if (null != pageInfo) {
                tableData.setTotal(pageInfo.getTotal());
                tableData.setMore(pageInfo.isHasNextPage());
                tableData.setRows(pageInfo.getList());
            }
        }

        if (CollectionUtils.isNotEmpty(tableData.getRows())) {
            List<TableColumn> columns = this.getColumns(dataSet.getCode());
            if (CollectionUtils.isNotEmpty(columns)) {
                Map<String, String> columnMap = this.getColumnMap(columns);
                List<Map<String, Object>> list = Lists.newArrayList();
                for (Map<String, Object> args : tableData.getRows()) {
                    LinkedHashMap<String, Object> var = Maps.newLinkedHashMap();
                    for (Map.Entry<String, Object> args1 : args.entrySet()) {
                        var.put(columnMap.get(args1.getKey()), args1.getValue());
                    }
                    list.add(var);
                }
                if (CollectionUtils.isNotEmpty(list)) {
                    tableData.setRows(list);
                }
            }
        }
        return tableData;
    }

    @Override
    public TableData getDataInfoPage(BiDataSet dataSet, Integer page, Integer size) throws Exception {
        TableData tableData = new TableData();
        if (DataSetTypeEnum.DIRECT.getKey().equals(dataSet.getType())) {
            DbContext context = new DbContext();
            context.setDbId(dataSet.getRefSourceId());
            context.setTableName(dataSet.getTableName());
            context.setPage(page);
            context.setSize(size);
            tableData = dbSelector.getTableData(context);
        } else {
            if (DataSetTypeEnum.MODEL.getKey().equals(dataSet.getType())) {
                //判断是否在整理中
                BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, dataSet.getRefModelCode()));
                if (YesOrNoEnum.YES.getKey().equals(model.getSyncStatus())) {
                    throw new BizException(ResourceMessageEnum.DATA_SET_SYNC.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DATA_SET_SYNC.getMessage(), ThreadLocalHolder.getLang()));
                }
            }
            String querySql = "SELECT * FROM " + dataSet.getTableName();
            PageInfo<Map<String, Object>> pageInfo = dbHandler.executePageQuery(querySql, page, size);
            if (null != pageInfo) {
                tableData.setTotal(pageInfo.getTotal());
                tableData.setMore(pageInfo.isHasNextPage());
                tableData.setRows(pageInfo.getList());
            }
        }

        return tableData;
    }

    @Override
    public List<Map<String, Object>> getDataInfo(String id) throws Exception {
        BiDataSet dataSet = setMapper.selectById(id);
        if (null == dataSet) {
            throw new BizException(ResourceMessageEnum.DATA_SET_NOT_EXIST.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DATA_SET_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));

        }
        String querySql = "SELECT * FROM " + dataSet.getTableName();
        List<Map<String, Object>> list;
        if (DataSetTypeEnum.DIRECT.getKey().equals(dataSet.getType())) {
            DbContext context = new DbContext();
            context.setDbId(dataSet.getRefSourceId());
            context.setQuerySql(querySql);
            list = dbSelector.executeQuery(context);
        } else {
            if (DataSetTypeEnum.MODEL.getKey().equals(dataSet.getType())) {
                //判断是否在整理中
                BiEtlModel model = modelService.getOne(new LambdaQueryWrapper<BiEtlModel>().eq(BiEtlModel::getCode, dataSet.getRefModelCode()));
                if (YesOrNoEnum.YES.getKey().equals(model.getSyncStatus())) {
                    throw new BizException(ResourceMessageEnum.DATA_SET_SYNC.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DATA_SET_SYNC.getMessage(), ThreadLocalHolder.getLang()));
                }
            }
            list = dbHandler.executeQuery(querySql);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String code, boolean canDel) {
        BiDataSet dataSet = setMapper.selectOne(new LambdaQueryWrapper<BiDataSet>().eq(BiDataSet::getCode, code));
        if (null != dataSet) {
            if (dataSet.getCreateUser().equals(BiTenantConfigController.OPERATOR)) {
                throw new BizException(ResourceMessageEnum.DEFAULT_DATA_LOCK.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.DEFAULT_DATA_LOCK.getMessage(), ThreadLocalHolder.getLang()));
            }

            if (StringUtils.isNotBlank(dataSet.getType())) {
                if (!canDel && DataSetTypeEnum.MODEL.getKey().equals(dataSet.getType())) {
                    throw new BizException(ResourceMessageEnum.DELETE_IN_MODEL.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DELETE_IN_MODEL.getMessage(), ThreadLocalHolder.getLang()));
                }
                if (DataSetTypeEnum.DEFAULT.getKey().equals(dataSet.getType())) {
                    throw new BizException(ResourceMessageEnum.DEFAULT_DATA_LOCK.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DEFAULT_DATA_LOCK.getMessage(), ThreadLocalHolder.getLang()));
                }
                if (DataSetTypeEnum.COPY.getKey().equals(dataSet.getType())) {
                    String deleteSql = "drop table " + dataSet.getTableName();
                    biEtlDbMapper.truncateTable(deleteSql);
                }
                //删除度量维度配置
                folderService.remove(new LambdaQueryWrapper<BiUiModelFolder>().eq(BiUiModelFolder::getModelId, dataSet.getCode()));
                fieldService.remove(new LambdaQueryWrapper<BiUiModelField>().eq(BiUiModelField::getModelId, dataSet.getCode()));
            } else {
                int count = setMapper.selectCount(new LambdaQueryWrapper<BiDataSet>()
                        .eq(BiDataSet::getParentId, dataSet.getId()));
                if (count > 0) {
                    throw new BizException(ResourceMessageEnum.FILE_EXIST.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.FILE_EXIST.getMessage(), ThreadLocalHolder.getLang()));
                }
            }
            setMapper.deleteById(dataSet.getId());
            //删除权限配置
            LambdaQueryWrapper<BiUiAnalyseUserResource> resourceQueryWrapper = new LambdaQueryWrapper<>();
            resourceQueryWrapper.eq(BiUiAnalyseUserResource::getResourceId, dataSet.getId());
            resourceQueryWrapper.in(BiUiAnalyseUserResource::getResourceType,
                    Lists.newArrayList(ResourcesTypeEnum.DATA_SET.getCode(), ResourcesTypeEnum.DATA_SET_CATEGORY.getCode()));
            userResourceService.remove(resourceQueryWrapper);
        }
    }

    @Override
    public void delRelationByDbId(String id) {
        List<BiDataSet> dataSetList = setMapper.selectList(new LambdaQueryWrapper<BiDataSet>()
                .eq(BiDataSet::getRefSourceId, id));
        if (CollectionUtils.isNotEmpty(dataSetList)) {
            List<String> codes = dataSetList.stream().map(BiDataSet::getCode).collect(Collectors.toList());
            //删除度量维度配置
            folderService.remove(new LambdaQueryWrapper<BiUiModelFolder>()
                    .in(BiUiModelFolder::getModelId, codes));
            fieldService.remove(new LambdaQueryWrapper<BiUiModelField>()
                    .in(BiUiModelField::getModelId, codes));
        }
    }

    @Override
    public String folderCreate(String folderName) {
        BiDataSet set = setMapper.selectOne(new LambdaQueryWrapper<BiDataSet>()
                .eq(BiDataSet::getCreateUser, ThreadLocalHolder.getOperator())
                .eq(BiDataSet::getTableDesc, folderName));
        if (null == set) {
            set = new BiDataSet();
            set.setTableName(folderName);
            set.setTableDesc(folderName);
            set.setParentId("0");
            set.setCode(GenerateCodeUtil.generate());
            set.setIsFile(YesOrNoEnum.YES.getKey());
            set.setTenantId(ThreadLocalHolder.getTenantId());
            setMapper.insert(set);
        }
        return set.getId();
    }

    @Override
    public String createCopy(String parentFolderId, String tableName, String tableNameDesc) {
        BiDataSet dataSet = setMapper.selectOne(new LambdaQueryWrapper<BiDataSet>()
                .eq(BiDataSet::getTableName, tableName)
                .eq(BiDataSet::getTableDesc, tableNameDesc));
        if (null == dataSet) {
            dataSet = new BiDataSet();
            dataSet.setCode(GenerateCodeUtil.generate());
            dataSet.setType(DataSetTypeEnum.COPY.getKey());
            dataSet.setTableName(tableName);
            dataSet.setTableDesc(tableNameDesc);
            dataSet.setParentId(parentFolderId);
            dataSet.setIsFile(YesOrNoEnum.NO.getKey());
            dataSet.setTenantId(ThreadLocalHolder.getTenantId());
            dataSet.setComments(tableNameDesc);
            setMapper.insert(dataSet);
        }
        return dataSet.getCode();
    }


    private BiEtlModel getModel(List<BiEtlModel> list, String code) {
        if (CollectionUtils.isNotEmpty(list)) {
            for (BiEtlModel model : list) {
                if (model.getCode().equals(code)) {
                    list.remove(model);
                    return model;
                }
            }
        }
        return null;
    }

    private void initDataSet(String parentId) {
        //初始化默认数据源的表作为数据集
        BiEtlDatabaseInf biEtlDatabaseInf = databaseInfService.initDatabaseInfo();

        CreateDataSetDto dataSetDto = new CreateDataSetDto();
        dataSetDto.setFolderId(parentId);
        dataSetDto.setRefSourceId(biEtlDatabaseInf.getId());
        dataSetDto.setTableName(biEtlDatabaseInf.getDbName());
        dataSetDto.setTableNameDesc(biEtlDatabaseInf.getName());
        dataSetDto.setPermissionDto(null);
        dataSetDto.setComments("默认数据集");
        create(dataSetDto);

        BiDataSet defaultTable = new BiDataSet();
        defaultTable.setCode(GenerateCodeUtil.generate());
        defaultTable.setType(DataSetTypeEnum.DEFAULT.getKey());
        defaultTable.setTableName("ORDERS_USCA_BI");
        defaultTable.setTableDesc("ORDERS_USCA_BI");
        defaultTable.setParentId(parentId);
        defaultTable.setIsFile(YesOrNoEnum.NO.getKey());
        defaultTable.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(defaultTable);

        BiDataSet chineseOrder = new BiDataSet();
        chineseOrder.setCode(GenerateCodeUtil.generate());
        chineseOrder.setType(DataSetTypeEnum.DEFAULT.getKey());
        chineseOrder.setTableName("TEST_CHINESE_ORDER");
        chineseOrder.setTableDesc("中国订单");
        chineseOrder.setParentId(parentId);
        chineseOrder.setIsFile(YesOrNoEnum.NO.getKey());
        chineseOrder.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(chineseOrder);

        BiDataSet chineseRefund = new BiDataSet();
        chineseRefund.setCode(GenerateCodeUtil.generate());
        chineseRefund.setType(DataSetTypeEnum.DEFAULT.getKey());
        chineseRefund.setTableName("TEST_CHINESE_REFUND");
        chineseRefund.setTableDesc("中国退货");
        chineseRefund.setParentId(parentId);
        chineseRefund.setIsFile(YesOrNoEnum.NO.getKey());
        chineseRefund.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(chineseRefund);

        BiDataSet chineseSalesman = new BiDataSet();
        chineseSalesman.setCode(GenerateCodeUtil.generate());
        chineseSalesman.setType(DataSetTypeEnum.DEFAULT.getKey());
        chineseSalesman.setTableName("TEST_CHINESE_SALESMAN");
        chineseSalesman.setTableDesc("中国销售员");
        chineseSalesman.setParentId(parentId);
        chineseSalesman.setIsFile(YesOrNoEnum.NO.getKey());
        chineseSalesman.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(chineseSalesman);

        BiDataSet globalOrder = new BiDataSet();
        globalOrder.setCode(GenerateCodeUtil.generate());
        globalOrder.setType(DataSetTypeEnum.DEFAULT.getKey());
        globalOrder.setTableName("TEST_GLOBAL_ORDER");
        globalOrder.setTableDesc("世界订单");
        globalOrder.setParentId(parentId);
        globalOrder.setIsFile(YesOrNoEnum.NO.getKey());
        globalOrder.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(globalOrder);
    }

    private Map<String, String> getColumnMap(List<TableColumn> columns) {
        Map<String, String> map = Maps.newHashMap();
        for (TableColumn column : columns) {
            map.put(column.getName(), column.getDesc());
        }
        return map;
    }
}
