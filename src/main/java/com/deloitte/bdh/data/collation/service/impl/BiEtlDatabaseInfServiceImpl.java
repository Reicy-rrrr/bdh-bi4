package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.base.MongoHelper;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlDatabaseInfMapper;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.FileTypeEnum;
import com.deloitte.bdh.data.collation.enums.PoolTypeEnum;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.BiEtlDbFile;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.data.collation.service.BiEtlDbFileService;
import com.deloitte.bdh.data.collation.service.FileReadService;
import com.deloitte.bdh.data.collation.service.FtpService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
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
    private NifiProcessService nifiProcessService;
    @Autowired
    private FtpService ftpService;
    @Autowired
    private FileReadService fileReadService;
    @Autowired
    private BiEtlDbFileService biEtlDbFileService;
    @Autowired
    private MongoHelper mongoHelper;
    @Autowired
    private DbSelector dbSelector;

    @Override
    public PageResult<List<BiEtlDatabaseInf>> getResources(GetResourcesDto dto) {
        LambdaQueryWrapper<BiEtlDatabaseInf> fUOLamQW = new LambdaQueryWrapper();
        if (!StringUtil.isEmpty(dto.getTenantId())) {
            fUOLamQW.eq(BiEtlDatabaseInf::getTenantId, dto.getTenantId());
        }
        // 根据数据源名称模糊查询
        if (StringUtils.isNotBlank(dto.getName())) {
            fUOLamQW.like(BiEtlDatabaseInf::getName, dto.getName());
        }
        if (StringUtils.isNotBlank(dto.getEffect())) {
            fUOLamQW.eq(BiEtlDatabaseInf::getEffect, dto.getEffect());
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
            case Hana:
                inf = createResourceFromDB(dto);
                break;
            case File_Csv:
                inf = createResourceFromFile(dto);
                break;
            case File_Excel:
                inf = createResourceFromFile(dto);
                break;
            case Hive2:
                inf = createResourceFromHive(dto);
                break;
            case Hive:
                inf = createResourceFromHive(dto);
                break;
            case SQLServer:
                inf = createResourceFromDB(dto);
                break;
            case Oracle:
                inf = createResourceFromDB(dto);
                break;
            case Mysql:
                inf = createResourceFromDB(dto);
                break;
            default:
                logger.error("未找到对应的数据模型的类型!");

        }
        return inf;
    }

    @Override
    public BiEtlDatabaseInf createFileResource(CreateFileResourcesDto dto) throws Exception {
        // 文件信息id
        String fileId = dto.getFileId();
        if (StringUtils.isBlank(fileId)) {
            logger.error("保存文件型数据源失败，接收到的文件信息id为空！");
            throw new BizException("保存文件型数据源失败，文件信息id不能为空！");
        }

        BiEtlDbFile dbFile = biEtlDbFileService.getById(fileId);
        if (dbFile == null) {
            logger.error("保存文件型数据源失败，根据id[{}]未查询到文件信息！", fileId);
            throw new BizException("保存文件型数据源失败，未查询到文件信息！");
        }
        // 如果文件状态为已读，则不允许在重复保存
        if (dbFile.getReadFlag() == 0) {
            logger.error("保存文件型数据源失败，根据id[{}]查询到文件已经读取过！", fileId);
            throw new BizException("保存文件型数据源失败，文件已被读取过！");
        }

        // 初始化创建dto，调用创建数据源接口
        CreateResourcesDto createDto = new CreateResourcesDto();
        String fileType = dbFile.getFileType();
        BeanUtils.copyProperties(dto, createDto);
        if (fileType.endsWith(FileTypeEnum.Csv.getValue())) {
            createDto.setType(SourceTypeEnum.File_Csv.getType());
        } else {
            createDto.setType(SourceTypeEnum.File_Excel.getType());
        }
        createDto.setCreateUser(dto.getOperator());
        BiEtlDatabaseInf inf = createResource(createDto);

        // 根据数据源信息初始化mongodb集合名称
        String collectionName = initMongoCollectionName(inf);
        // 将mongodb集合名称暂存到数据源的数据库名称字段中
        inf.setDbName(collectionName);
        this.updateById(inf);

        String fileName = dbFile.getStoredFileName();
        String filePath = dbFile.getFilePath();
        // 从ftp服务器获取文件
        byte[] fileBytes = ftpService.getFileBytes(filePath, fileName);
        // 读取文件
        fileReadService.read(fileBytes, fileType, dto.getColumns(), collectionName);
        // 设置文件的关联数据源id
        dbFile.setDbId(inf.getId());
        // 修改文件状态为已读
        dbFile.setReadFlag(0);
        biEtlDbFileService.updateById(dbFile);
        return inf;
    }

    @Override
    public BiEtlDatabaseInf appendFileResource(AppendFileResourcesDto dto) throws Exception {
        // 数据源id
        String dbId = dto.getDbId();
        if (StringUtils.isBlank(dbId)) {
            logger.error("追加文件型数据源失败，接收到的数据源id为空！");
            throw new BizException("追加文件型数据源失败，数据源id不能为空！");
        }
        // 文件信息id
        String fileId = dto.getFileId();
        if (StringUtils.isBlank(fileId)) {
            logger.error("追加文件型数据源失败，接收到的文件信息id为空！");
            throw new BizException("追加文件型数据源失败，文件信息id不能为空！");
        }

        BiEtlDatabaseInf database = this.getById(dbId);
        if (database == null) {
            logger.error("未查询到数据源，错误的id[{}]", dbId);
            throw new BizException("未查询到数据源，错误的id[{" + dbId + "}]");
        }
        SourceTypeEnum sourceType = SourceTypeEnum.values(database.getType());
        if (SourceTypeEnum.File_Excel != sourceType && SourceTypeEnum.File_Csv != sourceType) {
            logger.error("该数据源不是文件型，数据源类型[{}]", sourceType.getTypeName());
            throw new BizException("未查询到文件型数据源，错误的id[{" + dbId + "}]");
        }

        BiEtlDbFile dbFile = biEtlDbFileService.getById(fileId);
        if (dbFile == null) {
            logger.error("追加文件型数据源失败，根据id[{}]未查询到文件信息！", fileId);
            throw new BizException("追加文件型数据源失败，未查询到文件信息！");
        }
        // 如果文件状态为已读，则不允许在重复保存
        if (dbFile.getReadFlag() == 0) {
            logger.error("追加文件型数据源失败，根据id[{}]查询到文件已经读取过！", fileId);
            throw new BizException("追加文件型数据源失败，文件已被读取过！");
        }

        // 获取已有集合名称
        String collectionName = database.getDbName();
        // 读取文件
        String fileName = dbFile.getStoredFileName();
        String filePath = dbFile.getFilePath();
        // 从ftp服务器获取文件
        byte[] fileBytes = ftpService.getFileBytes(filePath, fileName);
        // 读取文件
        String fileType = dbFile.getFileType();
        fileReadService.read(fileBytes, fileType, dto.getColumns(), collectionName);
        // 设置文件的关联数据源id
        dbFile.setDbId(database.getId());
        // 修改文件状态为已读
        dbFile.setReadFlag(0);
        biEtlDbFileService.updateById(dbFile);
        return database;
    }

    @Override
    public BiEtlDatabaseInf resetFileResource(ResetFileResourcesDto dto) throws Exception {
        // 数据源id
        String dbId = dto.getDbId();
        if (StringUtils.isBlank(dbId)) {
            logger.error("重置文件型数据源失败，接收到的数据源id为空！");
            throw new BizException("重置文件型数据源失败，数据源id不能为空！");
        }
        // 文件信息id
        String fileId = dto.getFileId();
        if (StringUtils.isBlank(fileId)) {
            logger.error("重置文件型数据源失败，接收到的文件信息id为空！");
            throw new BizException("重置文件型数据源失败，文件信息id不能为空！");
        }

        BiEtlDatabaseInf database = this.getById(dbId);
        if (database == null) {
            logger.error("未查询到数据源，错误的id[{}]", dbId);
            throw new BizException("未查询到数据源，错误的id[{" + dbId + "}]");
        }
        SourceTypeEnum sourceType = SourceTypeEnum.values(database.getType());
        if (SourceTypeEnum.File_Excel != sourceType && SourceTypeEnum.File_Csv != sourceType) {
            logger.error("该数据源不是文件型，数据源类型[{}]", sourceType.getTypeName());
            throw new BizException("未查询到文件型数据源，错误的id[{" + dbId + "}]");
        }

        BiEtlDbFile dbFile = biEtlDbFileService.getById(fileId);
        if (dbFile == null) {
            logger.error("重置文件型数据源失败，根据id[{}]未查询到文件信息！", fileId);
            throw new BizException("重置文件型数据源失败，未查询到文件信息！");
        }
        // 如果文件状态为已读，则不允许在重复保存
        if (dbFile.getReadFlag() == 0) {
            logger.error("重置文件型数据源失败，根据id[{}]查询到文件已经读取过！", fileId);
            throw new BizException("重置文件型数据源失败，文件已被读取过！");
        }

        // 清空集合中已有的数据
        String collectionName = database.getDbName();
        mongoHelper.removeAll(collectionName);
        // 删除文件信息及已上传ftp服务器的文件
        biEtlDbFileService.deleteByDbId(dbId);

        // 读取新文件
        String fileName = dbFile.getStoredFileName();
        String filePath = dbFile.getFilePath();
        // 从ftp服务器获取文件
        byte[] fileBytes = ftpService.getFileBytes(filePath, fileName);
        // 读取文件
        String fileType = dbFile.getFileType();
        fileReadService.read(fileBytes, fileType, dto.getColumns(), collectionName);
        // 设置文件的关联数据源id
        dbFile.setDbId(database.getId());
        // 修改文件状态为已读
        dbFile.setReadFlag(0);
        biEtlDbFileService.updateById(dbFile);
        return database;
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

    @Override
    public String testConnection(TestConnectionDto dto) throws Exception {
        DbContext context = new DbContext();
        context.setSourceTypeEnum(SourceTypeEnum.values(dto.getDbType()));
        context.setDbUrl(NifiProcessUtil.getDbUrl(dto.getDbType(), dto.getIp(), dto.getPort(), dto.getDbName()));
        context.setDbUserName(dto.getDbUserName());
        context.setDbPassword(dto.getDbPassword());
        context.setDriverName(SourceTypeEnum.getDriverNameByType(dto.getDbType()));
        return dbSelector.test(context);
    }

    @Override
    public List<String> getTables(String dbId) throws Exception {
        DbContext context = new DbContext();
        context.setDbId(dbId);
        return dbSelector.getTables(context);
    }

    @Override
    public List<String> getFields(String dbId, String tableName) throws Exception {
        DbContext context = new DbContext();
        context.setDbId(dbId);
        context.setTableName(tableName);
        return dbSelector.getFields(context);
    }

    @Override
    public TableSchema getTableSchema(GetTableSchemaDto dto) throws Exception {
        DbContext context = new DbContext();
        context.setDbId(dto.getDbId());
        context.setTableName(dto.getTableName());
        TableSchema schema = dbSelector.getTableSchema(context);
        return schema;
    }

    @Override
    public TableData getTableData(GetTableDataDto dto) throws Exception {
        DbContext context = new DbContext();
        context.setDbId(dto.getDbId());
        context.setTableName(dto.getTableName());
        context.setPage(dto.getPage());
        context.setSize(dto.getSize());
        TableData data = dbSelector.getTableData(context);
        return data;
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
        inf.setTypeName(SourceTypeEnum.getNameByType(inf.getType()));
        inf.setEffect(EffectEnum.DISABLE.getKey());
        inf.setCreateDate(LocalDateTime.now());
        inf.setModifiedDate(LocalDateTime.now());

        // 调用nifi 创建 获取rootgroupid
        Map<String, Object> sourceMap = nifiProcessService.getRootGroupInfo();
        inf.setVersion("1");
        inf.setControllerServiceId(null);
        Map groupFlow = MapUtils.getMap(sourceMap, "processGroupFlow");
        inf.setRootGroupId(MapUtils.getString(groupFlow, "id"));
        int insert = biEtlDatabaseInfMapper.insert(inf);

        /*// 调用nifi 创建 AvroSchemaRegistry
        Map<String, Object> createRegistryParams = Maps.newHashMap();
        createRegistryParams.put("type", PoolTypeEnum.AvroSchemaRegistry.getKey());
        createRegistryParams.put("name", PoolTypeEnum.AvroSchemaRegistry.getvalue() + System.currentTimeMillis());
        createRegistryParams.put("avro-reg-validated-field-names", "true");
        createRegistryParams.put("records", jsonTemplate);
        Map<String, Object> registryMap = nifiProcessService.createOtherControllerService(createRegistryParams);

        String registryId = MapUtils.getString(registryMap, "id");
        BiEtlDbService registryCS = new BiEtlDbService();
        registryCS.setServiceId(registryId);
        registryCS.setDbId(inf.getId());
        registryCS.setPropertyName("schema-registry");
        registryCS.setTenantId(inf.getTenantId());
        registryCS.setCreateDate(inf.getCreateDate());
        registryCS.setCreateUser(inf.getCreateUser());
        biEtlDbCSService.insert(registryCS);

        // 调用nifi 创建 CSVReader
        Map<String, Object> createReaderParams = Maps.newHashMap();
        createReaderParams.put("type", PoolTypeEnum.CSVReader.getKey());
        createReaderParams.put("name", PoolTypeEnum.CSVReader.getvalue() + System.currentTimeMillis());
        createReaderParams.put("schema-access-strategy", "schema-name");
        createReaderParams.put("schema-registry", registryId);
        // Treat First Line as Header
        createReaderParams.put("Skip Header Line", "true");
        // Ignore CSV Header Column Names
        createReaderParams.put("ignore-csv-header", "false");

        Map<String, Object> readerMap = nifiProcessService.createOtherControllerService(createReaderParams);

        BiEtlDbService readerCS = new BiEtlDbService();
        readerCS.setServiceId(MapUtils.getString(readerMap, "id"));
        readerCS.setDbId(inf.getId());
        readerCS.setPropertyName("record-reader");
        readerCS.setTenantId(inf.getTenantId());
        readerCS.setCreateDate(inf.getCreateDate());
        readerCS.setCreateUser(inf.getCreateUser());
        biEtlDbCSService.insert(readerCS);

        // 调用nifi 创建 JsonRecordSetWriter
        Map<String, Object> createWriterParams = Maps.newHashMap();
        createWriterParams.put("type", PoolTypeEnum.JsonRecordSetWriter.getKey());
        createWriterParams.put("name", PoolTypeEnum.JsonRecordSetWriter.getvalue() + System.currentTimeMillis());
        createWriterParams.put("schema-access-strategy", "schema-name");
        createWriterParams.put("schema-registry", registryId);
        Map<String, Object> writerMap = nifiProcessService.createOtherControllerService(createWriterParams);

        BiEtlDbService writerCS = new BiEtlDbService();
        writerCS.setServiceId(MapUtils.getString(writerMap, "id"));
        writerCS.setDbId(inf.getId());
        writerCS.setPropertyName("record-writer");
        writerCS.setTenantId(inf.getTenantId());
        writerCS.setCreateDate(inf.getCreateDate());
        writerCS.setCreateUser(inf.getCreateUser());
        biEtlDbCSService.insert(writerCS);*/
        return inf;
    }

    private BiEtlDatabaseInf createResourceFromDB(CreateResourcesDto dto) throws Exception {
        if (StringUtils.isAnyBlank(dto.getDbName(), dto.getDbPassword(), dto.getDbUser(), dto.getPort())) {
            throw new RuntimeException(String.format("配置数据源相关参数不全:%s", JsonUtil.obj2String(dto)));
        }

        BiEtlDatabaseInf inf = new BiEtlDatabaseInf();
        BeanUtils.copyProperties(dto, inf);
        inf.setPoolType(PoolTypeEnum.DBCPConnectionPool.getKey());
        inf.setDriverName(SourceTypeEnum.getDriverNameByType(inf.getType()));
        inf.setTypeName(SourceTypeEnum.getNameByType(inf.getType()));

        //todo 应该读取配置
        if (SourceTypeEnum.Mysql.getType().equals(dto.getType())) {
            inf.setDriverLocations("/usr/java/jdk1.8.0_171/mysql-connector-java-8.0.21.jar");
        } else if (SourceTypeEnum.Oracle.getType().equals(dto.getType())) {
            inf.setDriverLocations("/usr/java/jdk1.8.0_171/ojdbc8-19.7.0.0.jar");
        } else if (SourceTypeEnum.SQLServer.getType().equals(dto.getType())) {
            inf.setDriverLocations("/usr/java/jdk1.8.0_171/sqljdbc4-4.0.jar");
        } else if (SourceTypeEnum.Hana.getType().equals(dto.getType())) {
            inf.setDriverLocations("/data/hana/ngdbc-2.3.56.jar");
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

    private BiEtlDatabaseInf createResourceFromHive(CreateResourcesDto dto) throws Exception {
        if (StringUtils.isAnyBlank(dto.getDbName(), dto.getDbPassword(), dto.getDbUser(), dto.getPort())) {
            throw new RuntimeException(String.format("配置数据源相关参数不全:%s", JsonUtil.obj2String(dto)));
        }

        BiEtlDatabaseInf inf = new BiEtlDatabaseInf();
        BeanUtils.copyProperties(dto, inf);
        inf.setPoolType(PoolTypeEnum.HiveConnectionPool.getKey());
        inf.setDriverName(SourceTypeEnum.getDriverNameByType(inf.getType()));
        inf.setTypeName(SourceTypeEnum.getNameByType(inf.getType()));

        inf.setEffect(EffectEnum.DISABLE.getKey());
        inf.setCreateDate(LocalDateTime.now());
        inf.setModifiedDate(LocalDateTime.now());

        // 调用nifi 创建 controllerService
        Map<String, Object> createParams = Maps.newHashMap();
        // 连接池类型
        createParams.put("type", inf.getPoolType());
        createParams.put("name", inf.getName());
        createParams.put("comments", inf.getComments());

        createParams.put("hive-db-connect-url", NifiProcessUtil.getDbUrl(inf.getType(), inf.getAddress(), inf.getPort(), inf.getDbName()));
        createParams.put("hive-config-resources", "/data/hive-site.xml");
        createParams.put("hive-db-user", inf.getDbUser());
        createParams.put("hive-db-password", inf.getDbPassword());
        Map<String, Object> sourceMap = nifiProcessService.createOtherControllerService(createParams);

        //nifi 返回后设置补充dto
        inf.setVersion(NifiProcessUtil.getVersion(sourceMap));
        inf.setControllerServiceId(MapUtils.getString(sourceMap, "id"));
        inf.setRootGroupId(MapUtils.getString(sourceMap, "parentGroupId"));
        biEtlDatabaseInfMapper.insert(inf);
        return inf;
    }

    /**
     * 初始化生成mongo集合名称
     * 默认：租户id + "_" + yyyyMMdd + "_" + dbId
     *
     * @param inf
     * @return
     */
    private String initMongoCollectionName(BiEtlDatabaseInf inf) {
        if (inf == null) {
            return null;
        }

        StringBuilder collectionName = new StringBuilder(32);
        String now = DateUtils.formatShortDate(new Date());
        collectionName.append(inf.getTenantId()).append("_").append(now).append("_").append(inf.getId());
        return collectionName.toString();
    }
}
