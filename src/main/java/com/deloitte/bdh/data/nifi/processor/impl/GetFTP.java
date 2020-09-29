package com.deloitte.bdh.data.nifi.processor.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.data.enums.ProcessorTypeEnum;
import com.deloitte.bdh.data.model.BiEtlDbRef;
import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import com.deloitte.bdh.data.nifi.Processor;
import com.deloitte.bdh.data.nifi.ProcessorContext;
import com.deloitte.bdh.data.nifi.processor.AbstractProcessor;
import com.deloitte.bdh.data.service.BiEtlDbRefService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("GetFTP")
public class GetFTP extends AbstractProcessor {

    /**
     * ftp地址
     **/
    @Value("${ftp.server.host}")
    private String host;

    /**
     * ftp端口
     **/
    @Value("${ftp.server.port}")
    private int port;

    /**
     * ftp用户名
     **/
    @Value("${ftp.server.username}")
    private String username;

    /**
     * ftp密码
     **/
    @Value("${ftp.server.password}")
    private String password;

    @Autowired
    private BiEtlDbRefService etlDbRefService;

    @Override
    public Map<String, Object> save(ProcessorContext context) throws Exception {
        // 配置数据源的
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("Hostname", host);
        properties.put("Port", String.valueOf(port));
        properties.put("Username", username);
        properties.put("Password", password);
        properties.put("Remote Path", context.getBiEtlDatabaseInf().getAddress());
        properties.put("File Filter Regex", context.getBiEtlDatabaseInf().getDbName());
        properties.put("Delete Original", "true");

        // 调度相关的默认值
        Map<String, Object> config = Maps.newHashMap();
        config.put("schedulingPeriod", "1 * * * * ?");
        config.put("schedulingStrategy", "CRON_DRIVEN");
        config.put("properties", properties);

        // processor 公共的
        Map<String, Object> component = Maps.newHashMap();
        component.put("name", MapUtils.getString(context.getReq(), "name"));
        component.put("type", ProcessorTypeEnum.GetFTP.getvalue());
        component.put("config", config);

        //新建 processor
        BiEtlProcessor biEtlProcessor = createProcessor(context, component);

        Processor processor = new Processor();
        BeanUtils.copyProperties(biEtlProcessor, processor);

        // 新建 processor param
        if (MapUtils.isNotEmpty(component)) {
            List<BiEtlParams> paramsList = transferToParams(context, component, biEtlProcessor);
            paramsService.saveBatch(paramsList);
            processor.setList(paramsList);
        }

        // 该组件有关联表的信息
        BiEtlDbRef dbRef = new BiEtlDbRef();
        dbRef.setCode(GenerateCodeUtil.genDbRef());
        dbRef.setSourceId(context.getBiEtlDatabaseInf().getId());
        dbRef.setProcessorCode(processor.getCode());
        dbRef.setProcessorsCode(context.getProcessors().getCode());
        dbRef.setModelCode(context.getModel().getCode());
        dbRef.setCreateDate(LocalDateTime.now());
        dbRef.setCreateUser(MapUtils.getString(context.getReq(), "createUser"));
        dbRef.setTenantId(context.getModel().getTenantId());
        etlDbRefService.save(dbRef);

        // 反显
        processor.setDbRef(dbRef);
        context.addProcessor(processor);
        return null;
    }

    @Override
    protected Map<String, Object> rSave(ProcessorContext context) throws Exception {
        Processor processor = context.getTempProcessor();
        processorService.delProcessor(processor.getId());
        List<BiEtlParams> paramsList = paramsService.list(new LambdaQueryWrapper<BiEtlParams>().eq(BiEtlParams::getRelCode, processor.getCode()));
        if (CollectionUtils.isNotEmpty(paramsList)) {
            List<String> list = paramsList
                    .stream()
                    .map(BiEtlParams::getId)
                    .collect(Collectors.toList());
            paramsService.removeByIds(list);
        }
        //删除该组件有关联表的信息
        etlDbRefService.removeById(context.getTempProcessor().getDbRef().getId());
        return null;
    }

    @Override
    protected Map<String, Object> delete(ProcessorContext context) throws Exception {

        return null;
    }

    @Override
    protected Map<String, Object> rDelete(ProcessorContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> update(ProcessorContext context) throws Exception {
        return null;
    }

    @Override
    protected Map<String, Object> rUpdate(ProcessorContext context) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> validate(ProcessorContext context) throws Exception {
        return null;

    }

    @Override
    protected ProcessorTypeEnum processorType() {
        return ProcessorTypeEnum.GetFTP;
    }

}
