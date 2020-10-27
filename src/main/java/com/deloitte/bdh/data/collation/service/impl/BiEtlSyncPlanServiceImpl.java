package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.enums.PlanStatusEnum;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.model.BiEtlSyncPlan;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlSyncPlanMapper;
import com.deloitte.bdh.data.collation.model.BiProcessors;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.data.collation.service.BiProcessorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
@Service
@DS(DSConstant.BI_DB)
public class BiEtlSyncPlanServiceImpl extends AbstractService<BiEtlSyncPlanMapper, BiEtlSyncPlan> implements BiEtlSyncPlanService {

    @Resource
    private BiEtlSyncPlanMapper syncPlanMapper;
    @Autowired
    private BiEtlMappingConfigService configService;
    @Autowired
    private BiProcessorsService processorsService;

    @Override
    public void process(String type) throws Exception {
        if ("0".equals(type)) {
            sync();
        } else {
            etl();
        }
    }

    private void sync() {
        //寻找类型为同步，状态为待执行的计划 todo limit
        List<BiEtlSyncPlan> list = syncPlanMapper.selectList(new LambdaQueryWrapper<BiEtlSyncPlan>()
                .eq(BiEtlSyncPlan::getPlanType, "0")
                .eq(BiEtlSyncPlan::getPlanStatus, PlanStatusEnum.TO_EXECUTE.getKey())
                .orderByAsc(BiEtlSyncPlan::getCreateDate)
        );
        list.forEach(s -> {
            //组装数据 启动nifi 改变执行状态
            BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                    .eq(BiEtlMappingConfig::getCode, s.getRefMappingCode())
            );

            //是第一次 还是 定时同步？是增量还是全量？
            SyncTypeEnum typeEnum = SyncTypeEnum.getEnumByKey(config.getType());
            //todo 基于条件组装清空语句

            //启动NIFI
            BiProcessors processors = processorsService.getOne(new LambdaQueryWrapper<BiProcessors>()
                    .eq(BiProcessors::getCode, config.getRefProcessorsCode())
            );

            //todo
        });
    }

    private void etl() {

    }
}
