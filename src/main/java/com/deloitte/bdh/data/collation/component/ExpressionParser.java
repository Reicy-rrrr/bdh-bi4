package com.deloitte.bdh.data.collation.component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公式解析器
 *
 * @author chenghzhang
 * @date 2020/12/15
 */
public class ExpressionParser {
    private static final String default_regex = "\\$\\{(.+?)\\}";

    /**
     * 获取公式中的变量
     *
     * @param expression 计算公式
     * @return
     */
    public static List<String> getParams(String expression) {
        Matcher matcher = Pattern.compile(default_regex).matcher(expression);
        List<String> list = new ArrayList();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * 获取公式中的变量（去重）
     *
     * @param expression 计算公式
     * @return
     */
    public static List<String> getUniqueParams(String expression) {
        Matcher matcher = Pattern.compile(default_regex).matcher(expression);
        Set<String> params = new LinkedHashSet();
        while (matcher.find()) {
            params.add(matcher.group());
        }
        return new ArrayList<>(params);
    }

    /**
     * 格式化公式（设置变量值）
     *
     * @param expression 计算公式
     * @param data       变量值
     * @return
     */
    public static String format(String expression, Map<String, String> data) {
        Matcher m = Pattern.compile(default_regex).matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            String value = data.get(key);
            m.appendReplacement(sb, value == null ? "" : value);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
