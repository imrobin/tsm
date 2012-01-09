

package com.justinmobile.core.utils.web;



public class KeyLongValue {
	private String key;
	private Long value;
	/**
	 * @param key
	 * @param value
	 */
	public KeyLongValue(String key, Long value) {
		this.key = key;
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public Long getValue() {
		return value;
	}
	public void setValue(Long value) {
		this.value = value;
	}

}



