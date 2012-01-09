package com.justinmobile.tsm.cms2ac.security.scp02;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.justinmobile.core.dao.support.EnumPersistentable;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.MessageI18NUtils;
import com.justinmobile.core.utils.web.EnumJsonable;
import com.justinmobile.tsm.cms2ac.engine.SecureUtil;

public enum SecureAlgorithm implements EnumPersistentable, EnumJsonable {
	/** 3DES-ECB */
	TRIPLE_DES_ECB(0x81) {
		public byte[] decrypt(byte[] ciphertext, byte[] key) {
			return SecureUtil.decryptTripleDesEcb(ArrayUtils.subarray(key, 0, 8), ArrayUtils.subarray(key, 8, 16), ciphertext);
		}
	},
	/** 3DES-CBC */
	TRIPLE_DES_CBC(0x82) {
		@Override
		public byte[] decrypt(byte[] ciphertext, byte[] key) {
			return SecureUtil.decryptTripleDesCbc(ArrayUtils.subarray(key, 0, 8), ArrayUtils.subarray(key, 8, 16), ciphertext);
		}
	},
	/** DES-CBC */
	DES_CBC(0x84) {
		@Override
		public byte[] decrypt(byte[] ciphertext, byte[] key) {
			return SecureUtil.decryptDesCbc(key, ciphertext);
		}
	},
	/** AES */
	AES(0x88) {
		@Override
		public byte[] decrypt(byte[] ciphertext, byte[] key) {
			return SecureUtil.decryptAes(ciphertext, key);
		}
	},
	/** 未知算法 */
	UNKNOWN(0x00) {
		@Override
		public byte[] decrypt(byte[] ciphertext, byte[] key) {
			throw new PlatformException(PlatformErrorCode.UNCOMPLETED_METHOD);
		}
	};

	public static final String NAME = "com.justinmobile.tsm.cms2ac.security.scp02.SecureAlgorithm";

	public static final String I18N_KEY = "secure.algorithm";

	public static final Map<String, String> I18N_CONFIG = MessageI18NUtils.getValuesByKey(I18N_KEY);

	int value;

	/**
	 * 解密数据
	 * 
	 * @param ciphertext
	 *            待解密的密文
	 * @param key
	 *            解密密钥
	 * @return 解密后的明文
	 * @throws PlatformErrorCode.SECURE_UNKNOWN_ALGORITHM
	 *             如果解密算法不支持
	 */
	abstract public byte[] decrypt(byte[] ciphertext, byte[] key);

	@Override
	public int getValue() {
		return value;
	}

	SecureAlgorithm(int value) {
		this.value = value;
	}

	public static SecureAlgorithm valueOf(int value) {
		SecureAlgorithm[] secureAlgorithms = SecureAlgorithm.values();

		for (SecureAlgorithm secureAlgorithm : secureAlgorithms) {
			if (value == secureAlgorithm.getValue()) {
				return secureAlgorithm;
			}
		}

		return UNKNOWN;
	}

	@Override
	public Map<String, Object> toJson() {
		HashMap<String, Object> mappedSecureAlgorithm = new HashMap<String, Object>();

		mappedSecureAlgorithm.put("name", getDescription());
		mappedSecureAlgorithm.put("value", ConvertUtils.int2HexString(value));

		return mappedSecureAlgorithm;
	}

	public String getDescription() {
		return I18N_CONFIG.get(name());
	}

}
