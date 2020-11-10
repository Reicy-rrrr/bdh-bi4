package com.deloitte.bdh.common.config;

import com.deloitte.bdh.data.collation.nifi.template.TemplateEnum;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

@Component
public class TemplateConfig implements ApplicationRunner {
    private static Map<String, String> cacheMap = Maps.newHashMap();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String path = this.getClass().getClassLoader().getResource(".").getPath() + "template";
        File file = new File(path);
        String[] fileNames = file.list();
        for (String var : fileNames) {
            File temp = new File(path + "/" + var);
            String context = FileUtils.readFileToString(temp, "utf-8");
            cacheMap.put(var.substring(0, var.indexOf(".")), context);
        }
    }

    public static String get(TemplateEnum templateEnum) {
        if (MapUtils.isEmpty(cacheMap)) {
            throw new RuntimeException("模板未加载成功");
        }
        return cacheMap.get(templateEnum.getKey());
    }
}








