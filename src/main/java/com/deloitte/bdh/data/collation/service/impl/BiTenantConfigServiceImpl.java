package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.properties.BdhDataSourceProperties;
import com.deloitte.bdh.common.properties.BiProperties;
import com.deloitte.bdh.common.util.GetIpAndPortUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.service.AnalyseModelService;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.PoolTypeEnum;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.service.NifiProcessService;
import com.deloitte.bdh.data.collation.service.XxJobService;
import com.deloitte.bdh.data.collation.model.BiTenantConfig;
import com.deloitte.bdh.data.collation.dao.bi.BiTenantConfigMapper;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.deloitte.bdh.data.collation.service.BiTenantConfigService;
import com.deloitte.bdh.common.base.AbstractService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-12-08
 */
@Service
@DS(DSConstant.BI_DB)
public class BiTenantConfigServiceImpl extends AbstractService<BiTenantConfigMapper, BiTenantConfig> implements BiTenantConfigService {

    @Autowired
    private BdhDataSourceProperties properties;
    @Resource
    private BiTenantConfigMapper configMapper;
    @Resource
    private NifiProcessService nifiProcessService;
    @Resource
    private XxJobService jobService;
    @Resource
    private BiProperties biProperties;
    @Resource
    private BiDataSetService dataSetService;
    @Resource
    private AnalyseModelService analyseModelService;
    @Resource
    private BiEtlModelService modelService;

    @Override
    public void init() throws Exception {
        //每个租户只会有一条数据
        BiTenantConfig config = configMapper.selectOne(new LambdaQueryWrapper<>());

        boolean insert = false;
        if (null == config) {
            insert = true;
            config = new BiTenantConfig();
            config.setCreateDate(LocalDateTime.now());
            config.setCreateUser(ThreadLocalHolder.getOperator());
            config.setType(SourceTypeEnum.Mysql.getType());
            config.setEffect(EffectEnum.DISABLE.getKey());
            config.setTenantId(ThreadLocalHolder.getTenantId());
        }
        if (null == config.getRootGroupId()) {
            config.setRootGroupId(initNifiGroup());
        }
        if (null != config.getRootGroupId() && null == config.getControllerServiceId()) {
            config.setControllerServiceId(initNifiControllerService(config.getRootGroupId()));
        }
        if (null != config.getRootGroupId() && null == config.getReaderId()) {
            config.setReaderId(initReader(config.getRootGroupId()));
        }
        if (insert) {
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }

        //调度校验
        checkTaskGroup();
//        initBiTask();
//        initEtlTask();
        modelService.initModelTree();
        //初始化数据源与数据集文件夹与数据
        dataSetService.initDataSet();
        //初始化分析默认文件夹与数据
        analyseModelService.initDefaultData();
        config.setEffect(EffectEnum.ENABLE.getKey());
        configMapper.updateById(config);
    }

    @Override
    public String getGroupId() {
        BiTenantConfig config = configMapper.selectOne(new LambdaQueryWrapper<>());
        return config.getRootGroupId();
    }

    @Override
    public String getControllerServiceId() {
        BiTenantConfig config = configMapper.selectOne(new LambdaQueryWrapper<>());
        return config.getControllerServiceId();
    }

    @Override
    public String getReaderId() {
        BiTenantConfig config = configMapper.selectOne(new LambdaQueryWrapper<>());
        return config.getReaderId();
    }

    private String initNifiGroup() {
        try {
            //调用NIFI 创建模板
            Map<String, Object> reqNifi = Maps.newHashMap();
            reqNifi.put("name", biProperties.getEvn() + "_TenantCode_" + ThreadLocalHolder.getTenantCode());
            reqNifi.put("comments", "TenantCode_" + ThreadLocalHolder.getTenantCode() + "的顶级模板");
            reqNifi.put("position", JsonUtil.JsonStrToMap(NifiProcessUtil.randPosition()));
            Map<String, Object> sourceMap = nifiProcessService.createProcessGroup(reqNifi, null);
            return MapUtils.getString(sourceMap, "id");
        } catch (Exception e) {
            log.error("initNifiGroup error:", e);
            return null;
        }
    }

    private String initNifiControllerService(String rootGroupId) {
        try {
            String datasourceName = DSConstant.BI_DB + "-" + ThreadLocalHolder.getTenantCode();
            datasourceName = datasourceName.substring(1);
            DataSourceProperty dataSourceProperty = properties.getDatasource().get(datasourceName);
            if (null == dataSourceProperty) {
                throw new RuntimeException("未找到该租户的数据库");
            }
            String userName = dataSourceProperty.getUsername();
            String passWord = dataSourceProperty.getPassword();
            String url = dataSourceProperty.getUrl().split("\\?")[0];

            //调用nifi 创建 controllerService
            Map<String, Object> createParams = Maps.newHashMap();
            createParams.put("id", rootGroupId);
            createParams.put("type", PoolTypeEnum.DBCPConnectionPool.getKey());
            createParams.put("name", biProperties.getEvn() + "_TenantCode_" + ThreadLocalHolder.getTenantCode() + "的本地mysql数据源");
            createParams.put("dbUser", userName);
            createParams.put("passWord", passWord);
            createParams.put("dbUrl", url);
            createParams.put("driverName", SourceTypeEnum.Mysql.getDriverName());
            createParams.put("driverLocations", biProperties.getMysqlDriver());
            createParams.put("comments", biProperties.getEvn() + "_TenantCode_" + ThreadLocalHolder.getTenantCode() + "的本地mysql数据源");
            Map<String, Object> sourceMap = nifiProcessService.createControllerService(createParams);
            nifiProcessService.runControllerService(MapUtils.getString(sourceMap, "id"), EffectEnum.ENABLE.getKey());
            return MapUtils.getString(sourceMap, "id");
        } catch (Exception e) {
            log.error("initNifiControllerService error:", e);
            return null;
        }
    }

    private void checkTaskGroup() {
        if (null == jobService.getGroupByTenant()) {
            if (!jobService.saveGroup()) {
                throw new RuntimeException("创建调度平台的租户编码失败");
            }
        }
    }

    private void initBiTask() throws Exception {
        String code = "BI-SYNC-" + ThreadLocalHolder.getTenantCode();
        if (null == jobService.getJob(code)) {
            jobService.add(code, GetIpAndPortUtil.getIpAndPort() + "/bi/biEtlSyncPlan/sync",
                    "0 */2 * * * ?", null);
            jobService.start(code);

        }
    }

    private void initEtlTask() throws Exception {
        String code = "BI-ETL-" + ThreadLocalHolder.getTenantCode();
        if (null == jobService.getJob(code)) {
            jobService.add(code, GetIpAndPortUtil.getIpAndPort() + "/bi/biEtlSyncPlan/etl",
                    "0 */2 * * * ?", null);
            jobService.start(code);
        }
    }


    private String initReader(String rootGroupId) {
        try {
            // 调用nifi 创建 CSVReader
            Map<String, Object> createReaderParams = Maps.newHashMap();
            createReaderParams.put("id", rootGroupId);
            createReaderParams.put("type", "org.apache.nifi.avro.AvroReader");
            createReaderParams.put("name", ThreadLocalHolder.getTenantCode() + "_CSVReader");
            createReaderParams.put("cache-size", "2000");

            Map<String, Object> sourceMap = nifiProcessService.createOtherControllerService(createReaderParams);
            nifiProcessService.runControllerService(MapUtils.getString(sourceMap, "id"), EffectEnum.ENABLE.getKey());
            return MapUtils.getString(sourceMap, "id");
        } catch (Exception e) {
            log.error("initReader error:", e);
            return null;
        }
    }
}
