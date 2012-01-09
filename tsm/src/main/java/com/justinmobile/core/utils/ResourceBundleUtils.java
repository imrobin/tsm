package com.justinmobile.core.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.justinmobile.core.utils.encode.JsonBinder;

public class ResourceBundleUtils {
	
	private static final String FILE_PATH = "i18n/messages";
	
	private static final String SEPARATOR_CHAR = ",";
	
	private static ResourceBundle res = ResourceBundle.getBundle(FILE_PATH);
	
	private static Properties p = MapUtils.toProperties(MapUtils.toMap(res));
	
	/**
	 * 简单得到properties文件中key对应的value，没找到key时返回null
	 * @param key
	 * @return
	 */
	public static String getStringMessage(String key) {
		if(p == null){
			return res.getString(key);
		}
		return p.getProperty(key, null);
	}
	
	/**
	 * 得到properties文件中的key对应的value,转换成Map
	 * value中每条记录的分隔符默认为","
	 * Map中key和value的分隔符为":"
	 * @param key
	 * @return
	 */
	public static Map<String, String> getMapMessage(String key) {
		try {
			String[] strs = StringUtils.split(getStringMessage(key), SEPARATOR_CHAR);
			if (ArrayUtils.isEmpty(strs)) {
				return null;
			}
			Map<String, String> map = new LinkedHashMap<String, String>();
			for (String str : strs) {
				String[] buf = StringUtils.split(str, ":");
				if (ArrayUtils.isEmpty(buf)) {
					return null;
				}
				map.put(buf[0], buf[1]);
			}
			return map;
		} catch (MissingResourceException e) {
			return null;
		}
	}
	
	/**
	 * 得到properties文件中的key对应的value,转换成Array
	 * 先转成Map,然后再转成Array
	 * @param key
	 * @return
	 */
	public static String getArrayMessage(String key) {
		Map<String, String> map = getMapMessage(key);
		return getArrayMessage(map);
		
	}
	
	/**
	 * 得到properties文件中的key对应的value,转换成JsonArray
	 * 先转成Map,然后再转成JsonArray
	 * @param key
	 * @return
	 */
	public static String getJsonArrayMessage(String key) {
		Map<String, String> map = getMapMessage(key);
		return getJsonArrayMessage(map);
		
	}
	
	public static String getArrayMessage(Map<String, String> map) {
		if (MapUtils.isNotEmpty(map)) {
			StringBuffer buf = new StringBuffer();
			buf.append("[");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				buf.append("['" + entry.getKey() + "',");
				buf.append("'" + entry.getValue() + "'],");
			}
			buf.deleteCharAt(buf.length() - 1);
			buf.append("]");
			return buf.toString();
		} else {
			return null;
		}
		
	}
	
	public static String getJsonArrayMessage(Map<String, String> map) {
		return JsonBinder.buildNormalBinder().toJson(map);
	}
	
}
