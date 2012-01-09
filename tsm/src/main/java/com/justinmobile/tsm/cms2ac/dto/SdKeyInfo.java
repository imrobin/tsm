package com.justinmobile.tsm.cms2ac.dto;

public class SdKeyInfo {

	private int keyId;

	private int keyVersion;

	private int keyType;

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

	public int getKeyType() {
		return keyType;
	}

	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	private int keyLength;

	public SdKeyInfo(byte[] info) {
		this.keyId = info[0];
		this.keyVersion = info[1];
		this.keyType = info[2];
		this.keyLength = info[3];
	}

}
