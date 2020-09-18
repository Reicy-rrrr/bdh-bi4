package com.deloitte.bdh.common.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * String 工具类
 *
 * @author dahpeng
 */
public class StringUtil {

	private static Pattern linePattern = Pattern.compile("_(\\w)");
	private static Pattern humpPattern = Pattern.compile("[A-Z]");
	private static final String REPLACE_LABEL = "{}";

	/**
	 * 下划线转驼峰
	 */
	public static String lineToHump(String str) {
		if (null == str || "".equals(str)) {
			return str;
		}
		str = str.toLowerCase();
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);

		str = sb.toString();
		str = str.substring(0, 1).toUpperCase() + str.substring(1);

		return str;
	}

	/**
	 * 驼峰转下划线,效率比上面高
	 */
	public static String humpToLine(String str) {
		Matcher matcher = humpPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 驼峰转下划线(简单写法，效率低于{@link #humpToLine(String)})
	 */
	public static String humpToLine2(String str) {
		return str.replaceAll("[A-Z]", "_$0").toLowerCase();
	}

	/**
	 * 首字母转小写
	 */
	public static String toLowerCaseFirstOne(String s) {
		if (StringUtils.isBlank(s)) {
			return s;
		}
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1))
					.toString();
		}
	}

	/**
	 * 首字母转大写
	 */
	public static String toUpperCaseFirstOne(String s) {
		if (StringUtils.isBlank(s)) {
			return s;
		}
		if (Character.isUpperCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuffer()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1))
					.toString();
		}
	}

	/**
	 * object转String
	 */
	public static String getString(Object object) {
		return getString(object, "");
	}

	public static String getString(Object object, String defaultValue) {
		if (null == object) {
			return defaultValue;
		}
		try {
			return object.toString();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * object转Integer
	 */
	public static int getInt(Object object) {
		return getInt(object, -1);
	}

	public static int getInt(Object object, Integer defaultValue) {
		if (null == object) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(object.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 判断字符串是否为空
	 * yuankliu
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value)
	{
		return null == value || "".equals(value) || "null".equalsIgnoreCase(value);
	}

	/**
	 * object转Boolean
	 */
	public static boolean getBoolean(Object object) {
		return getBoolean(object, false);
	}

	public static boolean getBoolean(Object object, Boolean defaultValue) {
		if (null == object) {
			return defaultValue;
		}
		try {
			return Boolean.parseBoolean(object.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 用objs[]的值去替换字符串s中的{}符号
	 */
	public static String replaceParams(String s, Object... objs) {
		if (s == null) {
			return s;
		}
		if (objs == null || objs.length == 0) {
			return s;
		}
		if (s.indexOf(REPLACE_LABEL) == -1) {
			StringBuilder result = new StringBuilder();
			result.append(s);
			for (Object obj : objs) {
				result.append(" ").append(obj);
			}
			return result.toString();
		}

		String[] stra = new String[objs.length];
		int len = s.length();
		for (int i = 0; i < objs.length; i++) {
			if (objs[i] != null) {
				stra[i] = objs[i].toString();
			} else {
				stra[i] = "null";
			}
			len += stra[i].length();
		}

		StringBuilder result = new StringBuilder(len);
		int cursor = 0;
		int index = 0;
		for (int start; (start = s.indexOf(REPLACE_LABEL, cursor)) != -1;) {
			result.append(s.substring(cursor, start));
			if (index < stra.length) {
				result.append(stra[index]);
			} else {
				result.append(REPLACE_LABEL);
			}
			cursor = start + 2;
			index++;
		}
		result.append(s.substring(cursor, s.length()));
		if (index < objs.length) {
			for (int i = index; i < objs.length; i++) {
				result.append(" ").append(objs[i]);
			}
		}
		return result.toString();
	}

}
