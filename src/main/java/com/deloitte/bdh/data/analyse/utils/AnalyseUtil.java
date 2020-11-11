package com.deloitte.bdh.data.analyse.utils;

public class AnalyseUtil {
    public static String getCurrentUser() {
        return "1";
    }

    public static String join(String split, String[] params) {
        String result = null;
        if (params != null) {
            for (String content : params) {
                result = join(result, content, split);
            }
        }
        return result;
    }

    public static String join(String left, String right, String split) {
        if (empty(left)) {
            return right;
        } else {
            if (empty(left)) {
                return left;
            }
            return left + split + right;
        }
    }

    public static Boolean empty(Object value) {
        if (value == null || "".equals(value) || "ALL".equals(value) || "null".equals(value) || "NULL".equals(value) || "undefined".equals(value)) {
            return true;
        }
        return false;
    }
}
