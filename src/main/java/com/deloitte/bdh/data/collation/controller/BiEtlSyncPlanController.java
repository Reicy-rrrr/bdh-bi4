package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.service.BiEtlSyncPlanService;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.MimeHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
@RestController
@RequestMapping("/biEtlSyncPlan")
public class BiEtlSyncPlanController {
    @Autowired
    private BiEtlSyncPlanService planService;

    @ApiOperation(value = "调度处理", notes = "数据同步")
    @GetMapping("/process")
    public RetResult<Void> process(String tenantId) throws Exception {
        reSetHeader(tenantId);
        planService.process();
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "调度处理", notes = "数据整理")
    @GetMapping("/etl")
    public RetResult<Void> etl(String tenantId) throws Exception {
        reSetHeader(tenantId);
        planService.etl();
        return RetResponse.makeOKRsp();
    }

    private void reSetHeader(String value) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        reflectSetparam(request, "x-bdh-tenant-code", value);
    }

    /**
     * 修改header信息，key-value键值对儿加入到header中
     *
     * @param request
     * @param key
     * @param value
     */
    private void reflectSetparam(HttpServletRequest request, String key, String value) throws Exception {
        Class<? extends HttpServletRequest> requestClass = request.getClass();
        Field request1 = requestClass.getDeclaredField("request");
        request1.setAccessible(true);
        Object o = request1.get(request);
        Field coyoteRequest = o.getClass().getDeclaredField("coyoteRequest");
        coyoteRequest.setAccessible(true);
        Object o1 = coyoteRequest.get(o);
        Field headers = o1.getClass().getDeclaredField("headers");
        headers.setAccessible(true);
        MimeHeaders o2 = (MimeHeaders) headers.get(o1);
        o2.addValue(key).setString(value);
    }
}
