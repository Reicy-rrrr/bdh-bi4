package com.deloitte.bdh.data.collation.database.convertor;

import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.vo.TableField;

import java.util.List;

/**
 * 数据源转化处理
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
public interface DbConvertor {

    /**
     * 字段转换
     *
     * @param fields
     * @param context
     */
    void convertFieldType(List<TableField> fields, DbContext context);
}
