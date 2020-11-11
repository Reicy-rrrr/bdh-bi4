package com.deloitte.bdh.data.collation.nifi.template.servie.impl;

import com.deloitte.bdh.common.config.TemplateConfig;
import com.deloitte.bdh.data.collation.enums.RunStatusEnum;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.deloitte.bdh.data.collation.nifi.template.TemplateEnum;
import com.deloitte.bdh.data.collation.nifi.template.config.Template;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;


@Service
public class EtlTransferImpl extends AbstractTransfer {
    @Resource
    private NifiProcessService nifiProcessService;

    @Override
    public String add(String modelGroupId, String templateType, Params params) throws Exception {
        //读取配置
        String tempJson = TemplateConfig.get(TemplateEnum.valueOf(templateType));

        //获取配置
        String templateId = super.getTemplateId(templateType);
        Template param = params.params();
        param.setDttTemplateId(templateId);
        param.setDttModelGroupId(modelGroupId);
        tempJson = replace(tempJson, param);
        Map<String, Object> result = nifiProcessService.createByTemplate(modelGroupId, tempJson);
        return super.parseGroupId(result);
    }

    @Override
    public void del(String processGroupId) throws Exception {
        stop(processGroupId);
        nifiProcessService.delProcessGroup(processGroupId);
    }

    @Override
    public void run(String processGroupId) throws Exception {
        nifiProcessService.runState(processGroupId, RunStatusEnum.RUNNING.getKey(), true);
    }

    @Override
    public void stop(String processGroupId) throws Exception {
        nifiProcessService.runState(processGroupId, RunStatusEnum.STOP.getKey(), true);
        Map<String, Object> processGroup = nifiProcessService.getProcessGroupFull(processGroupId);
        List<String> list = super.parseConnections(processGroup);
        Map<String, String> processors = super.parseProcessors(processGroup);
        //先终止
        for (Map.Entry<String, String> var : processors.entrySet()) {
            nifiProcessService.terminate(var.getKey());
        }
        //后清空 ，此处应该基于group 清空
        for (String var : list) {
            nifiProcessService.dropConnections(var);
        }
    }

    @Override
    public void clear(String processGroupId) throws Exception {
        Map<String, Object> processGroup = nifiProcessService.getProcessGroupFull(processGroupId);
        Map<String, String> processors = super.parseProcessors(processGroup);
        for (Map.Entry<String, String> var : processors.entrySet()) {
            nifiProcessService.clearRequest(var.getKey());
        }
    }


    private String replace(String tempJson, Object object) throws IllegalAccessException {
        Map<String, String> map = Maps.newHashMap();
        Class clazz = object.getClass();
        Field[] childFields = clazz.getDeclaredFields();
        Field[] superFields = clazz.getSuperclass().getDeclaredFields();
        Field[] fields = ArrayUtils.addAll(childFields, superFields);
        for (Field field : fields) {
            String name = field.getName();
            field.setAccessible(true);
            map.put(name, String.valueOf(field.get(object)));
        }
        for (Map.Entry<String, String> var : map.entrySet()) {
            if ("null".equals(var.getValue())) {
                tempJson = tempJson.replaceAll("\"" + var.getKey() + "\"", var.getValue());
            } else {
                tempJson = tempJson.replaceAll(var.getKey(), var.getValue());
            }
        }
        return tempJson;
    }
}
