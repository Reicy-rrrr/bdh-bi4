package com.deloitte.bdh.data.collation.service.impl;

import com.deloitte.bdh.data.analyse.enums.PermittedActionEnum;
import com.deloitte.bdh.data.analyse.enums.ResourcesTypeEnum;
import com.deloitte.bdh.data.analyse.model.request.SelectCategoryDto;
import com.deloitte.bdh.data.analyse.service.AnalyseUserResourceService;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.service.BiComponentService;
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
import com.deloitte.bdh.data.collation.model.request.CreateDataSetDto;
import com.deloitte.bdh.data.collation.model.request.CreateDataSetFileDto;
import com.deloitte.bdh.data.collation.model.request.DataSetReNameDto;
import com.deloitte.bdh.data.collation.model.request.GetDataSetInfoDto;
import com.deloitte.bdh.data.collation.model.request.GetDataSetPageDto;
import com.deloitte.bdh.data.collation.model.resp.DataSetResp;
import com.deloitte.bdh.data.collation.service.BiComponentParamsService;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private BiComponentParamsService componentParamsService;
    @Autowired
    private BiComponentService componentService;
    @Resource
    private DbHandler dbHandler;
    @Resource
    private DbSelector dbSelector;

    @Resource
    AnalyseUserResourceService userResourceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BiDataSet> getFiles() {
//        List<BiDataSet> setList = setMapper.selectList(new LambdaQueryWrapper<BiDataSet>()
//                .eq(BiDataSet::getParentId, "0")
//                .eq(BiDataSet::getIsFile, YesOrNoEnum.YES.getKey())
//                .orderByDesc(BiDataSet::getCreateDate)
//        );

        SelectDataSetDto selectDataSetDto = new SelectDataSetDto();
        selectDataSetDto.setUserId(ThreadLocalHolder.getOperator());
        selectDataSetDto.setResourceType(ResourcesTypeEnum.DATA_SET_CATEGORY.getCode());
        selectDataSetDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
        selectDataSetDto.setTenantId(ThreadLocalHolder.getTenantId());
        selectDataSetDto.setParentId("0");
        selectDataSetDto.setIsFile(YesOrNoEnum.YES.getKey());
        List<BiDataSet> setList = setMapper.selectDataSetCategory(selectDataSetDto);

        if (CollectionUtils.isEmpty(setList)) {
            BiDataSet set = new BiDataSet();
            set.setTableName("默认文件夹");
            set.setTableDesc("默认文件夹");
            set.setParentId("0");
            set.setCode(GenerateCodeUtil.generate());
            set.setIsFile(YesOrNoEnum.YES.getKey());
            set.setTenantId(ThreadLocalHolder.getTenantId());
            setMapper.insert(set);
            setList.add(set);

            //设置初始数据
            initDataSet(set.getId());
        }
        return setList;
    }

    @Override
    public PageResult<List<DataSetResp>> getDataSetPage(GetDataSetPageDto dto) {
        List<DataSetResp> result = Lists.newArrayList();

//        LambdaQueryWrapper<BiDataSet> fUOLamQW = new LambdaQueryWrapper();
//        fUOLamQW.eq(BiDataSet::getParentId, dto.getFileId());
//        fUOLamQW.eq(BiDataSet::getIsFile, YesOrNoEnum.NO.getKey());
//        fUOLamQW.orderByDesc(BiDataSet::getCreateDate);
//        List<BiDataSet> dataSetList = setMapper.selectList(fUOLamQW);

        SelectDataSetDto selectDataSetDto = new SelectDataSetDto();
        selectDataSetDto.setUserId(ThreadLocalHolder.getOperator());
        selectDataSetDto.setResourceType(ResourcesTypeEnum.DATA_SET.getCode());
        selectDataSetDto.setPermittedAction(PermittedActionEnum.VIEW.getCode());
        selectDataSetDto.setTenantId(ThreadLocalHolder.getTenantId());
        selectDataSetDto.setParentId(dto.getFileId());
        selectDataSetDto.setIsFile(YesOrNoEnum.NO.getKey());
        List<BiDataSet> dataSetList = setMapper.selectDataSetCategory(selectDataSetDto);

        if (CollectionUtils.isNotEmpty(dataSetList)) {
            for (BiDataSet dataSet : dataSetList) {
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
        PageInfo<BiEtlModel> pageInfo = new PageInfo(result);
        PageResult<List<DataSetResp>> pageResult = new PageResult(pageInfo);
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reName(DataSetReNameDto dto) {
        BiDataSet dataSet = setMapper.selectById(dto.getId());
        if (null == dataSet) {
            throw new RuntimeException("未找到目标对象");
        }
        String newTableDesc = dto.getToTableDesc() + DataSetTypeEnum.DIRECT.getSuffix();
        if (DataSetTypeEnum.MODEL.getKey().equals(dataSet.getType())) {
            newTableDesc = dto.getToTableDesc() + DataSetTypeEnum.MODEL.getSuffix();
        }

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
                    throw new RuntimeException("未找到目标模型");
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
                    throw new RuntimeException("未找到目标模型");
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
        userResourceService.saveResourcePermission(dto.getPermissionDto());
    }

    @Override
    public void create(CreateDataSetDto dto) {
        String tableDesc = dto.getTableNameDesc() + DataSetTypeEnum.DIRECT.getSuffix();
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
        userResourceService.saveResourcePermission(dto.getPermissionDto());
    }

    @Override
    public List<BiDataSet> getTableList() {
        List<BiDataSet> results = Lists.newArrayList();
        // 查询所有数据集
        List<BiDataSet> dataSetList = setMapper.selectList(new LambdaQueryWrapper<BiDataSet>()
                .eq(BiDataSet::getIsFile, YesOrNoEnum.NO)
                .orderByDesc(BiDataSet::getCreateDate)
        );
        if (CollectionUtils.isNotEmpty(dataSetList)) {
            results.addAll(dataSetList);
        }
        return results;
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
            case DEFAULT:
                //本地查询
                columns = dbHandler.getColumns(biDataSet.getTableName());
                break;
            default:
                if (StringUtils.isBlank(biDataSet.getRefSourceId())) {
                    throw new RuntimeException("远程连接时未找到数据源目标对象");
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
    public TableData getDataSetInfoPage(GetDataSetInfoDto dto) throws Exception {
        BiDataSet dataSet = setMapper.selectById(dto.getId());
        if (null == dataSet) {
            throw new RuntimeException("未找到目标对象");
        }

        if (DataSetTypeEnum.DIRECT.getKey().equals(dataSet.getType())) {
            DbContext context = new DbContext();
            context.setDbId(dataSet.getRefSourceId());
            context.setTableName(dataSet.getTableName());
            context.setPage(dto.getPage());
            context.setSize(dto.getSize());
            return dbSelector.getTableData(context);
        }

        //本地或初始化
        TableData tableData = new TableData();
        String querySql = "SELECT * FROM " + dataSet.getTableName();
        PageInfo<Map<String, Object>> pageInfo = dbHandler.executePageQuery(querySql, dto.getPage(), dto.getSize());
        if (null != pageInfo) {
            tableData.setTotal(pageInfo.getTotal());
            tableData.setMore(pageInfo.isHasNextPage());
            tableData.setRows(pageInfo.getList());
        }
        return tableData;
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
        defaultTable.setCode(GenerateCodeUtil.generate());
        chineseOrder.setType(DataSetTypeEnum.DEFAULT.getKey());
        chineseOrder.setTableName("TEST_CHINESE_ORDER");
        chineseOrder.setTableDesc("中国订单");
        chineseOrder.setParentId(parentId);
        chineseOrder.setIsFile(YesOrNoEnum.NO.getKey());
        chineseOrder.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(chineseOrder);

        BiDataSet chineseRefund = new BiDataSet();
        defaultTable.setCode(GenerateCodeUtil.generate());
        chineseRefund.setType(DataSetTypeEnum.DEFAULT.getKey());
        chineseRefund.setTableName("TEST_CHINESE_REFUND");
        chineseRefund.setTableDesc("中国退货");
        chineseRefund.setParentId(parentId);
        chineseRefund.setIsFile(YesOrNoEnum.NO.getKey());
        chineseRefund.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(chineseRefund);

        BiDataSet chineseSalesman = new BiDataSet();
        defaultTable.setCode(GenerateCodeUtil.generate());
        chineseSalesman.setType(DataSetTypeEnum.DEFAULT.getKey());
        chineseSalesman.setTableName("TEST_CHINESE_SALESMAN");
        chineseSalesman.setTableDesc("中国销售员");
        chineseSalesman.setParentId(parentId);
        chineseSalesman.setIsFile(YesOrNoEnum.NO.getKey());
        chineseSalesman.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(chineseSalesman);

        BiDataSet globalOrder = new BiDataSet();
        defaultTable.setCode(GenerateCodeUtil.generate());
        globalOrder.setType(DataSetTypeEnum.DEFAULT.getKey());
        globalOrder.setTableName("TEST_GLOBAL_ORDER");
        globalOrder.setTableDesc("世界订单");
        globalOrder.setParentId(parentId);
        globalOrder.setIsFile(YesOrNoEnum.NO.getKey());
        globalOrder.setTenantId(ThreadLocalHolder.getTenantId());
        setMapper.insert(globalOrder);
    }
}
