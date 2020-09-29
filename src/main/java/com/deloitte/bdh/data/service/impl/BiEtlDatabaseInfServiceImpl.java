package com.deloitte.bdh.data.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.dao.bi.BiEtlDatabaseInfMapper;
import com.deloitte.bdh.data.enums.EffectEnum;
import com.deloitte.bdh.data.enums.PoolTypeEnum;
import com.deloitte.bdh.data.enums.SourceTypeEnum;
import com.deloitte.bdh.data.integration.NifiProcessService;
import com.deloitte.bdh.data.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.model.request.*;
import com.deloitte.bdh.data.model.resp.FtpUploadResult;
import com.deloitte.bdh.data.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.data.service.FtpService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
public class BiEtlDatabaseInfServiceImpl extends AbstractService<BiEtlDatabaseInfMapper, BiEtlDatabaseInf> implements BiEtlDatabaseInfService {
    private static final Logger logger = LoggerFactory.getLogger(BiEtlDatabaseInfServiceImpl.class);

    @Resource
    private BiEtlDatabaseInfMapper biEtlDatabaseInfMapper;
    @Autowired
    NifiProcessService nifiProcessService;

    @Autowired
    private FtpService ftpService;

    @Override
    public PageResult<List<BiEtlDatabaseInf>> getResources(GetResourcesDto dto) {
        LambdaQueryWrapper<BiEtlDatabaseInf> fUOLamQW = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            fUOLamQW.eq(BiEtlDatabaseInf::getTenantId, dto.getTenantId());
        }
        fUOLamQW.orderByDesc(BiEtlDatabaseInf::getCreateDate);
        PageInfo<BiEtlDatabaseInf> pageInfo = new PageInfo(this.list(fUOLamQW));
        PageResult pageResult = new PageResult(pageInfo);
        return pageResult;
    }

    @Override
    public BiEtlDatabaseInf getResource(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new RuntimeException("查看单个resource 失败:id 不能为空");
        }
        return biEtlDatabaseInfMapper.selectById(id);
    }


    @Override
    public BiEtlDatabaseInf createResource(CreateResourcesDto dto) throws Exception {
        BiEtlDatabaseInf inf = null;
        SourceTypeEnum typeEnum = SourceTypeEnum.values(dto.getType());
        switch (typeEnum) {
            case File_Csv:
                inf = createResourceFromFile(dto);
                break;
            case File_Excel:
                inf = createResourceFromFile(dto);
                break;
            case Mysql_8:
                inf = createResourceFromMysql(dto);
                break;
            case Mysql_7:
                inf = createResourceFromMysql(dto);
                break;
            default:
                logger.error("未找到对应的数据模型的类型!");

        }
        return inf;
    }

    @Override
    public BiEtlDatabaseInf uploadResource(UploadResourcesDto dto) throws Exception {
        MultipartFile file = dto.getFile();
        // 租户id
        String tenantId = dto.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            throw new BizException("租户id不能为空");
        }
        // TODO 校验租户正确性，级租户与当前用户的管理关系
        FtpUploadResult uploadResult = ftpService.uploadExcelFile(file, tenantId);
        if (uploadResult == null) {
            throw new BizException("文件上传ftp失败！");
        }

        // 初始化创建dto，调用创建数据源接口
        CreateResourcesDto createDto = new CreateResourcesDto();
        BeanUtils.copyProperties(dto, createDto);

        // 暂时将文件名称存放到数据库名称字段，文件路径存放到地址
        createDto.setAddress(uploadResult.getFilePath());
        createDto.setDbName(uploadResult.getFileName());
        createDto.setType(SourceTypeEnum.File_Csv.getType());
        BiEtlDatabaseInf inf = createResource(createDto);
        return inf;
    }

    @Override
    public BiEtlDatabaseInf runResource(RunResourcesDto dto) throws Exception {
        BiEtlDatabaseInf inf = biEtlDatabaseInfMapper.selectById(dto.getId());
        if (!dto.getEffect().equals(inf.getEffect())) {
            if (!SourceTypeEnum.File_Csv.getType().equals(inf.getType()) && !SourceTypeEnum.File_Excel.getType().equals(inf.getType())) {
                String controllerServiceId = inf.getControllerServiceId();
                Map<String, Object> sourceMap = nifiProcessService.runControllerService(controllerServiceId, inf.getEffect());
                inf.setVersion(NifiProcessUtil.getVersion(sourceMap));
            }
            inf.setEffect(dto.getEffect());
            inf.setModifiedUser(dto.getModifiedUser());
            inf.setModifiedDate(LocalDateTime.now());
            biEtlDatabaseInfMapper.updateById(inf);
        }
        return inf;
    }

    @Override
    public void delResource(String id) throws Exception {
        BiEtlDatabaseInf inf = biEtlDatabaseInfMapper.selectById(id);
        if (EffectEnum.ENABLE.getKey().equals(inf.getEffect())) {
            throw new RuntimeException("启用状态下,不允许删除");
        }

        if (!SourceTypeEnum.File_Csv.getType().equals(inf.getType()) && !SourceTypeEnum.File_Excel.getType().equals(inf.getType())) {
            String controllerServiceId = inf.getControllerServiceId();
            //todo 需要校验 该数据源是否已经被引用 以及nifi 是否允许删除特定状态的数据源
            nifiProcessService.delControllerService(controllerServiceId);
        } else {
            //todo 文件型需要删除 ,查询被哪些processor引用了,且是否运行中，先停止再删掉
//            inf.getControllerServiceId()
        }
        biEtlDatabaseInfMapper.deleteById(id);
    }

    @Override
    public BiEtlDatabaseInf updateResource(UpdateResourcesDto dto) throws Exception {
        BiEtlDatabaseInf inf = biEtlDatabaseInfMapper.selectById(dto.getId());

        //todo  被 process 引入的数据源是否不能修改
        if (EffectEnum.ENABLE.getKey().equals(inf.getEffect())) {
            throw new RuntimeException("启用中的数据源不允许修改");
        }

        if (!SourceTypeEnum.File_Csv.getType().equals(inf.getType()) && !SourceTypeEnum.File_Excel.getType().equals(inf.getType())) {
            return updateResourceFromMysql(dto);
        } else {
            return updateResourceFromFile(dto);

        }
    }


    private BiEtlDatabaseInf updateResourceFromMysql(UpdateResourcesDto dto) throws Exception {
        if (StringUtils.isAllBlank(dto.getDbName(), dto.getDbPassword(), dto.getDbUser(), dto.getPort())) {
            throw new RuntimeException(String.format("配置数据源相关参数不全:%s", JsonUtil.obj2String(dto)));
        }

        BiEtlDatabaseInf source = biEtlDatabaseInfMapper.selectById(dto.getId());
        BiEtlDatabaseInf biEtlDatabaseInf = new BiEtlDatabaseInf();
        BeanUtils.copyProperties(dto, biEtlDatabaseInf);
        //根据type 变更
        biEtlDatabaseInf.setDriverName(SourceTypeEnum.getDriverNameByType(biEtlDatabaseInf.getType()));
        biEtlDatabaseInf.setTypeName(SourceTypeEnum.getNameByType(biEtlDatabaseInf.getType()));
        biEtlDatabaseInf.setEffect(EffectEnum.DISABLE.getKey());
        biEtlDatabaseInf.setModifiedDate(LocalDateTime.now());

        //调用nifi
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("Database User", biEtlDatabaseInf.getDbUser());
        properties.put("Password", biEtlDatabaseInf.getDbPassword());
        properties.put("Database Connection URL", NifiProcessUtil.getDbUrl(biEtlDatabaseInf.getType(),
                biEtlDatabaseInf.getAddress(), biEtlDatabaseInf.getPort(), biEtlDatabaseInf.getDbName()));
        properties.put("Database Driver Class Name", source.getDriverName());
        properties.put("database-driver-locations", source.getDriverLocations());

        Map<String, Object> request = Maps.newHashMap();
        request.put("id", source.getControllerServiceId());
        request.put("name", biEtlDatabaseInf.getName());
        request.put("comments", biEtlDatabaseInf.getComments());
        request.put("properties", properties);

        Map<String, Object> sourceMap = nifiProcessService.updControllerService(request);
        biEtlDatabaseInf.setVersion(NifiProcessUtil.getVersion(sourceMap));
        biEtlDatabaseInfMapper.updateById(biEtlDatabaseInf);
        return biEtlDatabaseInf;
    }

    private BiEtlDatabaseInf updateResourceFromFile(UpdateResourcesDto dto) throws Exception {
        BiEtlDatabaseInf source = biEtlDatabaseInfMapper.selectById(dto.getId());
        if (!source.getAddress().equals(dto.getAddress())) {
            //todo 若修改文件型数据源的地址， 该数据源已经有processor，需停止processor 后，再修改

        }
        BiEtlDatabaseInf biEtlDatabaseInf = new BiEtlDatabaseInf();
        BeanUtils.copyProperties(dto, biEtlDatabaseInf);
        biEtlDatabaseInf.setModifiedDate(LocalDateTime.now());
        biEtlDatabaseInfMapper.updateById(biEtlDatabaseInf);
        return biEtlDatabaseInf;
    }


    private BiEtlDatabaseInf createResourceFromFile(CreateResourcesDto dto) throws Exception {
        BiEtlDatabaseInf inf = new BiEtlDatabaseInf();
        BeanUtils.copyProperties(dto, inf);
        inf.setPoolType(PoolTypeEnum.DBCPConnectionPool.getKey());
        inf.setDriverName(SourceTypeEnum.getDriverNameByType(inf.getType()));
        inf.setTypeName(SourceTypeEnum.getNameByType(inf.getType()));
        inf.setEffect(EffectEnum.DISABLE.getKey());
        inf.setCreateDate(LocalDateTime.now());
        inf.setModifiedDate(LocalDateTime.now());

        //调用nifi 创建 获取rootgroupid
        Map<String, Object> sourceMap = nifiProcessService.getRootGroupInfo();
        inf.setVersion("1");
        inf.setControllerServiceId(null);
        inf.setRootGroupId(MapUtils.getString(sourceMap, "parentGroupId"));
        biEtlDatabaseInfMapper.insert(inf);
        return inf;
    }

    private BiEtlDatabaseInf createResourceFromMysql(CreateResourcesDto dto) throws Exception {
        if (StringUtils.isAllBlank(dto.getDbName(), dto.getDbPassword(), dto.getDbUser(), dto.getPort())) {
            throw new RuntimeException(String.format("配置数据源相关参数不全:%s", JsonUtil.obj2String(dto)));
        }

        BiEtlDatabaseInf inf = new BiEtlDatabaseInf();
        BeanUtils.copyProperties(dto, inf);
        inf.setPoolType(PoolTypeEnum.DBCPConnectionPool.getKey());
        inf.setDriverName(SourceTypeEnum.getDriverNameByType(inf.getType()));
        inf.setTypeName(SourceTypeEnum.getNameByType(inf.getType()));

        //todo 应该读取配置
        if (SourceTypeEnum.Mysql_8.getType().equals(dto.getType())) {
            inf.setDriverLocations("/usr/java/jdk1.8.0_171/mysql-connector-java-8.0.21.jar");
        } else {
            inf.setDriverLocations("/usr/java/jdk1.8.0_171/mysql-connector-java-8.0.21.jar");
        }
        inf.setEffect(EffectEnum.DISABLE.getKey());
        inf.setCreateDate(LocalDateTime.now());
        inf.setModifiedDate(LocalDateTime.now());

        //调用nifi 创建 controllerService
        Map<String, Object> createParams = Maps.newHashMap();
        //连接池类型
        createParams.put("type", inf.getPoolType());
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

}
