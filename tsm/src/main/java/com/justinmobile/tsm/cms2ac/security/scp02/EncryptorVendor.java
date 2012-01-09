package com.justinmobile.tsm.cms2ac.security.scp02;

import java.util.HashMap;
import java.util.Map;

import com.justinmobile.core.utils.MessageI18NUtils;

public enum EncryptorVendor {

	KEYOU("keyou"), HSM("hsm"), KOAL("koal");

	public static final String I18N_KEY = "encryptor.vendor";

	public static final Map<String, String> I18N_CONFIG = MessageI18NUtils.getValuesByKey(I18N_KEY);

	private String value;

	EncryptorVendor(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Map<String, Object> export() {
		Map<String, Object> result = new HashMap<String, Object>();

		for (EncryptorVendor encryptorVendor : EncryptorVendor.values()) {
			Map<String, Object> mappedEncryptorVendor = new HashMap<String, Object>();
			mappedEncryptorVendor.put("name", I18N_CONFIG.get(encryptorVendor.value));
			mappedEncryptorVendor.put("value", encryptorVendor.value);

			result.put(encryptorVendor.name(), mappedEncryptorVendor);
		}

		return result;
	}
}
