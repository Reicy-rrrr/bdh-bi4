package com.deloitte.bdh.common.dialect;

import com.github.pagehelper.Page;
import com.github.pagehelper.dialect.AbstractHelperDialect;
import com.github.pagehelper.util.MetaObjectUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 扩展pagehelper hana数据源的支持
 * 除了继承AbstractHelperDialect，还需要注册方言：PageAutoDialect.registerDialectAlias("sap", HanaDialect.class);
 *
 * @author chenghzhang
 */
public class HanaDialect extends AbstractHelperDialect {

    @Override
    public Object processPageParameter(MappedStatement ms, Map<String, Object> paramMap, Page page, BoundSql boundSql, CacheKey pageKey) {
        paramMap.put(PAGEPARAMETER_FIRST, page.getStartRow());
        paramMap.put(PAGEPARAMETER_SECOND, page.getPageSize());
        //处理pageKey
        pageKey.update(page.getStartRow());
        pageKey.update(page.getPageSize());
        //处理参数配置
        if (boundSql.getParameterMappings() != null) {
            List<ParameterMapping> newParameterMappings = new ArrayList(boundSql.getParameterMappings());
            if (page.getStartRow() == 0) {
                newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_SECOND, int.class).build());
            } else {
                newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_SECOND, int.class).build());
                newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_FIRST, long.class).build());
            }
            MetaObject metaObject = MetaObjectUtil.forObject(boundSql);
            metaObject.setValue("parameterMappings", newParameterMappings);
        }
        return paramMap;
    }

    @Override
    public String getPageSql(String sql, Page page, CacheKey pageKey) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        if (page.getStartRow() == 0) {
            sqlBuilder.append("\n LIMIT ? OFFSET 1");
        } else {
            sqlBuilder.append("\n LIMIT ? OFFSET ? ");
        }
        return sqlBuilder.toString();
    }

}
