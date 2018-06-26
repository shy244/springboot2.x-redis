package com.lumi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 功能描述： 处理json的工具类，负责json数据转换成java对象和java对象转换成json<br>
 */
public class JsonUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
	
	private static final int CODE_SUCCESS = 200;

	public static Map<String, String> processKeyValueString(String keyValue) {
		if (keyValue == null) {
			return null;
		}
		Map<String, String> retMap = new HashMap<String, String>();
		keyValue = keyValue.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\{", "")
				.replaceAll("\\}", "");
		String[] array = keyValue.split(",");
		for (String s : array) {
			String[] kv = s.split(":");
			if (kv.length == 2) {
				retMap.put(kv[0], kv[1]);
			}
		}
		return retMap;
	}

	/**
	 * 从一个JSON 对象字符格式中得到一个java对象
	 */
	public static <T> T getObject4JsonString(String jsonString, Class<T> pojoCalss) {
		return JSON.parseObject(jsonString, pojoCalss);
	}

	/**
	 * 从json HASH表达式中获取一个map，改map支持嵌套功能
	 */
	public static Map<String, Object> getMap4Json(String jsonString) {
		return JSON.parseObject(jsonString, new TypeReference<Map<String, Object>>() {
		});
	}

	/**
	 * 将一个java对象转换为Map对象
	 */
	public static Map<String, Object> getMap4Object(Object object) {
		String josn = getJsonString4JavaPOJO(object);
		return getMap4Json(josn);
	}

	/**
	 * 从json数组中得到相应java数组
	 */
	public static Object[] getObjectArray4Json(String jsonString) {
		JSONArray jsonArray = JSONArray.parseArray(jsonString);
		return jsonArray.toArray();
	}

	/**
	 * 从json对象集合表达式中得到一个java对象列表
	 */
	public static <T> List<T> getList4Json(String jsonString, Class<T> pojoClass) {
		return JSON.parseArray(jsonString, pojoClass);
	}

	/**
	 * 从json数组中解析出java字符串数组
	 */
	public static String[] getStringArray4Json(String jsonString) {
		JSONArray jsonArray = JSONArray.parseArray(jsonString);
		String[] stringArray = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			stringArray[i] = jsonArray.getString(i);
		}
		return stringArray;
	}

	/**
	 * 从json数组中解析出javaLong型对象数组
	 */
	public static Long[] getLongArray4Json(String jsonString) {
		JSONArray jsonArray = JSONArray.parseArray(jsonString);
		Long[] longArray = new Long[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			longArray[i] = jsonArray.getLong(i);

		}
		return longArray;
	}

	/**
	 * 从json数组中解析出java Integer型对象数组
	 */
	public static Integer[] getIntegerArray4Json(String jsonString) {
		JSONArray jsonArray = JSONArray.parseArray(jsonString);
		Integer[] integerArray = new Integer[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			integerArray[i] = jsonArray.getInteger(i);
		}
		return integerArray;
	}

	/**
	 * 从json数组中解析出java Integer型对象数组
	 */
	public static Double[] getDoubleArray4Json(String jsonString) {
		JSONArray jsonArray = JSONArray.parseArray(jsonString);
		Double[] doubleArray = new Double[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			doubleArray[i] = jsonArray.getDouble(i);
		}
		return doubleArray;
	}

	/**
	 * 将java对象转换成json字符串
	 * 
	 * @param javaObj
	 * @return
	 */
	public static String getJsonString4JavaPOJO(Object javaObj) {
		JSONObject json = (JSONObject) JSON.toJSON(javaObj);
		return json.toString();
	}

	/**对象转为json**/
	public static String getJsonFromObj(Object object) {
		return JSON.toJSONString(object, SerializerFeature.PrettyFormat,
				SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteNullStringAsEmpty);
	}
	
	/**对象转为json**/
	public static String getJsonFromObjNoFormat(Object object) {
		return JSON.toJSONString(object,
				SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteNullStringAsEmpty);
	}

	/**把普通的map转换为 {name:"columnName", value:"columnValue"}**/
	public static String mapToNameValueJson(Map<String, Object> map) {
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			Map<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("name", entry.getKey());
			tmp.put("value", entry.getValue());
			retList.add(tmp);
		}
		return getJsonFromObj(retList);
	}

	/**把普通的map转换为 {name:columnName, value:columnValue,selected:true}**/
	public static String[] mapToEchartsMapJson(Map<String, Object> map, int firstIndex) {
		List<Map<String, Object>> retListAll = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> retListSelected = new ArrayList<Map<String, Object>>();
		Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
		int index = 1;
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			Map<String, Object> tmp = new HashMap<String, Object>();
			Map<String, Object> tmpSelected = new HashMap<String, Object>();
			tmp.put("name", entry.getKey());
			tmp.put("value", entry.getValue());
			if (index <= firstIndex) {
				tmpSelected.put("name", entry.getKey());
				tmpSelected.put("value", entry.getValue());
				tmp.put("selected", true);
				retListSelected.add(tmpSelected);
			}
			retListAll.add(tmp);
			index++;
		}
		String[] strArray = { getJsonFromObj(retListAll), getJsonFromObj(retListSelected) };
		return strArray;
	}

	public static String getJsonString4JavaArray(Object[] objects) {
		return JSON.toJSONString(objects, true);
	}

	/**
	 * 将java对象转换成json字符串,并设定日期格式
	 * @param javaObj
	 * @param dataFormat
	 * @return
	 */
	public static String getJsonString4JavaPOJO(Object javaObj, String dataFormat) {
		return JSON.toJSONStringWithDateFormat(javaObj, dataFormat, SerializerFeature.WriteDateUseDateFormat);

	}

	/**
	 * 根据LIST，和总数向页面打印列表数据
	 */
	public static <T> void returnJsonListData(HttpServletResponse response, List<T> list, int count, String name) {
		Map<String, Object> mapJson = new Hashtable<String, Object>();
		mapJson.put("total", count);
		mapJson.put("rows", list);
		String jsonStr = JSON.toJSONString(mapJson, true);
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			LOGGER.error(LogUtil.getLogStr("IOException", "500", e.getMessage()), e);
		}
		out.print(jsonStr);
		out.close();
	}

	/**
	 * 向页面回去data字符串
	 */
	public static void returnJsonStringData(HttpServletResponse response, String data) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			LOGGER.error(LogUtil.getLogStr("IOException", "500", e.getMessage()), e);
		}
		out.write(data);
		out.close();
	}

	/**
	 * 将list转换为json字符串
	 */
	public static <T> String getJsonArray4JavaList(List<T> list) {
		return JSON.toJSONString(list, true);
	}

	/**
	 * @description 获得简单通用的JSON字符串，包括返回码和数据（或错误信息）
	 * @param code 返回码，表示：成功(200)，失败(404...)
	 * @param content 返回内容，成功返回数据，失败返回错误信息
	 * @return 转化后的JSON字符串
	 */
	public static String getCommonJson(int code, Object content) {
		Map<String, Object> commonMap = new HashMap<String, Object>();
		commonMap.put("code", code);
		if (code == CODE_SUCCESS) {	//若是正常状态码
			commonMap.put("data", content);
		} else {
			commonMap.put("msg", content);
		}
		return JSON.toJSONString(commonMap, SerializerFeature.PrettyFormat,
				SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteNullStringAsEmpty);
	}

}