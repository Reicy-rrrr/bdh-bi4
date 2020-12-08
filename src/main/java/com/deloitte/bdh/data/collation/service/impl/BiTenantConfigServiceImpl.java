package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.properties.BdhDataSourceProperties;
import com.deloitte.bdh.common.util.GetIpAndPortUtil;
import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.enums.PoolTypeEnum;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.integration.XxJobService;
import com.deloitte.bdh.data.collation.model.BiTenantConfig;
import com.deloitte.bdh.data.collation.dao.bi.BiTenantConfigMapper;
import com.deloitte.bdh.data.collation.service.BiTenantConfigService;
import com.deloitte.bdh.common.base.AbstractService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.SocketException;
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

    @Override
    public void init() throws Exception {
        //每个租户只会有一条数据
        BiTenantConfig config = configMapper.selectOne(new LambdaQueryWrapper<BiTenantConfig>()
                .eq(BiTenantConfig::getTenantId, ThreadLocalHolder.getTenantCode()));
        if (null == config) {
            config = new BiTenantConfig();
            config.setType(SourceTypeEnum.Mysql.getType());
        }
        if (null == config.getRootGroupId()) {
            config.setRootGroupId(initNifiGroup());
        }
        if (null != config.getRootGroupId() && null == config.getControllerServiceId()) {
            config.setControllerServiceId(initNifiControllerService(config.getRootGroupId()));
        }
        checkTaskGroup();
        initBiTask();
        initEtlTask();
        configMapper.insert(config);

    }

    private String initNifiGroup() {
        try {
            //调用NIFI 创建模板
            Map<String, Object> reqNifi = Maps.newHashMap();
            reqNifi.put("name", "ZH_" + ThreadLocalHolder.getTenantCode());
            reqNifi.put("comments", "ZH_" + ThreadLocalHolder.getTenantCode() + "的顶级模板");
            reqNifi.put("position", JsonUtil.JsonStrToMap(NifiProcessUtil.randPosition()));
            Map<String, Object> sourceMap = nifiProcessService.createProcessGroup(reqNifi, null);
            return MapUtils.getString(sourceMap, "id");
        } catch (Exception e) {
            log.error("initNifiGroup error:" + e);
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
            createParams.put("name", "ZH_" + ThreadLocalHolder.getTenantCode() + "的本地mysql数据源");
            createParams.put("dbUser", userName);
            createParams.put("passWord", passWord);
            createParams.put("dbUrl", url);
            createParams.put("driverName", SourceTypeEnum.Mysql.getDriverName());
            createParams.put("driverLocations", "/usr/java/jdk1.8.0_171/mysql-connector-java-8.0.21.jar");
            createParams.put("comments", "ZH_" + ThreadLocalHolder.getTenantCode() + "的本地mysql数据源");
            Map<String, Object> sourceMap = nifiProcessService.createControllerService(createParams);
            nifiProcessService.runControllerService(MapUtils.getString(sourceMap, "id"), EffectEnum.ENABLE.getKey());
            return MapUtils.getString(sourceMap, "id");
        } catch (Exception e) {
            log.error("initNifiControllerService error:" + e);
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
}
