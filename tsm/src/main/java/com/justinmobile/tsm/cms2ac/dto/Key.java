package com.justinmobile.tsm.cms2ac.dto;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.security.scp02.SecureAlgorithm;

public class Key {

	protected static final int KEY_LENGHT_BYTE_CCONUT = 16;

	protected String keyValue;

	protected String keyCheckValue;

	protected int keyIndex;

	protected int keyId;

	protected int keyVersion;

	protected SecureAlgorithm algorithm;

	protected byte[] disperseFactor = new byte[] {};

	protected byte[] sessionSalt = new byte[] {};;

	protected byte[] encryptedKey;

	protected Map<String, Object> keyProfiles = new HashMap<String, Object>();

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(byte[] keyValue) {
		this.keyValue = ConvertUtils.byteArray2HexString(keyValue);
	}

	public String getKeyCheckValue() {
		return keyCheckValue;
	}

	public void setKeyCheckValue(byte[] keyCheckValue) {
		this.keyCheckValue = ConvertUtils.byteArray2HexString(keyCheckValue);
	}

	public void padKeyLength() {
		this.keyValue = StringUtils.leftPad(this.keyValue, KEY_LENGHT_BYTE_CCONUT * 2, '0');
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public void setKeyCheckValue(String keyCheckValue) {
		this.keyCheckValue = keyCheckValue;
	}

	public int getKeyIndex() {
		return keyIndex;
	}

	public void setKeyIndex(int keyIndex) {
		this.keyIndex = keyIndex;
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public int getKeyVersion() {
		return keyVersion;
	}

	public void setKeyVersion(int keyVersion) {
		this.keyVersion = keyVersion;
	}

	public SecureAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(SecureAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public byte[] getDisperseFactor() {
		return disperseFactor;
	}

	public void setDisperseFactor(byte[] disperseFactor) {
		this.disperseFactor = disperseFactor;
	}

	public byte[] getSessionSalt() {
		return sessionSalt;
	}

	public void setSessionSalt(byte[] sessionSalt) {
		this.sessionSalt = sessionSalt;
	}

	public void addKeyProfile(String name, KeyProfile keyProfile) {
		keyProfiles.put(name, keyProfile);
	}

	public void addKeyProfile(String name, ApplicationKeyProfile keyProfile) {
		keyProfiles.put(name, keyProfile);
	}

	@SuppressWarnings("unchecked")
	public <T> T getKeyProfile(String name) {
		return (T) keyProfiles.get(name);
	}

	public byte[] getEncryptedKey() {
		return encryptedKey;
	}

	public void setEncryptedKey(byte[] encryptedKey) {
		this.encryptedKey = encryptedKey;
	}
}
