package com.deloitte.bdh.data.db.impl;

import com.deloitte.bdh.common.util.NifiProcessUtil;
import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.db.DbProcess;
import com.deloitte.bdh.data.db.DbSelector;
import com.deloitte.bdh.data.db.dto.DbContext;
import com.deloitte.bdh.data.enums.SourceTypeEnum;
import com.deloitte.bdh.data.model.BiEtlDatabaseInf;
import com.deloitte.bdh.data.service.BiEtlDatabaseInfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbSelectorImpl implements DbSelector {
    @Autowired
    private BiEtlDatabaseInfService biEtlDatabaseInfService;

    @Override
    public String work(DbContext context) throws Exception {
        String result;
        context(context);
        switch (context.getMethod()) {
            case 1:
                //getTable
                result = SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbProcess.class).getTables(context);
                break;
            case 2:
                //getFields
                result = SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbProcess.class).getFields(context);
                break;
            default:
                //test
                result = SpringUtil.getBean(context.getSourceTypeEnum().getTypeName(), DbProcess.class).test(context);
        }
        return result;
    }

    private void context(DbContext context) {
        BiEtlDatabaseInf inf = biEtlDatabaseInfService.getById(context.getDbId());
        String url = NifiProcessUtil.getDbUrl(inf.getType(), inf.getAddress(), inf.getPort(), inf.getDbName());
        context.setSourceTypeEnum(SourceTypeEnum.values(inf.getType()));
        context.setDbUrl(url);
        context.setDbUserName(inf.getDbUser());
        context.setDbPassword(inf.getDbPassword());
        context.setDriverName(inf.getDriverName());
    }
}
