package com.justinmobile.core.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.jdom.IllegalDataException;

public class MessageI18NUtils {

	private static ResourceBundle rb = ResourceBundle.getBundle("i18n/messages", Locale.getDefault());

	public static Map<String, String> getValuesByKey(String key) {
		HashMap<String, String> mappedKey = new HashMap<String, String>();

		String valueString = rb.getString(key);
		String[] keyValuePairs = valueString.split(",");

		for (String keyValuePair : keyValuePairs) {
			String[] keyValueArray = keyValuePair.split(":");

			if (2 != keyValueArray.length) {// 如果每一个键值对不是用“:”隔开，抛出异常
				throw new IllegalDataException("illegal i18n config: " + key);
			}

			mappedKey.put(keyValueArray[0], keyValueArray[1]);
		}

		return mappedKey;
	}
}
