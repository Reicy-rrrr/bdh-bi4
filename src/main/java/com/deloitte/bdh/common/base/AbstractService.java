package com.deloitte.bdh.common.base;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.StringUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @author dahpeng
 * @Description: 基于通用MyBatis Mapper插件的Service接口的实现
 * @date 2018/4/18 11:28
 */
public abstract class AbstractService<M extends Mapper<T>, T> extends ServiceImpl<M, T> implements Service<T> {
    @Resource
    protected AsyncTaskExecutor executor;

    @Resource
    protected LocaleMessageService localeMessageService;

    /**
     * 获取租户的主键信息
     *
     * @return
     */
    @Override
    public String getSequence() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        String tenantCode = request.getHeader("x-bdh-tenant-code");
        if (StringUtil.isEmpty(tenantCode)) {
            throw new BizException("当前请求租户信息失败");
        }
        String sequenceValue = baseMapper.selectSequence();
        return tenantCode + "-" + sequenceValue;
    }

}