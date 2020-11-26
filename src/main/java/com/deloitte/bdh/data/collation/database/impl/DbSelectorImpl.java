package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.collation.dao.bi.BiEtlDatabaseInfMapper;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.database.po.TableSchema;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.deloitte.bdh.data.collation.model.BiEtlDatabaseInf;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("dbSelector")
public class DbSelectorImpl implements DbSelector {
    @Resource
    private BiEtlDatabaseInfMapper biEtlDatabaseInfMapper;

    @Override
    public String test(DbContext context) throws Exception {
        return SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbSelector.class).test(context);
    }

    @Override
    public List<String> getTables(DbContext context) throws Exception {
        context(context);
        return SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbSelector.class).getTables(context);
    }

    @Override
    public List<String> getFields(DbContext context) throws Exception {
        context(context);
        return SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbSelector.class).getFields(context);
    }

    @Override
    public TableSchema getTableSchema(DbContext context) throws Exception {
        context(context);
        TableSchema result = SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbSelector.class).getTableSchema(context);
        return result;
    }

    @Override
    public TableData getTableData(DbContext context) throws Exception {
        context(context);
        TableData result = SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbSelector.class).getTableData(context);
        return result;
    }

    @Override
    public long getTableCount(DbContext context) throws Exception {
        context(context);
        long count = SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbSelector.class).getTableCount(context);
        return count;
    }

    @Override
    public List<Map<String, Object>> executeQuery(DbContext context) throws Exception {
        context(context);
        List<Map<String, Object>> result = SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbSelector.class).executeQuery(context);
        return result;
    }

    @Override
    public PageInfo<Map<String, Object>> executePageQuery(DbContext context) throws Exception {
        context(context);
        PageInfo<Map<String, Object>> result = SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbSelector.class).executePageQuery(context);
        return result;
    }

    private void context(DbContext context) {
        BiEtlDatabaseInf inf = biEtlDatabaseInfMapper.selectById(context.getDbId());
        context.setSourceTypeEnum(SourceTypeEnum.values(inf.getType()));
        if (!context.getSourceTypeEnum().equals(SourceTypeEnum.File_Csv)
                && !context.getSourceTypeEnum().equals(SourceTypeEnum.File_Excel)) {
            String url = NifiProcessUtil.getDbUrl(inf.getType(), inf.getAddress(), inf.getPort(), inf.getDbName());
            context.setDbUrl(url);
        }
        context.setDbUserName(inf.getDbUser());
        context.setDbPassword(inf.getDbPassword());
        context.setDriverName(inf.getDriverName());
        context.setDbName(inf.getDbName());
    }
}
