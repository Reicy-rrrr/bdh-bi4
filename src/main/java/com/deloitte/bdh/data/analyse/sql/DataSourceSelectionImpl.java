package com.deloitte.bdh.data.analyse.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.sql.dto.SqlContext;
import com.deloitte.bdh.data.collation.enums.DataSetTypeEnum;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DataSourceSelectionImpl implements DataSourceSelection {
    private static final String ANALYSE_PREFIX = "analyse";

    @Resource
    private BiDataSetService dataSetService;


    @Override
    public AnalyseSql getBean(SqlContext context) {
        AnalyseSql bean = null;
        BiDataSet dataSet = dataSetService.getOne(new LambdaQueryWrapper<BiDataSet>()
                .eq(BiDataSet::getTableDesc, context.getModel().getTableName()));

        if (null == dataSet) {
            throw new RuntimeException("未在数据集找到目标对象");
        }
        context.getModel().setTableName(dataSet.getTableName());
        DataSetTypeEnum typeEnum = DataSetTypeEnum.getEnumByKey(dataSet.getType());
        switch (typeEnum) {
            case DEFAULT:
            case MODEL:
                //本地
                bean = SpringUtil.getBean(ANALYSE_PREFIX + "Local", AnalyseSql.class);

                break;
            default:
//                Object object = SpringUtil.getBean(ANALYSE_PREFIX + SourceTypeEnum.Mysql.getTypeName(), Sql.class)
//                        .assembly(model, method);
        }

        return bean;
    }
}
