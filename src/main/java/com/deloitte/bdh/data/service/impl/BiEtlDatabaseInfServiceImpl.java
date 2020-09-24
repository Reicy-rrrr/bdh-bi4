package com.deloitte.bdh.data.service.impl;

import java.time.LocalDateTime;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.enums.ControllerServieStateEnum;
import com.deloitte.bdh.data.enums.EffectEnum;
import com.deloitte.bdh.data.enums.PoolTypeEnum;
import com.deloitte.bdh.data.enums.SourceTypeEnum;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.integration.impl.NifiProcessServiceImpl;
import com.deloitte.bdh.data.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.dao.bi.BiEtlDatabaseInfMapper;
import com.deloitte.bdh.data.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.model.request.GetResourcesDto;
import com.deloitte.bdh.data.model.request.RunResourcesDto;
import com.deloitte.bdh.data.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.common.base.AbstractService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class BiEtlDatabaseInfServiceImpl extends AbstractService<BiEtlDatabaseInfMapper, BiEtlDatabaseInf> implements BiEtlDatabaseInfService {
    private static final Logger logger = LoggerFactory.getLogger(BiEtlDatabaseInfServiceImpl.class);

    @Resource
    private BiEtlDatabaseInfMapper biEtlDatabaseInfMapper;
    @Autowired
    NifiProcessService nifiProcessService;

    @Override
    public PageResult<List<BiEtlDatabaseInf>> getResources(GetResourcesDto dto) {
        PageHelper.startPage(dto.getPage(), dto.getSize());

        LambdaQueryWrapper<BiEtlDatabaseInf> fUOLamQW = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            fUOLamQW.eq(BiEtlDatabaseInf::getTenantId, dto.getTenantId());
        }

        PageInfo<BiEtlDatabaseInf> pageInfo = new PageInfo(this.list(fUOLamQW));
        PageResult pageResult = new PageResult(pageInfo);
        return pageResult;
    }


    @Override
    public BiEtlDatabaseInf createResource(CreateResourcesDto dto) throws Exception {
        //组装dto
        BiEtlDatabaseInf inf = new BiEtlDatabaseInf();
        BeanUtils.copyProperties(dto, inf);
        inf.setTypeDesc(PoolTypeEnum.DBCPConnectionPool.getKey());
        inf.setDriverName(SourceTypeEnum.getDriverNameByType(inf.getType()));
        //todo 应该读取配置
        inf.setDriverLocations("/usr/java/jdk1.8.0_171/mysql-connector-java-8.0.21.jar");
        inf.setEffect(EffectEnum.NO.getKey());
        inf.setCreateDate(LocalDateTime.now());
        inf.setModifiedDate(LocalDateTime.now());
        //todo
        inf.setIp("");

        //调用nifi 创建 controllerService
        Map<String, Object> createParams = Maps.newHashMap();
        //连接池类型
        createParams.put("type", inf.getTypeDesc());
        createParams.put("name", inf.getName());
        createParams.put("dbUser", inf.getDbUser());
        createParams.put("passWord", inf.getDbPassword());
        createParams.put("dbUrl", NifiProcessUtil.getDbUrl(inf.getType(), inf.getAddress(), inf.getPort(), inf.getDbName()));
        createParams.put("driverName", inf.getDriverName());
        createParams.put("driverLocations", inf.getDriverLocations());
        createParams.put("comments", inf.getComments());
        Map<String, Object> sourceMap = nifiProcessService.createControllerService(createParams);

        //nifi 返回后设置补充dto
        inf.setVersion(NifiProcessUtil.getVersion(sourceMap));
        inf.setControllerServiceId(MapUtils.getString(sourceMap, "id"));
        inf.setRootGroupId(MapUtils.getString(sourceMap, "parentGroupId"));
        biEtlDatabaseInfMapper.insert(inf);
        return inf;
    }

    @Override
    public BiEtlDatabaseInf runResource(RunResourcesDto dto) throws Exception {
        BiEtlDatabaseInf inf = biEtlDatabaseInfMapper.selectById(dto.getId());
        inf.setEffect(dto.getEffect());

        String controllerServiceId = inf.getControllerServiceId();
        String state = EffectEnum.YES.getKey().equals(inf.getEffect())
                ? ControllerServieStateEnum.ENABLED.getKey() : ControllerServieStateEnum.DISABLED.getKey();

        Map<String, Object> sourceMap = nifiProcessService.runControllerService(controllerServiceId, state);

        inf.setVersion(NifiProcessUtil.getVersion(sourceMap));
        inf.setModifiedUser(dto.getModifiedUser());
        inf.setModifiedDate(LocalDateTime.now());
        biEtlDatabaseInfMapper.updateById(inf);
        return inf;
    }

    @Override
    public void delResource(String id) throws Exception {
        BiEtlDatabaseInf inf = biEtlDatabaseInfMapper.selectById(id);
        if (EffectEnum.YES.getKey().equals(inf.getEffect())) {
            throw new RuntimeException("启用状态下,不允许删除");
        }
        String controllerServiceId = inf.getControllerServiceId();

        //todo 需要校验 该数据源是否已经被引用 以及nifi 是否允许删除特定状态的数据源
        Map<String, Object> sourceMap = nifiProcessService.delControllerService(controllerServiceId);
        biEtlDatabaseInfMapper.deleteById(id);
        logger.info("删除数据成功:{}", JsonUtil.obj2String(sourceMap));
    }
}
