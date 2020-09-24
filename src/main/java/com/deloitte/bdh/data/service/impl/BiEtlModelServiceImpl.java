package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.enums.EffectEnum;
import com.deloitte.bdh.data.enums.RunStatusEnum;
import com.deloitte.bdh.data.enums.SourceTypeEnum;
import com.deloitte.bdh.data.enums.YesOrNoEnum;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.model.BiEtlModel;
import com.deloitte.bdh.data.dao.bi.BiEtlModelMapper;
import com.deloitte.bdh.data.model.request.CreateModelDto;
import com.deloitte.bdh.data.model.request.GetModelPageDto;
import com.deloitte.bdh.data.model.request.RunModelDto;
import com.deloitte.bdh.data.model.request.UpdateModelDto;
import com.deloitte.bdh.data.service.BiEtlModelService;
import com.deloitte.bdh.common.base.AbstractService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-09-24
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlModelServiceImpl extends AbstractService<BiEtlModelMapper, BiEtlModel> implements BiEtlModelService {
    private static final Logger logger = LoggerFactory.getLogger(BiEtlModelServiceImpl.class);

    @Resource
    private BiEtlModelMapper biEtlModelMapper;
    @Autowired
    NifiProcessService nifiProcessService;

    @Override
    public PageResult<List<BiEtlModel>> getModelPage(GetModelPageDto dto) {
        LambdaQueryWrapper<BiEtlModel> fUOLamQW = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            fUOLamQW.eq(BiEtlModel::getTenantId, dto.getTenantId());
        }
        fUOLamQW.orderByDesc(BiEtlModel::getCreateDate);
        PageInfo<BiEtlModel> pageInfo = new PageInfo(this.list(fUOLamQW));
        PageResult<List<BiEtlModel>> pageResult = new PageResult(pageInfo);
        return pageResult;
    }

    @Override
    public BiEtlModel getModel(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个Model 失败:id 不能为空");
        }
        return this.getById(id);
    }

    @Override
    public BiEtlModel createModel(CreateModelDto dto) throws Exception {
        String modelCode = "ETL" + System.currentTimeMillis();
        //处理文件夹
        BiEtlModel inf = doFile(modelCode, dto);
        if (!StringUtil.isEmpty(inf.getCode())) {
            return inf;
        }

        //处理模板
        inf = doModel(modelCode, dto);
        return inf;
    }

    @Override
    public BiEtlModel runProcesGroup(RunModelDto dto) throws Exception {
        //todo
        return null;
    }

    @Override
    public void delModel(String id) throws Exception {
        BiEtlModel inf = biEtlModelMapper.selectById(id);

        //todo 模板删除判断下面是否有process
        if (RunStatusEnum.RUNNING.getKey().equals(inf.getStatus())) {
            throw new RuntimeException("运行状态下,不允许删除");
        }

        String processGroupId = inf.getProcessGroupId();
        Map<String, Object> sourceMap = nifiProcessService.delProcessGroup(processGroupId);
        biEtlModelMapper.deleteById(id);
        logger.info("删除数据成功:{}", JsonUtil.obj2String(sourceMap));
    }

    @Override
    public BiEtlModel updateModel(UpdateModelDto dto) throws Exception {
        BiEtlModel inf = biEtlModelMapper.selectById(dto.getId());
        //todo  被 process 引入的数据源是否不能修改
        if (RunStatusEnum.RUNNING.getKey().equals(inf.getStatus())) {
            throw new RuntimeException("运行中的 model 不允许修改");
        }
//        if (EffectEnum.ENABLE.getKey().equals(inf.getEffect())) {
//            throw new RuntimeException("启用中的 model 不允许修改");
//        }

        if (!StringUtil.isEmpty(dto.getName())) {
            inf.setName(dto.getName());
        }
        if (!StringUtil.isEmpty(dto.getComments())) {
            inf.setComments(dto.getComments());
        }
//        if (StringUtil.isEmpty(dto.getEffect())) {
//            inf.setEffect(dto.getEffect());
//        }

        //调用nifi
        Map<String, Object> reqNifi = Maps.newHashMap();
        reqNifi.put("id", inf.getProcessGroupId());
        reqNifi.put("name", inf.getName());
        reqNifi.put("comments", inf.getComments());
//        reqNifi.put("stat", inf.getComments());

        Map<String, Object> sourceMap = nifiProcessService.updProcessGroup(reqNifi);
        inf.setVersion(NifiProcessUtil.getVersion(sourceMap));
        biEtlModelMapper.updateById(inf);
        return inf;
    }


    private BiEtlModel doFile(String modelCode, CreateModelDto dto) {
        BiEtlModel inf = new BiEtlModel();
        //文件夹
        if (YesOrNoEnum.YES.getKey().equals(dto.getIsFile())) {
            inf.setCode(modelCode);
            if ("0".equals(dto.getParentCode())) {
                inf.setRootCode(modelCode);
            } else {
                //查询上级是否是文件夹
                Map<String, Object> query = Maps.newHashMap();
                query.put("code", dto.getParentCode());
                List<BiEtlModel> modelList = biEtlModelMapper.selectByMap(query);
                if (CollectionUtils.isEmpty(modelList)) {
                    throw new RuntimeException("未找到上级的文件夹信息");
                }
                if (YesOrNoEnum.NO.getKey().equals(modelList.get(0).getIsFile())) {
                    throw new RuntimeException("只能在文件夹下面创建子文件");
                }
            }
            inf.setName(dto.getName());
            inf.setComments(dto.getComments());
            inf.setCreateUser(dto.getCreateUser());
            inf.setCreateDate(LocalDateTime.now());
            biEtlModelMapper.insert(inf);
        }
        return inf;
    }


    private BiEtlModel doModel(String modelCode, CreateModelDto dto) throws Exception {
        BiEtlModel inf = new BiEtlModel();
        BeanUtils.copyProperties(dto, inf);
        inf.setCode(modelCode);
        if (StringUtil.isEmpty(inf.getPosition())) {
            inf.setPosition(NifiProcessUtil.randPosition());
        }
        if (!"0".equals(dto.getParentCode())) {
            throw new RuntimeException("非文件夹模式下只能创建ETL模板");
        }
        //生效、失效的状态
        inf.setEffect(EffectEnum.ENABLE.getKey());
        //初始化 为未运行状态 对应nifi stopped RUNNIG
        inf.setStatus(RunStatusEnum.STOP.getKey());
        inf.setCreateDate(LocalDateTime.now());
        // 设置 validate
        inf.setValidate(YesOrNoEnum.NO.getKey());
        inf.setIp("");

        //调用NIFI 创建模板
        Map<String, Object> reqNifi = Maps.newHashMap();
        reqNifi.put("name", inf.getName());
        reqNifi.put("comments", inf.getComments());
        reqNifi.put("position", JsonUtil.string2Obj(inf.getPosition(), Map.class));
        Map<String, Object> sourceMap = nifiProcessService.createProcessGroup(reqNifi, null);

        //nifi 返回后设置补充dto
        inf.setVersion(NifiProcessUtil.getVersion(sourceMap));
        inf.setProcessGroupId(MapUtils.getString(sourceMap, "id"));
        biEtlModelMapper.insert(inf);
        return inf;
    }
}
