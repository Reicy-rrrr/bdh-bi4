package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.data.collation.component.constant.ComponentCons;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.ComponentTypeEnum;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.model.*;
import com.deloitte.bdh.data.collation.dao.bi.BiComponentMapper;
import com.deloitte.bdh.data.collation.model.resp.BiComponentTree;
import com.deloitte.bdh.data.collation.service.*;
import com.deloitte.bdh.common.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
public class BiComponentServiceImpl extends AbstractService<BiComponentMapper, BiComponent> implements BiComponentService {

    @Resource
    private BiComponentMapper biComponentMapper;
    @Autowired
    private BiComponentParamsService componentParamsService;
    @Autowired
    private BiProcessorsService processorsService;
    @Autowired
    private BiEtlMappingConfigService configService;
    @Autowired
    private BiEtlMappingFieldService fieldService;
    @Autowired
    private DbHandler dbHandler;

    @Override
    public BiComponentTree selectTree(String modelCode, String componentCode) {
        return biComponentMapper.selectTree(modelCode, componentCode);
    }

    @Override
    public void remove(String id) throws Exception {
        BiComponent component = biComponentMapper.selectById(id);
        if (component.getType().equals(ComponentTypeEnum.DATASOURCE.getKey())) {
            List<BiComponentParams> paramsList = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                    .eq(BiComponentParams::getRefComponentCode, component.getCode())
            );
            Optional<BiComponentParams> optional = paramsList.stream()
                    .filter(p -> p.getParamKey().equals(ComponentCons.BELONG_MAPPING_CODE)).findAny();
            if (optional.isPresent()) {
                BiEtlMappingConfig config = configService.getOne(new LambdaQueryWrapper<BiEtlMappingConfig>()
                        .eq(BiEtlMappingConfig::getCode, optional.get().getCode())
                );

                if (!SyncTypeEnum.DIRECT.getKey().toString().equals(config.getType())) {
                    //删除nifi配置
                    Optional<BiComponentParams> optionalProcessorsCode = paramsList.stream()
                            .filter(p -> p.getParamKey().equals(ComponentCons.REF_PROCESSORS_CDOE)).findAny();
                    if (optionalProcessorsCode.isPresent()) {
                        processorsService.removeProcessors(optionalProcessorsCode.get().getParamValue());
                    }
                    //移除表
                    dbHandler.drop(config.getToTableName());
                }
                configService.removeById(config.getId());
                fieldService.remove(new LambdaQueryWrapper<BiEtlMappingField>()
                        .eq(BiEtlMappingField::getRefCode, config.getCode())
                );
            }
        }

        if (component.getType().equals(ComponentTypeEnum.OUT.getKey())) {
            List<BiComponentParams> paramsList = componentParamsService.list(new LambdaQueryWrapper<BiComponentParams>()
                    .eq(BiComponentParams::getRefComponentCode, component.getCode())
            );

            Optional<BiComponentParams> optionalMappingCode = paramsList.stream()
                    .filter(p -> p.getParamKey().equals(ComponentCons.BELONG_MAPPING_CODE)).findAny();
            optionalMappingCode.ifPresent(biComponentParams ->
                    fieldService.remove(new LambdaQueryWrapper<BiEtlMappingField>()
                            .eq(BiEtlMappingField::getRefCode, biComponentParams.getParamValue())
                    ));

            Optional<BiComponentParams> optionalTableName = paramsList.stream()
                    .filter(p -> p.getParamKey().equals(ComponentCons.TO_TABLE_NAME)).findAny();
            optionalTableName.ifPresent(biComponentParams -> dbHandler.drop(biComponentParams.getParamValue()));

            Optional<BiComponentParams> optionalProcessorsCode = paramsList.stream()
                    .filter(p -> p.getParamKey().equals(ComponentCons.REF_PROCESSORS_CDOE)).findAny();
            if (optionalProcessorsCode.isPresent()) {
                processorsService.removeProcessors(optionalProcessorsCode.get().getParamValue());
            }

        }

        biComponentMapper.deleteById(id);
        componentParamsService.remove(new LambdaQueryWrapper<BiComponentParams>()
                .eq(BiComponentParams::getRefComponentCode, component.getCode())
        );
    }


}
