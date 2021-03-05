package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.enums.EffectEnum;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.collation.model.BiEtlMappingConfig;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlMappingConfigMapper;
import com.deloitte.bdh.data.collation.model.BiEtlMappingField;
import com.deloitte.bdh.data.collation.service.BiEtlDatabaseInfService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingConfigService;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.data.collation.service.BiEtlMappingFieldService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
public class BiEtlMappingConfigServiceImpl extends AbstractService<BiEtlMappingConfigMapper, BiEtlMappingConfig> implements BiEtlMappingConfigService {
    @Resource
    private BiEtlMappingConfigMapper configMapper;
    @Autowired
    private DbHandler dbHandler;
    @Autowired
    private DbSelector dbSelector;
    @Autowired
    private BiEtlDatabaseInfService databaseInfService;
    @Autowired
    private BiEtlMappingFieldService fieldService;

    @Override
    public void validateSource(String modelCode) throws Exception {
        List<BiEtlMappingConfig> mappingConfigs = configMapper.selectList(new LambdaQueryWrapper<BiEtlMappingConfig>()
                .eq(BiEtlMappingConfig::getRefModelCode, modelCode)
        );

        if (CollectionUtils.isEmpty(mappingConfigs)) {
            throw new BizException(ResourceMessageEnum.DATASOURCE_NO_REF.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DATASOURCE_NO_REF.getMessage(), ThreadLocalHolder.getLang()));
        }

        for (BiEtlMappingConfig s : mappingConfigs) {
            validate(s);
        }
    }

    @Override
    public String validateSource(BiEtlMappingConfig config) {
        String result = null;
        try {
            validate(config);
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    private void validate(BiEtlMappingConfig config) throws Exception {
        BiEtlDatabaseInf databaseInf = databaseInfService.getById(config.getRefSourceId());
        if (null == databaseInf) {
            throw new BizException(ResourceMessageEnum.DATA_SOURCE_NOT_EXIST.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DATA_SOURCE_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (!databaseInf.getEffect().equals(EffectEnum.ENABLE.getKey())) {
            throw new BizException(ResourceMessageEnum.DATASOURCE_NO_EFFECT.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DATASOURCE_NO_EFFECT.getMessage(), ThreadLocalHolder.getLang()));
        }

        List<BiEtlMappingField> recordFieldList = fieldService.list(new LambdaQueryWrapper<BiEtlMappingField>()
                .eq(BiEtlMappingField::getRefCode, config.getCode()));
        if (CollectionUtils.isEmpty(recordFieldList)) {
            throw new BizException(ResourceMessageEnum.NO_FIELD_INFO.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.NO_FIELD_INFO.getMessage(), ThreadLocalHolder.getLang()), config.getCode());
        }
        List<String> recordFields = recordFieldList.stream().map(BiEtlMappingField::getFieldName).collect(Collectors.toList());

        // 校验数据源的表以及数据结构是否发生变化
        String diffFields = null;
        switch (Integer.parseInt(config.getType())) {
            case 0:
            case 1:
            case 2:
                DbContext dbContext = new DbContext();
                dbContext.setDbId(config.getRefSourceId());
                List<String> tables = dbSelector.getTables(dbContext);
                if (CollectionUtils.isEmpty(tables)) {
                    throw new BizException(ResourceMessageEnum.DATASOURCE_CHANGE_1.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DATASOURCE_CHANGE_1.getMessage(), ThreadLocalHolder.getLang()));
                }
                if (!tables.contains(config.getFromTableName())) {
                    throw new BizException(ResourceMessageEnum.DATASOURCE_CHANGE_2.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DATASOURCE_CHANGE_2.getMessage(), ThreadLocalHolder.getLang()),
                            config.getFromTableName());
                }
                dbContext.setTableName(config.getFromTableName());

                List<String> fromFields = dbSelector.getFields(dbContext);
                if (CollectionUtils.isEmpty(fromFields)) {
                    throw new BizException(ResourceMessageEnum.DATASOURCE_CHANGE_3.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DATASOURCE_CHANGE_3.getMessage(), ThreadLocalHolder.getLang()));
                }
                diffFields = findDiffFields(recordFields, fromFields);
                break;
            case 3:
                if (!dbHandler.isTableExists(config.getFromTableName())) {
                    throw new BizException(ResourceMessageEnum.DATASOURCE_CHANGE_4.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DATASOURCE_CHANGE_4.getMessage(), ThreadLocalHolder.getLang()),
                            config.getFromTableName());
                }

                List<TableColumn> tableColumns = dbHandler.getColumns(config.getFromTableName());
                if (CollectionUtils.isEmpty(tableColumns)) {
                    throw new BizException(ResourceMessageEnum.DATASOURCE_CHANGE_5.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.DATASOURCE_CHANGE_5.getMessage(), ThreadLocalHolder.getLang()));
                }
                List<String> localFields = tableColumns.stream().map(TableColumn::getName).collect(Collectors.toList());
                diffFields = findDiffFields(recordFields, localFields);
                break;
            default:
                throw new BizException(ResourceMessageEnum.DATASOURCE_UNKNOWN.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.DATASOURCE_UNKNOWN.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (null != diffFields) {
            throw new BizException(ResourceMessageEnum.DATASOURCE_CHANGE_6.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DATASOURCE_CHANGE_6.getMessage(), ThreadLocalHolder.getLang()), diffFields);
        }
    }

    private String findDiffFields(List<String> recordFields, List<String> fromFields) {
        List<String> diff = Lists.newArrayList();
        for (String localField : recordFields) {
            if (!fromFields.contains(localField)) {
                diff.add(localField);
            }
        }
        return CollectionUtils.isNotEmpty(diff) ? StringUtils.join(diff, ",") : null;
    }

}
