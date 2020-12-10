package com.deloitte.bdh.data.collation.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.dao.bi.BiDataSetMapper;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.request.DataSetReNameDto;
import com.deloitte.bdh.data.collation.model.request.GetDataSetPageDto;
import com.deloitte.bdh.data.collation.model.resp.DataSetResp;
import com.deloitte.bdh.data.collation.service.BiComponentParamsService;
import com.deloitte.bdh.data.collation.service.BiComponentService;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
    private BiComponentService componentService;
    @Autowired
    private BiComponentParamsService componentParamsService;

    @Override
    public List<BiDataSet> getFiles() {
        List<BiDataSet> setList = setMapper.selectList(new LambdaQueryWrapper<BiDataSet>()
                .eq(BiDataSet::getParentId, "0")
                .eq(BiDataSet::getIsFile, YesOrNoEnum.YES.getKey())
                .orderByDesc(BiDataSet::getCreateDate)
        );

        if (CollectionUtils.isEmpty(setList)) {
            BiDataSet set = new BiDataSet();
            set.setTableName("默认文件夹");
            set.setTableDesc("默认文件夹");
            set.setParentId("0");
            set.setIsFile(YesOrNoEnum.YES.getKey());
            set.setTenantId(ThreadLocalHolder.getTenantId());
            setMapper.insert(set);
            setList.add(set);
        }
        return setList;
    }

    @Override
    public PageResult<List<DataSetResp>> getDataSetPage(GetDataSetPageDto dto) {
        List<DataSetResp> result = Lists.newArrayList();

        LambdaQueryWrapper<BiDataSet> fUOLamQW = new LambdaQueryWrapper();
        fUOLamQW.eq(BiDataSet::getParentId, dto.getFileId());
        fUOLamQW.eq(BiDataSet::getIsFile, YesOrNoEnum.NO.getKey());
        fUOLamQW.orderByDesc(BiDataSet::getCreateDate);
        List<BiDataSet> dataSetList = setMapper.selectList(fUOLamQW);

        if (CollectionUtils.isNotEmpty(dataSetList)) {
            for (BiDataSet dataSet : dataSetList) {
                DataSetResp dataSetResp = new DataSetResp();
                BeanUtils.copyProperties(dataSet, dataSetResp);
                result.add(dataSetResp);
            }
            //获取模板编码
            List<String> modelCodeList = result.stream().map(DataSetResp::getRefModelCode).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(modelCodeList)) {
                //获取model与component
                List<BiEtlModel> modelList = modelService.list(new LambdaQueryWrapper<BiEtlModel>().in(BiEtlModel::getCode, modelCodeList));

                for (BiDataSet dataSet : dataSetList) {
                    DataSetResp dataSetResp = new DataSetResp();
                    BeanUtils.copyProperties(dataSet, dataSetResp);
                    BiEtlModel model = getModel(modelList, dataSet.getRefModelCode());
                    if (null != model) {
                        dataSetResp.setModelName(model.getName());
                        if (null != model.getLastExecuteDate()) {
                            dataSetResp.setLastExecuteDate(model.getLastExecuteDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        }
                    }
                    result.add(dataSetResp);
                }
            }
        }
        PageInfo<BiEtlModel> pageInfo = new PageInfo(result);
        PageResult<List<DataSetResp>> pageResult = new PageResult(pageInfo);
        return pageResult;
    }

    @Override
    public void reName(DataSetReNameDto dto) {
        BiComponentParams param = componentParamsService.getOne(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getParamKey, ComponentCons.TO_TABLE_DESC)
                .eq(BiComponentParams::getRefModelCode, dto.getCode())
        );
        if (null == param) {
            throw new RuntimeException("未找到目标模型");
        }
        if (!param.getParamValue().equals(dto.getToTableDesc())) {
            List<BiComponentParams> allTableDesc = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                    .eq(BiComponentParams::getParamKey, ComponentCons.TO_TABLE_DESC)
                    .ne(BiComponentParams::getRefModelCode, dto.getCode())
            );
            if (CollectionUtils.isNotEmpty(allTableDesc)) {
                Optional<BiComponentParams> optional = allTableDesc.stream()
                        .filter(p -> p.getParamValue().equals(dto.getToTableDesc())).findAny();

                if (optional.isPresent()) {
                    throw new RuntimeException("存在相同的表名称");
                }
            }
            param.setParamValue(dto.getToTableDesc());
            componentParamsService.updateById(param);
        }
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

}
