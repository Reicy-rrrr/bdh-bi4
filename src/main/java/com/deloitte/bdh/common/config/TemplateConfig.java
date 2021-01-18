package com.deloitte.bdh.common.config;

import com.deloitte.bdh.data.collation.nifi.template.TemplateEnum;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;

@Component
public class TemplateConfig implements ApplicationRunner {
    private static Map<String, String> cacheMap = Maps.newHashMap();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //获取容器资源解析器
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 获取远程服务器IP和端口
        //获取所有匹配的文件
        Resource[] resources = resolver.getResources("template/*.*");
        for (Resource resource : resources) {
            InputStream stream = null;
            try {
                //获得文件流，因为在jar文件中，不能直接通过文件资源路径拿到文件，但是可以在jar包中拿到文件流
                stream = resource.getInputStream();
                String context = IOUtils.toString(stream, Charsets.toCharset("utf-8"));
                String name = resource.getFilename();
                cacheMap.put(name.substring(0, name.indexOf(".")), context);

            } finally {
                IOUtils.closeQuietly(stream);
            }
        }
    }

    public static String get(TemplateEnum templateEnum) {
        if (MapUtils.isEmpty(cacheMap)) {
            throw new RuntimeException("模板未加载成功");
        }
        return cacheMap.get(templateEnum.getKey());
    }

}








