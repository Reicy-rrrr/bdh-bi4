package com.deloitte.bdh.data.analyse.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiModelFieldMapper;
import com.deloitte.bdh.data.analyse.model.BiUiModelField;
import com.deloitte.bdh.data.analyse.service.AnalyseModelFieldService;
import com.deloitte.bdh.data.collation.model.request.CreateResourcesDto;
import com.deloitte.bdh.data.collation.model.request.UpdateResourcesDto;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
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
 * @author bo.wang
 * @since 2020-10-21
 */
@Service
@DS(DSConstant.BI_DB)
public class AnalyseModelFieldServiceImpl extends AbstractService<BiUiModelFieldMapper, BiUiModelField> implements AnalyseModelFieldService {
    @Resource
    BiUiModelFieldMapper biUiModelFieldMapper;

    @Override
    public Map<String, List<String>> getTables(String tableName) {
        List<Map<String, Object>> list = biUiModelFieldMapper.selectTable(tableName);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, List<String>> result = Maps.newHashMap();
            //get table
            for (Map<String, Object> listVar : list) {
                String key = String.valueOf(listVar.get("MODEL_ID"));
                String field = String.valueOf(listVar.get("NAME"));
                if (result.containsKey(key)) {
                    result.get(key).add(field);
                } else {
                    List<String> valueList = Lists.newArrayList();
                    valueList.add(field);
                    result.put(key, valueList);
                }
            }
            return result;
        }
        return null;
    }
}
