package com.deloitte.bdh.data.collation.database.convertor.impl;

import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.collation.database.convertor.DbConvertor;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableField;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据源转化处理Impl
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
@Service("dbConvertor")
public class DbConvertorImpl implements DbConvertor {

    /**
     * 字段转换
     *
     * @param fields
     */
    @Override
    public void convertFieldType(List<TableField> fields, DbContext context) {
        SpringUtil.getBean(getConvertType(context), DbConvertor.class).convertFieldType(fields, context);
    }

    private String getConvertType(DbContext context) {
        SourceTypeEnum sourceType = context.getSourceTypeEnum();
        String result = null;
        // 根据落地的数据类型选择
        String localSourceType = "mysql";
        if ("mysql".equals(localSourceType)) {
            switch (sourceType) {
                case Mysql:
                    result = "mysqlToMysql";
                    break;
                case Oracle:
                    result = "oracleToMysql";
                    break;
                case SQLServer:
                    result = "sqlserverToMysql";
                    break;
                case Hana:
                    result = "hanaToMysql";
                    break;
                default:
                    result = "mysqlToMysql";
            }
        } else {
            switch (sourceType) {
                case Mysql:
                    result = "mysqlToHive";
                    break;
                default:
                    result = "mysqlToHive";
            }
        }
        return result;
    }
}
