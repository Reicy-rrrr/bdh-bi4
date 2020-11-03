package com.deloitte.bdh.common.config;


import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.DataSourceNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.deloitte.bdh.common.util.ThreadLocalUtil;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @author Ashen
 * @date 04/08/2020
 */
@Configuration
@Component
public class DsBdhProcessorConfig extends DsProcessor {
    private static ThreadLocal<String> local = new ThreadLocal<>();

    @Autowired
    private DataSource dataSource;


    @Override
    public boolean matches(String key) {
        return DSConstant.BASE_DB.equals(key) || DSConstant.PLATFORM_DB.equals(key)
                || DSConstant.BI_DB.equals(key) || DSConstant.EXPENSE_DB.equals(key);
    }

    @Override
    public String doDetermineDatasource(MethodInvocation invocation, String key) {
        String tenantCode = ThreadLocalUtil.getTenantCode();
        if (null == tenantCode) {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (null != attributes) {
                HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
                tenantCode = request.getHeader("x-bdh-tenant-code");
            }
            ThreadLocalUtil.set("tenantCode", tenantCode);
        }
        String datasourceName = key;
        //去掉首字母#
        if (!DSConstant.BASE_DB.equals(key)) {
            datasourceName += "-" + tenantCode;
        }
        datasourceName = datasourceName.substring(1);
        Object datasource = null;
        try {
            DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
            datasource = ds.getDataSource(datasourceName);
        } catch (Exception e) {
        }
        if (datasource == null) {
            throw new DataSourceNotFoundException("未匹配到对应的数据源:" + datasourceName);
        }
        return datasourceName;
    }
}
