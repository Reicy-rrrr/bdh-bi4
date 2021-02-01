package com.deloitte.bdh.data.collation.evm.utils;

import com.deloitte.bdh.data.collation.component.ExpressionHandler;
import com.deloitte.bdh.data.collation.evm.dto.Sheet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class RuleParseUtil {

    public static String value(String ruleExpression, Map<String, Sheet> map, String var) {
        List<String> list = getParseText(ruleExpression);
        if (list.size() == 1) {
            return singleValue(list.get(0), ruleExpression, map, var);
        }
        return calculateValue(list, ruleExpression, map, var);
    }

    /**
     * 直接取值
     */
    public static String singleValue(String singText, String ruleExpression, Map<String, Sheet> map, String var) {
        String tempValue = "0";
        String[] own = singText.split("#");
        String[] text = own[1].split("\\.");
        Sheet sheet = map.get(text[0]);
        if (null != sheet) {
            if ("N".equals(own[0])) {
                tempValue = sheet.yCellValue(var, text[1]);
            }
            if ("A".equals(own[0])) {
                tempValue = sheet.xCellValueAfter(var, text[1]);
                if(null ==tempValue){
                    tempValue = sheet.yCellValue(var, text[1]);
                }
            }
            if ("P".equals(own[0])) {
                tempValue = sheet.xCellValuePre(var, text[1]);
                if(null ==tempValue){
                    tempValue = sheet.yCellValue(var, text[1]);
                }
            }
        }
        ruleExpression = ruleExpression.replace(singText, tempValue);
        return ruleExpression.replace("{", "").replace("}", "");
    }

    /**
     * 计算
     */
    public static String calculateValue(List<String> calList, String ruleExpression, Map<String, Sheet> map, String var) {
        Map<String, String> textmate = Maps.newHashMap();
        for (String str : calList) {
            String tempValue = singleValue(str, str, map, var);
            textmate.put(str, tempValue);
        }
        for (Map.Entry<String, String> entry : textmate.entrySet()) {
            ruleExpression = ruleExpression.replace(entry.getKey(), entry.getValue());
        }
        ruleExpression = ruleExpression.replace("{", "").replace("}", "");
        try {
            String result = String.valueOf(ExpressionHandler.scriptEngine.eval(ruleExpression));
            return "Infinity".equals(result) ? "0" : new BigDecimal(result).setScale(5, BigDecimal.ROUND_HALF_UP).toString();
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }


    private static List<String> getParseText(String str) {
        List<String> result = Lists.newLinkedList();
        List<Integer> left = getIndexNo(str, "{");
        List<Integer> right = getIndexNo(str, "}");
        for (int i = 0; i < left.size(); i++) {
            result.add(str.substring(left.get(i) + 1, right.get(i)));
        }
        return result;
    }

    private static List<Integer> getIndexNo(String str, String symbol) {
        List<Integer> list = Lists.newLinkedList();
        int index = str.indexOf(symbol);
        boolean cont = true;
        do {
            int value = str.indexOf(symbol, index);
            if (value != -1) {
                list.add(value);
                index = value + 1;
                continue;
            }
            cont = false;
        } while (cont);
        return list;
    }

}
