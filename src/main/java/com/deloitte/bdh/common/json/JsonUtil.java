package com.deloitte.bdh.common.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * json 转换工具类
 *
 * @author dahpeng
 */
public class JsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	private static final ObjectMapper objectmapper = new ObjectMapper();

	private JsonUtil() {

	}

	/**
	 * function:把json数据转换为MAP集合
	 *
	 * @param json 数据
	 * @return Map  集合
	 */
	@SuppressWarnings({"unchecked"})
	public static Map<String, Object> readJsonToMap(String json) {
		Map<String, Object> resultMap = new HashMap<String, Object>(0);
		try {
			resultMap = objectmapper.readValue(json, Map.class);
		} catch (Exception e) {
			logger.error("JSON转MAP集合失败!!");
			throw new JSONException();
		}
		return resultMap;
	}

	/**
	 * function：把map 集合转化成json数据
	 *
	 * @return String
	 */
	public static String readMapToJson(Map<String, Object> map) {
		String resultJson = "";
		try {
			resultJson = objectmapper.writeValueAsString(map);
		} catch (Exception e) {
			logger.error("map集合转换成json数据格式失败!!");
			throw new JSONException();
		}
		return resultJson;
	}

	/**
	 * function:把json集合数据转换成List集合
	 *
	 * @return List<LinkedHashMap<String, Object>>
	 */
	@SuppressWarnings("unchecked")
	public static List<LinkedHashMap<String, Object>> readJsonToList(String json) {
		List<LinkedHashMap<String, Object>> resultList = new ArrayList<LinkedHashMap<String, Object>>(
				0);
		try {
			resultList = objectmapper.readValue(json, List.class);
		} catch (Exception e) {
			logger.error("json:" + json + "转List集合失败!!");
			throw new JSONException();
		}
		return resultList;
	}

	/**
	 * function：把数组json格式转成javaList集合
	 *
	 * @param beanClass 集合所装的数据类型
	 * @param json 数据json格式
	 * @return List<T> 转换后的list集合
	 */
	public static <T> List<T> readJsonToListBean(Class<?> beanClass, String json) {
		JavaType javaType = getCollectionType(ArrayList.class, beanClass);
		List<T> resultList = new ArrayList<T>(0);
		try {
			resultList = objectmapper.readValue(json, javaType);
		} catch (Exception e) {
			logger.error("json:" + json + "转" + beanClass.getName() + "对象List失败!!");
			throw new JSONException();
		}
		return resultList;
	}

	/**
	 * function :返回集合类型信息
	 *
	 * @param collectionClass 集合class信息
	 * @param elementClasses 集合元素class信息
	 * @return JavaType   返回类型信息
	 */
	private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return objectmapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	@SuppressWarnings("unchecked")
	public static <T> T readJsonToObjectByFastjson(String json, Class<?> clazz) {
		try {
			return (T) JSON.parseObject(json, clazz);
		} catch (Exception e) {
			logger.error("json:" + json + "转" + clazz.getName() + "对象失败!!");
			throw new JSONException();
		}
	}

	public static String readObjToJsonByFastjson(Object obj) {
		String Json = JSON.toJSONString(obj);
		return Json;
	}

	public static String readObjToJson(Object obj) {
		String Json = "";
		try {
			Json = objectmapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			throw new JSONException();
		} catch (JsonMappingException e) {
			throw new JSONException();
		} catch (IOException e) {
			throw new JSONException();
		}
		return Json;
	}

	/**
	 * json字符串转化为Map对象
	 */
	public static HashMap JsonStrToMap(String jsonStr) {
		HashMap<String, String> data = new HashMap<String, String>();
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		Set<String> keySet = jsonObject.keySet();
		for (String key : keySet) {
			String value = String.valueOf(jsonObject.get(key));
			data.put(key, value);
		}
		return data;
	}

	/**
	 * <p>Resume: 将String转换为JSONObject</p> <p>Description: </p>
	 *
	 * @author peizhide
	 * @createtime 2017年6月15日 上午10:10:11
	 */
	public static JSONObject StringToJSONObject(Object obj) {
		JSONObject json = JSONObject.parseObject(obj.toString());
		return json;
	}

	/**
	 * <p>Resume: 将String转换为JSONArray</p> <p>Description: </p>
	 *
	 * @author peizhide
	 * @createtime 2017年6月15日 上午10:10:30
	 */
	@SuppressWarnings("unchecked")
	public static <T, clazz> clazz StringToClass(String obj, Class<?> t) {
		Object result = JSON.parseObject(obj, t);
		return (clazz) result;
	}
}

